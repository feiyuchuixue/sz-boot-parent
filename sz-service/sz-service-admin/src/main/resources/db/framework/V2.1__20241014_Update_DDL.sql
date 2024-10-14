-- 【结构性变更】 修改关联自增id相关字段类型为bigint
ALTER TABLE `generator_table`
    MODIFY COLUMN `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

ALTER TABLE `generator_table_column`
    MODIFY COLUMN `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    MODIFY COLUMN `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';