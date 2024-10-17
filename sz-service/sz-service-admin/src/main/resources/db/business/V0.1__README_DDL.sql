-- 这是一个描述文件！！  以下是演示示例，不会生成到业务数据库中！！
-- 在此目录下（/db/business）创建你要执行的业务迁移脚本，命名规则：V1.1__"${your_name}".sql。版本需号大于1（yml配置flyway.business.baseline-version）。
CREATE TABLE `teacher` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
                           `age` int DEFAULT NULL COMMENT '年龄',
                           `level` int DEFAULT NULL COMMENT '年级',
                           `subject` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '科目',
                           `join_time` datetime DEFAULT NULL COMMENT '加入时间',
                           `del_flag` enum('T','F') COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '删除标识',
                           `create_id` bigint DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_id` bigint DEFAULT NULL COMMENT '更新人',
                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师表';