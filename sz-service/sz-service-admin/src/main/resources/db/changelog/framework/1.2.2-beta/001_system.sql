--liquibase formatted sql

--changeset 升职哦（sz）:20250526_2022
--comment: 增加eTag长度，修复文件上传时eTag过长导致的错误
ALTER TABLE `sys_file` MODIFY COLUMN `e_tag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'eTag标识';