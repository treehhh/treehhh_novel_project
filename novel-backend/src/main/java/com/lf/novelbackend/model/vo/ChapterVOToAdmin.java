package com.lf.novelbackend.model.vo;

import com.lf.novelbackend.model.entity.Comment;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChapterVOToAdmin {
    private String id;                  // 小说Id
    private String title;               // 章节名
    private Integer chapterNumber;       // 章节序号
    private String content;             // 章节内容
    private List<Comment> comments;     // 章节评论
    private Integer isRelease;     // 是否发布（0=未发布，1=已发布）
    private Long reviewId;             // 审核人 ID
    private Integer reviewStatus;      // 审核状态（枚举）
    private String reviewComment;       // 审核意见
    private Date reviewTime;            // 审核时间
    private Date createTime;            // 章节创建时间
    private Date updateTime;            // 章节最后编辑时间
}
