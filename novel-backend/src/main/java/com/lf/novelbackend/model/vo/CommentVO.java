package com.lf.novelbackend.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVO {
    private String id;          // 评论标识ID
    private Long userId;        // 评论人 ID
    private UserVO user;       // 评论人信息
    private String content;       // 评论内容
    private Date commentTime;     // 评论时间
}
