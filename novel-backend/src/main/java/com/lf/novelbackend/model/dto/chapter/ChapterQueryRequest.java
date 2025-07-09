package com.lf.novelbackend.model.dto.chapter;

import com.lf.novelbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChapterQueryRequest extends PageRequest {
    private String id;                   // 小说Id
    private Integer isRelease;     // 是否发布（0=未发布，1=已发布）
    private Integer reviewStatus;      // 审核状态（枚举）
    private Long reviewId;             // 审核人 ID

}
