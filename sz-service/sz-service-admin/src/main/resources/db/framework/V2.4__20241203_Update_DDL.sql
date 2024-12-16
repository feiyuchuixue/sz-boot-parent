-- 新增模板文件表
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

-- 新增模板文件管理菜单
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`)VALUES ('8231a369712e4f8f8ac09fce232cd034', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/sysTempFile', 'SysTempFileView', '模版文件管理', 'DocumentCopy', '/system/sysTempFile/index', '', 1000, 2, '1002002', 'sys.temp.file.query_table', 'F', 'T', 'F', 'F', 'F', 'F', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`)VALUES ('e931c84b8bc945a7b6ba2d58c8a93afc', '8231a369712e4f8f8ac09fce232cd034', '', '', '新增', '', '', '', 100, 3, '1002003', 'sys.temp.file.create', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`)VALUES ('87a26b76daad47c2a12c470605563c4a', '8231a369712e4f8f8ac09fce232cd034', '', '', '修改', '', '', '', 200, 3, '1002003', 'sys.temp.file.update', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`)VALUES ('f1ef824156c0402c90189d58afb1613e', '8231a369712e4f8f8ac09fce232cd034', '', '', '删除', '', '', '', 300, 3, '1002003', 'sys.temp.file.remove', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

-- 新增管理员菜单权限
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('8231a369712e4f8f8ac09fce232cd034', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('e931c84b8bc945a7b6ba2d58c8a93afc', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('87a26b76daad47c2a12c470605563c4a', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('f1ef824156c0402c90189d58afb1613e', 1);

-- 新增模板管理初始数据
INSERT INTO `sys_temp_file` (`sys_file_id`, `temp_name`, `url`, `remark`, `del_flag`, `create_id`, `create_time`, `update_id`, `update_time`) VALUES (98, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '', 'F', 1, '2024-12-16 20:33:12', 1, '2024-12-16 20:33:36');
INSERT INTO `sys_temp_file_history` (`sys_temp_file_id`, `sys_file_id`, `temp_name`, `url`, `remark`, `create_id`, `create_time`, `update_id`, `update_time`) VALUES (1, 97, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (43) (203252.669).xlsx', '', 1, '2024-12-16 20:33:12', NULL, NULL);
INSERT INTO `sys_temp_file_history` (`sys_temp_file_id`, `sys_file_id`, `temp_name`, `url`, `remark`, `create_id`, `create_time`, `update_id`, `update_time`) VALUES (1, 98, '教师统计模板.xlsx', 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '', 1, '2024-12-16 20:33:36', NULL, NULL);
INSERT INTO  `sys_file` (`id`, `filename`, `dir_tag`, `size`, `url`, `create_time`, `object_name`, `context_type`, `e_tag`, `create_id`) VALUES (98, '教师统计 (203323.951).xlsx', 'tmp', 9866, 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (203323.951).xlsx', '2024-12-16 20:33:24', 'tmp/20241216/教师统计 (203323.951).xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '8bba8015aa748013cc8295a13637fb3a', 1);
INSERT INTO  `sys_file` (`id`, `filename`, `dir_tag`, `size`, `url`, `create_time`, `object_name`, `context_type`, `e_tag`, `create_id`) VALUES (97, '教师统计 (43) (203252.669).xlsx', 'tmp', 9866, 'https://minioapi.szadmin.cn/test/tmp/20241216/教师统计 (43) (203252.669).xlsx', '2024-12-16 20:32:53', 'tmp/20241216/教师统计 (43) (203252.669).xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '8bba8015aa748013cc8295a13637fb3a', 1);
INSERT INTO  `sys_file` (`id`, `filename`, `dir_tag`, `size`, `url`, `create_time`, `object_name`, `context_type`, `e_tag`, `create_id`) VALUES (99, '微信图片_20240420160033.jpg', 'user', 20276, 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg', '2024-12-16 20:39:57', 'user/20241216/微信图片_20240420160033.jpg', 'image/jpeg', '322e08e6b47cd85dec6a7b8dc9e88476', 1);
UPDATE `sys_user` SET `logo` = 'https://minioapi.szadmin.cn/test/user/20241216/微信图片_20240420160033.jpg' WHERE `id` = 1;
