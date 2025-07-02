package com.lf.novelbackend.controller;

import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.common.BaseResponse;
import com.lf.novelbackend.common.ResultUtils;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.dto.novel.NovelUpdateRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.vo.NovelVO;
import com.lf.novelbackend.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 用户接口
 */
@RestController
@RequestMapping("/novel")
@Slf4j
public class NovelController {

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
    public BaseResponse<String> addNovel(@RequestBody NovelAddRequest novelAddRequest) {
        if (novelAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = novelService.addNovel(novelAddRequest);
        ThrowUtils.throwIf(StrUtil.isBlank(id),ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(id);
    }

//    /**
//     * 删除用户(管理员)
//     *
//     * @param deleteRequest
//     * @return
//     */
//    @PostMapping("/delete")
//    public BaseResponse<Boolean> deleteNovel(@RequestBody DeleteRequest deleteRequest) {
//        if (deleteRequest == null || deleteRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean b = novelService.removeById(deleteRequest.getId());
//        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "管理员删除用户失败");
//        return ResultUtils.success(b);
//    }
//
//    /**
//     * 更新用户(管理员)
//     *
//     * @param novelUpdateRequest
//     * @return
//     */
//    @PostMapping("/update")
//    public BaseResponse<Boolean> updateNovel(@RequestBody NovelUpdateRequest novelUpdateRequest) {
//        if (novelUpdateRequest == null || StrUtil.isBlank(novelUpdateRequest.getId())) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Novel novel = new Novel();
//        BeanUtils.copyProperties(novelUpdateRequest, novel);
//        boolean result = novelService.updateById(novel);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "管理员更新用户失败");
//        return ResultUtils.success(true);
//    }

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
        Novel novel = mongoTemplate.findById(id, Novel.class);
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

//    /**
//     * 分页获取用户列表
//     *
//     * @param novelQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<Novel>> listNovelByPage(@RequestBody NovelQueryRequest novelQueryRequest,
//                                                     HttpServletRequest request) {
//        if (novelQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long current = novelQueryRequest.getCurrent();
//        long size = novelQueryRequest.getPageSize();
//        Page<Novel> novelPage = novelService.page(new Page<>(current, size),
//                novelService.getQueryWrapper(novelQueryRequest));
//        return ResultUtils.success(novelPage);
//    }
//
//    /**
//     * 分页获取用户封装列表（仅管理员）
//     *
//     * @param novelQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    @NovelTypeAuthCheck(mustNovelType = ADMIN_TYPE)
//    public BaseResponse<Page<NovelVO>> listNovelVOByPage(@RequestBody NovelQueryRequest novelQueryRequest,
//                                                         HttpServletRequest request) {
//        if (novelQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        int current = novelQueryRequest.getCurrent();
//        int size = novelQueryRequest.getPageSize();
//        BaseResponse<Page<Novel>> pageBaseResponse = this.listNovelByPage(novelQueryRequest, request);
//        Page<Novel> novelPage = pageBaseResponse.getData();
//        Page<NovelVO> novelVOPage = new Page<>(current, size, novelPage.getTotal());
//        List<NovelVO> novelVOList = novelService.getNovelVOList(novelPage.getRecords());
//        novelVOPage.setRecords(novelVOList);
//        return ResultUtils.success(novelVOPage);
//    }
}
