--liquibase formatted sql

--changeset 升职哦（sz）:20260112_2114
ALTER TABLE `sys_config` ADD COLUMN `frontend_visible` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '该参数是否需要前端加载、缓存及使用' after `is_lock`;