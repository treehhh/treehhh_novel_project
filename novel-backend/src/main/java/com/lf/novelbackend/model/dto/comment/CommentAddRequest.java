package com.lf.novelbackend.model.dto.comment;

import com.lf.novelbackend.model.dto.chapter.ChapterIdRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentAddRequest extends ChapterIdRequest {
    private String content;       // 评论内容
}
