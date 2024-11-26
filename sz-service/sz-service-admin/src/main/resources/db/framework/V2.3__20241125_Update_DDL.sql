-- 【结构性变更】 修改文件表结构
ALTER TABLE `sys_file`
    CHANGE COLUMN `type` `dir_tag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录标识';

ALTER TABLE `sys_file`
    MODIFY COLUMN `size` bigint DEFAULT NULL COMMENT '文件大小',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `sys_file`
    ADD COLUMN `object_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对象名（唯一）',
    ADD COLUMN `context_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件类型',
    ADD COLUMN `e_tag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'eTag标识',
    ADD COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID';

INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `sort`, `deep`, `menu_type_cd`, `permissions`, `has_children`) VALUES ('c4896e8735a745bda9b47ecaf50f46f2', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/fileManage', 'SysFileView', '文件管理', 'Files', '/system/fileManage/index', 900, 2, '1002002', 'sys.file.query_table', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `sort`, `deep`, `menu_type_cd`, `permissions`, `has_children`) VALUES ('2868079355ce4b6c985b1b746dbb0952', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '新增', '', '', 100, 3, '1002003', 'sys.file.create', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `sort`, `deep`, `menu_type_cd`, `permissions`, `has_children`) VALUES ('49c75878b4d445f8be5f69e21e18b70d', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '修改', '', '', 200, 3, '1002003', 'sys.file.update', 'F');
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `sort`, `deep`, `menu_type_cd`, `permissions`, `has_children`) VALUES ('c55de3135b864579bda79c279f4129a9', 'c4896e8735a745bda9b47ecaf50f46f2', '', '', '删除', '', '', 300, 3, '1002003', 'sys.file.remove', 'F');

INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('c4896e8735a745bda9b47ecaf50f46f2', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('2868079355ce4b6c985b1b746dbb0952', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('49c75878b4d445f8be5f69e21e18b70d', 1);
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`) VALUES ('c55de3135b864579bda79c279f4129a9', 1);