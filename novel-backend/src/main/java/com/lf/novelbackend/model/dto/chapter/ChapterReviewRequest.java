package com.lf.novelbackend.model.dto.chapter;

import com.lf.novelbackend.common.ReviewRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChapterReviewRequest extends ReviewRequest {
    private String id;
    private Integer chapterNumber;
}
