--liquibase formatted sql

--changeset 升职哦（sz）:20250603_1400
--comment: 更新 sys_temp_file 表，将url varchar 改为 json数组
-- 1️⃣ 新增临时 JSON 数组字段
ALTER TABLE sys_temp_file ADD COLUMN url_json JSON DEFAULT NULL COMMENT '地址(JSON数组格式)';

-- 2️⃣ 将原有 varchar(url) 数据更新为 JSON 数组格式，例如 ["原url"]
UPDATE sys_temp_file SET url_json = JSON_ARRAY(url);

-- 3️⃣ 删除原来的 varchar(url) 字段
ALTER TABLE sys_temp_file DROP COLUMN url;

-- 4️⃣ 将临时字段 url_json 改名为 url
ALTER TABLE sys_temp_file CHANGE COLUMN url_json url JSON DEFAULT NULL COMMENT '地址(JSON数组格式)';
