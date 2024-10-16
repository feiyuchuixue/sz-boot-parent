-- 【结构性变更】 修改关联自增id相关字段类型为bigint
-- #代码生成
ALTER TABLE `generator_table`
    MODIFY COLUMN `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

ALTER TABLE `generator_table_column`
    MODIFY COLUMN `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    MODIFY COLUMN `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

-- #小程序用户
ALTER TABLE `mini_user` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '小程序用户ID';

-- #参数配置
ALTER TABLE `sys_config`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数配置ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间';

-- #数据角色
ALTER TABLE `sys_data_role`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据角色ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

ALTER TABLE `sys_data_role_menu`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    MODIFY COLUMN `role_id` bigint DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）';

ALTER TABLE `sys_data_role_relation`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    MODIFY COLUMN `role_id` bigint DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）',
    MODIFY COLUMN `relation_id` bigint DEFAULT NULL COMMENT '关联表id，联动relation_type_cd（部门ID或个人ID）';

-- #部门
ALTER TABLE `sys_dept`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    MODIFY COLUMN `pid` bigint NOT NULL COMMENT '父级ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

ALTER TABLE `sys_dept_closure`
    MODIFY COLUMN `ancestor_id` bigint NOT NULL COMMENT '祖先节点ID',
    MODIFY COLUMN `descendant_id` bigint NOT NULL COMMENT '后代节点ID';

ALTER TABLE `sys_dept_leader`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门领导人ID',
    MODIFY COLUMN `leader_id` bigint NOT NULL COMMENT '领导人ID（sys_user_id）';

-- #字典
ALTER TABLE `sys_dict`
    MODIFY COLUMN `id` bigint NOT NULL COMMENT '字典ID(规则)',
    MODIFY COLUMN `sys_dict_type_id` bigint NOT NULL COMMENT '关联sys_dict_type ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
    MODIFY COLUMN `delete_id` bigint DEFAULT NULL COMMENT '删除人ID',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `delete_time` datetime DEFAULT NULL COMMENT '删除时间';

ALTER TABLE `sys_dict_type`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
    MODIFY COLUMN `delete_id` bigint DEFAULT NULL COMMENT '删除人ID',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `delete_time` datetime DEFAULT NULL COMMENT '删除时间';

-- #文件
ALTER TABLE `sys_file`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID';

-- #菜单
UPDATE sys_menu
SET
    create_id = CASE WHEN create_id = '' THEN NULL ELSE create_id END,
    update_id = CASE WHEN update_id = '' THEN NULL ELSE update_id END,
    delete_id = CASE WHEN delete_id = '' THEN NULL ELSE delete_id END
WHERE
    create_id = '' OR update_id = '' OR delete_id = '';

ALTER TABLE `sys_menu`
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
    MODIFY COLUMN `delete_id` bigint DEFAULT NULL COMMENT '删除人ID',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    MODIFY COLUMN `delete_time` datetime DEFAULT NULL COMMENT '删除时间';

-- #角色
ALTER TABLE `sys_role`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
    MODIFY COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间';

ALTER TABLE `sys_role_menu`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '系统角色-菜单ID',
    MODIFY COLUMN `role_id` bigint DEFAULT NULL COMMENT 'sys_role_id （角色表）';

-- #用户
ALTER TABLE `sys_user`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';

ALTER TABLE `sys_user_data_role`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-数据角色关联ID',
    MODIFY COLUMN `role_id` bigint DEFAULT NULL COMMENT '数据角色ID (sys_data_role_id)',
    MODIFY COLUMN `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)';

ALTER TABLE `sys_user_dept`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-部门关系ID',
    MODIFY COLUMN `dept_id` bigint DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
    MODIFY COLUMN `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)';

ALTER TABLE `sys_user_role`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-角色关联ID',
    MODIFY COLUMN `role_id` bigint DEFAULT NULL COMMENT '角色ID (sys_role_id)',
    MODIFY COLUMN `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)';

-- #教师统计（演示）
ALTER TABLE `teacher_statistics`
    MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    MODIFY COLUMN `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
    MODIFY COLUMN `update_id` bigint DEFAULT NULL COMMENT '更新人ID';