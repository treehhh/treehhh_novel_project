package com.lf.novelbackend.model.dto.novel;

import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Comment;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
public class NovelUpdateRequest {
    private String id;                  // 自定义业务 ID
    private String title;               // 小说名
    private List<String> tags;          // 小说标签（数组）
    private String category;      // 小说分类（单/多分类数组）
    private String coverUrl;            // 封面 URL
    private String description;         // 小说简介
}
