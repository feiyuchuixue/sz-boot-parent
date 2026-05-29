--liquibase formatted sql

--changeset 升职哦（sz）:20250717_1321
--comment: 新增部门角色关联数据库表
CREATE TABLE IF NOT EXISTS `sys_dept_role`  (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门-角色关联ID',
                                  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
                                  `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID(sys_role_id)',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统部门-角色关联表' ROW_FORMAT = DYNAMIC;

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
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('191878090b294ec5892a68081dd91428', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/sysLoginLog', 'SysLoginLogView', '登陆日志', 'Location', '/system/sysLoginLog/index', '', 1100, 2, '1002002', 'sys.login.log.query_table', 'F', 'T', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F');
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`, `use_data_scope`) VALUES ('52fb3db605334671bb0dfe4f50cb1147', '191878090b294ec5892a68081dd91428', '', '', '导出', '', '', '', 100, 3, '1002003', 'sys.login.log.export', 'F', 'F', 'F', 'F', 'F', 'F', NULL, NULL, NULL, NULL, 'F', NULL, NULL, 'F');

--changeset 升职哦（sz）:20250729_0304
--comment: 新增登陆状态字典
INSERT IGNORE INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`, `type`) VALUES (1009, '登陆状态', 'login_status', 'F', 'T', 'F', '', '2025-07-29 22:49:03', '2025-07-29 22:49:03', NULL, 1, 1, NULL, 'system');
INSERT IGNORE INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1009002, 1009, '登陆失败', '', 1, 'danger', '', 'F', 'T', 'F', '2025-07-29 22:49:34', '2025-07-29 22:49:34', NULL, 1, 1, NULL);
INSERT IGNORE INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1009001, 1009, '登陆成功', '', 1, 'success', '', 'F', 'T', 'F', '2025-07-29 22:49:22', '2025-07-29 22:49:22', NULL, 1, 1, NULL);

INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('191878090b294ec5892a68081dd91428', 1);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('52fb3db605334671bb0dfe4f50cb1147', 1);

