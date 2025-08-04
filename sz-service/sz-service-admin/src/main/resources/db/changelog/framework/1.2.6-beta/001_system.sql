--liquibase formatted sql

--changeset 升职哦（sz）:20250717_1321
--comment: 新增部门角色关联数据库表
CREATE TABLE IF NOT EXISTS `sys_dept_role`  (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门-角色关联ID',
                                  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
                                  `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色ID(sys_role_id)',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统部门-角色关联表' ROW_FORMAT = DYNAMIC;