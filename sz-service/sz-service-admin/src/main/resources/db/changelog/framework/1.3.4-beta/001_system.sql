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