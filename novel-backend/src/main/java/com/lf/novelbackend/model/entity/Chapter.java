package com.lf.novelbackend.model.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Chapter {
    private String title;               // 章节名
    private Integer chapterNumber;       // 章节序号
    private String content;             // 章节内容
    private List<Comment> comments;     // 章节评论
    private Integer isRelease = 0;     // 是否发布（0=未发布，1=已发布）
    private Long reviewId;             // 审核人 ID
    private Integer reviewStatus = 0;      // 审核状态（枚举）
    private String reviewComment;       // 审核意见
    private Date reviewTime;            // 审核时间
    private Date createTime = new Date();            // 章节创建时间
    private Date updateTime = new Date();            // 章节最后编辑时间
    private Integer isDelete = 0;               // 逻辑删除标记（0=未删除，1=已删除）
}
