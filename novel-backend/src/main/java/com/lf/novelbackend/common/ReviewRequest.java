package com.lf.novelbackend.common;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer reviewStatus;      // 审核状态（枚举）
    private String reviewComment;       // 审核意见
}
