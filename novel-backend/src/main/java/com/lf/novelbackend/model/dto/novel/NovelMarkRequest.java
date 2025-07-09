package com.lf.novelbackend.model.dto.novel;

import lombok.Data;

@Data
public class NovelMarkRequest {
    private String id;  // 小说id
    private Double rating; // 分数
}
