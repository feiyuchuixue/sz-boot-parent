-- 【结构性变更】 代码生成column表
ALTER TABLE `generator_table_column` ADD COLUMN  `dict_show_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '字典展示方式（0 唯一标识；1 别名）';