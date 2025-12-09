--liquibase formatted sql

--changeset 升职哦（sz）:20251208_1936
-- 以下为演示环境脚本，-- 仅供演示使用，实际业务请删除

ALTER TABLE `teacher_statistics` ADD COLUMN `content_html` mediumtext COLLATE utf8mb4_general_ci COMMENT '内容HTML';