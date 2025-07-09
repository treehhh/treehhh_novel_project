package com.lf.novelbackend.model.dto.comment;

import com.lf.novelbackend.model.dto.chapter.ChapterIdRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentDeleteRequest extends ChapterIdRequest {
    private String commentId;  // 评论标识id
}
