--liquibase formatted sql

--changeset 升职哦（sz）:20260127_2015
--comment: 添加设置账户类型菜单
INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `del_flag`, `use_data_scope`) VALUES ('b3d357cf69034286988f05a346f2412c', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置账户类型', '', '', '', 900, 3, '1002003', 'sys.user.admin_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F');

--changeset 升职哦（sz）:20260127_2020
--comment: 为超级管理员分配设置账户类型权限
INSERT INTO `sys_role_menu` (`menu_id`, `role_id`, `permission_type`, `data_scope_cd`) VALUES ('b3d357cf69034286988f05a346f2412c', 1, 'menu', NULL);

--changeset 升职哦（sz）:20260127_2025
--comment: 清除菜单-设置数据角色按钮
DELETE FROM `sys_role_menu` WHERE menu_id = '30942929802f41cc850722c78db089e7';
DELETE FROM `sys_menu` WHERE id = '30942929802f41cc850722c78db089e7' ;