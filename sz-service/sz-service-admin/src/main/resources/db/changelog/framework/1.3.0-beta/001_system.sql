--liquibase formatted sql

--changeset 升职哦（sz）:20250920_1414
--comment: 更新菜单权限标识

-- ===============================================
-- ⚠️【破坏性更新】请谨慎升级！
-- 菜单路由会发生改变
-- ===============================================
-- 账号管理
UPDATE sys_menu SET permissions = '', path = '/system/account' WHERE id = '140c9ed43ef54542bbcdde8a5d928400';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('0bef80d81a264f689d091613053c659e', '140c9ed43ef54542bbcdde8a5d928400', '', '', '查询账号', '', '', '', 99, 3, '1002003', 'sys.user.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 角色管理
UPDATE sys_menu SET permissions = '', path = '/system/role' WHERE id = 'c6dd479d5b304731be403d7551c60d70';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('abecbec6abe74023abaa653f827a3b0f', 'c6dd479d5b304731be403d7551c60d70', '', '', '查询角色', '', '', '', 99, 3, '1002003', 'sys.role.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 菜单管理
UPDATE sys_menu SET permissions = '', path = '/system/menu' WHERE id = '99c2ee7b882749e597bcd62385f368fb';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('9dfeebc408e1490e88bf10db3b5e1239', '99c2ee7b882749e597bcd62385f368fb', '', '', '查询菜单', '', '', '', 99, 3, '1002003', 'sys.menu.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 字典管理
UPDATE sys_menu SET permissions = '', path = '/system/dict' WHERE id = 'dcb6aabcd910469ebf3efbc7e43282d4';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('d90946bc9ed54e598e3c4471dbd1f496', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '查询字典', '', '', '', 99, 3, '1002003', 'sys.dict.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 参数管理
UPDATE sys_menu SET permissions = '', path = '/system/conf' WHERE id = '29d33eba6b73420287d8f7e64aea62b3';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('bef8f1330490494f924314eec9687055', '29d33eba6b73420287d8f7e64aea62b3', '', '', '查询参数', '', '', '', 99, 3, '1002003', 'sys.config.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 客户端管理
UPDATE sys_menu SET permissions = '', path = '/system/client' WHERE id = '9e731ff422184fc1be2022c5c985735e';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('6307bd1221fb474b8c8777afaed4440f', '9e731ff422184fc1be2022c5c985735e', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.client.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 部门管理
UPDATE sys_menu SET permissions = '', path = '/system/dept' WHERE id = '8354d626cc65487594a7c38e98de1bad';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('31dca47db78f4c14b9cfe65226061b65', '8354d626cc65487594a7c38e98de1bad', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.dept.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 数据权限
UPDATE sys_menu SET permissions = '', path = '/system/data' WHERE id = '0444cd2c01584f0687264b6205536691';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('2ae05bd0afaa4c6dbc173bf1dd0da2cf', '0444cd2c01584f0687264b6205536691', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.data.role.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 文件管理
UPDATE sys_menu SET permissions = '', path = '/system/file' WHERE id = 'c4896e8735a745bda9b47ecaf50f46f2';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('62dd478ed2f54792b08ed9202e73d2cf', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.file.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 模板文件管理
UPDATE sys_menu SET permissions = '', path = '/system/fileTemp' WHERE id = '8231a369712e4f8f8ac09fce232cd034';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('47e5805374bc44a2831126676b8c78dd', '8231a369712e4f8f8ac09fce232cd034', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.temp.file.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');
-- 登录日志
UPDATE sys_menu SET permissions = '' WHERE id = '191878090b294ec5892a68081dd91428';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('c555243b02814270948a240e8d2d3408', '191878090b294ec5892a68081dd91428', '', '', '查询', '', '', '', 99, 3, '1002003', 'sys.login.log.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

UPDATE sys_menu SET permissions = '' WHERE id = '0e529e8a9dbf450898b695e051c36d48';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('765d139874424930b0a672128712de9b', '0e529e8a9dbf450898b695e051c36d48', '', '', '查询', '', '', '', 99, 3, '1002003', 'generator.list', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

UPDATE sys_menu SET permissions = '' WHERE id = '85b54322630f43a39296488a5e76ba16';
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', '85b54322630f43a39296488a5e76ba16', '', '', '查询', '', '', '', 99, 2, '1002003', 'teacher.statistics.query_table', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

--changeset 升职哦（sz）:20250920_1520
--comment: 更新角色菜单关联
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('0bef80d81a264f689d091613053c659e', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('abecbec6abe74023abaa653f827a3b0f', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('9dfeebc408e1490e88bf10db3b5e1239', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('d90946bc9ed54e598e3c4471dbd1f496', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('bef8f1330490494f924314eec9687055', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('6307bd1221fb474b8c8777afaed4440f', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('31dca47db78f4c14b9cfe65226061b65', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('2ae05bd0afaa4c6dbc173bf1dd0da2cf', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('62dd478ed2f54792b08ed9202e73d2cf', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('47e5805374bc44a2831126676b8c78dd', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('c555243b02814270948a240e8d2d3408', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('765d139874424930b0a672128712de9b', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 3);

--changeset 升职哦（sz）:20251030_1925
--comment: 修改sys_role_menu表, 增加字段
ALTER TABLE `sys_role_menu` ADD COLUMN `permission_type` enum('menu','scope') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'menu' COMMENT '权限类型（功能权限；数据权限）';
ALTER TABLE `sys_role_menu` ADD COLUMN `data_scope_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据权限范围，data_scope字典';
UPDATE `sys_role_menu` SET `permission_type` = 'menu' WHERE `permission_type` IS NULL;

--changeset 升职哦（sz）:20251031_1930
ALTER TABLE `sys_data_role_relation` ADD COLUMN `menu_id` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单id';

--changeset 升职哦（sz）:20251103_2312
UPDATE `sys_menu` SET `is_hidden` = 'T' WHERE `id` = '0444cd2c01584f0687264b6205536691';
UPDATE `sys_menu` SET `use_data_scope` = 'T' WHERE `pid` IN ( SELECT `id` FROM (SELECT `id` FROM `sys_menu` WHERE `is_hidden` = 'T' AND `menu_type_cd` != '1002003') AS temp);
ALTER TABLE `sys_data_role_menu` COMMENT='系统数据角色-菜单表【废弃, from 2025-v1.3.0】';
ALTER TABLE `sys_data_role` COMMENT='系统数据角色表【废弃, from 2025-v1.3.0】';

--changeset 升职哦（sz）:20251104_1933
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `is_lock`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES ('超级管理员角色权限配置', 'sys.admin.superAdminRoleId', '1', 'T', 1, '2025-11-04 13:06:45', 1, '2025-11-04 16:32:25', '超管权限，指定的生效角色 ID。');
UPDATE `sys_dict` SET `sys_dict_type_id` = 1001, `code_name` = '测试用户', `alias` = '', `sort` = 3, `callback_show_style` = 'warning', `remark` = '', `is_lock` = 'T', `is_show` = 'T', `del_flag` = 'F', `create_time` = '2023-08-20 16:38:58', `update_time` = '2025-11-05 14:56:22', `delete_time` = NULL, `create_id` = NULL, `update_id` = 1, `delete_id` = NULL WHERE `id` = 1001001;
UPDATE `sys_dict` SET `sys_dict_type_id` = 1001, `code_name` = '超级管理员', `alias` = '', `sort` = 2, `callback_show_style` = 'danger', `remark` = '', `is_lock` = 'T', `is_show` = 'T', `del_flag` = 'F', `create_time` = '2023-08-20 16:39:05', `update_time` = '2025-11-05 14:56:34', `delete_time` = NULL, `create_id` = NULL, `update_id` = 1, `delete_id` = NULL WHERE `id` = 1001002;
UPDATE `sys_dict` SET `sys_dict_type_id` = 1001, `code_name` = '普通用户', `alias` = '', `sort` = 1, `callback_show_style` = 'primary', `remark` = '', `is_lock` = 't', `is_show` = 'T', `del_flag` = 'F', `create_time` = '2023-08-20 16:39:11', `update_time` = '2025-11-05 14:56:39', `delete_time` = NULL, `create_id` = NULL, `update_id` = 1, `delete_id` = NULL WHERE `id` = 1001003;