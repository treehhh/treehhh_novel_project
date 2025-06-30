package com.lf.novelbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 编辑时间
     */
    private Date updateTime;

    /**
     * 逻辑删除（0-否、1-是）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}