package com.lf.novelbackend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "novels")
public class Novel {
    private String id;                  // 自定义业务 ID
    private String title;               // 小说名
    private List<String> tags;          // 小说标签（数组）
    private String category;      // 小说分类（单/多分类数组）
    private String coverUrl;            // 封面 URL
    private String description;         // 小说简介
    private Long authorId;            // 作者 ID
    private Double rating;              // 小说评分（浮点数）
    private Integer ratingCount;            // 评分人数（整数）
    private Integer viewCount;              // 浏览人数（整数）
    private Integer totalChapters;          // 总章节数（整数）
    private List<Comment> comments;     // 书评（嵌套文档数组）
    private List<Chapter> chapters;     // 章节（嵌套文档数组）
    private Date createTime;            // 创建时间
    private Date updateTime;            // 最后编辑时间
    private Integer isDelete;               // 逻辑删除标记（0=未删除，1=已删除）
}
