--liquibase formatted sql

--changeset 升职哦（sz）:20250924_1930
-- ===============================================
-- ⚠️【破坏性更新】请谨慎升级！
-- 此操作会删除数据，请务必提前备份相关数据！
-- ===============================================
-- 删除 演示数据
DELETE FROM `sys_file` WHERE id IN (97, 98, 99);
DELETE FROM `sys_temp_file` WHERE id = 1 and sys_file_id = 98;
DELETE FROM `sys_temp_file_history` WHERE sys_temp_file_id = 1;
-- 此改动为修正之前的设计缺陷，将sys_temp_file和sys_temp_file_history的url字段由varchar改为json类型，以支持存储多个文件地址
ALTER TABLE `sys_temp_file` MODIFY COLUMN  `url` json DEFAULT NULL COMMENT '地址(JSON数组对象)';
ALTER TABLE `sys_temp_file_history` MODIFY COLUMN  `url` json DEFAULT NULL COMMENT '地址(JSON数组对象)';
ALTER TABLE `sys_temp_file` ADD COLUMN  alias varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '标识，唯一';

--changeset 升职哦（sz）:20251020_2250
INSERT IGNORE INTO `sys_file` (`id`, `filename`, `dir_tag`, `size`, `url`, `create_time`, `object_name`, `context_type`, `e_tag`, `create_id`) VALUES (100, '教师统计模板.xlsx', 'tmp', 9866, 'https://minioapi.szadmin.cn/test/excel/教师统计模板.xlsx', '2025-10-20 22:36:53', 'tmp/20251020/教师统计模板.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '8bba8015aa748013cc8295a13637fb3a', 1);
INSERT IGNORE INTO `sys_temp_file` (`id`, `sys_file_id`, `temp_name`, `remark`, `del_flag`, `create_id`, `create_time`, `update_id`, `update_time`, `url`, `alias`) VALUES (2, 100, '教师统计模板.xlsx', '教师统计-导入模板', 'F', 1, '2025-10-20 22:37:22', 1, '2025-10-20 22:37:22', '[{\"url\": \"https://minioapi.szadmin.cn/test/excel/教师统计模板.xlsx\", \"etag\": \"8bba8015aa748013cc8295a13637fb3a\", \"size\": 9866, \"dirTag\": \"tmp\", \"fileId\": 100, \"filename\": \"教师统计模板.xlsx\", \"metaData\": null, \"objectName\": \"excel/教师统计模板.xlsx\", \"contextType\": \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"}]', 'jstj');
INSERT IGNORE INTO `sys_temp_file_history` (`id`, `sys_temp_file_id`, `sys_file_id`, `temp_name`, `url`, `remark`, `create_id`, `create_time`, `update_id`, `update_time`) VALUES (3, 2, 100, '教师统计模板.xlsx', '[{\"url\": \"https://minioapi.szadmin.cn/test/excel/教师统计模板.xlsx\", \"etag\": \"8bba8015aa748013cc8295a13637fb3a\", \"size\": 9866, \"dirTag\": \"tmp\", \"fileId\": 100, \"filename\": \"教师统计模板.xlsx\", \"metaData\": null, \"objectName\": \"excel/教师统计模板.xlsx\", \"contextType\": \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"}]', '教师统计-导入模板', 1, '2025-10-20 22:37:22', 1, '2025-10-20 22:37:22');