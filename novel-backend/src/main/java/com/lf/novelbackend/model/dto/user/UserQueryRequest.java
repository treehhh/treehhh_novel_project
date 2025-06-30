package com.lf.novelbackend.model.dto.user;

import com.baomidou.mybatisplus.annotation.*;
import com.lf.novelbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}