/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : 127.0.0.1:3306
 Source Schema         : sz_admin_test

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 30/04/2024 10:56:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for generator_table
-- ----------------------------
DROP TABLE IF EXISTS `generator_table`;
CREATE TABLE `generator_table`  (
  `table_id` int NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名称',
  `table_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表描述',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实体类名称',
  `camel_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'camel实体类名称',
  `tpl_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用的模版',
  `package_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成作者名',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成方式(0 zip压缩包；1 自定义路径)',
  `options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他参数',
  `parent_menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级菜单id',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成路径',
  `path_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'api生成路径',
  `path_web` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'web生成路径',
  `menu_init_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否自动创建菜单路由（1 是）',
  `btn_permission_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否自动创建按钮权限 (1 是)',
  `has_import` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否支持导入(1 是)',
  `has_export` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否支持导出(1 是)',
  `generate_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'all' COMMENT '生成类型（全量：all，后端：server，接口：service）',
  `create_id` int NULL DEFAULT NULL COMMENT '创建者编号',
  `update_id` int NULL DEFAULT NULL COMMENT '更新者编号',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of generator_table
-- ----------------------------
INSERT INTO `generator_table` VALUES (6, 'teacher_statistics', '教师统计总览表', 'TeacherStatistics', 'teacherStatistics', 'crud', 'com.sz.admin', 'teacher', 'teacherStatistics', '教师统计', 'sz-admin', '1', '', '0', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', 'E:\\code\\Gitlab\\sz-framework\\sz-admin', '1', '1', '1', '1', 'all', 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table` VALUES (14, 'sys_dept', '部门表', 'SysDept', 'sysDept', 'crud', 'com.sz.admin', 'sysdept', 'sysDept', '部门管理', 'sz-admin', '1', '', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', 'E:\\code\\Gitlab\\sz-framework\\sz-admin', '1', '1', '0', '0', 'all', 1, 1, '2024-03-20 16:25:31', '2024-03-20 16:40:44');
INSERT INTO `generator_table` VALUES (16, 'sys_user_dept', '用户-部门关系表', 'SysUserDept', 'sysUserDept', 'crud', 'com.sz.admin', 'sysuser', 'sysUserDept', '用户-部门关系表', 'sz-admin', '0', '', '0', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', '', '1', '1', '0', '0', 'all', 1, 1, '2024-03-26 16:19:33', '2024-04-02 09:50:16');
INSERT INTO `generator_table` VALUES (17, 'sys_dept_leader', '部门领导人表', 'SysDeptLeader', 'sysDeptLeader', 'crud', 'com.sz.admin', 'sysdept', 'sysDeptLeader', '负责人管理', 'sz-admin', '1', '', '0', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', '', '1', '1', '0', '0', 'service', 1, 1, '2024-03-26 16:19:44', '2024-03-26 17:34:47');
INSERT INTO `generator_table` VALUES (19, 'sys_dept_closure', '部门祖籍关系表', 'SysDeptClosure', 'sysDeptClosure', 'crud', 'com.sz.admin', 'sysdept', 'sysDeptClosure', '部门祖籍关系', 'sz-admin', '1', '', '8354d626cc65487594a7c38e98de1bad', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', '', '1', '1', '0', '0', 'all', 1, 1, '2024-03-28 16:41:33', '2024-03-28 16:43:07');
INSERT INTO `generator_table` VALUES (21, 'mini_program', '小程序管理', 'MiniProgram', 'miniProgram', 'crud', 'com.sz.admin', 'miniprogram', 'miniProgram', '小程序管理', 'sz-admin', '1', '', '0', '/', '/Users/zhyuan/code/sz-boot-parent/sz-service/sz-service-admin', '/Users/zhyuan/code/sz-admin', '1', '1', '0', '0', 'all', 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table` VALUES (22, 'mini_program_carousel', '小程序-轮播图', 'MiniProgramCarousel', 'miniProgramCarousel', 'crud', 'com.sz.admin', 'miniprogram', 'miniProgramCarousel', '轮播图', 'sz-admin', '1', '', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/', '/Users/zhyuan/code/sz-boot-parent/sz-service/sz-service-admin', '/Users/zhyuan/code/sz-admin', '1', '1', '0', '0', 'all', 1, 1, '2024-04-09 15:54:29', '2024-04-09 17:04:09');
INSERT INTO `generator_table` VALUES (23, 'mini_program_grid', '小程序-金刚区', 'MiniProgramGrid', 'miniProgramGrid', 'crud', 'com.sz.admin', 'miniprogram', 'miniProgramGrid', '金刚区', 'sz-admin', '1', '', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/', '/Users/zhyuan/code/sz-boot-parent/sz-service/sz-service-admin', '/Users/zhyuan/code/sz-admin', '1', '1', '0', '0', 'all', 1, 1, '2024-04-09 15:54:29', '2024-04-09 17:04:18');
INSERT INTO `generator_table` VALUES (24, 'sys_user', '系统用户表', 'SysUser', 'sysUser', 'crud', 'com.sz.admin', 'sysuser', 'sysUser', '系统用户表', 'sz-admin', '0', NULL, NULL, '/', '/Users/zhyuan/code/sz-boot-parent/sz-service/sz-service-admin', '', '1', '1', '1', '1', 'all', 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table` VALUES (28, 'sys_organization', '系统组织表', 'SysOrganization', 'sysOrganization', 'crud', 'com.sz.admin', 'sysorganization', 'sysOrganization', '系统组织表', 'sz-admin', '0', '', '', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', '', '1', '1', '1', '1', 'all', 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table` VALUES (29, 'program_service', '应用服务', 'ProgramService', 'programService', 'crud', 'com.sz.admin', 'programservice', 'programService', '应用服务', 'sz-admin', '0', '', '0', '/', 'E:\\code\\Gitlab\\sz-framework\\sz-boot-parent_tags-flex\\sz-boot-parent\\sz-service\\sz-service-admin', 'E:\\code\\Gitlab\\sz-framework\\sz-admin', '1', '1', '1', '1', 'all', 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');

-- ----------------------------
-- Table structure for generator_table_column
-- ----------------------------
DROP TABLE IF EXISTS `generator_table_column`;
CREATE TABLE `generator_table_column`  (
  `column_id` int NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` int NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `search_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '搜索类型',
  `ts_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ts类型',
  `java_type_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'java类型包名',
  `java_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `up_camel_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'get开头的驼峰字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否查询字段（1是）',
  `is_import` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否导入字段(1 是)',
  `is_export` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否导出字段(1 是)',
  `is_autofill` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否自动填充(1 是)',
  `is_unique_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否进行唯一校验(1 是)',
  `autofill_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自动填充类型',
  `query_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询方式',
  `html_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型',
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `is_logic_del` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否逻辑删除(1 是)',
  `options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他设置',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_id` int NULL DEFAULT NULL COMMENT '创建者编号',
  `update_id` int NULL DEFAULT NULL COMMENT '更新者编号',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 301 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table Column' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of generator_table_column
-- ----------------------------
INSERT INTO `generator_table_column` VALUES (76, 6, 'id', 'id', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table_column` VALUES (77, 6, 'year', '统计年限', 'varchar(4)', 'String', 'input', 'string', '', 'year', 'Year', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 2, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table_column` VALUES (78, 6, 'month', '统计月份', 'varchar(2)', 'String', 'input', 'string', '', 'month', 'Month', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 3, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table_column` VALUES (79, 6, 'during_time', '统计年月', 'varchar(10)', 'String', 'date-picker', 'string', '', 'duringTime', 'DuringTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 4, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table_column` VALUES (80, 6, 'teacher_id', '教师id', 'varchar(32)', 'String', 'input', 'string', '', 'teacherId', 'TeacherId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 5, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:39');
INSERT INTO `generator_table_column` VALUES (81, 6, 'teacher_common_type', '讲师区分类型', 'int', 'Integer', 'select', 'number', '', 'teacherCommonType', 'TeacherCommonType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'select', 'account_status', '0', '', 6, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (82, 6, 'total_teaching', '授课总数', 'int', 'Integer', 'input', 'number', '', 'totalTeaching', 'TotalTeaching', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input-number', '', '0', '', 7, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (83, 6, 'total_class_count', '服务班次数', 'int', 'Integer', 'input', 'number', '', 'totalClassCount', 'TotalClassCount', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input-number', '', '0', '', 8, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (84, 6, 'total_hours', '课时总数', 'decimal(10,2)', 'BigDecimal', 'input', 'number', 'java.math.BigDecimal', 'totalHours', 'TotalHours', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input-number', '', '0', '', 9, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (85, 6, 'check_status', '核对状态', 'int', 'Integer', 'select', 'number', '', 'checkStatus', 'CheckStatus', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'select', 'account_status', '0', '', 10, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (86, 6, 'check_time', '核对时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'checkTime', 'CheckTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'datetime', '', '0', '', 11, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (87, 6, 'create_time', '生成时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 12, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (88, 6, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 13, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (89, 6, 'last_sync_time', '最近一次同步时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'lastSyncTime', 'LastSyncTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'datetime', '', '0', '', 14, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (90, 6, 'remark', '备注', 'varchar(255)', 'String', 'input', 'string', '', 'remark', 'Remark', '0', '0', '0', '1', '1', '1', '0', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 15, 1, 1, '2024-01-10 13:32:54', '2024-03-15 10:15:40');
INSERT INTO `generator_table_column` VALUES (165, 14, 'id', 'id', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:44');
INSERT INTO `generator_table_column` VALUES (166, 14, 'name', '部门名称', 'varchar(32)', 'String', 'input', 'string', '', 'name', 'Name', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'LIKE', 'input', '', '0', '', 2, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:44');
INSERT INTO `generator_table_column` VALUES (167, 14, 'pid', '父级id', 'int', 'Integer', 'input', 'number', '', 'pid', 'Pid', '0', '0', '1', '1', '1', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 3, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:44');
INSERT INTO `generator_table_column` VALUES (168, 14, 'deep', '层级', 'int', 'Integer', 'input', 'number', '', 'deep', 'Deep', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 4, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:44');
INSERT INTO `generator_table_column` VALUES (169, 14, 'sort', '排序', 'int', 'Integer', 'input', 'number', '', 'sort', 'Sort', '0', '0', '0', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 5, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (170, 14, 'has_children', '是否有子级', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'hasChildren', 'HasChildren', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 6, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (171, 14, 'is_lock', '是否锁定', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'isLock', 'IsLock', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (172, 14, 'del_flag', '删除标识', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 8, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (173, 14, 'remark', '备注', 'varchar(64)', 'String', 'input', 'string', '', 'remark', 'Remark', '0', '0', '0', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 9, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (174, 14, 'create_id', '创建人', 'int', 'Integer', 'input', 'number', '', 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', '', 10, 1, 1, '2024-03-20 16:25:32', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (175, 14, 'update_id', '更新人', 'int', 'Integer', 'input', 'number', '', 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', '', 11, 1, 1, '2024-03-20 16:25:33', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (176, 14, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 12, 1, 1, '2024-03-20 16:25:33', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (177, 14, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 13, 1, 1, '2024-03-20 16:25:33', '2024-03-20 16:40:45');
INSERT INTO `generator_table_column` VALUES (181, 16, 'id', '', 'int(10) unsigned zerofill', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-03-26 16:19:34', '2024-04-02 09:50:16');
INSERT INTO `generator_table_column` VALUES (182, 16, 'dept_id', 'sys_dept_id', 'int', 'Integer', 'input', 'number', '', 'deptId', 'DeptId', '0', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 2, 1, 1, '2024-03-26 16:19:34', '2024-04-02 09:50:16');
INSERT INTO `generator_table_column` VALUES (183, 16, 'user_id', 'sys_user_id', 'int', 'Integer', 'input', 'number', '', 'userId', 'UserId', '0', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 3, 1, 1, '2024-03-26 16:19:34', '2024-04-02 09:50:16');
INSERT INTO `generator_table_column` VALUES (184, 17, 'id', '', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-03-26 16:19:44', '2024-03-26 17:34:47');
INSERT INTO `generator_table_column` VALUES (185, 17, 'dept_id', '', 'int', 'Integer', 'input', 'number', '', 'deptId', 'DeptId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 2, 1, 1, '2024-03-26 16:19:44', '2024-03-26 17:34:47');
INSERT INTO `generator_table_column` VALUES (186, 17, 'leader_id', 'sys_user_id', 'int', 'Integer', 'input', 'number', '', 'leaderId', 'LeaderId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 3, 1, 1, '2024-03-26 16:19:44', '2024-03-26 17:34:48');
INSERT INTO `generator_table_column` VALUES (190, 19, 'ancestor_id', '祖先节点ID', 'int', 'Integer', 'input', 'number', '', 'ancestorId', 'AncestorId', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-03-28 16:41:34', '2024-03-28 16:43:07');
INSERT INTO `generator_table_column` VALUES (191, 19, 'descendant_id', '后代节点ID', 'int', 'Integer', 'input', 'number', '', 'descendantId', 'DescendantId', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 2, 1, 1, '2024-03-28 16:41:34', '2024-03-28 16:43:07');
INSERT INTO `generator_table_column` VALUES (192, 19, 'depth', '祖先节点到后代节点的距离', 'int', 'Integer', 'input', 'number', '', 'depth', 'Depth', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 3, 1, 1, '2024-03-28 16:41:34', '2024-03-28 16:43:08');
INSERT INTO `generator_table_column` VALUES (202, 21, 'id', '', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '1', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (203, 21, 'program_id', '小程序id', 'varchar(255)', 'String', 'input', 'string', '', 'programId', 'ProgramId', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 2, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (204, 21, 'program_name', '小程序名称', 'varchar(255)', 'String', 'input', 'string', '', 'programName', 'ProgramName', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'LIKE', 'input', '', '0', '', 3, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (205, 21, 'status', '使用状态', 'char(1)', 'String', 'select', 'string', '', 'status', 'Status', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'radio', 'mini_program_status', '0', '', 4, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (206, 21, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 5, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (207, 21, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 6, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:39');
INSERT INTO `generator_table_column` VALUES (208, 21, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:40');
INSERT INTO `generator_table_column` VALUES (209, 21, 'create_id', '', 'int', 'Integer', 'input', 'number', '', 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', '', 8, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:40');
INSERT INTO `generator_table_column` VALUES (210, 21, 'update_id', '', 'int', 'Integer', 'input', 'number', '', 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', '', 9, 1, 1, '2024-04-08 15:02:24', '2024-04-08 16:54:40');
INSERT INTO `generator_table_column` VALUES (211, 22, 'id', '', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '1', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:09');
INSERT INTO `generator_table_column` VALUES (212, 22, 'program_id', '小程序ID', 'varchar(255)', 'String', 'input', 'string', '', 'programId', 'ProgramId', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 2, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:09');
INSERT INTO `generator_table_column` VALUES (213, 22, 'title', '标题', 'varchar(255)', 'String', 'input', 'string', '', 'title', 'Title', '0', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 3, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (214, 22, 'pic_url', '图片路径', 'text', 'String', 'input', 'string', '', 'picUrl', 'PicUrl', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 4, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (215, 22, 'path', '菜单路由', 'varchar(255)', 'String', 'input', 'string', '', 'path', 'Path', '0', '0', '0', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input', 'mini_program_status', '0', '', 5, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (216, 22, 'status', '状态', 'varchar(255)', 'String', 'select', 'string', '', 'status', 'Status', '0', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'radio', '', '0', '', 6, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (217, 22, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-04-09 15:54:30', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (218, 22, 'create_id', '', 'int', 'Integer', 'input', 'number', '', 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', '', 8, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (219, 22, 'update_id', '', 'int', 'Integer', 'input', 'number', '', 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', '', 9, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (220, 22, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 10, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (221, 22, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 11, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:10');
INSERT INTO `generator_table_column` VALUES (222, 23, 'id', '', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '1', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:18');
INSERT INTO `generator_table_column` VALUES (223, 23, 'program_id', '小程序ID', 'varchar(255)', 'String', 'input', 'string', '', 'programId', 'ProgramId', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 2, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:18');
INSERT INTO `generator_table_column` VALUES (224, 23, 'title', '标题', 'varchar(255)', 'String', 'input', 'string', '', 'title', 'Title', '0', '0', '1', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 3, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:18');
INSERT INTO `generator_table_column` VALUES (225, 23, 'pic_url', '图片路径', 'varchar(255)', 'String', 'input', 'string', '', 'picUrl', 'PicUrl', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 4, 1, 1, '2024-04-09 15:54:31', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (226, 23, 'path', '菜单路由', 'varchar(255)', 'String', 'input', 'string', '', 'path', 'Path', '0', '0', '0', '1', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input', '', '0', '', 5, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (227, 23, 'status', '状态', 'varchar(255)', 'String', 'select', 'string', '', 'status', 'Status', '0', '0', '0', '1', '1', '1', '1', '0', '0', '0', '0', '', 'EQ', 'radio', 'mini_program_status', '0', '', 6, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (228, 23, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (229, 23, 'create_id', '', 'int', 'Integer', 'input', 'number', '', 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', '', 8, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (230, 23, 'update_id', '', 'int', 'Integer', 'input', 'number', '', 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', '', 9, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (231, 23, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 10, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (232, 23, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 11, 1, 1, '2024-04-09 15:54:32', '2024-04-09 17:04:19');
INSERT INTO `generator_table_column` VALUES (233, 24, 'id', 'id', 'int', 'Integer', 'input', 'number', NULL, 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (234, 24, 'username', '用户名', 'varchar(32)', 'String', 'input', 'string', NULL, 'username', 'Username', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 2, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (235, 24, 'pwd', '密码', 'varchar(512)', 'String', 'input', 'string', NULL, 'pwd', 'Pwd', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'textarea', '', '0', NULL, 3, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (236, 24, 'phone', '手机号', 'varchar(20)', 'String', 'input', 'string', NULL, 'phone', 'Phone', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (237, 24, 'nickname', '昵称', 'varchar(32)', 'String', 'input', 'string', NULL, 'nickname', 'Nickname', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'LIKE', 'input', '', '0', NULL, 5, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (238, 24, 'sex', '性别(0 未知 1 男 2 女)', 'tinyint(1)', 'Integer', 'select', 'number', NULL, 'sex', 'Sex', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 6, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (239, 24, 'birthday', '生日', 'varchar(255)', 'String', 'input', 'string', NULL, 'birthday', 'Birthday', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 7, 1, NULL, '2024-04-09 17:25:21', NULL);
INSERT INTO `generator_table_column` VALUES (240, 24, 'logo', '头像地址', 'varchar(255)', 'String', 'input', 'string', NULL, 'logo', 'Logo', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 8, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (241, 24, 'age', '年龄，--废弃，以生日为主', 'int', 'Integer', 'input', 'number', NULL, 'age', 'Age', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 9, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (242, 24, 'id_card', '身份证', 'varchar(32)', 'String', 'input', 'string', NULL, 'idCard', 'IdCard', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 10, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (243, 24, 'email', '邮箱地址', 'varchar(256)', 'String', 'input', 'string', NULL, 'email', 'Email', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 11, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (244, 24, 'account_status_cd', '账户状态 (如 冻结；禁言；正常。 关联字典表account_status)', 'varchar(10)', 'String', 'select', 'string', NULL, 'accountStatusCd', 'AccountStatusCd', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 12, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (245, 24, 'user_tag_cd', '标签（自定义关联到字典表）', 'varchar(255)', 'String', 'select', 'string', NULL, 'userTagCd', 'UserTagCd', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 13, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (246, 24, 'last_login_time', '最近一次登录时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'lastLoginTime', 'LastLoginTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'datetime', '', '0', NULL, 14, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (247, 24, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', NULL, 15, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (248, 24, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', NULL, 16, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (249, 24, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', NULL, 'delFlag', 'DelFlag', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', NULL, 'EQ', NULL, '', '0', NULL, 17, 1, NULL, '2024-04-09 17:25:22', NULL);
INSERT INTO `generator_table_column` VALUES (250, 24, 'create_id', '', 'int', 'Integer', 'input', 'number', NULL, 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', NULL, 18, 1, NULL, '2024-04-09 17:25:23', NULL);
INSERT INTO `generator_table_column` VALUES (251, 24, 'update_id', '', 'int', 'Integer', 'input', 'number', NULL, 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', NULL, 19, 1, NULL, '2024-04-09 17:25:23', NULL);
INSERT INTO `generator_table_column` VALUES (283, 28, 'id', '组织id', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (284, 28, 'org_pid', '上级组织id,没有为0', 'int', 'Integer', 'input', 'number', '', 'orgPid', 'OrgPid', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input-number', '', '0', '', 2, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (285, 28, 'org_name', '组织名称', 'varchar(255)', 'String', 'input', 'string', '', 'orgName', 'OrgName', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'LIKE', 'input', '', '0', '', 3, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (286, 28, 'org_type_cd', '组织类型 （字典表org_type）', 'varchar(255)', 'String', 'select', 'string', '', 'orgTypeCd', 'OrgTypeCd', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'select', '', '0', '', 4, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (287, 28, 'create_time', '', 'timestamp', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 5, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (288, 28, 'update_time', '', 'timestamp', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 6, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (289, 28, 'del_flag', '', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-04-16 15:58:06', '2024-04-19 20:12:33');
INSERT INTO `generator_table_column` VALUES (290, 29, 'id', '', 'int', 'Integer', 'input', 'number', '', 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', '', 'EQ', 'input-number', '', '0', '', 1, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (291, 29, 'type_id', '服务类型ID', 'int', 'Integer', 'select', 'number', '', 'typeId', 'TypeId', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 2, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (292, 29, 'service_name', '服务名称', 'varchar(255)', 'String', 'input', 'string', '', 'serviceName', 'ServiceName', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'LIKE', 'input', '', '0', '', 3, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (293, 29, 'pic_url', '图标路径', 'varchar(255)', 'String', 'input', 'string', '', 'picUrl', 'PicUrl', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 4, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (294, 29, 'path', '路由', 'varchar(255)', 'String', 'input', 'string', '', 'path', 'Path', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 5, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (295, 29, 'status', '状态（字典 - 应用状态(mini_program_status)）', 'varchar(255)', 'String', 'select', 'string', '', 'status', 'Status', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', '', 'EQ', 'input', '', '0', '', 6, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (296, 29, 'del_flag', '是否删除', 'enum(\'T\',\'F\')', 'String', 'input', 'string', '', 'delFlag', 'DelFlag', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', 'EQ', '', '', '0', '', 7, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (297, 29, 'create_id', '', 'int', 'Integer', 'input', 'number', '', 'createId', 'CreateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'input-number', '', '0', '', 8, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (298, 29, 'update_id', '', 'int', 'Integer', 'input', 'number', '', 'updateId', 'UpdateId', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'input-number', '', '0', '', 9, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (299, 29, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', '', 10, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');
INSERT INTO `generator_table_column` VALUES (300, 29, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', '', 11, 1, 1, '2024-04-29 09:13:38', '2024-04-29 09:16:33');

-- ----------------------------
-- Table structure for mini_user
-- ----------------------------
DROP TABLE IF EXISTS `mini_user`;
CREATE TABLE `mini_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `sys_user_id` int NULL DEFAULT NULL COMMENT '关联的系统用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小程序用户的唯一标识',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公众号的唯一标识',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `subscribe` tinyint NULL DEFAULT NULL COMMENT '是否订阅公众号（1是0否）',
  `sex` tinyint NULL DEFAULT NULL COMMENT '性别，0-未知 1-男性，2-女性',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mini_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`  (
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端key',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端秘钥',
  `grant_type_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'password' COMMENT '授权类型',
  `device_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int NOT NULL DEFAULT 1800 COMMENT 'token活跃超时时间',
  `timeout` int NOT NULL DEFAULT 604800 COMMENT 'token固定超时',
  `client_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '状态（正常 禁用）',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'F' COMMENT '删除标志',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统授权表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_client
-- ----------------------------
INSERT INTO `sys_client` VALUES ('195da9fcce574852b850068771cde034', 'sz-admin', '839ce050d3814949af9b2e1f815bc620', 'password', '1004001', 86400, 604800, '1003001', 'F', NULL, 1, '2024-01-22 13:43:51', 1, '2024-04-28 09:43:59', 'web端client1');
INSERT INTO `sys_client` VALUES ('86ec9c3c05ee47d98b546b8df4dbbfeb', '小程序', 'b0f84a5e1519439cb1eeeef391de7574', 'applet', '1004002', 86400, 604800, '1003001', 'F', NULL, 1, '2024-04-28 09:43:09', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `config_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数名',
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数key',
  `config_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数value',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否锁定',
  `create_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主体名称', 'sys.dept.entityName', 'xx公司', 'T', 1, '2024-03-22 10:42:46', 1, '2024-04-18 11:06:35', '公司主体名称');
INSERT INTO `sys_config` VALUES (2, '系统账户-初始密码', 'sys.user.initPwd', 'sz123456', 'T', 1, '2024-04-10 09:56:58', 1, '2024-04-10 10:13:28', '');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `pid` int NOT NULL COMMENT '父级id',
  `deep` int NULL DEFAULT NULL COMMENT '层级',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `has_children` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否有子级',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` int NULL DEFAULT NULL COMMENT '创建人',
  `update_id` int NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '研发部', 0, 1, 100, 'F', 'F', 'F', '是打发斯蒂芬斯蒂芬沈大高速大哥', 1, 1, '2024-04-01 14:29:17', '2024-04-09 10:02:52');
INSERT INTO `sys_dept` VALUES (2, '财务部', 0, 1, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-04-01 14:29:26', NULL);
INSERT INTO `sys_dept` VALUES (3, '销售部', 0, 1, 300, 'F', 'F', 'F', NULL, 1, NULL, '2024-04-01 14:29:32', NULL);
INSERT INTO `sys_dept` VALUES (4, '工程部', 0, 1, 400, 'F', 'F', 'F', '', 1, 1, '2024-04-01 14:29:43', '2024-04-07 16:20:20');
INSERT INTO `sys_dept` VALUES (5, '产品部', 0, 1, 500, 'T', 'F', 'F', '', 1, 1, '2024-04-01 14:29:54', '2024-04-09 10:00:53');
INSERT INTO `sys_dept` VALUES (6, '售前部', 5, 5, 100, 'T', 'F', 'F', '', 1, 1, '2024-04-01 14:30:03', '2024-04-09 10:04:34');
INSERT INTO `sys_dept` VALUES (7, '设计部', 5, 2, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-04-01 14:30:11', NULL);
INSERT INTO `sys_dept` VALUES (9, 'abc', 0, 1, 600, 'T', 'F', 'T', NULL, 1, 1, '2024-04-30 09:11:52', '2024-04-30 09:16:50');
INSERT INTO `sys_dept` VALUES (10, 'edf', 9, 2, 100, 'F', 'F', 'F', NULL, 1, NULL, '2024-04-30 09:16:50', NULL);

-- ----------------------------
-- Table structure for sys_dept_closure
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_closure`;
CREATE TABLE `sys_dept_closure`  (
  `ancestor_id` int NOT NULL COMMENT '祖先节点ID',
  `descendant_id` int NOT NULL COMMENT '后代节点ID',
  `depth` int NOT NULL COMMENT '祖先节点到后代节点的距离'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门祖籍关系表（闭包表closure_table）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept_closure
-- ----------------------------
INSERT INTO `sys_dept_closure` VALUES (1, 1, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 1, 1);
INSERT INTO `sys_dept_closure` VALUES (2, 2, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 2, 1);
INSERT INTO `sys_dept_closure` VALUES (3, 3, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 3, 1);
INSERT INTO `sys_dept_closure` VALUES (4, 4, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 4, 1);
INSERT INTO `sys_dept_closure` VALUES (5, 5, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 5, 1);
INSERT INTO `sys_dept_closure` VALUES (6, 6, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 7, 2);
INSERT INTO `sys_dept_closure` VALUES (7, 7, 0);
INSERT INTO `sys_dept_closure` VALUES (5, 7, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 6, 2);
INSERT INTO `sys_dept_closure` VALUES (5, 6, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 8, 1);
INSERT INTO `sys_dept_closure` VALUES (9, 9, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 9, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 10, 2);
INSERT INTO `sys_dept_closure` VALUES (10, 10, 0);
INSERT INTO `sys_dept_closure` VALUES (9, 10, 1);

-- ----------------------------
-- Table structure for sys_dept_leader
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_leader`;
CREATE TABLE `sys_dept_leader`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `dept_id` int NULL DEFAULT NULL,
  `leader_id` int NULL DEFAULT NULL COMMENT 'sys_user_id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门领导人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept_leader
-- ----------------------------
INSERT INTO `sys_dept_leader` VALUES (1, 4, 17);
INSERT INTO `sys_dept_leader` VALUES (2, 4, 18);
INSERT INTO `sys_dept_leader` VALUES (3, 5, 19);
INSERT INTO `sys_dept_leader` VALUES (4, 6, 21);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` int NOT NULL COMMENT '字典id(规则)',
  `sys_dict_type_id` int NOT NULL COMMENT '关联sys_dict_type id',
  `code_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名称',
  `alias` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典（Key）别名，某些情况下如果不想使用id作为key',
  `sort` int NOT NULL COMMENT '排序(正序)',
  `callback_show_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回显样式',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
  `is_show` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'T' COMMENT '是否展示',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `delete_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1000001, 1000, '正常', '', 1, 'success', '', 'F', 'T', 'F', '2023-08-20 16:30:23', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1000002, 1000, '禁用', '', 2, 'info', '', 'F', 'T', 'F', '2023-08-20 16:33:45', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1000003, 1000, '禁言', '', 3, 'info', '', 'F', 'T', 'F', '2023-08-20 16:33:54', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1001001, 1001, '测试用户', '', 0, 'info', '', 'F', 'T', 'F', '2023-08-20 16:38:58', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1001002, 1001, '超级管理员', '', 0, 'info', '', 'F', 'T', 'F', '2023-08-20 16:39:05', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1001003, 1001, '普通用户', '', 0, 'info', '', 'F', 'T', 'F', '2023-08-20 16:39:11', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1002001, 1002, '目录', '', 1, 'warning', '', 'F', 'T', 'F', '2023-08-21 11:23:05', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1002002, 1002, '菜单', '', 2, 'success', '', 'F', 'T', 'F', '2023-08-21 11:23:17', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1002003, 1002, '按钮', '', 3, 'danger', '', 'F', 'T', 'F', '2023-08-21 11:23:22', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1003001, 1003, '正常', '', 1, 'success', '', 'F', 'T', 'F', '2024-01-22 09:44:52', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1003002, 1003, '禁用', '', 2, 'info', '', 'F', 'T', 'F', '2024-01-22 09:45:16', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1004001, 1004, 'PC', '', 1, 'success', 'pc端', 'F', 'T', 'F', '2024-01-22 10:03:19', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1004002, 1004, '小程序', '', 2, 'success', '小程序端', 'F', 'T', 'F', '2024-01-22 10:03:47', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1004003, 1004, 'Androd', '', 3, 'success', '', 'F', 'T', 'F', '2024-01-22 10:04:35', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1004004, 1004, 'IOS', '', 4, 'success', '', 'F', 'T', 'F', '2024-01-22 10:04:42', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1005001, 1005, '密码认证', 'password', 100, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:32', '2024-04-12 16:35:15', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1005002, 1005, '小程序认证', 'applet', 300, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:40', '2024-04-12 16:51:58', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1005003, 1005, '三方认证', 'third', 400, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:51', '2024-04-12 16:51:49', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1005004, 1005, '短信认证', 'sms', 200, 'success', '', 'F', 'T', 'F', '2024-01-22 10:20:57', '2024-04-12 16:51:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1012001, 1012, '等待导出', '', 1, 'success', '等待执行', 'F', 'T', 'F', '2023-09-04 13:38:22', '2024-04-12 15:58:41', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1012002, 1012, '导出中', '', 2, 'info', '', 'F', 'T', 'F', '2023-09-04 13:38:38', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1012003, 1012, '导出完毕', '', 3, 'info', '', 'F', 'T', 'F', '2023-09-04 13:38:51', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1013001, 1013, 'jpg', '', 1, 'info', '', 'F', 'T', 'F', '2023-09-04 14:23:12', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1013002, 1013, 'png', '', 2, 'info', '', 'F', 'T', 'F', '2023-09-04 14:23:21', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1013003, 1013, 'xlsx', '', 4, 'info', '', 'F', 'T', 'F', '2023-09-04 14:23:32', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1013004, 1013, 'doc', '', 4, 'info', '', 'F', 'T', 'F', '2023-09-04 14:23:38', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1013005, 1013, 'pdf', '', 5, 'info', '', 'F', 'T', 'F', '2023-09-04 14:23:50', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1023001, 1023, '启用', '', 1, 'success', '', 'F', 'T', 'F', '2024-04-08 16:53:23', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1023002, 1023, '禁用', '', 2, 'danger', '', 'F', 'T', 'F', '2024-04-08 16:53:32', '2024-04-12 15:58:42', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '字典类型id',
  `type_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典类型名(中文)',
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型码(英文)',
  `is_lock` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否锁定，锁定的属性无法在页面进行修改',
  `is_show` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'T' COMMENT '显示与否',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '删除与否',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `delete_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1024 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1000, '账户状态', 'account_status', 'T', 'T', 'F', '', '2023-08-20 11:09:46', '2023-08-28 16:25:59', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1001, '用户标签', 'user_tag', 'F', 'T', 'F', '', '2023-08-20 14:22:40', '2023-08-24 15:24:58', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1002, '菜单类型', 'menu_type', 'T', 'T', 'F', '', '2023-08-21 11:20:47', '2023-08-28 16:25:55', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1003, '授权状态', 'sys_client_status', 'T', 'T', 'F', 'client授权状态', '2023-08-22 09:44:27', '2024-04-12 16:56:40', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1004, '设备类型', 'device_type', 'T', 'T', 'F', '', '2023-08-22 10:02:11', '2024-04-12 16:56:49', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1005, '授权类型', 'grant_type', 'T', 'T', 'F', '', '2023-08-22 10:15:58', '2024-04-12 16:57:04', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1012, '导出状态', 'export_status', 'F', 'T', 'F', 'excel导出的状态', '2023-09-04 13:36:56', '2023-09-04 13:36:56', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1013, '文件类型', 'file_type', 'F', 'T', 'F', '', '2023-09-04 14:22:54', '2023-09-04 14:22:54', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1023, '应用状态', 'program_status', 'F', 'T', 'F', '', '2024-04-08 16:52:43', '2024-04-10 17:21:20', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_export_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_export_info`;
CREATE TABLE `sys_export_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '导出的文件名称',
  `export_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '导出状态,关联字典表export_status',
  `create_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '导出信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_export_info
-- ----------------------------
INSERT INTO `sys_export_info` VALUES (2, NULL, NULL, 312, '2023-09-04 11:22:03');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型',
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件大小',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件域名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_login_detail
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_detail`;
CREATE TABLE `sys_login_detail`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `access_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'access_token',
  `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'refresh_token',
  `last_login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近一次登录时间',
  `access_expiration_time` datetime NULL DEFAULT NULL COMMENT '失效时间',
  `refresh_expiration_time` datetime NULL DEFAULT NULL,
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录ip',
  `has_refresh` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否刷新过',
  `refresh_time` datetime NULL DEFAULT NULL COMMENT '刷新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户登录信息表 -- 废弃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login_detail
-- ----------------------------
INSERT INTO `sys_login_detail` VALUES (15, 1, 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTWVNURU0iLCJleHAiOjE2OTQ2ODQzNTYsInVzZXJuYW1lIjoiYWRtaW4ifQ.syQ1iYy4mgQwe7O3Y-m6Wh_-wxfCKHQmNQe8JoOsbWA', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTWVNURU0iLCJleHAiOjE2OTUyMDI3NTYsInVzZXJuYW1lIjoiYWRtaW4ifQ.KE2ivwtq1VHJlOE-Kdmgm_Bwj1ebVIGKYNUFOEfnr54', '2023-09-13 17:39:16', '2023-09-14 09:39:16', '2023-09-20 09:39:16', '', 'F', NULL);
INSERT INTO `sys_login_detail` VALUES (16, 1, 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTWVNURU0iLCJleHAiOjE2OTQ2ODQ0MjgsInVzZXJuYW1lIjoiYWRtaW4ifQ.A1Uw7qEjl8uw4Qkqp1fGMvVrLaMNl0aCCqyU1a2te9w', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTWVNURU0iLCJleHAiOjE2OTUyMDI4MjgsInVzZXJuYW1lIjoiYWRtaW4ifQ.2SrhYxSrRfDTcnl9oaAZkRY6RUXrFMUM8lAKJV2LkAM', '2023-09-13 17:40:28', '2023-09-14 09:40:28', '2023-09-20 09:40:28', '192.168.56.1', 'F', NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单表id',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父级id',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路径',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由名称',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'icon图标',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组件路径',
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '外链地址',
  `sort` int NOT NULL COMMENT '排序',
  `deep` int NOT NULL COMMENT '层级',
  `menu_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型 （字典表menu_type）',
  `permissions` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '按钮权限',
  `is_hidden` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否隐藏',
  `has_children` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否有子级',
  `is_link` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '路由外链时填写的访问地址',
  `is_full` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '菜单是否全屏',
  `is_affix` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '菜单是否固定在标签页',
  `is_keep_alive` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '当前路由是否缓存',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人',
  `update_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
  `delete_id` int NULL DEFAULT NULL,
  `delete_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('006bdbacd71a481f88b6acf895529acd', '8354d626cc65487594a7c38e98de1bad', '', '', '修改', '', '', '', 2, 3, '1002003', 'sys.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-03-19 15:42:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('00e13dd7c3364170a9ff1aa9306a8083', '796a6c7af9884e9282b4808f83e2248c', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:07', '2024-04-24 22:07:07', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('012efc4ef8d24304a8562534f319524a', '0e529e8a9dbf450898b695e051c36d48', '', '', '预览按钮', '', '', '', 6, 3, '1002003', 'generator.preview', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:23:47', '2024-02-15 10:23:47', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('02d082d29e28435d945a5c347943b395', 'c44437df837046fb807c3aacf1486316', '', '', '删除', '', '', '', 3, 1, '1002003', 'zhy.test.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('02ff008fde914a87ad493a94fe1335ed', '0', '/sysdept/sysDeptLeader', 'SysDeptLeaderView', '负责人管理', '', '/sysdept/sysDeptLeader/index', '', 12, 0, '1002002', 'sys.dept.leader.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-26 16:22:06', '2024-04-09 08:45:14', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('05194ef5fa7a4a308a44f6f5c6791c3a', '99c2ee7b882749e597bcd62385f368fb', '', '', '编辑菜单', '', '', '', 2, 3, '1002003', 'sys.menu.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:31:35', '2024-02-04 14:02:04', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0606e70f21c74584bb3a9f377132b8ca', '740b6d45c5574eceb3d5d8da48dd7de4', '', '', '删除', '', '', '', 3, 2, '1002003', 'mini.program.grid.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-16 15:47:01', '2024-04-16 15:47:01', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('07698d179568448590a3c0457589e9f0', '45ace3a5d03f4f898fbead17b891b4cd', '', '', '新增', '', '', '', 1, 3, '1002003', 'sys.client.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 14:17:45', '2024-02-06 15:53:58', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('079fd14a2c7e498195f3419e2cd37e50', '8cae2a9b6d4a493f9c6fc571dde80177', '', '', '修改', '', '', '', 2, 4, '1002003', 'sys.dept.closure.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:56:27', '2024-04-08 16:06:43', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0933b165ffc14d558e8de43ccb6687f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '编辑角色', '', '', '', 2, 3, '1002003', 'sys.role.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:43', '2024-02-04 14:18:05', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0986927f20a542adaeb25c5343381e8f', '1d44785168c64d20b3baf4982172c7c3', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 14:25:16', '2024-01-16 13:33:54', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0b99c56a310d49599c2fbf6d2a1a7c01', '7985bd01ba8742f59d49c68efdf9af41', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:09:31', '2024-01-12 14:20:35', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0bcaac4610e340d9877db7385ac76b35', '74d67db6e83d43d9ad8a7b20e95a7847', '', '', '修改', '', '', '', 2, 2, '1002003', 'program.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-08 15:12:18', '2024-04-11 16:02:25', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0be0aa158de14d4d9edf3a4451267f6f', 'd703079c73514a20a260e8066be081ad', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:11:44', '2024-04-19 20:11:44', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0c3d6c39929a472c9295e6a3ae877007', 'b9580cbb5bd64ddbb1f262ad86ee1253', '', '', '删除', '', '', '', 3, 1, '1002003', 'zhy.test.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:06:30', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0e529e8a9dbf450898b695e051c36d48', 'da1b46db642f42978f83ed5eb34870ce', '/toolbox/generator', 'generator', '代码生成', 'Brush', '/toolbox/generator/index', '', 1, 2, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:50:39', '2024-02-15 10:19:21', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0f98b89c67e54cb0bcff2b56aa98832f', '140c9ed43ef54542bbcdde8a5d928400', '', '', '新增账号', '', '', '', 1, 3, '1002003', 'sys.user.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:55', '2023-11-28 14:35:14', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0ff78b2fd4be4ccba057ebd55a98810f', '4bdf3e32af464d1ab1877eeb3e6bf4eb', '', '', '删除', '', '', '', 3, 2, '1002003', 'program.carousel.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:24:24', '2024-04-11 16:04:57', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('10cac186b0c4458788f81ce5b0bf91a9', '56528aa8ef2d4781b1f2a3049962ae74', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:04', '2024-04-24 22:07:04', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11e5781ef88a4bff82496c36c840882f', '74d67db6e83d43d9ad8a7b20e95a7847', '', '', '金刚区', '', '', '', 500, 2, '1002003', 'program.grid', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 14:39:03', '2024-04-11 16:03:10', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('13616d40ac544d128654c5a7878e4217', '796a6c7af9884e9282b4808f83e2248c', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:07', '2024-04-24 22:07:07', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('140c9ed43ef54542bbcdde8a5d928400', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/accountManage', 'accountManage', '账号管理', 'UserFilled', '/system/accountManage/index', '', 1, 2, '1002002', 'sys.user.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:53:49', '2024-02-15 08:49:32', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('16127584d8c9426f9091d2848f982822', '74d67db6e83d43d9ad8a7b20e95a7847', '', '', '轮播', '', '', '', 400, 2, '1002003', 'program.carousel', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 10:56:07', '2024-04-11 16:02:49', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('166727f83fa64faca0819ae0d2b36a97', '966dd820947f471786f894d18881f1e2', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.user.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1a25f87c0cd94b89a82aee685027c336', 'd703079c73514a20a260e8066be081ad', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:11:44', '2024-04-19 20:11:44', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1a86a9d2b3ca49439277fff9f499c7cd', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典', '', '', '', 6, 3, '1002003', 'sys.dict.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:58:26', '2024-02-04 13:58:26', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1a8fd69471b24bb4bd6d1f79e37334b1', 'd703079c73514a20a260e8066be081ad', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:11:44', '2024-04-19 20:11:44', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1a9a8f38b57945939374341527daee9f', '98aac82c34f44943b67ebc23807aac8e', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-10 14:15:28', '2024-01-12 14:20:07', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1c2f19edf5a14a7e90724e7a0c63ec1e', '0', '/program', 'program', '应用管理', 'ChatDotRound', '', '', 17, 0, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-08 15:12:18', '2024-04-11 16:01:00', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1cc109fadbed4aa3b12fae87ffe33bcb', 'c7eebed0d13d48fa9b093e35b5570d6b', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:18:08', '2024-01-12 14:20:10', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1d44785168c64d20b3baf4982172c7c3', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 10, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-12 14:25:16', '2024-01-16 13:33:54', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1f103dc0ac39437a937e6f7d06442669', '796a6c7af9884e9282b4808f83e2248c', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:07', '2024-04-24 22:07:07', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1f855f45932746f49984c7b848aa4866', '76961f7833794316a30e3eca9e4e53d8', '', '', '删除', '', '', '', 3, 1, '1002003', 'program.service.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2304fb6815bf415a8bb16ee9539df685', '740b6d45c5574eceb3d5d8da48dd7de4', '', '', '修改', '', '', '', 2, 2, '1002003', 'mini.program.grid.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-16 15:47:01', '2024-04-16 15:47:01', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('27c1a1e2fe5b49c39147493d8ceae7f5', '1e76e97c14544554b97348c888f90378', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:16:22', '2024-04-19 20:16:22', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('28f6e24e47664380952869c68959d207', '381b712003a34618ba657329db589a36', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 07:56:22', '2024-01-12 14:23:03', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('29d33eba6b73420287d8f7e64aea62b3', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/configManage', 'configManage', '参数管理', 'Key', '/system/configManage/index', '', 5, 2, '1002002', 'sys.config.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-11-24 09:54:25', '2024-02-15 10:01:46', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2a5fc90593b64ef0a7ebb54d818c0776', '76961f7833794316a30e3eca9e4e53d8', '', '', '导出', '', '', '', 5, 1, '1002003', 'program.service.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2c468a2cd0974739a1c55c189fe4e7c4', '76961f7833794316a30e3eca9e4e53d8', '', '', '修改', '', '', '', 2, 1, '1002003', 'program.service.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2d00280a8f5d46ea96db38e1da44e99f', '5ba2846e8dba4482843ec45f2110c9ee', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:12:19', '2024-04-19 20:12:19', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2d6b78ad03de4cf1a3899f25cd7fe0ee', '0e529e8a9dbf450898b695e051c36d48', '', '', '删除按钮', '', '', '', 4, 3, '1002003', 'generator.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:22:50', '2024-02-15 10:22:50', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2f7cd92d79b240bab1cb3c8429f6d38b', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/sysdept/sysDept', 'SysDeptView', '部门管理', '', '/sysdept/sysDept/index', '', 9, 2, '1002002', 'sys.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-21 08:54:50', '2024-03-25 15:37:52', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('310d02bb121645d1b7a7f949f48c981b', '0e529e8a9dbf450898b695e051c36d48', '', '', '生成按钮', '', '', '', 3, 3, '1002003', 'generator.generator', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:21:47', '2024-02-15 10:22:02', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('32b41c37e2154ee5a789422f35880f86', '7985bd01ba8742f59d49c68efdf9af41', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:09:31', '2024-01-12 14:20:11', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('330a1a0a857c4ad1a95327db5134e420', '140c9ed43ef54542bbcdde8a5d928400', '', '', '解锁', '', '', '', 5, 3, '1002003', 'sys.user.unlock_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-29 14:06:49', '2024-02-29 14:06:49', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('346007dfb160441dbc36bcb52a871a3a', 'b9580cbb5bd64ddbb1f262ad86ee1253', '', '', '修改', '', '', '', 2, 1, '1002003', 'zhy.test.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:06:30', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('381b712003a34618ba657329db589a36', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 8, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-12 07:56:22', '2024-01-12 14:23:03', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3a14fbc0ceb44f52a543db2b772d2ca0', '5ba2846e8dba4482843ec45f2110c9ee', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:12:19', '2024-04-19 20:12:19', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3a54d488132b4331bf3cd5e6d86ffcf4', '29d33eba6b73420287d8f7e64aea62b3', '', '', '修改参数', '', '', '', 2, 3, '1002003', 'sys.config.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:38', '2023-11-28 14:38:42', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3ba9407560a1490583fefa10b22bc74f', '8354d626cc65487594a7c38e98de1bad', '', '', '删除', '', '', '', 3, 3, '1002003', 'sys.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-03-19 15:42:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3bf1c5d451284035b8a10430c4571d9b', '4bdf3e32af464d1ab1877eeb3e6bf4eb', '', '', '修改', '', '', '', 2, 2, '1002003', 'program.carousel.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:24:24', '2024-04-11 16:04:40', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3d71bc1bdfd1417fbf451441378d78a5', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 9, 0, '1002002', '', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 14:23:40', '2024-01-12 14:25:11', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3d7eed8398d3457c897b2e8bf838e9c6', '0e529e8a9dbf450898b695e051c36d48', '', '', '编辑按钮', '', '', '', 2, 3, '1002003', 'generator.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:21:05', '2024-02-15 10:21:05', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3d864b20f9c044c7b9c2e994e8596a78', '8db8349b311741449d16c568abf88bef', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.user.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:50:59', '2024-04-09 08:45:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('41fb53223dc64cd295576836f72f9fc4', '2f7cd92d79b240bab1cb3c8429f6d38b', '', '', '新增', '', '', '', 1, 3, '1002003', 'sys.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-21 08:54:51', '2024-03-25 15:37:52', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('445b73dda9a34ad681d2705a7abcf2f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '删除角色', '', '', '', 3, 3, '1002003', 'sys.role.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:05', '2024-02-04 14:18:11', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('45ace3a5d03f4f898fbead17b891b4cd', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/sys/sysClient', 'SysClientView', '客户端管理', '', '/sys/sysClient/index', '', 7, 2, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-22 14:17:44', '2024-02-06 15:53:58', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('46341200019a48d683df8d44e8ee9282', 'a6d540dff11f4b189897ed45df2c3be3', '', '', '新增', '', '', '', 1, 2, '1002003', 'mini.program.grid.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-25 10:22:49', '2024-04-25 10:22:49', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('47aa188a9b254baf8c109c59d0193622', 'f5a405a228b94d31b5d45e5d0eec6327', '', '', '新增', '', '', '', 1, 4, '1002003', 'sys.dept.closure.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 16:43:25', '2024-04-08 16:06:39', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('49d8597562af42f0a2381a09bb786651', '76961f7833794316a30e3eca9e4e53d8', '', '', '导入', '', '', '', 4, 1, '1002003', 'program.service.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4a378db765e74f33a7996ce2058bf59a', 'f16772e7bcd84241bd21b17da4cbfb75', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 08:54:41', '2024-01-12 14:20:14', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4a6a115d70b548739d9a5e317c55f720', '4bdf3e32af464d1ab1877eeb3e6bf4eb', '', '', '新增', '', '', '', 1, 2, '1002003', 'program.carousel.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:24:24', '2024-04-11 16:04:30', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4bdf3e32af464d1ab1877eeb3e6bf4eb', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/program/programCarousel', 'ProgramCarouselView', '轮播图管理', 'PictureFilled', '/program/programCarousel/index', '', 2, 1, '1002002', 'program.carousel.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-09 16:24:24', '2024-04-11 16:03:50', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4c55f109b47846478b33f473e0f0cd29', 'c7eebed0d13d48fa9b093e35b5570d6b', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:18:08', '2024-01-12 14:20:15', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4c9070a222b84972a4d29d6e742ff08b', 'd18935639a2744d0b78811d9ffc1003d', '', '', '修改', '', '', '', 2, 2, '1002003', 'mini.program.grid.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:53', '2024-04-09 17:12:25', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4f39ef0fd2f748f6ab7d6d20d98bc4af', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典类型', '', '', '', 1, 3, '1002003', 'sys.dict.add_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:52:38', '2024-02-04 14:00:12', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('514f7f91a4924d70b629f720a8bedc2a', 'd703079c73514a20a260e8066be081ad', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:11:44', '2024-04-19 20:11:44', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('532e92b8d79c4736bff95af10d27f49a', '796a6c7af9884e9282b4808f83e2248c', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:07', '2024-04-24 22:07:07', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5850261d643147c2886069d76566c8f0', 'dd3a161fb8e849649f4789369e656959', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.dept.leader.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:11', '2024-04-20 16:13:11', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5969721f47ad45669425248fdc189de1', '7557b748089742558cdece286625d56c', '', '', '新增', '', '', '', 1, 2, '1002003', 'program.grid.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:31:46', '2024-04-11 16:05:41', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5b33ac3d630543d09d1388fae4d13fc0', '9e731ff422184fc1be2022c5c985735e', '', '', '修改', '', '', '', 2, 3, '1002003', 'sys.client.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-01-22 10:52:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5b5fb3748c6a4ed5a4dda3877508c3a7', 'c6dd479d5b304731be403d7551c60d70', '', '', '设置权限', '', '', '', 4, 3, '1002003', 'sys.role.setting_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:35', '2024-02-04 14:18:18', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5c3631a1d8b243469c2fe2164a6505a5', '6bcddfc406ed4b908bec3674b34f70a2', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:06', '2024-04-20 16:13:06', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5e1f43e03488414cb62b9a8bf6fb0d81', 'f16772e7bcd84241bd21b17da4cbfb75', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 08:54:41', '2024-01-12 14:20:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('60ffa95b8ae3455d85dea68892fa3730', '8db8349b311741449d16c568abf88bef', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.user.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:50:59', '2024-04-09 08:45:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('62aaed7b669149d094c20bcbbc0342cd', 'f16772e7bcd84241bd21b17da4cbfb75', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 08:54:41', '2024-01-12 14:20:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('62bb91daa19c4c4d839bd5cc37b87d04', 'c7eebed0d13d48fa9b093e35b5570d6b', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:18:08', '2024-01-12 14:20:23', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('676d02c401eb4388a958759e768811ef', '1e76e97c14544554b97348c888f90378', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:16:22', '2024-04-19 20:16:22', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('686a5522b0334d4da51aa15b3fd1a303', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置部门', '', '', '', 700, 3, '1002003', 'sys.user.dept_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-12 14:33:21', '2024-04-12 14:33:21', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6bb2fcbe5d5c403aac61e3e4fdf43a90', '1e76e97c14544554b97348c888f90378', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:16:22', '2024-04-19 20:16:22', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6c46fd01faf042fc9dd4a9c9b9ef2c5a', '9e731ff422184fc1be2022c5c985735e', '', '', '新增', '', '', '', 1, 3, '1002003', 'sys.client.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-01-22 10:52:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6d560e73bb0b44df8b2af04a02b98ecb', '381b712003a34618ba657329db589a36', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 07:56:22', '2024-01-12 14:23:03', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6e25a716c1a646009a9be90b16f0a682', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置角色', '', '', '', 4, 3, '1002003', 'sys.user.role_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:53', '2024-04-12 14:35:12', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6e45725f7d304e27badab761ea7d6598', '74d67db6e83d43d9ad8a7b20e95a7847', '', '', '新增', '', '', '', 1, 2, '1002003', 'program.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-08 15:12:18', '2024-04-11 16:02:06', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('71bca5f31a1c4d4b8c111be7b3a92b96', 'd703079c73514a20a260e8066be081ad', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:11:44', '2024-04-19 20:11:44', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7391f12ad51049c2b86d231d39708c71', '85b54322630f43a39296488a5e76ba16', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-02-17 10:19:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('73d312f4fa8949ddba3d9807c0c56f00', '85b54322630f43a39296488a5e76ba16', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-02-17 10:19:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('740b6d45c5574eceb3d5d8da48dd7de4', '1c2f19edf5a14a7e90724e7a0c63ec1e', '\\miniprogram\\miniProgramGrid', 'MiniProgramGridView', '金刚区', '', '\\miniprogram\\miniProgramGrid\\index', '', 6, 1, '1002002', 'mini.program.grid.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-16 15:47:01', '2024-04-16 15:47:01', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('749589d693bb4819883c83562c453923', '1d44785168c64d20b3baf4982172c7c3', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 14:25:16', '2024-01-16 13:33:54', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('74d67db6e83d43d9ad8a7b20e95a7847', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/program/program', 'ProgramList', '应用列表', 'Menu', '/program/program/index', '', 1, 1, '1002002', 'program.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-09 14:18:11', '2024-04-11 16:01:46', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7557b748089742558cdece286625d56c', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/program/programGrid', 'ProgramGridView', '金刚区管理', 'Grid', '/program/programGrid/index', '', 3, 1, '1002002', 'program.grid.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-09 16:31:46', '2024-04-11 16:05:30', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('76961f7833794316a30e3eca9e4e53d8', '0', '/programservice/programService', 'ProgramServiceView', '应用服务', '', '/programservice/programService/index', '', 18, 0, '1002002', 'program.service.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('786cf450e32d46b398e441fdbf9c836f', 'eaedc51777af4748b6a0fc9970d386be', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('78858c6cd2ac42fba863cc8a2f741d05', '56528aa8ef2d4781b1f2a3049962ae74', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:04', '2024-04-24 22:07:04', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7985bd01ba8742f59d49c68efdf9af41', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 6, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-11 09:09:31', '2024-01-11 09:10:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7d3501bed21644369fefe88ccb2d5af8', '9b16853532e846409fbbb102a3484484', '', '', '新增', '', '', '', 1, 4, '1002003', 'sys.dept.closure.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:09', '2024-04-20 16:13:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8061d8e79be744bf91b7b438f8e8e887', '85b54322630f43a39296488a5e76ba16', '', '', '导出', '', '', '', 5, 1, '1002003', 'teacher.statistics.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-02-17 10:19:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('81647226a2d047e8ab0b70472350ee69', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典', '', '', '', 4, 3, '1002003', 'sys.dict.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:54:55', '2024-02-04 13:57:59', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('818cc6e1889d46579525ad8ab921eeb8', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典类型', '', '', '', 2, 3, '1002003', 'sys.dict.update_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:39:29', '2024-02-04 14:00:18', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8255bac5eae748a0a8500167963b3e00', '140c9ed43ef54542bbcdde8a5d928400', '', '', '编辑账号', '', '', '', 2, 3, '1002003', 'sys.user.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:32:15', '2023-11-28 15:13:31', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('82e6188cb9cf4c3c86e45afd94e6893c', '966dd820947f471786f894d18881f1e2', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.user.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8354d626cc65487594a7c38e98de1bad', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/deptManage', 'SysDeptView', '部门管理', 'svg-org', '/system/deptManage/index', '', 7, 2, '1002002', 'sys.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-03-26 15:36:30', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('837dc4f1075343aeae25fd3a0a6b91be', 'b9580cbb5bd64ddbb1f262ad86ee1253', '', '', '导出', '', '', '', 5, 1, '1002003', 'zhy.test.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:06:31', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('845dc562252143358cd1829c8b6ff190', '7557b748089742558cdece286625d56c', '', '', '删除', '', '', '', 3, 2, '1002003', 'program.grid.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:31:46', '2024-04-11 16:05:52', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('85b54322630f43a39296488a5e76ba16', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', 'svg-org', '/teacher/teacherStatistics/index', '', 11, 0, '1002002', 'teacher.statistics.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-03-26 17:03:04', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('88b2e5def2ff474fa8bf3537d4a2fe5b', '0', '/system', 'system', '系统管理', 'Tools', '', '', 2, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:52:10', '2024-02-07 16:54:15', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8cae2a9b6d4a493f9c6fc571dde80177', '8354d626cc65487594a7c38e98de1bad', '/sysdept/sysDeptClosure', 'SysDeptClosureView', '部门祖籍关系', '', '/sysdept/sysDeptClosure/index', '', 5, 3, '1002002', 'sys.dept.closure.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-02 09:56:27', '2024-04-08 16:06:43', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8cc171f471bf49df98b35958858cd6fb', '02ff008fde914a87ad493a94fe1335ed', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.dept.leader.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-26 16:22:06', '2024-04-09 08:45:14', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8d0b8b57a58e41a5a5e840cc2b3703f4', '9e731ff422184fc1be2022c5c985735e', '', '', '删除', '', '', '', 3, 3, '1002003', 'sys.client.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-01-22 10:52:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8d1d89baae22415d9e2444ac4e08c443', 'd18935639a2744d0b78811d9ffc1003d', '', '', '新增', '', '', '', 1, 2, '1002003', 'mini.program.grid.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:53', '2024-04-09 17:12:25', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8d66bc0f93504582b1679c9dd99b9cfd', 'a6d540dff11f4b189897ed45df2c3be3', '', '', '修改', '', '', '', 2, 2, '1002003', 'mini.program.grid.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-25 10:22:49', '2024-04-25 10:22:49', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8db8349b311741449d16c568abf88bef', '0', '\\sysuser\\sysUserDept', 'SysUserDeptView', '用户-部门关系表', '', '\\sysuser\\sysUserDept\\index', '', 16, 0, '1002002', 'sys.user.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-02 09:50:58', '2024-04-09 08:45:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8ef7c60892ea4d32be9f5006f5bcea24', 'd18935639a2744d0b78811d9ffc1003d', '', '', '删除', '', '', '', 3, 2, '1002003', 'mini.program.grid.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:53', '2024-04-09 17:12:25', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8fd6721941494fd5bbe16bec82b235be', '8354d626cc65487594a7c38e98de1bad', '', '', '新增', '', '', '', 1, 3, '1002003', 'sys.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-03-19 15:42:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9175c715644f417697b660abc7ecd3a2', '796a6c7af9884e9282b4808f83e2248c', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:07', '2024-04-24 22:07:07', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('91ccb13b5c174583803a4c492a5dfdb6', '85b54322630f43a39296488a5e76ba16', '', '', '导入', '', '', '', 4, 1, '1002003', 'teacher.statistics.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-02-17 10:19:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9338bf2f57984825bc227bb618f9db81', '99c2ee7b882749e597bcd62385f368fb', '', '', '新增菜单', '', '', '', 1, 3, '1002003', 'sys.menu.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 14:27:50', '2024-02-04 14:01:57', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('966dd820947f471786f894d18881f1e2', '0', '\\sysuserdept\\sysUserDept', 'SysUserDeptView', '用户-部门关系表', '', '\\sysuserdept\\sysUserDept\\index', '', 15, 0, '1002002', 'sys.user.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('970c5ce491ae43ee997af4138ff71aeb', '98aac82c34f44943b67ebc23807aac8e', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-10 14:15:28', '2024-01-12 14:20:25', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9759724129124357858e0aa8bf934e18', '7985bd01ba8742f59d49c68efdf9af41', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:09:31', '2024-01-12 14:20:27', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('97f11d74c98047ba80f011a3da9d882c', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典', '', '', '', 5, 3, '1002003', 'sys.dict.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:55:13', '2024-02-04 13:58:05', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9830d86487184961b90fc527c9604720', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典类型', '', '', '', 3, 3, '1002003', 'sys.dict.delete_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:57:33', '2024-02-04 13:57:53', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('98aac82c34f44943b67ebc23807aac8e', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 23, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-10 14:15:27', '2024-01-10 15:11:43', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('99c2ee7b882749e597bcd62385f368fb', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/menuMange', 'menuMange', '菜单管理', 'Menu', '/system/menuMange/index', '', 3, 2, '1002002', 'sys.menu.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:55:30', '2024-02-07 15:20:11', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9a026e18e4994806a03fa761ec7de002', 'eaedc51777af4748b6a0fc9970d386be', '', '', '导入', '', '', '', 4, 1, '1002003', 'teacher.statistics.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9a88b21beb1a46c488a0dffba687a9b2', 'eaedc51777af4748b6a0fc9970d386be', '', '', '导出', '', '', '', 5, 1, '1002003', 'teacher.statistics.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9dbecca28ea14b60bdcab4a38ee68609', '98aac82c34f44943b67ebc23807aac8e', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-10 14:15:28', '2024-01-12 14:20:29', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9e731ff422184fc1be2022c5c985735e', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/clientManage', 'ClientManageView', '客户端管理', 'Operation', '/system/clientManage/index', '', 6, 2, '1002002', 'sys.client.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-02-17 11:14:12', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9f521bc994c74c94ad1b603124e03730', '5ba2846e8dba4482843ec45f2110c9ee', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:12:19', '2024-04-19 20:12:19', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('a4cacc67b09c404eb17c3a166151fff5', '9b16853532e846409fbbb102a3484484', '', '', '删除', '', '', '', 3, 4, '1002003', 'sys.dept.closure.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:09', '2024-04-20 16:13:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('a5bc69ce5d5a4085ad5118d6c172cbca', '8cae2a9b6d4a493f9c6fc571dde80177', '', '', '新增', '', '', '', 1, 4, '1002003', 'sys.dept.closure.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:56:27', '2024-04-08 16:06:43', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('a5d707c17a094f9dbc9d85e3033bd21b', 'eaedc51777af4748b6a0fc9970d386be', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('a740557aec2842568456e4c6eea06196', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/miniprogram/miniProgramCarousel', 'MiniProgramCarouselView', '轮播图', '', '/miniprogram/miniProgramCarousel/index', '', 4, 1, '1002002', 'mini.program.carousel.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-09 16:57:25', '2024-04-09 17:12:17', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ac9b7c6fec064298a28351191826f5c2', 'b9580cbb5bd64ddbb1f262ad86ee1253', '', '', '导入', '', '', '', 4, 1, '1002003', 'zhy.test.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:06:31', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('aecd76499a2a4868ae65c560639a2d8f', '45ace3a5d03f4f898fbead17b891b4cd', '', '', '删除', '', '', '', 3, 3, '1002003', 'sys.client.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 14:17:45', '2024-02-06 15:53:58', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('aef1636be57a4536a4a9880266b140a0', '6bcddfc406ed4b908bec3674b34f70a2', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:06', '2024-04-20 16:13:06', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b428eba3f9a34025a46c394df5390b88', '29d33eba6b73420287d8f7e64aea62b3', '', '', '删除参数', '', '', '', 3, 3, '1002003', 'sys.config.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:58:06', '2023-11-28 14:39:00', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b5181c6849064a23aae3ff03db539e21', '02ff008fde914a87ad493a94fe1335ed', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.dept.leader.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-26 16:22:06', '2024-04-09 08:45:14', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b5ce6412c26447348a7267de3ea11a21', '0e529e8a9dbf450898b695e051c36d48', '', '', '导入按钮', '', '', '', 1, 3, '1002003', 'generator.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:19:21', '2024-02-15 10:19:21', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b64a91d26a5c40d9b58737ea260ef96e', '966dd820947f471786f894d18881f1e2', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.user.dept.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b6510a7847be4fb6aaf8a296ef2b976d', 'a6d540dff11f4b189897ed45df2c3be3', '', '', '删除', '', '', '', 3, 2, '1002003', 'mini.program.grid.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-25 10:22:49', '2024-04-25 10:22:49', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b8fdcc5348ed45f29e0fe860049b7efc', 'c44437df837046fb807c3aacf1486316', '', '', '导入', '', '', '', 4, 1, '1002003', 'zhy.test.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b9580cbb5bd64ddbb1f262ad86ee1253', '0', '\\zhytest\\zhyTest', 'ZhyTestView', 'zhy测试', '', '\\zhytest\\zhyTest\\index', '', 14, 0, '1002002', 'zhy.test.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-28 11:06:30', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b9cdca65e73e430280b96c7ffeb72496', '5ba2846e8dba4482843ec45f2110c9ee', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:12:19', '2024-04-19 20:12:19', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('bb5d5d516b9a452299f0292a581af442', 'f5a405a228b94d31b5d45e5d0eec6327', '', '', '修改', '', '', '', 2, 4, '1002003', 'sys.dept.closure.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 16:43:25', '2024-04-08 16:06:39', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c2d3802ad7004d47a5c3042f3264844f', 'dd3a161fb8e849649f4789369e656959', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.dept.leader.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:11', '2024-04-20 16:13:11', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c3e3286d4d5548f4885ee976b99cefae', '56528aa8ef2d4781b1f2a3049962ae74', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.organization.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:04', '2024-04-24 22:07:04', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c44437df837046fb807c3aacf1486316', '0', '/zhytest/zhyTest', 'ZhyTestView', 'zhy测试', '', '/zhytest/zhyTest/index', '', 13, 0, '1002002', 'zhy.test.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c6db54ad9e044f6eab2dc960aa97fe13', '6bcddfc406ed4b908bec3674b34f70a2', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:06', '2024-04-20 16:13:06', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c6dd479d5b304731be403d7551c60d70', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/roleManage', 'roleManage', '角色管理', 'User', '/system/roleManage/index', '', 2, 2, '1002002', 'sys.role.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:54:36', '2024-02-07 15:19:00', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c754fcf6192043e4b18ed4729b1ce7b5', '2f7cd92d79b240bab1cb3c8429f6d38b', '', '', '修改', '', '', '', 2, 3, '1002003', 'sys.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-21 08:54:51', '2024-03-25 15:37:52', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c7eebed0d13d48fa9b093e35b5570d6b', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 7, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-11 09:18:08', '2024-01-11 09:18:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c870b9f5feab49218c515f42179e1df8', '1d44785168c64d20b3baf4982172c7c3', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 14:25:16', '2024-01-16 13:33:54', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c89332c3b13d4df4b0bbc49d674f2a65', '1e76e97c14544554b97348c888f90378', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.organization.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:16:22', '2024-04-19 20:16:22', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ca4b4f1f78224404975215b4530ccd13', 'dd3a161fb8e849649f4789369e656959', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.dept.leader.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:11', '2024-04-20 16:13:11', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ca6b31f9d5bc428da856696001177151', 'c44437df837046fb807c3aacf1486316', '', '', '修改', '', '', '', 2, 1, '1002003', 'zhy.test.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('cb3500315dba4c2d83e4d92edf36dff7', '85b54322630f43a39296488a5e76ba16', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-02-17 10:19:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('cea01dcde9b24b5a8686bdc33c438cd7', '140c9ed43ef54542bbcdde8a5d928400', '', '', '删除账号', '', '', '', 3, 3, '1002003', 'sys.user.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:27', '2024-02-04 14:25:40', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d05e34fe51e94839aac8910ba1917e7d', '966dd820947f471786f894d18881f1e2', '', '', '删除', '', '', '', 3, 1, '1002003', 'sys.user.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d18935639a2744d0b78811d9ffc1003d', '1c2f19edf5a14a7e90724e7a0c63ec1e', '/miniprogram/miniProgramGrid', 'MiniProgramGridView', '宫格', '', '/miniprogram/miniProgramGrid/index', '', 5, 1, '1002002', 'mini.program.grid.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-04-09 16:57:53', '2024-04-09 17:12:25', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d1e71a9419d146938442b4d9b90ffcb8', '74d67db6e83d43d9ad8a7b20e95a7847', '', '', '删除', '', '', '', 3, 2, '1002003', 'program.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-08 15:12:18', '2024-04-11 16:02:39', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d5596730006f4cbd86fa884d42c0fca7', '1e76e97c14544554b97348c888f90378', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:16:22', '2024-04-19 20:16:22', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d58f8dd7a9bf4e619e55706f1b0b2ffe', 'f5a405a228b94d31b5d45e5d0eec6327', '', '', '删除', '', '', '', 3, 4, '1002003', 'sys.dept.closure.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 16:43:25', '2024-04-08 16:06:39', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d65da65169754cbb9b51d9688f891e9f', '7557b748089742558cdece286625d56c', '', '', '修改', '', '', '', 2, 2, '1002003', 'program.grid.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:31:46', '2024-04-11 16:05:47', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('d708acb89a194fe3a2a23971aabdfcb2', '2f7cd92d79b240bab1cb3c8429f6d38b', '', '', '删除', '', '', '', 3, 3, '1002003', 'sys.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-21 08:54:51', '2024-03-25 15:37:52', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('da1b46db642f42978f83ed5eb34870ce', '0', '/toolbox', 'toolbox', '工具箱', 'Briefcase', '', '', 100, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:46:08', '2023-12-21 10:27:41', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('dba0099a057d435ea154d912b4b9bc8c', 'c44437df837046fb807c3aacf1486316', '', '', '新增', '', '', '', 1, 1, '1002003', 'zhy.test.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('dcb6aabcd910469ebf3efbc7e43282d4', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/dictManage', 'dictManage', '字典管理', 'Reading', '/system/dictManage/index', '', 4, 2, '1002002', 'sys.dict.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:57:12', '2024-02-15 10:03:32', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('dcd08b29c1724b728053679b1568ea39', '6bcddfc406ed4b908bec3674b34f70a2', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:06', '2024-04-20 16:13:06', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ddf59b01b16a461fbbddebedfd27af55', '6bcddfc406ed4b908bec3674b34f70a2', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:06', '2024-04-20 16:13:06', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('de95cbd00a514abeb5e28d3acf5fc35a', 'a740557aec2842568456e4c6eea06196', '', '', '修改', '', '', '', 2, 2, '1002003', 'mini.program.carousel.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:25', '2024-04-09 17:12:17', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('debac95cdbac4d78b2d3baee4e562728', 'a740557aec2842568456e4c6eea06196', '', '', '新增', '', '', '', 1, 2, '1002003', 'mini.program.carousel.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:25', '2024-04-09 17:12:17', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('df2894b4c06e47cab84142d81edc494d', 'c6dd479d5b304731be403d7551c60d70', '', '', '新增角色', '', '', '', 1, 3, '1002003', 'sys.role.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:18', '2023-11-28 14:34:09', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e00f163916f34f6180780cbe4044d684', '29d33eba6b73420287d8f7e64aea62b3', '', '', 'SQL预览', '', '', '', 4, 3, '1002003', 'sys.config.preview_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:46:41', '2024-02-04 13:47:15', '1', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e02b6603fb524f5bad16dacdb8a0d6ef', 'c44437df837046fb807c3aacf1486316', '', '', '导出', '', '', '', 5, 1, '1002003', 'zhy.test.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 09:10:06', '2024-04-08 15:13:08', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e2d381e932c74f599f5f8d306ed7c78c', '56528aa8ef2d4781b1f2a3049962ae74', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.organization.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:04', '2024-04-24 22:07:04', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e5219880ce0f4437bb25a93f2821c355', '5ba2846e8dba4482843ec45f2110c9ee', '', '', '导出', '', '', '', 5, 1, '1002003', 'sys.organization.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-19 20:12:19', '2024-04-19 20:12:19', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e7ff8380863f408dbdd24f35c66097fe', 'b9580cbb5bd64ddbb1f262ad86ee1253', '', '', '新增', '', '', '', 1, 1, '1002003', 'zhy.test.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:06:30', '2024-04-08 15:13:19', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e815caf6e46f43b0a7ca4b14feb56919', '45ace3a5d03f4f898fbead17b891b4cd', '', '', '修改', '', '', '', 2, 3, '1002003', 'sys.client.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 14:17:45', '2024-02-06 15:53:58', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e91eeaea8f1546d3921839469fe247b6', '140c9ed43ef54542bbcdde8a5d928400', '', '', '重置密码', '', '', '', 600, 3, '1002003', 'sys.user_resetPwd', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-10 10:16:08', '2024-04-10 10:16:08', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e9203452f32a4492a8137b4043290d91', '966dd820947f471786f894d18881f1e2', '', '', '导入', '', '', '', 4, 1, '1002003', 'sys.user.dept.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-28 11:08:25', '2024-03-28 11:33:36', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('eaedc51777af4748b6a0fc9970d386be', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', 'Baseball', '/teacher/teacherStatistics/index', '', 10, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '1', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('eb2532cd98d7453e8948dd9e93790160', 'eaedc51777af4748b6a0fc9970d386be', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-16 13:34:10', '2024-02-17 10:14:18', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ecf3fe3accf9440b805b860b8dda7ab7', '8cae2a9b6d4a493f9c6fc571dde80177', '', '', '删除', '', '', '', 3, 4, '1002003', 'sys.dept.closure.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:56:28', '2024-04-08 16:06:43', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ed693138a47e4131b0efeb96c6f589fa', '56528aa8ef2d4781b1f2a3049962ae74', '', '', '修改', '', '', '', 2, 1, '1002003', 'sys.organization.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-24 22:07:04', '2024-04-24 22:07:04', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ede76f5e60b640aa9de2ba7216b90ceb', '29d33eba6b73420287d8f7e64aea62b3', '', '', '新增参数', '', '', '', 1, 3, '1002003', 'sys.config.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:19', '2023-11-28 14:38:23', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ee36ad68586e42fa8a896215c544cb76', '99c2ee7b882749e597bcd62385f368fb', '', '', 'SQL按钮', '', '', '', 4, 3, '1002003', 'sys.menu.sql_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:41:47', '2024-02-17 10:24:38', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ee489049b0994452af50fab5c6b4b865', '9b16853532e846409fbbb102a3484484', '', '', '修改', '', '', '', 2, 4, '1002003', 'sys.dept.closure.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-20 16:13:09', '2024-04-20 16:13:09', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('eeef43cb2d1a493fa3160994e909b6b4', '381b712003a34618ba657329db589a36', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-12 07:56:22', '2024-01-12 14:23:03', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f03fb5a922c84e3a89b35b548a35055e', '02ff008fde914a87ad493a94fe1335ed', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.dept.leader.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-26 16:22:06', '2024-04-09 08:45:14', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f16772e7bcd84241bd21b17da4cbfb75', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', '', '/teacher/teacherStatistics/index', '', 5, 0, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-11 08:54:41', '2024-01-11 09:05:26', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f369f41d92994339a4056171faa79d9a', '8db8349b311741449d16c568abf88bef', '', '', '新增', '', '', '', 1, 1, '1002003', 'sys.user.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-02 09:50:58', '2024-04-09 08:45:20', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f3b0ff3d385445a080cb12cf88bb7f91', 'a740557aec2842568456e4c6eea06196', '', '', '删除', '', '', '', 3, 2, '1002003', 'mini.program.carousel.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-09 16:57:25', '2024-04-09 17:12:17', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f5a405a228b94d31b5d45e5d0eec6327', '8354d626cc65487594a7c38e98de1bad', '\\sysdept\\sysDeptClosure', 'SysDeptClosureView', '部门祖籍关系', '', '\\sysdept\\sysDeptClosure\\index', '', 4, 3, '1002002', 'sys.dept.closure.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-28 16:43:24', '2024-04-08 16:06:39', '', '', 'T', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f5bc5b3ab95b4f2d8b9efcf62469090f', '76961f7833794316a30e3eca9e4e53d8', '', '', '新增', '', '', '', 1, 1, '1002003', 'program.service.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-28 19:00:38', '2024-04-28 19:00:38', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('f92fe12eefbf4b9dbf6e42de22627719', '740b6d45c5574eceb3d5d8da48dd7de4', '', '', '新增', '', '', '', 1, 2, '1002003', 'mini.program.grid.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-16 15:47:01', '2024-04-16 15:47:01', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('fa0c65ad783d4bf9b919a6db02ef1428', '99c2ee7b882749e597bcd62385f368fb', '', '', '删除菜单', '', '', '', 3, 3, '1002003', 'sys.menu.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 16:28:57', '2024-02-04 14:02:11', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('fbdcbcc0ccf547b4b78a4fc2cf303236', '0e529e8a9dbf450898b695e051c36d48', '', '', 'zip下载按钮', '', '', '', 5, 3, '1002003', 'generator.zip', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:23:17', '2024-02-15 10:23:25', '1', '1', 'F', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简介',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_id` int NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (9, '超级管理员', '12', 'F', '2023-08-29 14:24:48', '2023-09-15 11:20:27', NULL, NULL);
INSERT INTO `sys_role` VALUES (14, '教师统计操作', '', 'T', '2023-12-22 10:07:11', '2024-02-15 10:34:33', NULL, NULL);
INSERT INTO `sys_role` VALUES (15, '字典管理', '', 'F', '2024-02-15 10:35:16', '2024-02-15 10:35:16', NULL, NULL);
INSERT INTO `sys_role` VALUES (16, '客户端管理', '', 'F', '2024-02-15 15:18:11', '2024-02-15 15:18:11', NULL, NULL);
INSERT INTO `sys_role` VALUES (17, '教师统计', '', 'F', '2024-02-17 11:07:15', '2024-04-29 05:39:23', NULL, NULL);
INSERT INTO `sys_role` VALUES (18, 'test1231', '123', 'T', '2024-04-29 05:16:47', '2024-04-29 05:16:55', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
  `role_id` int NOT NULL COMMENT 'sys_role_id （角色表）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2547 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色-菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1413, '9e731ff422184fc1be2022c5c985735e', 16);
INSERT INTO `sys_role_menu` VALUES (1414, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 16);
INSERT INTO `sys_role_menu` VALUES (1415, '5b33ac3d630543d09d1388fae4d13fc0', 16);
INSERT INTO `sys_role_menu` VALUES (1416, '8d0b8b57a58e41a5a5e840cc2b3703f4', 16);
INSERT INTO `sys_role_menu` VALUES (1417, '88b2e5def2ff474fa8bf3537d4a2fe5b', 16);
INSERT INTO `sys_role_menu` VALUES (2467, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 15);
INSERT INTO `sys_role_menu` VALUES (2468, '818cc6e1889d46579525ad8ab921eeb8', 15);
INSERT INTO `sys_role_menu` VALUES (2469, '9830d86487184961b90fc527c9604720', 15);
INSERT INTO `sys_role_menu` VALUES (2470, '81647226a2d047e8ab0b70472350ee69', 15);
INSERT INTO `sys_role_menu` VALUES (2471, '97f11d74c98047ba80f011a3da9d882c', 15);
INSERT INTO `sys_role_menu` VALUES (2472, '88b2e5def2ff474fa8bf3537d4a2fe5b', 15);
INSERT INTO `sys_role_menu` VALUES (2473, 'dcb6aabcd910469ebf3efbc7e43282d4', 15);
INSERT INTO `sys_role_menu` VALUES (2474, '88b2e5def2ff474fa8bf3537d4a2fe5b', 9);
INSERT INTO `sys_role_menu` VALUES (2475, '140c9ed43ef54542bbcdde8a5d928400', 9);
INSERT INTO `sys_role_menu` VALUES (2476, '0f98b89c67e54cb0bcff2b56aa98832f', 9);
INSERT INTO `sys_role_menu` VALUES (2477, '8255bac5eae748a0a8500167963b3e00', 9);
INSERT INTO `sys_role_menu` VALUES (2478, 'cea01dcde9b24b5a8686bdc33c438cd7', 9);
INSERT INTO `sys_role_menu` VALUES (2479, '6e25a716c1a646009a9be90b16f0a682', 9);
INSERT INTO `sys_role_menu` VALUES (2480, '330a1a0a857c4ad1a95327db5134e420', 9);
INSERT INTO `sys_role_menu` VALUES (2481, 'e91eeaea8f1546d3921839469fe247b6', 9);
INSERT INTO `sys_role_menu` VALUES (2482, '686a5522b0334d4da51aa15b3fd1a303', 9);
INSERT INTO `sys_role_menu` VALUES (2483, 'c6dd479d5b304731be403d7551c60d70', 9);
INSERT INTO `sys_role_menu` VALUES (2484, 'df2894b4c06e47cab84142d81edc494d', 9);
INSERT INTO `sys_role_menu` VALUES (2485, '0933b165ffc14d558e8de43ccb6687f6', 9);
INSERT INTO `sys_role_menu` VALUES (2486, '445b73dda9a34ad681d2705a7abcf2f6', 9);
INSERT INTO `sys_role_menu` VALUES (2487, '5b5fb3748c6a4ed5a4dda3877508c3a7', 9);
INSERT INTO `sys_role_menu` VALUES (2488, '99c2ee7b882749e597bcd62385f368fb', 9);
INSERT INTO `sys_role_menu` VALUES (2489, '9338bf2f57984825bc227bb618f9db81', 9);
INSERT INTO `sys_role_menu` VALUES (2490, '05194ef5fa7a4a308a44f6f5c6791c3a', 9);
INSERT INTO `sys_role_menu` VALUES (2491, 'fa0c65ad783d4bf9b919a6db02ef1428', 9);
INSERT INTO `sys_role_menu` VALUES (2492, 'ee36ad68586e42fa8a896215c544cb76', 9);
INSERT INTO `sys_role_menu` VALUES (2493, 'dcb6aabcd910469ebf3efbc7e43282d4', 9);
INSERT INTO `sys_role_menu` VALUES (2494, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 9);
INSERT INTO `sys_role_menu` VALUES (2495, '818cc6e1889d46579525ad8ab921eeb8', 9);
INSERT INTO `sys_role_menu` VALUES (2496, '9830d86487184961b90fc527c9604720', 9);
INSERT INTO `sys_role_menu` VALUES (2497, '81647226a2d047e8ab0b70472350ee69', 9);
INSERT INTO `sys_role_menu` VALUES (2498, '97f11d74c98047ba80f011a3da9d882c', 9);
INSERT INTO `sys_role_menu` VALUES (2499, '1a86a9d2b3ca49439277fff9f499c7cd', 9);
INSERT INTO `sys_role_menu` VALUES (2500, '29d33eba6b73420287d8f7e64aea62b3', 9);
INSERT INTO `sys_role_menu` VALUES (2501, 'ede76f5e60b640aa9de2ba7216b90ceb', 9);
INSERT INTO `sys_role_menu` VALUES (2502, '3a54d488132b4331bf3cd5e6d86ffcf4', 9);
INSERT INTO `sys_role_menu` VALUES (2503, 'b428eba3f9a34025a46c394df5390b88', 9);
INSERT INTO `sys_role_menu` VALUES (2504, '9e731ff422184fc1be2022c5c985735e', 9);
INSERT INTO `sys_role_menu` VALUES (2505, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 9);
INSERT INTO `sys_role_menu` VALUES (2506, '5b33ac3d630543d09d1388fae4d13fc0', 9);
INSERT INTO `sys_role_menu` VALUES (2507, '8d0b8b57a58e41a5a5e840cc2b3703f4', 9);
INSERT INTO `sys_role_menu` VALUES (2508, '8354d626cc65487594a7c38e98de1bad', 9);
INSERT INTO `sys_role_menu` VALUES (2509, '8fd6721941494fd5bbe16bec82b235be', 9);
INSERT INTO `sys_role_menu` VALUES (2510, '006bdbacd71a481f88b6acf895529acd', 9);
INSERT INTO `sys_role_menu` VALUES (2511, '3ba9407560a1490583fefa10b22bc74f', 9);
INSERT INTO `sys_role_menu` VALUES (2512, '85b54322630f43a39296488a5e76ba16', 9);
INSERT INTO `sys_role_menu` VALUES (2513, 'cb3500315dba4c2d83e4d92edf36dff7', 9);
INSERT INTO `sys_role_menu` VALUES (2514, '7391f12ad51049c2b86d231d39708c71', 9);
INSERT INTO `sys_role_menu` VALUES (2515, '73d312f4fa8949ddba3d9807c0c56f00', 9);
INSERT INTO `sys_role_menu` VALUES (2516, '91ccb13b5c174583803a4c492a5dfdb6', 9);
INSERT INTO `sys_role_menu` VALUES (2517, '8061d8e79be744bf91b7b438f8e8e887', 9);
INSERT INTO `sys_role_menu` VALUES (2518, '74d67db6e83d43d9ad8a7b20e95a7847', 9);
INSERT INTO `sys_role_menu` VALUES (2519, '6e45725f7d304e27badab761ea7d6598', 9);
INSERT INTO `sys_role_menu` VALUES (2520, '0bcaac4610e340d9877db7385ac76b35', 9);
INSERT INTO `sys_role_menu` VALUES (2521, 'd1e71a9419d146938442b4d9b90ffcb8', 9);
INSERT INTO `sys_role_menu` VALUES (2522, '16127584d8c9426f9091d2848f982822', 9);
INSERT INTO `sys_role_menu` VALUES (2523, '11e5781ef88a4bff82496c36c840882f', 9);
INSERT INTO `sys_role_menu` VALUES (2524, '4bdf3e32af464d1ab1877eeb3e6bf4eb', 9);
INSERT INTO `sys_role_menu` VALUES (2525, '4a6a115d70b548739d9a5e317c55f720', 9);
INSERT INTO `sys_role_menu` VALUES (2526, '3bf1c5d451284035b8a10430c4571d9b', 9);
INSERT INTO `sys_role_menu` VALUES (2527, '0ff78b2fd4be4ccba057ebd55a98810f', 9);
INSERT INTO `sys_role_menu` VALUES (2528, '7557b748089742558cdece286625d56c', 9);
INSERT INTO `sys_role_menu` VALUES (2529, '5969721f47ad45669425248fdc189de1', 9);
INSERT INTO `sys_role_menu` VALUES (2530, 'd65da65169754cbb9b51d9688f891e9f', 9);
INSERT INTO `sys_role_menu` VALUES (2531, '845dc562252143358cd1829c8b6ff190', 9);
INSERT INTO `sys_role_menu` VALUES (2532, '76961f7833794316a30e3eca9e4e53d8', 9);
INSERT INTO `sys_role_menu` VALUES (2533, 'f5bc5b3ab95b4f2d8b9efcf62469090f', 9);
INSERT INTO `sys_role_menu` VALUES (2534, '2c468a2cd0974739a1c55c189fe4e7c4', 9);
INSERT INTO `sys_role_menu` VALUES (2535, '1f855f45932746f49984c7b848aa4866', 9);
INSERT INTO `sys_role_menu` VALUES (2536, '49d8597562af42f0a2381a09bb786651', 9);
INSERT INTO `sys_role_menu` VALUES (2537, '2a5fc90593b64ef0a7ebb54d818c0776', 9);
INSERT INTO `sys_role_menu` VALUES (2538, 'da1b46db642f42978f83ed5eb34870ce', 9);
INSERT INTO `sys_role_menu` VALUES (2539, '0e529e8a9dbf450898b695e051c36d48', 9);
INSERT INTO `sys_role_menu` VALUES (2540, 'b5ce6412c26447348a7267de3ea11a21', 9);
INSERT INTO `sys_role_menu` VALUES (2541, '3d7eed8398d3457c897b2e8bf838e9c6', 9);
INSERT INTO `sys_role_menu` VALUES (2542, '310d02bb121645d1b7a7f949f48c981b', 9);
INSERT INTO `sys_role_menu` VALUES (2543, '2d6b78ad03de4cf1a3899f25cd7fe0ee', 9);
INSERT INTO `sys_role_menu` VALUES (2544, 'fbdcbcc0ccf547b4b78a4fc2cf303236', 9);
INSERT INTO `sys_role_menu` VALUES (2545, '012efc4ef8d24304a8562534f319524a', 9);
INSERT INTO `sys_role_menu` VALUES (2546, '1c2f19edf5a14a7e90724e7a0c63ec1e', 9);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `pwd` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '性别(0 未知 1 男 2 女)',
  `birthday` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生日',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `age` int NULL DEFAULT NULL COMMENT '年龄，--废弃，以生日为主',
  `id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `email` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `account_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户状态 (如 冻结；禁言；正常。 关联字典表account_status)',
  `user_tag_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '标签（自定义关联到字典表）',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'F' COMMENT '是否删除',
  `create_id` int NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username_index`(`username` ASC) USING BTREE,
  INDEX `create_time_index`(`create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$EZ9YQ.qlyrpTLs5FaIBS/uFdXQKrVXvWSwwUT7H3AjHx2aMiH3LVm', '199331231111', '系统管理员', 1, '', 'http://127.0.0.1:9001/sz-admin-test/user/20240420/c29bc1ba-0fe7-4aed-8887-9da3ee687006.jpg', 1, '', '', '1000001', '1001002', '2024-02-02 13:36:04', '2023-08-18 11:15:10', '2024-04-20 16:11:11', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (15, '2342', '$2a$10$96FDNq/IdXogtBuIEIkdN.vvV21XF.U6feMTGur/OjMHDe7KVQ6MK', NULL, '测试2342', NULL, NULL, NULL, 0, NULL, NULL, NULL, '', NULL, '2023-08-29 14:33:00', '2024-03-26 17:05:13', 'T', NULL, NULL);
INSERT INTO `sys_user` VALUES (16, '4324', '$2a$10$8dgA8AUrbctzUe7wtwa2y.vrq57uXOmpRBL5ZXUA23qjkuE0KW.re', NULL, '测试4324', 0, NULL, NULL, 0, '54324', NULL, NULL, '', NULL, '2023-08-29 14:33:34', '2024-03-26 17:05:19', 'T', NULL, NULL);
INSERT INTO `sys_user` VALUES (17, 'liuty', '$2a$10$kzrzhDlXW5qUPT7KgrnZMe.uAYJCY7K63sL3OeTRCV3xZKi4ChoMa', '25512341234', '升职哦', 1, '2024-04-18', 'http://127.0.0.1:9001/sz-admin-test/user/20240424/7f57329d-ea99-4dd0-b27f-e8482bb5b6fa.jpg', 1, '221111155511112222', '1312', '1000001', '', '2024-01-18 09:15:37', '2023-08-29 15:26:51', '2024-04-24 21:58:46', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (18, 'zhy', '$2a$10$96FDNq/IdXogtBuIEIkdN.vvV21XF.U6feMTGur/OjMHDe7KVQ6MK', '', '麻辣烫', 1, '', 'http://127.0.0.1:9001/sz-admin-test/user/20240420/c7045239-80e9-4191-b6a8-c47f2761ff01.jpg', 19, '', '', '1000001', '', '2023-08-30 15:10:49', '2023-08-30 15:08:46', '2024-04-29 05:37:14', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (19, 'test', '$2a$10$SYC4r1S6HhHjXwmd3z0Q0uOjKjnxt6b465Ry0iY2VYh45wHc76jaW', '', '测试', NULL, '', '', 0, '', '', '1000001', '', '2023-08-30 16:02:50', '2023-08-30 15:32:22', '2024-04-23 16:54:07', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (20, '25344', '$2a$10$.37TkVEgs4Osy1nqBsym.u08Ee6s.PD.qxk.mpqI3A5GoP18om0BK', '', '测试25344', NULL, '', 'http://127.0.0.1:9001/sz-admin-test/user/20240424/bacdc55e-4cc9-48a8-8d42-a4a5ae4f61f6.jpg', 0, '', '', '1000001', '', NULL, '2023-09-04 17:04:48', '2024-04-24 22:01:28', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (21, 'test11', '$2a$10$45ZoqNFSpcRRFTNmxjg4m.suIrIgsnWJnWCtnYJqkoV6.S2/PUdy6', '', 'test124231442', NULL, '', 'http://127.0.0.1:9001/sz-admin-test/user/20240424/e80b4163-d359-4be7-9ed5-e23c8fcdc509.jpg', 0, '', '', '1000001', '', NULL, '2023-09-08 08:32:55', '2024-04-24 21:59:41', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (22, '18186868846', '$2a$10$DMSlkYyIM05BOQobzBRuCeo4R0Cao9dQDGmL9PDIwUiMsWtRu1l4C', '', '18186868846', NULL, '', 'http://127.0.0.1:9000/sz-boot-parent-test/user/20240115/727581b0-114c-41e1-b092-31490bfbad4c.jpg', 0, '', '', '1000001', '', '2024-01-15 10:00:35', '2023-12-19 09:44:54', '2024-04-18 11:03:58', 'T', NULL, NULL);
INSERT INTO `sys_user` VALUES (23, '31', '$2a$10$BFmC05pnb4AULh55LdbZd.taHp9FxrVJi1.3cPaB.fjalH5ZWis3S', '321', '213', 1, NULL, '', 0, '421412', '321', '', '', NULL, '2024-04-12 10:08:48', '2024-04-12 10:24:08', 'T', NULL, NULL);
INSERT INTO `sys_user` VALUES (24, '31231231', '$2a$10$QEz/oW3HkRh/aqNN/kJmOed5XtoBGliGskLUXMUkPh9ijm/rVXUJm', '3', '132', 1, '2024-04-04', '', 0, '123', '3', '1000001', '1001003', NULL, '2024-04-12 10:48:18', '2024-04-22 10:35:52', 'T', NULL, NULL);
INSERT INTO `sys_user` VALUES (25, 'asdf34', '$2a$10$530qsorwsgbSdUZAOQyfdex7s4ONjTINYVV9APja8nKWDQeL4zIHO', '', '123', NULL, '', 'http://127.0.0.1:9001/sz-admin-test/user/20240424/4a37cf6b-2703-441e-9629-593a32ea2cca.png', 0, '', '', '1000001', '1001003', NULL, '2024-04-22 10:37:47', '2024-04-24 22:00:55', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (26, '3123123', '$2a$10$mbcJPPhpWS.GEXlLArj8yOyO79dKxny4PLPBDn59IvlJ0gb0Vw3Am', NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, '1000001', '1001003', NULL, '2024-04-29 06:12:51', '2024-04-29 06:12:51', 'F', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `dept_id` int NULL DEFAULT NULL COMMENT 'sys_dept_id',
  `user_id` int NULL DEFAULT NULL COMMENT 'sys_user_id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-部门关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
INSERT INTO `sys_user_dept` VALUES (44, 1, 17);
INSERT INTO `sys_user_dept` VALUES (45, 6, 17);
INSERT INTO `sys_user_dept` VALUES (46, 1, 18);
INSERT INTO `sys_user_dept` VALUES (47, 3, 19);
INSERT INTO `sys_user_dept` VALUES (48, 3, 20);
INSERT INTO `sys_user_dept` VALUES (52, 7, 24);
INSERT INTO `sys_user_dept` VALUES (53, 7, 22);
INSERT INTO `sys_user_dept` VALUES (60, 7, 21);
INSERT INTO `sys_user_dept` VALUES (61, 10, 21);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色id (sys_role_id)',
  `user_id` int NOT NULL COMMENT '用户id(sys_user_id)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户-角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (21, 9, 1);
INSERT INTO `sys_user_role` VALUES (22, 14, 1);
INSERT INTO `sys_user_role` VALUES (37, 15, 19);
INSERT INTO `sys_user_role` VALUES (38, 17, 19);
INSERT INTO `sys_user_role` VALUES (39, 15, 22);
INSERT INTO `sys_user_role` VALUES (40, 15, 17);
INSERT INTO `sys_user_role` VALUES (41, 16, 17);
INSERT INTO `sys_user_role` VALUES (42, 17, 17);

-- ----------------------------
-- Table structure for t_db_version
-- ----------------------------
DROP TABLE IF EXISTS `t_db_version`;
CREATE TABLE `t_db_version`  (
  `installed_rank` int NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `checksum` int NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `t_db_version_s_idx`(`success` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_db_version
-- ----------------------------
INSERT INTO `t_db_version` VALUES (1, '1.1', '20230509 Init DDL', 'SQL', 'V1.1__20230509_Init_DDL.sql', 2003887180, 'root', '2023-08-17 14:55:22', 8140, 1);

-- ----------------------------
-- Table structure for teacher_statistics
-- ----------------------------
DROP TABLE IF EXISTS `teacher_statistics`;
CREATE TABLE `teacher_statistics`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `year` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计年限',
  `month` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计月份',
  `during_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统计年月',
  `teacher_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师id',
  `teacher_common_type` int NOT NULL COMMENT '讲师区分类型',
  `total_teaching` int NULL DEFAULT NULL COMMENT '授课总数',
  `total_class_count` int NULL DEFAULT NULL COMMENT '服务班次数',
  `total_hours` decimal(10, 2) NULL DEFAULT NULL COMMENT '课时总数',
  `check_status` int NOT NULL DEFAULT 0 COMMENT '核对状态',
  `check_time` datetime NULL DEFAULT NULL COMMENT '核对时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '生成时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最近一次同步时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师统计总览表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher_statistics
-- ----------------------------
INSERT INTO `teacher_statistics` VALUES (3320, '2046', '12', '06', '4568965', 1000002, 14, 6, 12.00, 1000003, '2023-12-30 15:13:51', '2023-12-20 14:12:44', '2024-02-19 14:31:33', '2023-12-20 15:29:29', '斯33');
INSERT INTO `teacher_statistics` VALUES (3324, '2046', '01', '2046-01', '3', 1000001, 50, 62, 34.00, 1000002, '2024-03-04 09:13:06', '2024-03-04 09:13:12', '2024-03-04 09:13:52', '2024-03-04 09:13:07', '备注123');
INSERT INTO `teacher_statistics` VALUES (3325, '2047', '03', '2046-07', '3', 1000001, 3, 4, 5.00, 1000002, NULL, '2024-03-04 09:13:43', NULL, NULL, NULL);
INSERT INTO `teacher_statistics` VALUES (3326, '2047', '03', '2047-03', '4', 1000001, NULL, NULL, NULL, 1000001, NULL, '2024-03-04 09:14:19', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
