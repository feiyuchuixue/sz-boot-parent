-- 更新 sys_user id = 1 的 `user_tag_cd` = '1001002' 超级管理员
UPDATE `sys_user` SET `user_tag_cd` = '1001002' WHERE `id` = 1;
-- 设置关键性字典 锁定项
UPDATE `sys_dict_type` SET `is_lock` = 'T' WHERE `id` = 1001;
UPDATE `sys_dict` SET `is_lock` = 'T' WHERE `id` in (1001001, 1001002, 1001003, 1002001, 1002002, 1002003, 1005001);