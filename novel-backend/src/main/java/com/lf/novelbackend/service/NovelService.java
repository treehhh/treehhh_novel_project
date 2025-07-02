package com.lf.novelbackend.service;

import com.lf.novelbackend.model.dto.novel.NovelAddRequest;
import com.lf.novelbackend.model.entity.Novel;
import com.lf.novelbackend.model.vo.NovelVO;

import java.util.List;

public interface NovelService {
    NovelVO getNovelVO(Novel novel);

    List<NovelVO> getNovelVOList(List<Novel> novelList);

    String addNovel(NovelAddRequest novelAddRequest);
}
