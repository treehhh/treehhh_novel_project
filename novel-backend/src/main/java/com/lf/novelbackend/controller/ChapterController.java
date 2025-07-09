package com.lf.novelbackend.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.annotation.UserTypeAuthCheck;
import com.lf.novelbackend.common.BaseResponse;
import com.lf.novelbackend.common.ResultUtils;
import com.lf.novelbackend.constant.ReviewConstant;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.chapter.*;
import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.vo.ChapterVOToAdmin;
import com.lf.novelbackend.model.vo.ChapterVOToUser;
import com.lf.novelbackend.service.ChapterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.lf.novelbackend.constant.UserConstant.ADMIN_TYPE;
import static com.lf.novelbackend.constant.UserConstant.AUTHOR_TYPE;


/**
 * 用户接口
 */
@RestController
@RequestMapping("/chapter")
@Slf4j
public class ChapterController {

    @Resource
    private ChapterService chapterService;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 新建章节(作者)
     *
     * @param chapterAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addChapter(@RequestBody ChapterAddRequest chapterAddRequest, HttpServletRequest request) {
        if (chapterAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chapterService.addChapter(chapterAddRequest, request);
        return ResultUtils.success(true);
    }

    /**
     * 逻辑删除章节(作者)
     *
     * @param chapterIdRequest
     * @return
     */
    @PostMapping("/delete")
    @UserTypeAuthCheck(mustUserType = AUTHOR_TYPE)
    public BaseResponse<Boolean> deleteChapter(@RequestBody ChapterIdRequest chapterIdRequest, HttpServletRequest request) {
        if (chapterIdRequest == null || StrUtil.isBlank(chapterIdRequest.getId()) || ObjUtil.isNull(chapterIdRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = chapterService.deleteChapter(chapterIdRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 更新已发布章节信息(作者)
     *
     * @param chapterUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @UserTypeAuthCheck(mustUserType = AUTHOR_TYPE)
    public BaseResponse<Boolean> updateChapter(@RequestBody ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request) {
        if (chapterUpdateRequest == null || StrUtil.isBlank(chapterUpdateRequest.getId()) || ObjUtil.isNull(chapterUpdateRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = chapterService.updateChapter(chapterUpdateRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 保存章节信息(作者)
     *
     * @param chapterUpdateRequest
     * @return
     */
    @PostMapping("/save")
    @UserTypeAuthCheck(mustUserType = AUTHOR_TYPE)
    public BaseResponse<Boolean> saveChapter(@RequestBody ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request) {
        if (chapterUpdateRequest == null || StrUtil.isBlank(chapterUpdateRequest.getId()) || ObjUtil.isNull(chapterUpdateRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = chapterService.saveChapter(chapterUpdateRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 审核章节信息(管理员)
     *
     * @param chapterReviewRequest
     * @return
     */
    @PostMapping("/review")
    @UserTypeAuthCheck(mustUserType = ADMIN_TYPE)
    public BaseResponse<Boolean> reviewChapter(@RequestBody ChapterReviewRequest chapterReviewRequest, HttpServletRequest request) {
        if (chapterReviewRequest == null || StrUtil.isBlank(chapterReviewRequest.getId()) || ObjUtil.isNull(chapterReviewRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = chapterService.reviewChapter(chapterReviewRequest, request);
        return ResultUtils.success(b);
    }

    /**
     * 发表章节(作者)
     *
     * @param ChapterIdRequest
     * @return
     */
    @PostMapping("/release")
    @UserTypeAuthCheck(mustUserType = AUTHOR_TYPE)
    public BaseResponse<Boolean> releaseChapter(@RequestBody ChapterIdRequest ChapterIdRequest, HttpServletRequest request) {
        if (ChapterIdRequest == null || StrUtil.isBlank(ChapterIdRequest.getId()) || ObjUtil.isNull(ChapterIdRequest.getChapterNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = chapterService.releaseChapter(ChapterIdRequest, request);
        return ResultUtils.success(b);
    }


    /**
     * 获取单个章节（用户）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/user_vo")
    public BaseResponse<ChapterVOToUser> getChapterVOToUser(String id, Integer chapterNumber) {
        if (StrUtil.isBlank(id) || ObjUtil.isNull(chapterNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Query query = chapterService.getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        for (Chapter chapter : novel.getChapters()) {
            if (chapter.getChapterNumber().equals(chapterNumber) && chapter.getIsDelete() == 0
                    && chapter.getIsRelease() == 1 && chapter.getReviewStatus() == ReviewConstant.PASS_REVIEW) {
                return ResultUtils.success(chapterService.getChapterVOToUser(id, chapter));
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    }

    /**
     * 获取单个章节（管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/admin_vo")
    public BaseResponse<ChapterVOToAdmin> getChapterVOToAdmin(String id, Integer chapterNumber) {
        if (StrUtil.isBlank(id) || ObjUtil.isNull(chapterNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Query query = chapterService.getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        for (Chapter chapter : novel.getChapters()) {
            if (chapter.getChapterNumber().equals(chapterNumber) && chapter.getIsDelete() == 0
                    && chapter.getIsRelease() == 1) {
                return ResultUtils.success(chapterService.getChapterVOToAdmin(id, chapter));
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    }


    /**
     * 获取单个章节（作者）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/author_vo")
    public BaseResponse<ChapterVOToAdmin> getChapterVOToAuthor(String id, Integer chapterNumber, HttpServletRequest request) {
        if (StrUtil.isBlank(id) || ObjUtil.isNull(chapterNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 作者只能查看到自己小说的章节
        chapterService.validateAuthor(id, request);

        Query query = chapterService.getChapterQueryById(id, chapterNumber);
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        for (Chapter chapter : novel.getChapters()) {
            if (chapter.getChapterNumber().equals(chapterNumber) && chapter.getIsDelete() == 0) {
                return ResultUtils.success(chapterService.getChapterVOToAdmin(id, chapter));
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    }

    /**
     * 获取分页章节（用户）
     *
     * @param chapterQueryRequest
     * @return
     */
    @PostMapping("/page/user_vo")
    public BaseResponse<Page<ChapterVOToUser>> getChapterVOToUserPage(@RequestBody ChapterQueryRequest chapterQueryRequest) {
        if (chapterQueryRequest == null || StrUtil.isBlank(chapterQueryRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int current = chapterQueryRequest.getCurrent();
        int pageSize = chapterQueryRequest.getPageSize();
        // 用户只能查看 已发布、过审核 的章节
        chapterQueryRequest.setIsRelease(1);
        chapterQueryRequest.setReviewStatus(ReviewConstant.PASS_REVIEW);
        // 转换
        Page<Chapter> chapterPage = chapterService.getChapterPage(chapterQueryRequest);
        List<ChapterVOToUser> chapterVOToUserList = chapterService.getChapterVOToUserList(chapterQueryRequest.getId(), chapterPage.getContent());
        PageRequest pageRequest = PageRequest.of(current, pageSize);
        return ResultUtils.success(new PageImpl<>(chapterVOToUserList, pageRequest, chapterPage.getTotalElements()));
    }


    /**
     * 获取分页章节（管理员）
     *
     * @param chapterQueryRequest
     * @return
     */
    @PostMapping("/page/admin_vo")
    public BaseResponse<Page<ChapterVOToAdmin>> getChapterVOToAdminPage(@RequestBody ChapterQueryRequest chapterQueryRequest) {
        if (chapterQueryRequest == null || StrUtil.isBlank(chapterQueryRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int current = chapterQueryRequest.getCurrent();
        int pageSize = chapterQueryRequest.getPageSize();
        // 管理员只能查看“已发布”的章节
        chapterQueryRequest.setIsRelease(1);

        Page<Chapter> chapterPage = chapterService.getChapterPage(chapterQueryRequest);
        List<ChapterVOToAdmin> chapterVOToAdminList = chapterService.getChapterVOToAdminList(chapterQueryRequest.getId(), chapterPage.getContent());
        PageRequest pageRequest = PageRequest.of(current, pageSize);
        return ResultUtils.success(new PageImpl<>(chapterVOToAdminList, pageRequest, chapterPage.getTotalElements()));
    }

    /**
     * 获取分页章节（作者）
     *
     * @param chapterQueryRequest
     * @return
     */
    @PostMapping("/page/author_vo")
    public BaseResponse<Page<ChapterVOToAdmin>> getChapterVOToAuthorPage(@RequestBody ChapterQueryRequest chapterQueryRequest, HttpServletRequest request) {
        if (chapterQueryRequest == null || StrUtil.isBlank(chapterQueryRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 作者只能查看到自己小说的章节
        chapterService.validateAuthor(chapterQueryRequest.getId(), request);

        int current = chapterQueryRequest.getCurrent();
        int pageSize = chapterQueryRequest.getPageSize();
        Page<Chapter> chapterPage = chapterService.getChapterPage(chapterQueryRequest);
        List<ChapterVOToAdmin> chapterVOToAdminList = chapterService.getChapterVOToAdminList(chapterQueryRequest.getId(), chapterPage.getContent());
        PageRequest pageRequest = PageRequest.of(current, pageSize);
        return ResultUtils.success(new PageImpl<>(chapterVOToAdminList, pageRequest, chapterPage.getTotalElements()));
    }

}
