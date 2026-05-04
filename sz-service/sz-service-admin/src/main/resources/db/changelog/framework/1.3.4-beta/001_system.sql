--liquibase formatted sql

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

--changeset 升职哦（sz）:20260418_1810
--comment: 资源实体主表
CREATE TABLE `sys_resource` (
                                `id` bigint NOT NULL COMMENT '主键（雪花ID）',
                                `scene_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景编码，如 sso.provider.logo',
                                `object_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储键（相对路径/objectName），如 providers/github.svg',
                                `e_tag` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ETag 哈希值',
                                `origin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原始文件名',
                                `size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
                                `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'MIME 类型',
                                `storage_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储类型：LOCAL / OSS',
                                `biz_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务标识，如 providerKey',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '逻辑删除：T=已删除 F=正常',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `create_id` bigint DEFAULT NULL COMMENT '创建人',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                `update_id` bigint DEFAULT NULL COMMENT '更新人',
                                PRIMARY KEY (`id`),
                                KEY `idx_scene_code` (`scene_code`),
                                KEY `idx_biz_key` (`scene_code`,`biz_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资源实体主表';

--changeset 升职哦（sz）:20260501_1316
--comment: 清理菜单无效记录
DELETE FROM `sys_menu` WHERE `id` in ('0444cd2c01584f0687264b6205536691', '2ae05bd0afaa4c6dbc173bf1dd0da2cf', '8d92cf6f2f3248569d5dd6cb6b958d7c', 'f42b249ccfd44fdcbc2dba48a308c1f6', '3f555e4a01174a1d9b29be439668e32f', '2868079355ce4b6c985b1b746dbb0952', '49c75878b4d445f8be5f69e21e18b70d', 'c55de3135b864579bda79c279f4129a9', '52fb3db605334671bb0dfe4f50cb1147');

--changeset 升职哦（sz）:20260501_1317
--comment: 清理清理模板文件jstj测试记录
DELETE h FROM sys_temp_file_history h JOIN sys_temp_file t ON t.id = h.sys_temp_file_id WHERE t.alias = 'jstj';
DELETE FROM `sys_temp_file` WHERE `alias` = 'jstj';

--changeset 升职哦（sz）:20260501_1334
--comment: 新增演示记录
INSERT IGNORE INTO `sys_temp_file` (`id`, `sys_file_id`, `temp_name`, `remark`, `del_flag`, `create_id`, `create_time`, `update_id`, `update_time`, `url`, `alias`) VALUES (2, 407693840624005120, '教师统计导入模板新.xlsx', '', 'F', 1, '2026-05-01 13:06:33', 1, '2026-05-01 13:06:33', '[{\"accessUrl\": null, \"objectKey\": \"template/20260501/教师统计导入模板新.xlsx\", \"sceneCode\": \"template.excel\", \"originName\": \"教师统计导入模板新.xlsx\", \"resourceId\": 407687721621008384, \"contentType\": \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"}]', 'jstj');
INSERT IGNORE INTO `sys_temp_file_history` (`id`, `sys_temp_file_id`, `sys_file_id`, `temp_name`, `url`, `remark`, `create_id`, `create_time`, `update_id`, `update_time`) VALUES (3, 2, 407693840624005120, '教师统计导入模板新.xlsx', '[{\"accessUrl\": null, \"objectKey\": \"template/20260501/教师统计导入模板新.xlsx\", \"sceneCode\": \"template.excel\", \"originName\": \"教师统计导入模板新.xlsx\", \"resourceId\": 407687721621008384, \"contentType\": \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"}]', '', 1, '2026-05-01 13:06:33', 1, '2026-05-01 13:06:33');
INSERT IGNORE INTO `sys_resource` (`id`, `scene_code`, `object_key`, `e_tag`, `origin_name`, `size`, `content_type`, `storage_type`, `biz_key`, `del_flag`, `create_time`, `create_id`, `update_time`, `update_id`) VALUES (407693840624005120, 'template.excel', 'template/20260501/教师统计导入模板新.xlsx', NULL, '教师统计导入模板新.xlsx', 11656, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'LOCAL', NULL, 'F', '2026-05-01 13:30:46', 1, '2026-05-01 13:30:46', 1);

--changeset 升职哦（sz）:20260504_0920
--comment: 更新用户头像
UPDATE `sys_user` SET `logo` = 'logo/user/20260501/微信图片_20240420160033.jpg' WHERE `username` = 'admin';
UPDATE `sys_user` SET `logo` = 'logo/user/20260501/bg6.png' WHERE `username` = 'user';
UPDATE `sys_user` SET `logo` = 'logo/user/20260501/bg8.png' WHERE `username` = 'test1';