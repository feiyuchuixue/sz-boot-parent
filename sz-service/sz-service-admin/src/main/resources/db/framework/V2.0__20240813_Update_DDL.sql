-- 更新sys_menu deep层级
UPDATE `sys_menu` SET `deep` = 1 WHERE `id` = '85b54322630f43a39296488a5e76ba16';
-- 更新sys_menu permissions 标识
UPDATE `sys_menu` SET `permissions` = 'generator.list' WHERE `id` = '0e529e8a9dbf450898b695e051c36d48';

-- 角色表结构性改变
ALTER TABLE `sys_role` ADD COLUMN `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定';
ALTER TABLE `sys_role` ADD COLUMN `permissions` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '标识，唯一';

UPDATE `sys_role` SET `is_lock` = 'T', `permissions` = 'admin' WHERE `role_name` = '超级管理员';
UPDATE `sys_role` SET `permissions` = 'dict_menu' WHERE `role_name` = '字典管理';
UPDATE `sys_role` SET `permissions` = 'teacher_statics_menu' WHERE `role_name` = '教师统计';