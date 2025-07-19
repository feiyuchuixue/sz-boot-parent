--liquibase formatted sql

--changeset 升职哦（sz）:20250719_1626
--comment: 为 sys_file 表添加 status 和 uploadExpireTime 字段
ALTER TABLE sys_file
    ADD COLUMN status VARCHAR(50) NULL COMMENT '上传状态',
    ADD COLUMN upload_expire_time DATETIME NULL COMMENT '上传凭证过期时间';

UPDATE sys_file
SET
    status = 'COMPLETED',
    upload_expire_time = NOW()
WHERE id IS NOT NULL;
