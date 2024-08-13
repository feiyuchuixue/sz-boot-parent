-- 更新sys_menu deep层级
UPDATE `sys_menu` SET `deep` = 1 WHERE `id` = '85b54322630f43a39296488a5e76ba16';
-- 更新sys_menu permissions 标识
UPDATE `sys_menu` SET `permissions` = 'generator.list' WHERE `id` = '0e529e8a9dbf450898b695e051c36d48';