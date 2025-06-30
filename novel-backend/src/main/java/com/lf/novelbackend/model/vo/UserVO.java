package com.lf.novelbackend.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户封装响应
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

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
     * 用户类型（0-普通用户、1-作者、2-管理员）
     */
    private Integer userType;

    /**
     * 是否是VIP（0-否、1-是）
     */
    private Integer isVip;

    /**
     * 关注数
     */
    private Integer attentionCount;

    /**
     * 粉丝数
     */
    private Integer fansCount;

    /**
     * 创建时间
     */
    private Date createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}