package com.lf.novelbackend.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.common.BaseResponse;
import com.lf.novelbackend.common.ResultUtils;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.model.dto.chapter.ChapterIdRequest;
import com.lf.novelbackend.model.dto.comment.CommentAddRequest;
import com.lf.novelbackend.model.dto.comment.CommentDeleteRequest;
import com.lf.novelbackend.model.vo.CommentVO;
import com.lf.novelbackend.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 评论接口
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 发表评论
     *
     * @param commentAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addCommentToNovel(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (commentAddRequest == null || StrUtil.isBlank(commentAddRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = commentService.addComment(commentAddRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 逻辑删除评论
     *
     * @param commentDeleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody CommentDeleteRequest commentDeleteRequest, HttpServletRequest request) {
        if (commentDeleteRequest == null || StrUtil.isBlank(commentDeleteRequest.getId()) || StrUtil.isBlank(commentDeleteRequest.getCommentId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = commentService.deleteComment(commentDeleteRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 获取评论列表
     *
     * @param chapterIdRequest
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<List<CommentVO>> getCommentVOList(@RequestBody ChapterIdRequest chapterIdRequest) {
        if (chapterIdRequest == null || StrUtil.isBlank(chapterIdRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = chapterIdRequest.getId();
        Integer chapterNumber = chapterIdRequest.getChapterNumber();

        if (ObjUtil.isNull(chapterNumber)) {
            return ResultUtils.success(commentService.getCommentVOList(commentService.getCommentFromNovel(id)));
        }else{
            return ResultUtils.success(commentService.getCommentVOList(commentService.getCommentFromChapter(id,chapterNumber)));
        }
    }

}
