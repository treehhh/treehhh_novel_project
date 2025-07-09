package com.lf.novelbackend.model.dto.chapter;

import lombok.Data;

@Data
public class ChapterIdRequest {
    private String id;                   // 小说Id
    private Integer chapterNumber;       // 章节序号
}
