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
ALTER TABLE `sys_user` ADD COLUMN `data_scope_cd` int(2) DEFAULT NULL COMMENT '数据权限，data_scope';

-- 修改sys_user表，更新历史用户data_scope初始值
UPDATE `sys_user` SET `data_scope_cd` = '1006001' WHERE `id` = 1;
UPDATE `sys_user` SET `data_scope_cd` = '1006001' WHERE `id` = 2;

-- 修改sys_user表，create_time、update_time字段
ALTER TABLE `sys_user`
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间';

-- 修改sys_dept表，添加data_scope字段
ALTER TABLE `sys_dept` ADD COLUMN `data_scope_cd` int(2) DEFAULT NULL COMMENT '数据权限，data_scope';

-- 修改sys_dept表，更新历史用户data_scope初始值
UPDATE `sys_dept` SET `data_scope_cd` = '1006001';

-- 新增系统参数 sys.dept.dataScopeDefault
INSERT INTO `sz_admin_preview`.`sys_config` (`id`, `config_name`, `config_key`, `config_value`, `is_lock`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES (5, '默认部门数据权限', 'sys.dept.dataScopeDefault', '3', 'T', 1, '2024-06-26 17:05:31', NULL, NULL, '数据权限可选项（0：全部; 1：仅本部门; 2：本部门及以下; 3：仅本人;）');

-- 新增自定义数据权限
CREATE TABLE `sys_data_scope` (
                                  `id` INT NOT NULL,
                                  `relation_type_cd` VARCHAR (10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联类型，data_scope_relation_type',
                                  `relation_id` INT DEFAULT NULL COMMENT '关联表id',
                                  `options` JSON DEFAULT NULL COMMENT '关联内容',
                                  `create_id` INT DEFAULT NULL,
                                  `create_time` DATETIME DEFAULT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义数据权限表' ROW_FORMAT=DYNAMIC;

-- 新增字典
INSERT INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006, '数据权限', 'data_scope', 'T', 'T', 'F', '', '2024-06-25 18:54:21', '2024-06-25 19:12:46', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1007, '数据权限关联类型', 'data_scope_relation_type', 'T', 'T', 'F', '自定义数据权限的关联类型', '2024-06-25 18:55:37', '2024-06-25 19:12:48', NULL, NULL, NULL, NULL);

INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006001, 1006, '全部', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:55:48', '2024-06-25 19:11:28', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006002, 1006, '仅本部门', '', 2, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:56:57', '2024-06-25 19:11:29', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006003, 1006, '本部门及以下', '', 3, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:22', '2024-06-25 19:11:32', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006004, 1006, '仅本人', '', 4, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:57:57', '2024-06-25 19:11:34', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1006005, 1006, '自定义', '', 5, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:58:11', '2024-06-25 19:11:36', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1007001, 1007, '部门权限', '', 1, 'primary', '', 'T', 'T', 'F', '2024-06-25 18:59:00', '2024-06-25 19:11:38', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1007002, 1007, '个人权限', '', 2, 'primary', '个人权限高优先级', 'T', 'T', 'F', '2024-06-25 18:59:27', '2024-06-25 19:11:41', NULL, NULL, NULL, NULL);
