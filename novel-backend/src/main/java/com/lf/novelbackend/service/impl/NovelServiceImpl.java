package com.lf.novelbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.lf.novelbackend.exception.BusinessException;
import com.lf.novelbackend.exception.ErrorCode;
import com.lf.novelbackend.exception.ThrowUtils;
import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.model.vo.NovelVO;
import com.lf.novelbackend.model.vo.UserVO;
import com.lf.novelbackend.service.NovelService;
import com.lf.novelbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public String addNovel(NovelAddRequest novelAddRequest) {
        if (novelAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验字段
        String title = novelAddRequest.getTitle();
        List<String> tags = novelAddRequest.getTags();
        String category = novelAddRequest.getCategory();
        String coverUrl = novelAddRequest.getCoverUrl();
        String description = novelAddRequest.getDescription();

        ThrowUtils.throwIf(StrUtil.isBlank(title),ErrorCode.PARAMS_ERROR,"小说标题不能为空");
        ThrowUtils.throwIf(CollUtil.isEmpty(tags),ErrorCode.PARAMS_ERROR,"小说标签不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(category),ErrorCode.PARAMS_ERROR,"小说分类不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(coverUrl),ErrorCode.PARAMS_ERROR,"小说封面不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(description),ErrorCode.PARAMS_ERROR,"小说简介不能为空");

        // 数据库操作
        Novel novel = new Novel();
        BeanUtils.copyProperties(novelAddRequest,novel);
        Novel result = mongoTemplate.save(novel);
        return result.getId();
    }


}




