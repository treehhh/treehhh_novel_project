package com.lf.novelbackend.model.dto.chapter;

import lombok.Data;

@Data
public class ChapterAddRequest {
    private String id;                   // 小说Id
    private String title;               // 章节名
    private Integer chapterNumber;       // 章节序号
    private String content;             // 章节内容
}
