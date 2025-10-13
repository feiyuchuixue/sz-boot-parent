--liquibase formatted sql

--changeset 升职哦（sz）:20251013_1836
-- 以下为演示环境脚本，-- 仅供演示使用，实际业务请删除

ALTER TABLE `teacher_statistics` ADD COLUMN `url` json DEFAULT NULL COMMENT '文件地址(JSON)';