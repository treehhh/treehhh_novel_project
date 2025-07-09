package com.lf.novelbackend.controller;

import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.annotation.UserTypeAuthCheck;
import com.lf.novelbackend.common.BaseResponse;
import com.lf.novelbackend.common.DeleteByStringIdRequest;
import com.lf.novelbackend.common.ResultUtils;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.dto.novel.NovelQueryRequest;
import com.lf.novelbackend.model.dto.novel.NovelUpdateRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.vo.NovelVO;
import com.lf.novelbackend.service.NovelService;
import com.lf.novelbackend.service.UserService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.lf.novelbackend.constant.UserConstant.AUTHOR_TYPE;


/**
 * 用户接口
 */
@RestController
@RequestMapping("/novel")
@Slf4j
public class NovelController {
    @Resource
    private UserService userService;

    @Resource
    private NovelService novelService;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 创建小说(作者)
     *
     * @param novelAddRequest
     * @return
     */
    @PostMapping("/add")
    @UserTypeAuthCheck(mustUserType = AUTHOR_TYPE)
    public BaseResponse<String> addNovel(@RequestBody NovelAddRequest novelAddRequest, HttpServletRequest request) {
        if (novelAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = novelService.addNovel(novelAddRequest, request);
        ThrowUtils.throwIf(StrUtil.isBlank(id),ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(id);
    }

    /**
     * 逻辑删除小说(作者、管理员)
     *
     * @param deleteByStringIdRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNovel(@RequestBody DeleteByStringIdRequest deleteByStringIdRequest, HttpServletRequest request) {
        if (deleteByStringIdRequest == null || StrUtil.isBlank(deleteByStringIdRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = deleteByStringIdRequest.getId();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        // 判断要删除的数据是否存在
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅作者本人可操作
        User loginUser = userService.getLoginUser(request);
        Long authorId = novel.getAuthorId();
        ThrowUtils.throwIf(authorId == null, ErrorCode.NO_AUTH_ERROR, "非该小说作者");
        if (!authorId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非该小说作者");
        }
        // 数据库操作
        Update update = new Update();
        update.set("isDelete", 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新小说信息(作者、管理员)
     *
     * @param novelUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateNovel(@RequestBody NovelUpdateRequest novelUpdateRequest, HttpServletRequest request) {
        if (novelUpdateRequest == null || StrUtil.isBlank(novelUpdateRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        novelService.updateNovelById(novelUpdateRequest, request);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取小说
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Novel> getNovelById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        Novel novel = mongoTemplate.findOne(query, Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(novel);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<NovelVO> getNovelVOById(String id) {
        BaseResponse<Novel> response = getNovelById(id);
        Novel novel = response.getData();
        return ResultUtils.success(novelService.getNovelVO(novel));
    }

    /**
     * 分页获取小说列表
     *
     * @param novelQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Novel>> getNovelPage(@RequestBody NovelQueryRequest novelQueryRequest) {
        if (novelQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 这里的页码是从0开始的，为了统一，进行减一处理
        int current = novelQueryRequest.getCurrent() - 1;
        int size = novelQueryRequest.getPageSize();
        PageRequest pageRequest = PageRequest.of(current, size);
        Query query = novelService.getNovelQuery(novelQueryRequest);
        query.with(pageRequest);
        List<Novel> novelList = mongoTemplate.find(query, Novel.class);
        Page<Novel> page = new PageImpl<>(novelList, pageRequest, novelList.size());
        return ResultUtils.success(page);
    }

    /**
     * 分页获取小说封装列表（仅管理员）
     *
     * @param novelQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<NovelVO>> getNovelVOPage(@RequestBody NovelQueryRequest novelQueryRequest) {
        if (novelQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BaseResponse<Page<Novel>> pageBaseResponse = this.getNovelPage(novelQueryRequest);
        Page<Novel> novelPage = pageBaseResponse.getData();
        List<NovelVO> novelVOList = novelService.getNovelVOList(novelPage.getContent());
        Page<NovelVO> novelVOPage = new PageImpl<>(novelVOList, novelPage.getPageable(), novelPage.getTotalElements());
        return ResultUtils.success(novelVOPage);
    }

}
