package com.lf.novelbackend.service;

import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.dto.novel.NovelQueryRequest;
import com.lf.novelbackend.model.dto.novel.NovelUpdateRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.vo.NovelVO;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NovelService {
    NovelVO getNovelVO(Novel novel);

    List<NovelVO> getNovelVOList(List<Novel> novelList);

    String addNovel(NovelAddRequest novelAddRequest, HttpServletRequest request);

    Boolean updateNovelById(NovelUpdateRequest novelUpdateRequest, HttpServletRequest request);

    Query getNovelQuery(NovelQueryRequest novelQueryRequest);

}
