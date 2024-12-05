--
CREATE TABLE `sys_temp_file` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `sys_file_id` bigint DEFAULT NULL COMMENT '文件ID',
                                 `temp_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模版名',
                                 `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
                                 `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
                                 `del_flag` enum('T','F') COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                 `create_id` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_id` bigint DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='模版文件表';

CREATE TABLE `sys_temp_file_history` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `sys_temp_file_id` bigint DEFAULT NULL COMMENT '模版文件ID',
                                 `sys_file_id` bigint DEFAULT NULL COMMENT '文件ID',
                                 `temp_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模版名',
                                 `url` varchar(512) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
                                 `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
                                 `create_id` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_id` bigint DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='模版文件历史表';
