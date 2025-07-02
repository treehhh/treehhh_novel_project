package com.lf.novelbackend.model.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Comment {
    private Long userId;        // 评论人 ID
    private String userName;      // 评论人用户名
    private String avatar;        // 评论人头像
    private String content;       // 评论内容
    private Date commentTime;     // 评论时间
    private Integer isDelete;         // 逻辑删除标记（0=未删除，1=已删除）
}
