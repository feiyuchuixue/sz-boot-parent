--liquibase formatted sql

--changeset 升职哦（sz）:20250617_1530
CREATE TABLE `sso_user` (
                            `id` bigint NOT NULL COMMENT '平台用户id，sso client 的 center_id映射',
                            `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名，唯一',
                            `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                            `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                            `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                            `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码hash',
                            `platform_user_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '平台用户状态，platform_user_status',
                            `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像地址',
                            `last_login_time` datetime DEFAULT NULL COMMENT '上一次登录时间',
                            `last_login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上一次登录ip',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` enum('T','F') COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '逻辑删除',
                            `del_time` datetime DEFAULT NULL COMMENT '删除时间，(正常状态为null，删除时有时间)',
                            `del_id` bigint DEFAULT NULL COMMENT '操作人id',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='平台用户表';
