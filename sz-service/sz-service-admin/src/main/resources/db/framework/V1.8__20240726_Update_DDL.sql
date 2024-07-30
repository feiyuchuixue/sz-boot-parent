-- 【结构性变更】
ALTER TABLE `generator_table_column` MODIFY COLUMN `is_logic_del` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否逻辑删除(1 是)';

ALTER TABLE `generator_table` ADD COLUMN `is_autofill` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '是否自动填充(1 是)'