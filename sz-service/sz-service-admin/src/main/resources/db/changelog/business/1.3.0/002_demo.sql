--liquibase formatted sql

--changeset 升职哦（sz）:20251105_1932
DELETE FROM `sys_role` WHERE id = 3 and permissions = 'teacher_statics_menu';
-- 创建新的演示角色
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `remark`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `is_lock`, `permissions`) VALUES (3, '教师统计-全部', '演示：数据权限-全部', 'F', '2024-05-10 21:53:15', '2025-11-05 13:39:50', NULL, 1, 'T', 'teacher_statics_menu_1006001');
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `remark`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `is_lock`, `permissions`) VALUES (4, '教师统计-部门及以下', '演示：数据权限-部门及以下', 'F', '2025-11-05 11:24:40', '2025-11-05 13:39:45', 1, 1, 'T', 'teacher_statics_menu_1006002');
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `remark`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `is_lock`, `permissions`) VALUES (5, '教师统计-仅本部', '演示：数据权限-仅本部', 'F', '2025-11-05 11:25:21', '2025-11-05 13:40:02', 1, 1, 'T', 'teacher_statics_menu_1006003');
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `remark`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `is_lock`, `permissions`) VALUES (6, '教师统计-仅本人', '演示：数据权限-仅本人', 'F', '2025-11-05 13:39:19', '2025-11-05 13:40:08', 1, 1, 'T', 'teacher_statics_menu_1006004');
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `remark`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `is_lock`, `permissions`) VALUES (7, '教师统计-自定义', '演示：数据权限-自定义', 'F', '2025-11-05 13:46:50', '2025-11-05 13:46:50', 1, 1, 'T', 'teacher_statics_menu_1006005');
-- 为新的角色分配菜单权
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('cb3500315dba4c2d83e4d92edf36dff7', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('7391f12ad51049c2b86d231d39708c71', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('73d312f4fa8949ddba3d9807c0c56f00', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('91ccb13b5c174583803a4c492a5dfdb6', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('8061d8e79be744bf91b7b438f8e8e887', 3, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 3, 'scope', '1006001');
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('cb3500315dba4c2d83e4d92edf36dff7', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('7391f12ad51049c2b86d231d39708c71', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('73d312f4fa8949ddba3d9807c0c56f00', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('91ccb13b5c174583803a4c492a5dfdb6', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('8061d8e79be744bf91b7b438f8e8e887', 4, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 4, 'scope', '1006002');
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('cb3500315dba4c2d83e4d92edf36dff7', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('7391f12ad51049c2b86d231d39708c71', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('73d312f4fa8949ddba3d9807c0c56f00', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('91ccb13b5c174583803a4c492a5dfdb6', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('8061d8e79be744bf91b7b438f8e8e887', 6, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 6, 'scope', '1006004');
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('cb3500315dba4c2d83e4d92edf36dff7', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('7391f12ad51049c2b86d231d39708c71', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('73d312f4fa8949ddba3d9807c0c56f00', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('91ccb13b5c174583803a4c492a5dfdb6', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('8061d8e79be744bf91b7b438f8e8e887', 5, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 5, 'scope', '1006003');
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('ab1ac16c617d41979472ebe433c1f8e4', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('cb3500315dba4c2d83e4d92edf36dff7', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('7391f12ad51049c2b86d231d39708c71', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('73d312f4fa8949ddba3d9807c0c56f00', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('91ccb13b5c174583803a4c492a5dfdb6', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('8061d8e79be744bf91b7b438f8e8e887', 7, 'menu', NULL);
INSERT IGNORE INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('85b54322630f43a39296488a5e76ba16', 7, 'scope', '1006005');
-- 为自定义角色分配数据权限
INSERT IGNORE INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`, `menu_id`) VALUES (7, '1007001', 3, '85b54322630f43a39296488a5e76ba16');
INSERT IGNORE INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`, `menu_id`) VALUES (7, '1007001', 5, '85b54322630f43a39296488a5e76ba16');
INSERT IGNORE INTO `sys_data_role_relation` (`role_id`, `relation_type_cd`, `relation_id`, `menu_id`) VALUES (7, '1007002', 15, '85b54322630f43a39296488a5e76ba16');