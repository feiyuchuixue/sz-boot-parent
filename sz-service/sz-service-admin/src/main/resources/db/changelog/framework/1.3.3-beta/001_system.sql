--liquibase formatted sql

--changeset 升职哦（sz）:20260127_2015
--comment: 添加设置账户类型菜单
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`, `use_data_scope`) VALUES ('b3d357cf69034286988f05a346f2412c', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置账户类型', '', '', '', 900, 3, '1002003', 'sys.user.admin_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

--changeset 升职哦（sz）:20260127_2020
--comment: 为超级管理员分配设置账户类型权限
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('b3d357cf69034286988f05a346f2412c', 1, 'menu', NULL);

--changeset 升职哦（sz）:20260127_2025
--comment: 清除菜单-设置数据角色按钮
DELETE FROM `sys_role_menu` WHERE menu_id = '30942929802f41cc850722c78db089e7';
DELETE FROM `sys_menu` WHERE id = '30942929802f41cc850722c78db089e7' ;

--changeset 升职哦（sz）:20260418_1010
--comment: excel导入相关记录表
CREATE TABLE `sys_import_batch` (
                                    `id` bigint NOT NULL COMMENT '主键（雪花ID）',
                                    `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '导入批次ID（UUID）',
                                    `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型',
                                    `biz_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务名称',
                                    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '导入文件名',
                                    `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
                                    `total_count` int NOT NULL DEFAULT '0' COMMENT '总条数',
                                    `success_count` int NOT NULL DEFAULT '0' COMMENT '成功条数',
                                    `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败条数',
                                    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次状态',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_batch_id` (`batch_id`),
                                    KEY `idx_biz_type_create_time` (`biz_type`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='导入（Excel）批次表';

CREATE TABLE `sys_import_fail_record` (
                                          `id` bigint NOT NULL COMMENT '主键（雪花ID）',
                                          `batch_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '导入批次ID',
                                          `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型',
                                          `row_no` int NOT NULL COMMENT '失败行号',
                                          `biz_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务主识别值',
                                          `biz_key_label` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务主识别值标签',
                                          `error_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '错误码',
                                          `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '错误信息',
                                          `handle_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '处理状态',
                                          `row_data` json DEFAULT NULL COMMENT '当前失败行原始快照(JSON)',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_batch_id` (`batch_id`),
                                          KEY `idx_biz_type_create_time` (`biz_type`,`create_time`),
                                          KEY `idx_biz_type_handle_status` (`biz_type`,`handle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='导入（Excel）失败记录表';