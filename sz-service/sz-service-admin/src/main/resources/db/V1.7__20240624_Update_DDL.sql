-- 【结构性变更】
ALTER TABLE `sys_dict_type` ADD COLUMN `type` enum('system','business') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'business' COMMENT '字典类型: system 系统, business 业务';

UPDATE `sys_dict_type` SET `is_lock` = 'T',type = 'system' WHERE `id` <= 1005;

-- 修改teacher_statics表 增加字段
ALTER TABLE `teacher_statistics` ADD COLUMN `create_id` int DEFAULT NULL COMMENT '创建人id';
ALTER TABLE `teacher_statistics` ADD COLUMN `update_id` int DEFAULT NULL COMMENT '更新人id';
ALTER TABLE `teacher_statistics` ADD COLUMN `dept_scope` json DEFAULT NULL COMMENT '部门范围';

-- 修改sys_user表，create_time、update_time字段
ALTER TABLE `sys_user`
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间';

-- 新增字典
INSERT INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`, `type`) VALUES (1006, '数据权限', 'data_scope', 'T', 'T', 'F', '', '2024-06-25 18:54:21', '2024-06-25 19:12:46', NULL, NULL, NULL, NULL, 'system');
INSERT INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`, `type`) VALUES (1007, '数据权限关联类型', 'data_scope_relation_type', 'T', 'T', 'F', '自定义数据权限的关联类型', '2024-06-25 18:55:37', '2024-06-25 19:12:48', NULL, NULL, NULL, NULL, 'system');

INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006001, 1006, '全部', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:55:48', '2024-06-25 19:11:28', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006002, 1006, '本部门及以下', '', 2, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:56:57', '2024-06-25 19:11:29', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006003, 1006, '仅本部门', '', 3, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:22', '2024-06-25 19:11:32', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006004, 1006, '仅本人', '', 4, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:57', '2024-06-25 19:11:34', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006005, 1006, '自定义', '', 5, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:58:11', '2024-06-25 19:11:36', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1007001, 1007, '部门权限', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:59:00', '2024-06-25 19:11:38', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1007002, 1007, '个人权限', '', 2, 'primary', '个人权限高优先级', 'T', 'T', 'F', '2024-06-25 18:59:27', '2024-06-25 19:11:41', NULL, NULL, NULL, NULL);

-- 新增按钮权限
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`) VALUES ('30942929802f41cc850722c78db089e7', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置数据角色', '', '', '', 800, 3, '1002003', 'sys.user.data_role_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-02T19:22:37', '2024-07-11T20:27:03', '1', '1', 'F', NULL, NULL);
-- 设置管理员权限
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ( '30942929802f41cc850722c78db089e7', 1);
-- 新增系统参数
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `is_lock`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES ('业务字典起始号段', 'sys.dict.startNo', '2000', 'T', 1, '2024-07-08 17:29:16', NULL, NULL, '业务字典起始号段。1000作为默认的系统字典号段。');

-- 新增数据角色表
CREATE TABLE `sys_data_role` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
                                 `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
                                 `data_scope_cd` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据权限，data_scope字典',
                                 `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简介',
                                 `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
                                 `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '是否锁定',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `create_id` int DEFAULT NULL COMMENT '创建人',
                                 `update_id` int DEFAULT NULL COMMENT '更新人',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统数据角色表';

-- 新增 系统数据角色-菜单表
CREATE TABLE `sys_data_role_menu` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `role_id` int NOT NULL COMMENT 'sys_data_role_id （数据角色表）',
                                 `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统数据角色-菜单表';

