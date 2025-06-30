package com.lf.novelbackend.model.dto.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像（存储图片URL或路径）
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 用户类型（0-普通用户、1-作者、2-管理员）
     */
    private Integer userType;

    /**
     * 是否是VIP（0-否、1-是）
     */
    private Integer isVip;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}