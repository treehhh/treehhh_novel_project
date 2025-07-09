package com.lf.novelbackend.service;


import com.lf.novelbackend.common.DeleteByStringIdRequest;
import com.lf.novelbackend.model.dto.comment.CommentAddRequest;
import com.lf.novelbackend.model.dto.comment.CommentDeleteRequest;
import com.lf.novelbackend.model.entity.Comment;
import com.lf.novelbackend.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 86138
 * @createDate 2025-06-30 17:48:06
 */
public interface CommentService {

    CommentVO getCommentVO(Comment comment);

    List<CommentVO> getCommentVOList(List<Comment> commentList);

    Boolean addComment(CommentAddRequest commentAddRequest, HttpServletRequest request);

    Boolean deleteComment(CommentDeleteRequest commentDeleteRequest, HttpServletRequest request);

    List<Comment> getCommentFromNovel(String id);

    List<Comment> getCommentFromChapter(String id, Integer chapterNumber);
}
