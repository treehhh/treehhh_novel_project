package com.lf.novelbackend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 审核状态枚举
 */
public enum ReviewStatusEnum {

    UNDER_REVIEW("待审核", 0),
    PASS_REVIEW("审核通过", 1),
    NO_PASS_REVIEW("审核不通过", 2);

    private final String text;

    private final Integer value;

    ReviewStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ReviewStatusEnum anEnum : ReviewStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}