-- 新增 系统数据角色-关联表
CREATE TABLE `sys_data_role_relation` (
                                          `id` INT NOT NULL AUTO_INCREMENT,
                                          `role_id` INT NOT NULL COMMENT 'sys_data_role_id （数据角色表）',
                                          `relation_type_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联类型，data_scope_relation_type',
                                          `relation_id` int DEFAULT NULL COMMENT '关联表id，联动relation_type_cd',
                                          PRIMARY KEY ( `id` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC COMMENT = '系统数据角色-关联表';

-- 新增 系统用户-数据角色关联表
CREATE TABLE `sys_user_data_role` (
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `role_id` int NOT NULL COMMENT '数据角色id (sys_data_role_id)',
                                      `user_id` int NOT NULL COMMENT '用户id(sys_user_id)',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户-数据角色关联表';

-- 新增菜单
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)
VALUES ('0444cd2c01584f0687264b6205536691', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/dataRoleManage', 'SysDataRoleView', '数据权限', 'svg-scope', '/system/dataRoleManage/index', '', 800, 2, '1002002', 'sys.data.role.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-07-09T19:19:45', '2024-07-09T19:22:31', '', '1', 'F', NULL, NULL);

INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)
VALUES ('8d92cf6f2f3248569d5dd6cb6b958d7c', '0444cd2c01584f0687264b6205536691', '', '', '新增', '', '', '', 100, 3, '1002003', 'sys.data.role.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09T19:19:11', '2024-07-09T19:19:11', '', '', 'F', NULL, NULL);

INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)
VALUES ('f42b249ccfd44fdcbc2dba48a308c1f6', '0444cd2c01584f0687264b6205536691', '', '', '修改', '', '', '', 200, 3, '1002003', 'sys.data.role.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09T19:19:11', '2024-07-09T19:19:11', '', '', 'F', NULL, NULL);

INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)
VALUES ('3f555e4a01174a1d9b29be439668e32f', '0444cd2c01584f0687264b6205536691', '', '', '删除', '', '', '', 300, 3, '1002003', 'sys.data.role.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-07-09T19:19:11', '2024-07-09T19:19:11', '', '', 'F', NULL, NULL);

-- 修改菜单，增加字段
ALTER TABLE `sys_menu` ADD COLUMN `use_data_scope` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '菜单是否开启数据权限';

-- 设置管理员权限
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ( '0444cd2c01584f0687264b6205536691', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ( '8d92cf6f2f3248569d5dd6cb6b958d7c', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ( 'f42b249ccfd44fdcbc2dba48a308c1f6', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ( '3f555e4a01174a1d9b29be439668e32f', 1);

-- 【以下是测试数据部分】
DELETE FROM `teacher_statistics`;
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (22, '2018', '12', '03', '1503', 1000001, 12, 22, 15.00, 1000001, '2024-07-08 10:39:56', '2024-07-08 10:40:16', NULL, '2024-07-08 10:39:57', 'test1 创建记录', 3, NULL, '[4]');
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (23, '2019', '12', '03', '111', 1000001, 1, 2, 3.00, 1000001, '2024-07-08 10:41:09', '2024-07-08 10:41:18', NULL, '2024-07-08 10:41:11', 'test1 创建记录', 3, NULL, '[4]');
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (24, '2020', '12', '2020-12', '023', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:06:55', '2024-07-08 13:07:07', NULL, '2024-07-08 13:06:57', 'test1 创建记录', 3, NULL, '[4]');
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (25, '2021', '12', '2021-12', '123', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:13:56', '2024-07-08 13:13:59', NULL, NULL, 'test2 创建记录', 4, NULL, '[15]');
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (26, '2022', '12', '2022-12', '13123', 1000001, 1, 1, 1.00, 1000001, '2024-07-08 13:15:36', '2024-07-08 13:15:37', '2024-07-08 13:15:46', NULL, 'test3 创建记录', 5, 5, '[15]');
INSERT INTO `teacher_statistics` (`id`, `year`, `month`, `during_time`, `teacher_id`, `teacher_common_type`, `total_teaching`, `total_class_count`, `total_hours`, `check_status`, `check_time`, `create_time`, `update_time`, `last_sync_time`, `remark`, `create_id`, `update_id`, `dept_scope`) VALUES (27, '2099', '12', '12', '123123', 1000001, 1, 1, 1.00, 1000001, NULL, '2024-07-08 13:20:29', NULL, NULL, '管理员创建', 1, NULL, '[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]');
-- 测试用户
INSERT INTO `sys_user` (`id`, `username`, `pwd`, `phone`, `nickname`, `sex`, `birthday`, `logo`, `age`, `id_card`, `email`, `account_status_cd`, `user_tag_cd`, `last_login_time`, `create_time`, `update_time`, `del_flag`, `create_id`, `update_id`) VALUES (3, 'test1', '$2a$10$QXRq4OGoHahxlXbULJxIXe0RgOCdW7C716bes9qh4gopIVROAVxXW', '', '测试1-本部及以下', NULL, '', '', 0, '', '', '1000001', '1001003', NULL, '2024-07-08 08:47:31', '2024-07-08 09:17:41', 'F', 1, 1);
INSERT INTO `sys_user` (`id`, `username`, `pwd`, `phone`, `nickname`, `sex`, `birthday`, `logo`, `age`, `id_card`, `email`, `account_status_cd`, `user_tag_cd`, `last_login_time`, `create_time`, `update_time`, `del_flag`, `create_id`, `update_id`) VALUES (4, 'test2', '$2a$10$uMZA6KiYtvnLVHSukXiB2ufvKdp827nO/6p6jWn1ydEYoLA0kgPqK', '', '测试2-仅本部', NULL, '', '', 0, '', '', '1000001', '1001003', NULL, '2024-07-08 08:47:41', '2024-07-08 09:17:11', 'F', 1, 1);
INSERT INTO `sys_user` (`id`, `username`, `pwd`, `phone`, `nickname`, `sex`, `birthday`, `logo`, `age`, `id_card`, `email`, `account_status_cd`, `user_tag_cd`, `last_login_time`, `create_time`, `update_time`, `del_flag`, `create_id`, `update_id`) VALUES (5, 'test3', '$2a$10$UWKoQfMAFxk/qdTI4vQLgOjho5xtjNJhdbHmJNoYuNZkuOq2WCoZm', '', '测试3-仅本人', NULL, '', '', 0, '', '', '1000001', '1001003', NULL, '2024-07-08 08:47:50', '2024-07-08 09:17:56', 'F', 1, 1);
INSERT INTO `sys_user` (`id`, `username`, `pwd`, `phone`, `nickname`, `sex`, `birthday`, `logo`, `age`, `id_card`, `email`, `account_status_cd`, `user_tag_cd`, `last_login_time`, `create_time`, `update_time`, `del_flag`, `create_id`, `update_id`) VALUES (6, 'test4', '$2a$10$bCgJMtfSPhn6Mvn2AGx5z.NDVHXBvxl7/XEvlH52wbBpAWkLvwVVe', '', '测试4-自定义', NULL, '', '', 0, '', '', '1000001', '1001003', NULL, '2024-07-08 08:47:58', '2024-07-08 09:18:33', 'F', 1, 1);
-- 用户角色设置
INSERT INTO `sys_user_role` (`role_id`, `user_id`) VALUES (3, 3);
INSERT INTO `sys_user_role` (`role_id`, `user_id`) VALUES (3, 4);
INSERT INTO `sys_user_role` (`role_id`, `user_id`) VALUES (3, 5);
INSERT INTO `sys_user_role` (`role_id`, `user_id`) VALUES (3, 6);
-- 用户部门设置
INSERT INTO `sys_user_dept` (`dept_id`, `user_id`) VALUES (4, 3);
INSERT INTO `sys_user_dept` (`dept_id`, `user_id`) VALUES (15, 4);
INSERT INTO `sys_user_dept` (`dept_id`, `user_id`) VALUES (15, 5);

-- 开启教师统计-数据权限支持
UPDATE `sys_menu` SET `use_data_scope` = 'T' WHERE `id` = '85b54322630f43a39296488a5e76ba16';

-- 测试数据
INSERT INTO `sys_data_role` (`id`, `role_name`, `data_scope_cd`, `remark`, `del_flag`, `is_lock`, `create_time`, `update_time`, `create_id`, `update_id`) VALUES (1, '教师统计-本部门及以下', '1006002', '', 'F', 'T', '2024-07-15 15:35:05', '2024-07-15 16:57:19', 1, 1);
INSERT INTO `sys_data_role` (`id`, `role_name`, `data_scope_cd`, `remark`, `del_flag`, `is_lock`, `create_time`, `update_time`, `create_id`, `update_id`) VALUES (2, '教师统计-仅本部门', '1006003', '', 'F', 'T', '2024-07-15 15:36:03', NULL, 1, NULL);
INSERT INTO `sys_data_role` (`id`, `role_name`, `data_scope_cd`, `remark`, `del_flag`, `is_lock`, `create_time`, `update_time`, `create_id`, `update_id`) VALUES (3, '教师统计-仅本人', '1006004', '', 'F', 'T', '2024-07-15 15:36:46', NULL, 1, NULL);
INSERT INTO `sys_data_role` (`id`, `role_name`, `data_scope_cd`, `remark`, `del_flag`, `is_lock`, `create_time`, `update_time`, `create_id`, `update_id`) VALUES (4, '教师统计-自定义', '1006005', '', 'F', 'T', '2024-07-15 15:37:27', NULL, 1, NULL);

INSERT INTO `sys_data_role_menu` (`role_id`, `menu_id`) VALUES (2, '85b54322630f43a39296488a5e76ba16');
INSERT INTO `sys_data_role_menu` (`role_id`, `menu_id`) VALUES (3, '85b54322630f43a39296488a5e76ba16');
INSERT INTO `sys_data_role_menu` (`role_id`, `menu_id`) VALUES (4, '85b54322630f43a39296488a5e76ba16');
INSERT INTO `sys_data_role_menu` (`role_id`, `menu_id`) VALUES (1, '85b54322630f43a39296488a5e76ba16');

INSERT INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`) VALUES (2, '1007001', 15);
INSERT INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`) VALUES (3, '1007001', 15);
INSERT INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`) VALUES (4, '1007002', 5);
INSERT INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`) VALUES (4, '1007002', 3);
INSERT INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`) VALUES (1, '1007001', 4);

INSERT INTO `sys_user_data_role` (`role_id`, `user_id`) VALUES (2, 4);
INSERT INTO `sys_user_data_role` (`role_id`, `user_id`) VALUES (1, 3);
INSERT INTO `sys_user_data_role` (`role_id`, `user_id`) VALUES (3, 5);
INSERT INTO `sys_user_data_role` (`role_id`, `user_id`) VALUES (4, 6);
