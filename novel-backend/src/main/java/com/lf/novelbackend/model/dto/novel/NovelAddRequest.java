package com.lf.novelbackend.model.dto.novel;

import lombok.Data;

import java.util.List;

@Data
public class NovelAddRequest {
    private String title;               // 小说名
    private List<String> tags;          // 小说标签（数组）
    private String category;      // 小说分类（单/多分类数组）
    private String coverUrl;            // 封面 URL
    private String description;         // 小说简介
}
