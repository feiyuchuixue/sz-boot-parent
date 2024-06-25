-- 创建teacher表
CREATE TABLE `teacher` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                           `create_id` int DEFAULT NULL,
                           `area_id` int DEFAULT NULL,
                           `age` int DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师表' ROW_FORMAT=DYNAMIC;

-- 插入记录
INSERT INTO `teacher` (`id`, `name`, `create_id`, `area_id`, `age`) VALUES (1, '张老师', 1, 1, 26);
INSERT INTO `teacher` (`id`, `name`, `create_id`, `area_id`, `age`) VALUES (2, '王老师', 2, 3, 24);

-- 修改teacher_statics表 增加create_id、update_id字段
ALTER TABLE `teacher_statistics` ADD COLUMN `create_id` int DEFAULT NULL;
ALTER TABLE `teacher_statistics` ADD COLUMN `update_id` int DEFAULT NULL;

-- 修改teacher_statics表 记录
UPDATE `teacher_statistics` SET `create_id` = '1' WHERE `id` <= 13;
UPDATE `teacher_statistics` SET `create_id` = '2' WHERE `id` > 13;
UPDATE `teacher_statistics` SET `create_id` = '6' WHERE `id` = 5;
UPDATE `teacher_statistics` SET `create_id` = '7' WHERE `id` = 4;

-- 修改sys_user表，增加data_scope字段
ALTER TABLE `sys_user` ADD COLUMN `data_scope` int(2) DEFAULT NULL COMMENT '数据权限(详见DataScopeEnum) 0：全部; 1：仅本部门数据; 2：本部门及以下数据; 3：仅本人数据; 4：自定义数据;';

-- 修改sys_user表，更新历史用户data_scope初始值
UPDATE `sys_user` SET `data_scope` = '0' WHERE `id` = 1;
UPDATE `sys_user` SET `data_scope` = '0' WHERE `id` = 2;

-- 修改sys_user表，create_time、update_time字段
ALTER TABLE `sys_user`
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间';