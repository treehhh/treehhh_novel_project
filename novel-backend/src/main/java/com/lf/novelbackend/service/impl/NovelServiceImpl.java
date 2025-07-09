package com.lf.novelbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.dto.novel.NovelMarkRequest;
import com.lf.novelbackend.model.dto.novel.NovelQueryRequest;
import com.lf.novelbackend.model.dto.novel.NovelUpdateRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.entity.NovelUser;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.vo.NovelVO;
import com.lf.novelbackend.model.vo.UserVO;
import com.lf.novelbackend.service.ChapterService;
import com.lf.novelbackend.service.NovelService;
import com.lf.novelbackend.service.NovelUserService;
import com.lf.novelbackend.service.UserService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
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
 * @description 针对表【novel(用户表)】的数据库操作Service实现
 * @createDate 2025-06-30 17:48:06
 */
@Service
@Slf4j
public class NovelServiceImpl implements NovelService {

    @Resource
    private UserService userService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ChapterService chapterService;

    @Resource
    private NovelUserService novelUserService;

    @Override
    public NovelVO getNovelVO(Novel novel) {
        if (novel == null) {
            return null;
        }
        Long authorId = novel.getAuthorId();
        User user = userService.getById(authorId);
        UserVO userVO = userService.getUserVO(user);
        NovelVO novelVO = new NovelVO();
        novelVO.setAuthor(userVO);
        BeanUtils.copyProperties(novel, novelVO);
        return novelVO;
    }

    @Override
    public List<NovelVO> getNovelVOList(List<Novel> novelList) {
        if (CollUtil.isEmpty(novelList)) {
            return new ArrayList<>();
        }
        return novelList.stream().map(this::getNovelVO).collect(Collectors.toList());
    }

    @Override
    public String addNovel(NovelAddRequest novelAddRequest, HttpServletRequest request) {
        if (novelAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验字段
        String title = novelAddRequest.getTitle();
        List<String> tags = novelAddRequest.getTags();
        String category = novelAddRequest.getCategory();
        String coverUrl = novelAddRequest.getCoverUrl();
        String description = novelAddRequest.getDescription();

        ThrowUtils.throwIf(StrUtil.isBlank(title), ErrorCode.PARAMS_ERROR, "小说标题不能为空");
        ThrowUtils.throwIf(CollUtil.isEmpty(tags), ErrorCode.PARAMS_ERROR, "小说标签不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(category), ErrorCode.PARAMS_ERROR, "小说分类不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(coverUrl), ErrorCode.PARAMS_ERROR, "小说封面不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(description), ErrorCode.PARAMS_ERROR, "小说简介不能为空");

        // 补充字段
        Novel novel = new Novel();
        BeanUtils.copyProperties(novelAddRequest, novel);
        User loginUser = userService.getLoginUser(request);
        novel.setAuthorId(loginUser.getId());
        // 数据库操作
        Novel result = mongoTemplate.save(novel);
        return result.getId();
    }

    @Override
    public Boolean updateNovelById(NovelUpdateRequest novelUpdateRequest, HttpServletRequest request) {
        if (novelUpdateRequest == null || StrUtil.isBlank(novelUpdateRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = novelUpdateRequest.getId();
        String title = novelUpdateRequest.getTitle();
        List<String> tags = novelUpdateRequest.getTags();
        String category = novelUpdateRequest.getCategory();
        String coverUrl = novelUpdateRequest.getCoverUrl();
        String description = novelUpdateRequest.getDescription();

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));


        // 仅作者本人可操作
        chapterService.validateAuthor(id, request);

        // 拼接update条件
        Update update = new Update();
        if (StrUtil.isNotBlank(title)) {
            update.set("title", title);
        }
        if (CollUtil.isNotEmpty(tags)) {
            update.set("tags", tags);
        }
        if (StrUtil.isNotBlank(category)) {
            update.set("category", category);
        }
        if (StrUtil.isNotBlank(coverUrl)) {
            update.set("coverUrl", coverUrl);
        }
        if (StrUtil.isNotBlank(description)) {
            update.set("description", description);
        }
        update.set("isDelete", 0);
        // 数据库操作
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public Query getNovelQuery(NovelQueryRequest novelQueryRequest) {
        if (novelQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段
        String id = novelQueryRequest.getId();
        String title = novelQueryRequest.getTitle();
        List<String> tags = novelQueryRequest.getTags();
        String category = novelQueryRequest.getCategory();
        String description = novelQueryRequest.getDescription();
        Long authorId = novelQueryRequest.getAuthorId();
        Double rating = novelQueryRequest.getRating();
        Integer ratingCount = novelQueryRequest.getRatingCount();
        Integer viewCount = novelQueryRequest.getViewCount();
        String sortField = novelQueryRequest.getSortField();
        String sortOrder = novelQueryRequest.getSortOrder();

        // 拼接查询条件
        Query query = new Query();
        if (StrUtil.isNotBlank(id)) {
            query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        }
        if (StrUtil.isNotBlank(title)) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        if (StrUtil.isNotBlank(category)) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (StrUtil.isNotBlank(description)) {
            query.addCriteria(Criteria.where("description").regex(description, "i"));
        }
        if (CollUtil.isNotEmpty(tags)) {
            query.addCriteria(Criteria.where("tags").in(tags));
        }
        if (ObjUtil.isNotNull(authorId)) {
            query.addCriteria(Criteria.where("authorId").is(authorId));
        }

        // 逻辑删除拼接
        query.addCriteria(Criteria.where("isDelete").is(0));
        return query;
    }

    // TODO 要加锁和事务
    @Override
    public Boolean mark(NovelMarkRequest novelMarkRequest, HttpServletRequest request) {
        if (novelMarkRequest == null || StrUtil.isBlank(novelMarkRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验参数
        String id = novelMarkRequest.getId();
        Double myRating = novelMarkRequest.getRating();

        ThrowUtils.throwIf(myRating == null, ErrorCode.PARAMS_ERROR, "不能传递空的分数");

        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 每个用户每个小说限一次
        QueryWrapper<NovelUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("novelId", id);

        NovelUser novelUser = novelUserService.getOne(queryWrapper);
        ThrowUtils.throwIf(novelUser == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(novelUser.getIsRated() == 1, ErrorCode.OPERATION_ERROR, "已经打过分数了");

        // 获取分数和打分人数
        Novel novel = mongoTemplate.findById(new ObjectId(id), Novel.class);
        ThrowUtils.throwIf(novel == null, ErrorCode.NOT_FOUND_ERROR);
        Double curRating = novel.getRating();
        Integer ratingCount = novel.getRatingCount();

        // 计算新分数
        double newRating = (curRating * ratingCount + myRating) / (ratingCount + 1);
        newRating = NumberUtil.round(newRating, 1).doubleValue();

        // 更新小说分数和打分人数
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        query.addCriteria(Criteria.where("isDelete").is(0));
        Update update = new Update();
        update.set("rating", newRating);
        update.set("ratingCount", ratingCount + 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        ThrowUtils.throwIf(updateResult.getModifiedCount() < 1, ErrorCode.OPERATION_ERROR);
        // 修改小说-用户表打分标识
        novelUser.setIsRated(1);
        boolean result = novelUserService.updateById(novelUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




