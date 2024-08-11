-- 新增字典-SQL预览按钮
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)
    VALUES ('7a4544831af34e69aa73148bf84b9924', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', 'SQL按钮', '', '', '', 700, 3, '1002003', 'sys.dict.sql_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-08-11T16:10:09', '2024-08-11T16:10:09', '1', '', 'F', NULL, NULL);
-- 初始化角色菜单权限，增加SQL按钮
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('7a4544831af34e69aa73148bf84b9924', 1);