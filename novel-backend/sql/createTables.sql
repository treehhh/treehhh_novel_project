CREATE DATABASE IF NOT EXISTS my_novel;

USE my_novel;

CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                      userName VARCHAR(256) NOT NULL COMMENT '用户名',
                      avatar VARCHAR(1024) COMMENT '用户头像（存储图片URL或路径）',
                      phone VARCHAR(20) NOT NULL COMMENT '手机号',
                      password VARCHAR(256) NOT NULL COMMENT '密码（加密存储）',
                      userType TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型（0-普通用户、1-作者、2-管理员）',
                      isVip TINYINT NOT NULL DEFAULT 0 COMMENT '是否是VIP（0-否、1-是）',
                      attentionCount INT NOT NULL DEFAULT 0 COMMENT '关注数',
                      fansCount INT NOT NULL DEFAULT 0 COMMENT '粉丝数',
                      createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      editTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
                      updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
                      isDelete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-否、1-是）',
                      INDEX idx_userName(userName),
                      INDEX idx_userType(userType),
                      INDEX idx_isVip(isVip)
) COMMENT='用户表' collate = utf8mb4_unicode_ci;


CREATE TABLE novel_user
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    novelId        VARCHAR(255) NOT NULL COMMENT '小说id',
    userId         BIGINT       NOT NULL COMMENT '用户id',
    isRated        TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未打分，1=已打分',
    currentChapter INT COMMENT '上一次阅读章节数',
    createTime     DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime     DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
    isDelete       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-否、1-是）',
    INDEX idx_userId (userId)
) COMMENT ='小说-用户关系表' collate = utf8mb4_unicode_ci;

