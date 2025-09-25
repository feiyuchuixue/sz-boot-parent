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