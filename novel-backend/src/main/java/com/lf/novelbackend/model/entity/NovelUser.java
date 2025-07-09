package com.lf.novelbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 小说-用户关系表
 *
 * @TableName novel_user
 */
@TableName(value = "novel_user")
@Data
public class NovelUser implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 小说id
     */
    private String novelId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0=未打分，1=已打分
     */
    private Integer isRated;

    /**
     * 上一次阅读章节数
     */
    private Integer currentChapter;

    /**
     * 创建时间
     */
    private Date createTime;

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