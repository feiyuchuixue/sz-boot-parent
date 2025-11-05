--liquibase formatted sql

--changeset 升职哦（sz）:20251105_2005
ALTER TABLE `generator_table` ADD COLUMN `btn_data_scope_type` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否开启数据权限（1 是）' after `btn_permission_type`;