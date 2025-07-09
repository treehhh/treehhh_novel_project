package com.lf.novelbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.chapter.*;
import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.enums.ReviewStatusEnum;
import com.lf.novelbackend.model.vo.ChapterVOToAdmin;
import com.lf.novelbackend.model.vo.ChapterVOToUser;
import com.lf.novelbackend.service.ChapterService;
import com.lf.novelbackend.service.UserService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.lf.novelbackend.constant.ReviewConstant.UNDER_REVIEW;


/**
 * @author 86138
 * @createDate 2025-06-30 17:48:06
 */
@Service
@Slf4j
public class ChapterServiceImpl implements ChapterService {

    @Resource
    private UserService userService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public ChapterVOToUser getChapterVOToUser(String id, Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        ChapterVOToUser chapterVOToUser = new ChapterVOToUser();
        chapterVOToUser.setId(id);
        BeanUtils.copyProperties(chapter, chapterVOToUser);
        return chapterVOToUser;
    }

    @Override
    public ChapterVOToAdmin getChapterVOToAdmin(String id, Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        ChapterVOToAdmin chapterVOToAdmin = new ChapterVOToAdmin();
        chapterVOToAdmin.setId(id);
        BeanUtils.copyProperties(chapter, chapterVOToAdmin);
        return chapterVOToAdmin;
    }

    @Override
    public List<ChapterVOToUser> getChapterVOToUserList(String id, List<Chapter> chapterList) {
        if (CollUtil.isEmpty(chapterList)) {
            return new ArrayList<>();
        }
        return chapterList.stream().map(chapter -> this.getChapterVOToUser(id, chapter)).collect(Collectors.toList());
    }

    @Override
    public List<ChapterVOToAdmin> getChapterVOToAdminList(String id, List<Chapter> chapterList) {
        if (CollUtil.isEmpty(chapterList)) {
            return new ArrayList<>();
        }
        return chapterList.stream().map(chapter -> this.getChapterVOToAdmin(id, chapter)).collect(Collectors.toList());
    }

