package com.lf.novelbackend.service;


import com.lf.novelbackend.model.dto.chapter.*;
import com.lf.novelbackend.model.dto.comment.CommentDeleteRequest;
import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.vo.ChapterVOToAdmin;
import com.lf.novelbackend.model.vo.ChapterVOToUser;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ChapterService {


    ChapterVOToUser getChapterVOToUser(String id, Chapter chapter);

    ChapterVOToAdmin getChapterVOToAdmin(String id, Chapter chapter);

    List<ChapterVOToUser> getChapterVOToUserList(String id, List<Chapter> chapterList);

    List<ChapterVOToAdmin> getChapterVOToAdminList(String id, List<Chapter> chapterList);

    void addChapter(ChapterAddRequest chapterAddRequest, HttpServletRequest request);

    Boolean updateChapter(ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request);

    Boolean saveChapter(ChapterUpdateRequest chapterUpdateRequest, HttpServletRequest request);

    Boolean releaseChapter(ChapterIdRequest chapterIdRequest, HttpServletRequest request);

    Boolean deleteChapter(ChapterIdRequest chapterIdRequest, HttpServletRequest request);

//    List<Chapter> filterChapterList(ChapterQueryRequest chapterQueryRequest);

    Page<Chapter> getChapterPage(ChapterQueryRequest chapterQueryRequest);

    Query getChapterQueryById(String id, Integer chapterNumber);
}
