--liquibase formatted sql

--changeset 升职哦（sz）:20250725_1327
--comment: 新增登陆日志数据库表
CREATE TABLE IF NOT EXISTS `sys_login_log`  (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '登陆ID',
                                  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
                                  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                                  `login_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登陆状态',
                                  `login_time` datetime NULL DEFAULT NULL COMMENT '登陆时间',
                                  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登陆ip地址',
                                  `login_location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登陆地点',
                                  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器类型',
                                  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                                  `msg` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提示消息',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登陆日志表' ROW_FORMAT = DYNAMIC;

--changeset 升职哦（sz）:20250726_0927
--comment: 新增登陆日志菜单
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('191878090b294ec5892a68081dd91428', '0', '/system/sysLoginLog', 'SysLoginLogView', '登陆日志', '', '/system/sysLoginLog/index', '', 500, 1, '1002002', 'sys.login.log.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('52fb3db605334671bb0dfe4f50cb1147', '191878090b294ec5892a68081dd91428', '', '', '导出', '', '', '', 900, 2, '1002003', 'sys.login.log.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('7debbb064dba400dae21bed764a187df', '191878090b294ec5892a68081dd91428', '', '', '删除', '', '', '', 300, 2, '1002003', 'sys.login.log.remove', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('9999a6e7254e4e91a1c16ec68d06cf06', '191878090b294ec5892a68081dd91428', '', '', '导入', '', '', '', 700, 2, '1002003', 'sys.login.log.import', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'T', NULL, NULL, 'F');