    @Override
    public void addChapter(ChapterAddRequest chapterAddRequest, HttpServletRequest request) {
        if (chapterAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验字段
        String id = chapterAddRequest.getId();
        String title = chapterAddRequest.getTitle();
        Integer chapterNumber = chapterAddRequest.getChapterNumber();
        String content = chapterAddRequest.getContent();

        ThrowUtils.throwIf(StrUtil.isBlank(id), ErrorCode.PARAMS_ERROR, "小说Id不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(title), ErrorCode.PARAMS_ERROR, "章节标题不能为空");
        ThrowUtils.throwIf(ObjUtil.isNull(chapterNumber), ErrorCode.PARAMS_ERROR, "章节序号不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(content), ErrorCode.PARAMS_ERROR, "章节内容不能为空");

        // 仅作者本人可操作
        this.validateAuthor(id, request);

        // 校验数据库序号字段是否唯一
        Query query = getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel != null, ErrorCode.OPERATION_ERROR, "章节序号已存在");

        // 判断小说是否存在
        query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR, "小说不存在");

        // 补充字段
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterAddRequest, chapter);
        chapter.setReviewStatus(UNDER_REVIEW);

        // 数据库操作
        Update update = new Update().push("chapters", chapter);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "插入章节失败");
    }

    @Override
    public Boolean updateChapter(ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request) {
        if (chapterUpdateRequest == null || StrUtil.isBlank(chapterUpdateRequest.getId()) || ObjUtil.isNull(chapterUpdateRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = chapterUpdateRequest.getId();
        String title = chapterUpdateRequest.getTitle();
        Integer chapterNumber = chapterUpdateRequest.getChapterNumber();
        String content = chapterUpdateRequest.getContent();

        //TODO 修改的数据与之前数据一致的话无需交给管理员审核

        // 拼接query条件
        Query query = getChapterQueryById(id, chapterNumber);

        // 仅作者本人可操作
        this.validateAuthor(id, request);

        // 拼接update条件
        Update update = new Update();
        if (StrUtil.isNotBlank(title)) {
            update.set("chapters.$.title", title);
        }
        if (StrUtil.isNotBlank(content)) {
            update.set("chapters.$.content", content);
        }
        update.set("chapters.$.isDelete", 0);
        // 修改已发布章节需被重新审核
        update.set("chapters.$.reviewStatus", UNDER_REVIEW);
        update.set("chapters.$.reviewId", null);
        update.set("chapters.$.reviewComment", null);
        update.set("chapters.$.reviewTime", null);
        // 修改为“未发布”状态
        update.set("chapters.$.isRelease", 0);

        // 数据库操作
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "章节修改失败，或许是章节不存在");
        return true;
    }

    @Override
    public Boolean saveChapter(ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request) {
        if (chapterUpdateRequest == null || StrUtil.isBlank(chapterUpdateRequest.getId()) || ObjUtil.isNull(chapterUpdateRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = chapterUpdateRequest.getId();
        String title = chapterUpdateRequest.getTitle();
        Integer chapterNumber = chapterUpdateRequest.getChapterNumber();
        String content = chapterUpdateRequest.getContent();

        //TODO 、修改的数据与之前数据一致的话无需交给管理员审核

        // 拼接query条件
        Query query = getChapterQueryById(id, chapterNumber);

        // 仅作者本人可操作
        this.validateAuthor(id, request);

        // 拼接update条件
        Update update = new Update();
        if (StrUtil.isNotBlank(title)) {
            update.set("chapters.$.title", title);
        }
        if (StrUtil.isNotBlank(content)) {
            update.set("chapters.$.content", content);
        }
        update.set("chapters.$.isDelete", 0);
        // 数据库操作
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "章节保存失败，或许是章节不存在");
        return true;
    }

    @Override
    public Boolean reviewChapter(ChapterReviewRequest chapterReviewRequest, HttpServletRequest request) {
        if (chapterReviewRequest == null || StrUtil.isBlank(chapterReviewRequest.getId()) || ObjUtil.isNull(chapterReviewRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = chapterReviewRequest.getId();
        Integer chapterNumber = chapterReviewRequest.getChapterNumber();
        Integer reviewStatus = chapterReviewRequest.getReviewStatus();
        String reviewComment = chapterReviewRequest.getReviewComment();

        // 参数校验
        ReviewStatusEnum reviewStatusEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
        ThrowUtils.throwIf(reviewStatusEnum == null, ErrorCode.PARAMS_ERROR, "审核状态非法");

        // 校验章节是否存在
        Query query = getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 拼接update条件
        Update update = new Update();
        update.set("chapters.$.reviewStatus", reviewStatus);
        update.set("chapters.$.reviewId", loginUser.getId());
        update.set("chapters.$.reviewComment", reviewComment);
        update.set("chapters.$.reviewTime", new Date());

        // 数据库操作
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "章节审核修改失败");
        return true;
    }

    @Override
    public Boolean releaseChapter(ChapterIdRequest chapterIdRequest, HttpServletRequest request) {
        if (chapterIdRequest == null || StrUtil.isBlank(chapterIdRequest.getId()) || ObjUtil.isNull(chapterIdRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = chapterIdRequest.getId();
        Integer chapterNumber = chapterIdRequest.getChapterNumber();

        // 拼接query条件
        Query query = getChapterQueryById(id, chapterNumber);

        // 仅作者本人可操作
        this.validateAuthor(id, request);

        // 拼接update条件
        Update update = new Update();
        update.set("chapters.$.isRelease", 1);
        // 数据库操作
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR, "章节发表失败");
        return true;
    }


    @Override
    public Boolean deleteChapter(ChapterIdRequest chapterIdRequest, HttpServletRequest request) {
        if (chapterIdRequest == null || StrUtil.isBlank(chapterIdRequest.getId()) || ObjUtil.isNull(chapterIdRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        Integer chapterNumber = chapterIdRequest.getChapterNumber();
        String id = chapterIdRequest.getId();
        // 拼接查询条件
        Query query = getChapterQueryById(id, chapterNumber);
        // 仅作者本人可操作
        this.validateAuthor(id, request);
        // 数据库操作
        Update update = new Update();
        update.set("chapters.$.isDelete", 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR);
        return true;
    }


    /**
     * 获取筛选后的分页排序章节列表
     *
     * @param chapterQueryRequest
     * @return
     */
    @Override
    public Page<Chapter> getChapterPage(ChapterQueryRequest chapterQueryRequest) {
        if (chapterQueryRequest == null || StrUtil.isBlank(chapterQueryRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = chapterQueryRequest.getId();
        Integer isRelease = chapterQueryRequest.getIsRelease();
        Integer reviewStatus = chapterQueryRequest.getReviewStatus();
        Long reviewId = chapterQueryRequest.getReviewId();
        int current = chapterQueryRequest.getCurrent();
        int pageSize = chapterQueryRequest.getPageSize();
        int skip = (current - 1) * pageSize;

        // 动态参数处理
        List<AggregationOperation> stages = new ArrayList<>();

        // 筛选指定小说
        stages.add(Aggregation.match(Criteria.where("_id").is(new ObjectId(id))));
        stages.add(Aggregation.match(Criteria.where("isDelete").is(0)));

        // 展开数组
        stages.add(Aggregation.unwind("$chapters"));

        // 动态添加过滤条件（如果参数存在）
        if (ObjUtil.isNotNull(isRelease)) {
            stages.add(Aggregation.match(
                    Criteria.where("chapters.isRelease").is(isRelease)
            ));
        }
        if (ObjUtil.isNotNull(reviewStatus)) {
            stages.add(Aggregation.match(
                    Criteria.where("chapters.reviewStatus").is(reviewStatus)
            ));
        }
        if (ObjUtil.isNotNull(reviewId)) {
            stages.add(Aggregation.match(
                    Criteria.where("chapters.reviewId").is(reviewId)
            ));
        }

        stages.add(Aggregation.match(Criteria.where("chapters.isDelete").is(0)));
        // 排序
        stages.add(Aggregation.sort(Sort.by(Sort.Order.asc("chapters.chapterNumber"))));
        // 分页
        stages.add(Aggregation.skip(skip));
        stages.add(Aggregation.limit(pageSize));

        stages.add(Aggregation.replaceRoot("chapters"));

        // 3. 构建聚合管道
        Aggregation aggregation = Aggregation.newAggregation(stages);

        // 4. 执行查询
        AggregationResults<Chapter> results = mongoTemplate.aggregate(
                aggregation,
                "novels",
                Chapter.class
        );

        List<Chapter> chaptersList = results.getMappedResults();
        PageRequest pageRequest = PageRequest.of(current, pageSize);
        return new PageImpl<>(chaptersList, pageRequest, chaptersList.size());
    }

    @Override
    public Query getChapterQueryById(String id, Integer chapterNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        query.addCriteria(Criteria.where("chapters").elemMatch(
                Criteria.where("chapterNumber").is(chapterNumber)
                        .and("isDelete").is(0)
        ));
        return query;
    }

    @Override
    public void validateAuthor(String id, HttpServletRequest request) {
        Novel novel = mongoTemplate.findById(new ObjectId(id), Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long authorId = novel.getAuthorId();
        ThrowUtils.throwIf(authorId == null, ErrorCode.NO_AUTH_ERROR, "非该小说作者");
        if (!authorId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非该小说作者");
        }
    }
}




