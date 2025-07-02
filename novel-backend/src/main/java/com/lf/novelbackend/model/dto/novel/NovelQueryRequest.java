package com.lf.novelbackend.model.dto.novel;

import com.lf.novelbackend.common.PageRequest;
import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NovelQueryRequest extends PageRequest {
    private String id;                  // 自定义业务 ID
    private String title;               // 小说名
    private List<String> tags;          // 小说标签（数组）
    private String category;      // 小说分类（单/多分类数组）
    private String description;         // 小说简介
    private Long authorId;            // 作者 ID
    private Double rating;              // 小说评分（浮点数）
    private Integer ratingCount;            // 评分人数（整数）
    private Integer viewCount;              // 浏览人数（整数）
}
