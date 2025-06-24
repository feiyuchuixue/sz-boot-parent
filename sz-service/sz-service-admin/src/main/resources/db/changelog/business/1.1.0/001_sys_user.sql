--liquibase formatted sql

--changeset 升职哦（sz）:20250623_0944
ALTER TABLE `sys_user` ADD COLUMN `ucenter_id` bigint DEFAULT NULL COMMENT '平台用户id' AFTER `id`;
