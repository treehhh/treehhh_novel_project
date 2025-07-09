package com.lf.novelbackend.model.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Comment {
    private String id;          // 评论标识ID
    private Long userId;        // 评论人 ID
    private String content;       // 评论内容
    private Date commentTime = new Date();     // 评论时间
    private Integer isDelete = 0;         // 逻辑删除标记（0=未删除，1=已删除）
}
