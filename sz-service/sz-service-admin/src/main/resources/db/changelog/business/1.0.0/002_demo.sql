--liquibase formatted sql

--changeset 升职哦（sz）:20250720_2030
-- 以下为演示环境脚本，-- 仅供演示使用，实际业务请删除
ALTER TABLE teacher_statistics
    ADD COLUMN image_id VARCHAR(255) NULL COMMENT 'image_id';