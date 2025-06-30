package com.lf.novelbackend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户类型枚举
 */
public enum UserTypeEnum {

    USER("用户", 0),
    AUTHOR("作者", 1),
    ADMIN("管理员", 2);

    private final String text;

    private final Integer value;

    UserTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserTypeEnum anEnum : UserTypeEnum.values()) {
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
