CREATE TABLE `app_user`
(
    `id`               int                                                           NOT NULL COMMENT 'app 用户id(app_user_id)',
    `nick_name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户名',
    `name`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '真实姓名',
    `phone`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `email`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '邮箱号',
    `app_user_type_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户状态 (字典表app_user_type)',
    `bind_wechat_id`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '绑定app_wechat_id（如果有）',
    `bind_xiangyun_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定app_xiangyun_id （如果有）',
    `create_time`      timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'app用户表' ROW_FORMAT = DYNAMIC;

CREATE TABLE `app_wechat_user`
(
    `id`               varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `wechat_account`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信号',
    `wechat_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信昵称',
    `wechat_logo`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信头像',
    `wechat_phone`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '微信手机号',
    `open_id`          varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '微信open_id',
    `union_id`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信union_id',
    `create_time`      timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      timestamp                                                     NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信用户表' ROW_FORMAT = Dynamic;

CREATE TABLE `goods_info`
(
    `id`          int                                                           NOT NULL AUTO_INCREMENT,
    `user_id`     int                                                           NOT NULL,
    `goods_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `goods_img`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `goods_info`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `goods_price` decimal(10, 2)                                                NOT NULL,
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sys_dict`
(
    `id`          varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '字典id',
    `code_type`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '关联sys_dict_type id',
    `code_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '字典名称',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
    `sort`        int                                                           NOT NULL COMMENT '排序(正序)',
    `is_show`     enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'T' COMMENT '是否展示',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp                                                     NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_dict_type`
(
    `id`             varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '字典类型id',
    `code_type_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '字典类型名(中文)',
    `code_type_key`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '字典类型key(英文)',
    `remark`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
    `craete_time`    timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    timestamp                                                     NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_menu`
(
    `id`           int                                                           NOT NULL AUTO_INCREMENT COMMENT '菜单表id',
    `pid`          int                                                           NOT NULL COMMENT '父级id',
    `path`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路径',
    `name`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `title`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `icon`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT 'icon图标',
    `component`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组件路径',
    `redirect`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'redirect地址',
    `sort`         int                                                           NOT NULL COMMENT '排序',
    `deep`         int                                                           NOT NULL COMMENT '层级',
    `menu_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '菜单类型 （字典表menu_type）',
    `permissions`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '按钮权限',
    `is_hidden`    enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否隐藏',
    `has_children` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否有子级',
    `create_time`  timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_id`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `update_id`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_organization`
(
    `id`          int                                                           NOT NULL COMMENT '组织id',
    `org_pid`     int                                                           NOT NULL COMMENT '上级组织id,没有为0',
    `org_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织名称',
    `org_type_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织类型 （字典表org_type）',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `del_flag`   enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统组织表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_organization_user`
(
    `id`              int NOT NULL,
    `organization_id` int NOT NULL COMMENT '组织id (sys_organization_id)',
    `user_id`         int NOT NULL COMMENT '系统用户id （sys_user_id）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统组织-用户表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_region`
(
    `id`          int                                                           NOT NULL COMMENT ' 行政区id',
    `region_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行政区名称',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `del_flag`   enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统行政区（字典）表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_region_org`
(
    `id`        int NOT NULL,
    `region_id` int NOT NULL COMMENT '行政区id',
    `org_id`    int NOT NULL COMMENT '行政区直属组织id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统行政区-（直属）组织表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_region_tree`
(
    `id`          int       NOT NULL,
    `region_id`   int       NOT NULL COMMENT '行政区id',
    `region_pid`  int       NOT NULL COMMENT '父级行政区id，根节点(吉林省为0)',
    `deep`        int       NOT NULL COMMENT '层级（0 省级，1 市级，2 区县级）',
    `sort`        int       NOT NULL COMMENT '排序(层级下)',
    `id_delete`   enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统行政区规划表' ROW_FORMAT = Dynamic;


CREATE TABLE `sys_role`
(
    `id`          int                                                           NOT NULL AUTO_INCREMENT COMMENT '角色id',
    `role_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名称',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '简介',
    `del_flag`   enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_role_menu`
(
    `id`      int NOT NULL,
    `menu_id` int NOT NULL COMMENT 'sys_menu_id （菜单表）',
    `role_id` int NOT NULL COMMENT 'sys_role_id （角色表）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色-菜单表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user`
(
    `id`                int                                                           NOT NULL AUTO_INCREMENT COMMENT 'id',
    `account_name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户名(登录)',
    `username`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '姓名',
    `nick_name`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '昵称',
    `phone`             varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '手机号',
    `id_card`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证',
    `email`             varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
    `pwd`               varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `account_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户状态 (关联字典表account_status)',
    `user_tag`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '标签',
    `last_login_time`   timestamp NULL DEFAULT NULL COMMENT '最近一次登录时间',
    `create_time`       timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user_role`
(
    `id`      int NOT NULL AUTO_INCREMENT,
    `role_id` int NOT NULL COMMENT '角色id (sys_role_id)',
    `user_id` int NOT NULL COMMENT '用户id(sys_user_id)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户-角色关联表' ROW_FORMAT = Dynamic;

CREATE TABLE `user_info`
(
    `id`          int                                                         NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    `age`         int                                                         NOT NULL,
    `birthday`    varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    `create_time` timestamp                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;