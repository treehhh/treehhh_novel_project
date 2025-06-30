package com.lf.novelbackend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户类型枚举
 */
public enum IsVipEnum {

    ISNOTVIP("不是Vip", 0),
    ISVIP("是Vip", 1);

    private final String text;

    private final Integer value;

    IsVipEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static IsVipEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (IsVipEnum anEnum : IsVipEnum.values()) {
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
