--liquibase formatted sql

--changeset 升职哦（sz）:20251212_1954
ALTER TABLE `generator_table` ADD COLUMN `window_show_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '窗口展示方式(0 dialog弹窗；1 drawer 抽屉)';
