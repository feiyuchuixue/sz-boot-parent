--liquibase formatted sql

--changeset 升职哦（sz）:20260112_2114
ALTER TABLE `sys_config` ADD COLUMN `frontend_visible` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '该参数是否需要前端加载、缓存及使用' after `is_lock`;

--changeset 升职哦（sz）:20260116_2025
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `is_lock`, `frontend_visible`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES ('资源访问模式', 'oss.accessMode', 'private', 'F', 'T', 1, '2026-01-13 09:59:09', 1, '2026-01-16 13:57:52', '用于控制前端资源文件（如 Minio OSS）访问方式。当值为 public 时，前端直接使用数据库中存储的完整资源 URL 进行加载，适用于公开访问的资源；当值为 private 时，前端通过 Minio 获取私有资源的临时授权地址进行加载，适用于需权限验证的私有资源。');

--changeset 升职哦（sz）:20260116_2215
UPDATE `sys_config` SET `is_lock` = 'T' WHERE `config_key` = 'oss.accessMode';
UPDATE `sys_config` SET `is_lock` = 'T' WHERE `config_key` = 'sys.login.requestCycle';
UPDATE `sys_config` SET `is_lock` = 'T' WHERE `config_key` = 'sys.login.requestLimit';