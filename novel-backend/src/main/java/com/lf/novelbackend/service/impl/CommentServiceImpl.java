package com.lf.novelbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.comment.CommentAddRequest;
import com.lf.novelbackend.model.dto.comment.CommentDeleteRequest;
import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Comment;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.enums.UserTypeEnum;
import com.lf.novelbackend.model.vo.CommentVO;
import com.lf.novelbackend.service.ChapterService;
import com.lf.novelbackend.service.CommentService;
import com.lf.novelbackend.service.UserService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86138
 * @createDate 2025-06-30 17:48:06
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Resource
    private UserService userService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ChapterService chapterService;

    @Override
    public CommentVO getCommentVO(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        User user = userService.getById(comment.getUserId());
        commentVO.setUser(userService.getUserVO(user));
        return commentVO;
    }

    @Override
    public List<CommentVO> getCommentVOList(List<Comment> commentList) {
        if (CollUtil.isEmpty(commentList)) {
            return new ArrayList<>();
        }
        return commentList.stream().map(this::getCommentVO).collect(Collectors.toList());
    }

    @Override
    public Boolean addComment(CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (commentAddRequest == null || StrUtil.isBlank(commentAddRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取参数
        String content = commentAddRequest.getContent();
        String id = commentAddRequest.getId();
        Integer chapterNumber = commentAddRequest.getChapterNumber();

        // 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(content), ErrorCode.PARAMS_ERROR, "评论内容不能为空");

        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 准备插入内容
        Comment comment = new Comment();
        comment.setContent(commentAddRequest.getContent());
        comment.setId(RandomStringUtils.randomAlphanumeric(16));
        comment.setUserId(loginUser.getId());

        // 根据chapterNumber是否传入而确定评论位置
        Query query = new Query();
        Update update = new Update();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR, "小说不存在");
        if (ObjUtil.isNull(chapterNumber)) {
            // 发表小说评论
            update.push("comments", comment);
        } else {
            // 发表章节评论
            query.addCriteria(Criteria.where("chapters").elemMatch(
                    Criteria.where("chapterNumber").is(chapterNumber)
                            .and("isDelete").is(0)
            ));
            novel = mongoTemplate.findOne(query, Novel.class);
            ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR, "章节不存在");
            update.push("chapters.$.comments", comment);
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "发表评论失败");
        return true;
    }

    @Override
    public Boolean deleteComment(CommentDeleteRequest commentDeleteRequest, HttpServletRequest request) {
        if (commentDeleteRequest == null || StrUtil.isBlank(commentDeleteRequest.getId()) || StrUtil.isBlank(commentDeleteRequest.getCommentId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取参数
        String commentId = commentDeleteRequest.getCommentId();
        String id = commentDeleteRequest.getId();
        Integer chapterNumber = commentDeleteRequest.getChapterNumber();

        // 仅本人或管理员可操作
        User loginUser = userService.getLoginUser(request);
        UserTypeEnum userTypeEnum = UserTypeEnum.getEnumByValue(loginUser.getUserType());
        ThrowUtils.throwIf(userTypeEnum == null, ErrorCode.PARAMS_ERROR, "用户类型不存在");
        List<Comment> commentList;
        Long userId;
        if (ObjUtil.isNull(chapterNumber)) {
            commentList = this.getCommentFromNovel(id).stream().filter(comment -> comment.getId().equals(commentId)).collect(Collectors.toList());
        } else {
            commentList = this.getCommentFromChapter(id, chapterNumber).stream().filter(comment -> comment.getId().equals(commentId)).collect(Collectors.toList());
        }
        ThrowUtils.throwIf(CollUtil.isEmpty(commentList), ErrorCode.NOT_FOUND_ERROR, "评论为空，无法删除");
        userId = commentList.get(0).getUserId();
        if (!loginUser.getId().equals(userId) && !UserTypeEnum.ADMIN.getValue().equals(loginUser.getUserType())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅本人或管理员可操作");
        }

        // 操作数据库
        Update update = new Update();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));

        if (ObjUtil.isNull(chapterNumber)) {
            // 删除小说评论
            query.addCriteria(Criteria.where("comments.id").is(commentId));
            update.set("comments.$.isDelete", 1);
        } else {
            // 删除章节评论
            List<Comment> commentFromChapter = this.getCommentFromChapter(id, chapterNumber);
            for (Comment comment : commentFromChapter) {
                if (comment.getId().equals(commentId)) {
                    comment.setIsDelete(1);
                }
            }
            query.addCriteria(Criteria.where("chapters").elemMatch(
                    Criteria.where("chapterNumber").is(chapterNumber)
                            .and("isDelete").is(0)
            ));
            update.set("chapters.$.comments", commentFromChapter);
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 返回未删除的书评列表
     * @param id
     * @return
     */
    @Override
    public List<Comment> getCommentFromNovel(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        if (novel == null) {
            return null;
        }
        return novel.getComments().stream().filter(comment -> comment.getIsDelete() == 0).collect(Collectors.toList());
    }

    /**
     * 返回未删除的章节评论列表
     * @param id
     * @param chapterNumber
     * @return
     */
    @Override
    public List<Comment> getCommentFromChapter(String id, Integer chapterNumber) {
        if (StrUtil.isBlank(id) || chapterNumber == null) {
            return null;
        }
        Query query = chapterService.getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        if (novel == null) {
            return null;
        }
        List<Chapter> chapters = novel.getChapters().stream().filter(chapter -> chapter.getChapterNumber().equals(chapterNumber) && chapter.getIsDelete() == 0).collect(Collectors.toList());
        return chapters.get(0).getComments().stream().filter(comment -> comment.getIsDelete() == 0).collect(Collectors.toList());
    }


}




