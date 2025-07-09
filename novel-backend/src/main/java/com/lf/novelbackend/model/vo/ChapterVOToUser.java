package com.lf.novelbackend.model.vo;

import com.lf.novelbackend.model.entity.Comment;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChapterVOToUser {
    private String id;                  // 小说Id
    private String title;               // 章节名
    private Integer chapterNumber;       // 章节序号
    private String content;             // 章节内容
    private List<Comment> comments;     // 章节评论
    private Date createTime;            // 章节创建时间
}
