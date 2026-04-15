--liquibase formatted sql

--changeset 升职哦（sz）:20260411_1239
--以下为演示环境脚本，-- 仅供演示使用，实际业务请删除
ALTER TABLE `teacher_statistics` ADD COLUMN `has_invalid` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '是否无效（枚举情况演示字段，包含mf枚举使用，excel枚举处理）' ;