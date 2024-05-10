/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : 127.0.0.1:3306
 Source Schema         : sz_admin_preview

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 10/05/2024 23:00:45
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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of generator_table
-- ----------------------------
INSERT INTO `generator_table` VALUES (1, 'teacher_statistics', '教师统计总览表', 'TeacherStatistics', 'teacherStatistics', 'crud', 'com.sz.admin', 'teacherstatistics', 'teacherStatistics', '教师统计总览表', 'sz-admin', '0', NULL, '0', '/', 'E:\\dev\\Code\\Github\\sz-boot-parent\\sz-service\\sz-service-admin', '', '1', '1', '1', '1', 'all', 1, NULL, '2024-05-10 21:45:32', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Generator Table Column' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of generator_table_column
-- ----------------------------
INSERT INTO `generator_table_column` VALUES (1, 1, 'id', 'id', 'int', 'Long', 'input', 'number', NULL, 'id', 'Id', '1', '1', '0', '0', '1', '1', '0', '0', '0', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 1, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (2, 1, 'year', '统计年限', 'varchar(4)', 'String', 'input', 'string', NULL, 'year', 'Year', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 2, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (3, 1, 'month', '统计月份', 'varchar(2)', 'String', 'input', 'string', NULL, 'month', 'Month', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 3, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (4, 1, 'during_time', '统计年月', 'varchar(10)', 'String', 'date-picker', 'string', NULL, 'duringTime', 'DuringTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 4, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (5, 1, 'teacher_id', '教师id', 'varchar(32)', 'String', 'input', 'string', NULL, 'teacherId', 'TeacherId', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 5, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (6, 1, 'teacher_common_type', '讲师区分类型', 'int', 'Integer', 'select', 'number', NULL, 'teacherCommonType', 'TeacherCommonType', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'select', '', '0', NULL, 6, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (7, 1, 'total_teaching', '授课总数', 'int', 'Integer', 'input', 'number', NULL, 'totalTeaching', 'TotalTeaching', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 7, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (8, 1, 'total_class_count', '服务班次数', 'int', 'Integer', 'input', 'number', NULL, 'totalClassCount', 'TotalClassCount', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 8, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (9, 1, 'total_hours', '课时总数', 'decimal(10,2)', 'BigDecimal', 'input', 'number', 'java.math.BigDecimal', 'totalHours', 'TotalHours', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'input-number', '', '0', NULL, 9, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (10, 1, 'check_status', '核对状态', 'int', 'Integer', 'select', 'number', NULL, 'checkStatus', 'CheckStatus', '0', '0', '1', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'radio', '', '0', NULL, 10, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (11, 1, 'check_time', '核对时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'checkTime', 'CheckTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'datetime', '', '0', NULL, 11, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (12, 1, 'create_time', '生成时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'createTime', 'CreateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.INSERT', 'EQ', 'datetime', '', '0', NULL, 12, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (13, 1, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'updateTime', 'UpdateTime', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', 'FieldFill.UPDATE', 'EQ', 'datetime', '', '0', NULL, 13, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (14, 1, 'last_sync_time', '最近一次同步时间', 'datetime', 'LocalDateTime', 'date-picker', 'string', 'java.time.LocalDateTime', 'lastSyncTime', 'LastSyncTime', '0', '0', '0', '1', '1', '1', '1', '1', '1', '0', '0', NULL, 'EQ', 'datetime', '', '0', NULL, 14, 1, NULL, '2024-05-10 21:45:32', NULL);
INSERT INTO `generator_table_column` VALUES (15, 1, 'remark', '备注', 'varchar(255)', 'String', 'input', 'string', NULL, 'remark', 'Remark', '0', '0', '0', '1', '1', '1', '0', '1', '1', '0', '0', NULL, 'EQ', 'input', '', '0', NULL, 15, 1, NULL, '2024-05-10 21:45:32', NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mini_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`  (
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端key',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端秘钥',
  `grant_type_cd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'password' COMMENT '授权类型',
  `device_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int NULL DEFAULT 1800 COMMENT 'token活跃超时时间',
  `timeout` int NULL DEFAULT 604800 COMMENT 'token固定超时',
  `client_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '状态（正常 禁用）',
  `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'F' COMMENT '删除标志',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统授权表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_client
-- ----------------------------
INSERT INTO `sys_client` VALUES ('195da9fcce574852b850068771cde034', 'sz-admin', '839ce050d3814949af9b2e1f815bc620', 'password', '1004001', 86400, 604800, '1003001', 'F', NULL, 1, '2024-01-22 13:43:51', 1, '2024-04-12 16:06:49', 'web端client1');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主体名称', 'sys.dept.entityName', 'xx公司', 'T', 1, '2024-03-22 10:42:46', 1, '2024-05-10 19:55:41', '公司主体名称');
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
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '技术部', 0, 1, 100, 'T', 'F', 'F', NULL, 1, 1, '2024-05-10 21:40:03', '2024-05-10 21:40:46');
INSERT INTO `sys_dept` VALUES (2, '运营部', 0, 1, 200, 'T', 'F', 'F', NULL, 1, 1, '2024-05-10 21:40:13', '2024-05-10 21:41:34');
INSERT INTO `sys_dept` VALUES (3, '财务部', 0, 1, 300, 'T', 'F', 'F', NULL, 1, 1, '2024-05-10 21:40:19', '2024-05-10 21:42:03');
INSERT INTO `sys_dept` VALUES (4, '研发团队', 1, 3, 100, 'T', 'F', 'F', '', 1, 1, '2024-05-10 21:40:29', '2024-05-10 22:59:31');
INSERT INTO `sys_dept` VALUES (5, '测试团队', 1, 2, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:40:36', NULL);
INSERT INTO `sys_dept` VALUES (6, '运维团队', 1, 2, 300, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:40:46', NULL);
INSERT INTO `sys_dept` VALUES (7, '产品运营', 2, 2, 100, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:41:06', NULL);
INSERT INTO `sys_dept` VALUES (8, '用户运营', 2, 2, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:41:34', NULL);
INSERT INTO `sys_dept` VALUES (9, '会计团队', 3, 2, 100, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:41:49', NULL);
INSERT INTO `sys_dept` VALUES (10, '审计团队', 3, 2, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:42:03', NULL);
INSERT INTO `sys_dept` VALUES (11, '人力资源部', 0, 1, 400, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:42:19', NULL);
INSERT INTO `sys_dept` VALUES (12, '销售部', 0, 1, 500, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:42:27', NULL);
INSERT INTO `sys_dept` VALUES (13, '法务部', 0, 1, 600, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:42:37', NULL);
INSERT INTO `sys_dept` VALUES (14, '行政部', 0, 1, 700, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:42:43', NULL);
INSERT INTO `sys_dept` VALUES (15, '移动组', 4, 3, 100, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:43:28', NULL);
INSERT INTO `sys_dept` VALUES (16, '算法组', 4, 3, 200, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:43:36', NULL);
INSERT INTO `sys_dept` VALUES (17, '前端组', 4, 3, 300, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:43:44', NULL);
INSERT INTO `sys_dept` VALUES (18, '后端组', 4, 4, 400, 'T', 'F', 'F', '', 1, 1, '2024-05-10 21:43:53', '2024-05-10 21:44:12');
INSERT INTO `sys_dept` VALUES (19, '架构组', 4, 3, 500, 'F', 'F', 'F', NULL, 1, NULL, '2024-05-10 21:44:04', NULL);

-- ----------------------------
-- Table structure for sys_dept_closure
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_closure`;
CREATE TABLE `sys_dept_closure`  (
  `ancestor_id` int NOT NULL COMMENT '祖先节点ID',
  `descendant_id` int NOT NULL COMMENT '后代节点ID',
  `depth` int NOT NULL COMMENT '祖先节点到后代节点的距离'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门祖籍关系表（闭包表closure_table）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_closure
-- ----------------------------
INSERT INTO `sys_dept_closure` VALUES (1, 1, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 1, 1);
INSERT INTO `sys_dept_closure` VALUES (2, 2, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 2, 1);
INSERT INTO `sys_dept_closure` VALUES (3, 3, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 3, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 4, 2);
INSERT INTO `sys_dept_closure` VALUES (4, 4, 0);
INSERT INTO `sys_dept_closure` VALUES (1, 4, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 5, 2);
INSERT INTO `sys_dept_closure` VALUES (5, 5, 0);
INSERT INTO `sys_dept_closure` VALUES (1, 5, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 6, 2);
INSERT INTO `sys_dept_closure` VALUES (6, 6, 0);
INSERT INTO `sys_dept_closure` VALUES (1, 6, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 7, 2);
INSERT INTO `sys_dept_closure` VALUES (7, 7, 0);
INSERT INTO `sys_dept_closure` VALUES (2, 7, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 8, 2);
INSERT INTO `sys_dept_closure` VALUES (8, 8, 0);
INSERT INTO `sys_dept_closure` VALUES (2, 8, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 9, 2);
INSERT INTO `sys_dept_closure` VALUES (9, 9, 0);
INSERT INTO `sys_dept_closure` VALUES (3, 9, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 10, 2);
INSERT INTO `sys_dept_closure` VALUES (10, 10, 0);
INSERT INTO `sys_dept_closure` VALUES (3, 10, 1);
INSERT INTO `sys_dept_closure` VALUES (11, 11, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 11, 1);
INSERT INTO `sys_dept_closure` VALUES (12, 12, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 12, 1);
INSERT INTO `sys_dept_closure` VALUES (13, 13, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 13, 1);
INSERT INTO `sys_dept_closure` VALUES (14, 14, 0);
INSERT INTO `sys_dept_closure` VALUES (0, 14, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 15, 3);
INSERT INTO `sys_dept_closure` VALUES (1, 15, 2);
INSERT INTO `sys_dept_closure` VALUES (15, 15, 0);
INSERT INTO `sys_dept_closure` VALUES (4, 15, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 16, 3);
INSERT INTO `sys_dept_closure` VALUES (1, 16, 2);
INSERT INTO `sys_dept_closure` VALUES (16, 16, 0);
INSERT INTO `sys_dept_closure` VALUES (4, 16, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 17, 3);
INSERT INTO `sys_dept_closure` VALUES (1, 17, 2);
INSERT INTO `sys_dept_closure` VALUES (17, 17, 0);
INSERT INTO `sys_dept_closure` VALUES (4, 17, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 18, 3);
INSERT INTO `sys_dept_closure` VALUES (1, 18, 2);
INSERT INTO `sys_dept_closure` VALUES (18, 18, 0);
INSERT INTO `sys_dept_closure` VALUES (4, 18, 1);
INSERT INTO `sys_dept_closure` VALUES (0, 19, 3);
INSERT INTO `sys_dept_closure` VALUES (1, 19, 2);
INSERT INTO `sys_dept_closure` VALUES (19, 19, 0);
INSERT INTO `sys_dept_closure` VALUES (4, 19, 1);

-- ----------------------------
-- Table structure for sys_dept_leader
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_leader`;
CREATE TABLE `sys_dept_leader`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `dept_id` int NULL DEFAULT NULL,
  `leader_id` int NULL DEFAULT NULL COMMENT 'sys_user_id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门领导人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_leader
-- ----------------------------
INSERT INTO `sys_dept_leader` VALUES (1, 4, 362);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1000, '账户状态', 'account_status', 'T', 'T', 'F', '', '2023-08-20 11:09:46', '2023-08-28 16:25:59', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1001, '用户标签', 'user_tag', 'F', 'T', 'F', '', '2023-08-20 14:22:40', '2023-08-24 15:24:58', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1002, '菜单类型', 'menu_type', 'T', 'T', 'F', '', '2023-08-21 11:20:47', '2023-08-28 16:25:55', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1003, '授权状态', 'sys_client_status', 'T', 'T', 'F', 'client授权状态', '2023-08-22 09:44:27', '2024-04-12 16:56:40', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1004, '设备类型', 'device_type', 'T', 'T', 'F', '', '2023-08-22 10:02:11', '2024-04-12 16:56:49', NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1005, '授权类型', 'grant_type', 'T', 'T', 'F', '', '2023-08-22 10:15:58', '2024-04-12 16:57:04', NULL, NULL, NULL, NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '导出信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_export_info
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

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
  `menu_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单类型 （字典表menu_type）',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('006bdbacd71a481f88b6acf895529acd', '8354d626cc65487594a7c38e98de1bad', '', '', '修改', '', '', '', 200, 3, '1002003', 'sys.dept.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-05-10 20:54:02', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('012efc4ef8d24304a8562534f319524a', '0e529e8a9dbf450898b695e051c36d48', '', '', '预览按钮', '', '', '', 600, 3, '1002003', 'generator.preview', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:23:47', '2024-05-10 20:54:31', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('05194ef5fa7a4a308a44f6f5c6791c3a', '99c2ee7b882749e597bcd62385f368fb', '', '', '编辑菜单', '', '', '', 200, 3, '1002003', 'sys.menu.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:31:35', '2024-05-10 20:41:15', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0933b165ffc14d558e8de43ccb6687f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '编辑角色', '', '', '', 200, 3, '1002003', 'sys.role.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:43', '2024-05-10 20:41:01', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0e529e8a9dbf450898b695e051c36d48', 'da1b46db642f42978f83ed5eb34870ce', '/toolbox/generator', 'generator', '代码生成', 'Brush', '/toolbox/generator/index', '', 100, 2, '1002002', '', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:50:39', '2024-05-10 20:39:59', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('0f98b89c67e54cb0bcff2b56aa98832f', '140c9ed43ef54542bbcdde8a5d928400', '', '', '新增账号', '', '', '', 100, 3, '1002003', 'sys.user.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:55', '2024-05-10 20:40:31', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('13f06419e1d74bda97c0709e3ea0c8f0', 'b06f77bf931947c78cc5883b465bbd19', '', '', '导入', '', '', '', 4, 1, '1002003', 'teacher.statistics.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:48', '2024-05-10 21:45:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('140c9ed43ef54542bbcdde8a5d928400', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/accountManage', 'accountManage', '账号管理', 'UserFilled', '/system/accountManage/index', '', 100, 2, '1002002', 'sys.user.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:53:49', '2024-05-10 20:39:24', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('17fc60357760477d8d3ca9619e9b87c4', 'b06f77bf931947c78cc5883b465bbd19', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:48', '2024-05-10 21:45:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('1a86a9d2b3ca49439277fff9f499c7cd', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典', '', '', '', 600, 3, '1002003', 'sys.dict.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:58:26', '2024-05-10 20:41:45', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('26ecd3786ec74a369074f84b9269bc41', 'b06f77bf931947c78cc5883b465bbd19', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:48', '2024-05-10 21:45:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('29d33eba6b73420287d8f7e64aea62b3', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/configManage', 'configManage', '参数管理', 'Key', '/system/configManage/index', '', 500, 2, '1002002', 'sys.config.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-11-24 09:54:25', '2024-05-10 20:39:43', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('2d6b78ad03de4cf1a3899f25cd7fe0ee', '0e529e8a9dbf450898b695e051c36d48', '', '', '删除按钮', '', '', '', 400, 3, '1002003', 'generator.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:22:50', '2024-05-10 20:54:24', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('307951cd49394947b1b7f7dca2266546', '43866b83ef834b73adbd95338f9e606b', '', '', '导出', '', '', '', 5, 1, '1002003', 'teacher.statistics.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:32', '2024-05-10 21:45:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('310d02bb121645d1b7a7f949f48c981b', '0e529e8a9dbf450898b695e051c36d48', '', '', '生成按钮', '', '', '', 300, 3, '1002003', 'generator.generator', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:21:47', '2024-05-10 20:54:20', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('330a1a0a857c4ad1a95327db5134e420', '140c9ed43ef54542bbcdde8a5d928400', '', '', '解锁', '', '', '', 500, 3, '1002003', 'sys.user.unlock_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-29 14:06:49', '2024-05-10 20:40:49', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3a54d488132b4331bf3cd5e6d86ffcf4', '29d33eba6b73420287d8f7e64aea62b3', '', '', '修改参数', '', '', '', 200, 3, '1002003', 'sys.config.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:38', '2024-05-10 20:42:50', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3ba9407560a1490583fefa10b22bc74f', '8354d626cc65487594a7c38e98de1bad', '', '', '删除', '', '', '', 300, 3, '1002003', 'sys.dept.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-05-10 20:54:05', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('3d7eed8398d3457c897b2e8bf838e9c6', '0e529e8a9dbf450898b695e051c36d48', '', '', '编辑按钮', '', '', '', 200, 3, '1002003', 'generator.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:21:05', '2024-05-10 20:54:15', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('445b73dda9a34ad681d2705a7abcf2f6', 'c6dd479d5b304731be403d7551c60d70', '', '', '删除角色', '', '', '', 300, 3, '1002003', 'sys.role.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:05', '2024-05-10 20:41:04', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('4f39ef0fd2f748f6ab7d6d20d98bc4af', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典类型', '', '', '', 100, 3, '1002003', 'sys.dict.add_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 15:52:38', '2024-05-10 20:41:28', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5b33ac3d630543d09d1388fae4d13fc0', '9e731ff422184fc1be2022c5c985735e', '', '', '修改', '', '', '', 200, 3, '1002003', 'sys.client.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-05-10 20:53:52', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('5b5fb3748c6a4ed5a4dda3877508c3a7', 'c6dd479d5b304731be403d7551c60d70', '', '', '设置权限', '', '', '', 400, 3, '1002003', 'sys.role.setting_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:31:35', '2024-05-10 20:41:07', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('60966137f6de4d80a0bc6e7781f7df12', '43866b83ef834b73adbd95338f9e606b', '', '', '修改', '', '', '', 2, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:32', '2024-05-10 21:45:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('63e71c4867974a86a71e3df8bcd3e2e6', '43866b83ef834b73adbd95338f9e606b', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:32', '2024-05-10 21:45:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('686a5522b0334d4da51aa15b3fd1a303', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置部门', '', '', '', 700, 3, '1002003', 'sys.user.dept_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-12 14:33:21', '2024-04-12 14:33:21', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6c15038105da47fa9d519b9cdec7f1cf', '43866b83ef834b73adbd95338f9e606b', '', '', '新增', '', '', '', 1, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:32', '2024-05-10 21:45:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6c46fd01faf042fc9dd4a9c9b9ef2c5a', '9e731ff422184fc1be2022c5c985735e', '', '', '新增', '', '', '', 100, 3, '1002003', 'sys.client.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-05-10 20:53:49', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('6e25a716c1a646009a9be90b16f0a682', '140c9ed43ef54542bbcdde8a5d928400', '', '', '设置角色', '', '', '', 400, 3, '1002003', 'sys.user.role_set_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:53', '2024-05-10 20:40:46', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7391f12ad51049c2b86d231d39708c71', '85b54322630f43a39296488a5e76ba16', '', '', '修改', '', '', '', 200, 1, '1002003', 'teacher.statistics.update', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:54:38', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('73d312f4fa8949ddba3d9807c0c56f00', '85b54322630f43a39296488a5e76ba16', '', '', '删除', '', '', '', 300, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:54:42', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('7abbd9193f9746679e3eead86697e860', '43866b83ef834b73adbd95338f9e606b', '', '', '导入', '', '', '', 4, 1, '1002003', 'teacher.statistics.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:32', '2024-05-10 21:45:32', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8061d8e79be744bf91b7b438f8e8e887', '85b54322630f43a39296488a5e76ba16', '', '', '导出', '', '', '', 500, 1, '1002003', 'teacher.statistics.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:54:49', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('81647226a2d047e8ab0b70472350ee69', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '新增字典', '', '', '', 400, 3, '1002003', 'sys.dict.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:54:55', '2024-05-10 20:41:37', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('818cc6e1889d46579525ad8ab921eeb8', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典类型', '', '', '', 200, 3, '1002003', 'sys.dict.update_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:39:29', '2024-05-10 20:41:31', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8255bac5eae748a0a8500167963b3e00', '140c9ed43ef54542bbcdde8a5d928400', '', '', '编辑账号', '', '', '', 200, 3, '1002003', 'sys.user.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:32:15', '2024-05-10 20:40:35', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('82f4776aaf714700802cfb6c93dffa15', 'b06f77bf931947c78cc5883b465bbd19', '', '', '导出', '', '', '', 5, 1, '1002003', 'teacher.statistics.export', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:48', '2024-05-10 21:45:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8354d626cc65487594a7c38e98de1bad', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/deptManage', 'SysDeptView', '部门管理', 'svg-org', '/system/deptManage/index', '', 700, 2, '1002002', 'sys.dept.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-05-10 20:39:51', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('85b54322630f43a39296488a5e76ba16', '0', '/teacher/teacherStatistics', 'TeacherStatisticsView', '教师统计', 'svg-org', '/teacher/teacherStatistics/index', '', 300, 0, '1002002', 'teacher.statistics.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:39:09', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('88b2e5def2ff474fa8bf3537d4a2fe5b', '0', '/system', 'system', '系统管理', 'Tools', '', '', 100, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:52:10', '2024-05-10 20:38:55', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8d0b8b57a58e41a5a5e840cc2b3703f4', '9e731ff422184fc1be2022c5c985735e', '', '', '删除', '', '', '', 300, 3, '1002003', 'sys.client.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-05-10 20:53:55', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('8fd6721941494fd5bbe16bec82b235be', '8354d626cc65487594a7c38e98de1bad', '', '', '新增', '', '', '', 100, 3, '1002003', 'sys.dept.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-03-19 15:42:48', '2024-05-10 20:53:59', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('91ccb13b5c174583803a4c492a5dfdb6', '85b54322630f43a39296488a5e76ba16', '', '', '导入', '', '', '', 400, 1, '1002003', 'teacher.statistics.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:54:45', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9338bf2f57984825bc227bb618f9db81', '99c2ee7b882749e597bcd62385f368fb', '', '', '新增菜单', '', '', '', 100, 3, '1002003', 'sys.menu.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 14:27:50', '2024-05-10 20:41:13', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('97f11d74c98047ba80f011a3da9d882c', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '编辑字典', '', '', '', 500, 3, '1002003', 'sys.dict.update_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:55:13', '2024-05-10 20:41:42', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9830d86487184961b90fc527c9604720', 'dcb6aabcd910469ebf3efbc7e43282d4', '', '', '删除字典类型', '', '', '', 300, 3, '1002003', 'sys.dict.delete_type_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-04 13:57:33', '2024-05-10 20:41:34', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('99c2ee7b882749e597bcd62385f368fb', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/menuMange', 'menuMange', '菜单管理', 'Menu', '/system/menuMange/index', '', 300, 2, '1002002', 'sys.menu.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:55:30', '2024-05-10 20:39:34', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('9e731ff422184fc1be2022c5c985735e', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/clientManage', 'ClientManageView', '客户端管理', 'Operation', '/system/clientManage/index', '', 600, 2, '1002002', 'sys.client.query_table', 'F', 'T', 'F', 'F', 'F', 'F', '2024-01-22 10:52:09', '2024-05-10 20:39:47', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b428eba3f9a34025a46c394df5390b88', '29d33eba6b73420287d8f7e64aea62b3', '', '', '删除参数', '', '', '', 300, 3, '1002003', 'sys.config.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:58:06', '2024-05-10 20:53:43', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('b5ce6412c26447348a7267de3ea11a21', '0e529e8a9dbf450898b695e051c36d48', '', '', '导入按钮', '', '', '', 100, 3, '1002003', 'generator.import', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:19:21', '2024-05-10 20:54:12', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('c6dd479d5b304731be403d7551c60d70', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/roleManage', 'roleManage', '角色管理', 'User', '/system/roleManage/index', '', 200, 2, '1002002', 'sys.role.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:54:36', '2024-05-10 20:39:30', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('cb3500315dba4c2d83e4d92edf36dff7', '85b54322630f43a39296488a5e76ba16', '', '', '新增', '', '', '', 100, 1, '1002003', 'teacher.statistics.create', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-17 10:19:32', '2024-05-10 20:54:35', '', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('cea01dcde9b24b5a8686bdc33c438cd7', '140c9ed43ef54542bbcdde8a5d928400', '', '', '删除账号', '', '', '', 300, 3, '1002003', 'sys.user.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:33:27', '2024-05-10 20:40:41', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('da1b46db642f42978f83ed5eb34870ce', '0', '/toolbox', 'toolbox', '工具箱', 'Briefcase', '', '', 200, 1, '1002001', '', 'F', 'T', 'F', 'F', 'F', 'F', '2023-12-08 13:46:08', '2024-05-10 20:39:17', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('dcb6aabcd910469ebf3efbc7e43282d4', '88b2e5def2ff474fa8bf3537d4a2fe5b', '/system/dictManage', 'dictManage', '字典管理', 'Reading', '/system/dictManage/index', '', 400, 2, '1002002', 'sys.dict.query_table', 'F', 'T', 'F', 'F', 'F', 'T', '2023-08-29 13:57:12', '2024-05-10 20:39:39', '8', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('df2894b4c06e47cab84142d81edc494d', 'c6dd479d5b304731be403d7551c60d70', '', '', '新增角色', '', '', '', 100, 3, '1002003', 'sys.role.create_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-09-06 10:30:18', '2024-05-10 20:40:57', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e19083a9ac404698b4362d63d7048b7b', 'b06f77bf931947c78cc5883b465bbd19', '', '', '删除', '', '', '', 3, 1, '1002003', 'teacher.statistics.remove', 'F', 'F', 'F', 'F', 'F', 'F', '2024-05-10 21:45:48', '2024-05-10 21:45:48', '', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('e91eeaea8f1546d3921839469fe247b6', '140c9ed43ef54542bbcdde8a5d928400', '', '', '重置密码', '', '', '', 600, 3, '1002003', 'sys.user_resetPwd', 'F', 'F', 'F', 'F', 'F', 'F', '2024-04-10 10:16:08', '2024-04-10 10:16:08', '1', '', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ede76f5e60b640aa9de2ba7216b90ceb', '29d33eba6b73420287d8f7e64aea62b3', '', '', '新增参数', '', '', '', 100, 3, '1002003', 'sys.config.add_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-11-24 09:57:19', '2024-05-10 20:42:46', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('ee36ad68586e42fa8a896215c544cb76', '99c2ee7b882749e597bcd62385f368fb', '', '', 'SQL按钮', '', '', '', 400, 3, '1002003', 'sys.menu.sql_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2024-01-11 09:41:47', '2024-05-10 20:41:21', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('fa0c65ad783d4bf9b919a6db02ef1428', '99c2ee7b882749e597bcd62385f368fb', '', '', '删除菜单', '', '', '', 300, 3, '1002003', 'sys.menu.delete_btn', 'F', 'F', 'F', 'F', 'F', 'F', '2023-08-31 16:28:57', '2024-05-10 20:41:18', '1', '1', 'F', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('fbdcbcc0ccf547b4b78a4fc2cf303236', '0e529e8a9dbf450898b695e051c36d48', '', '', 'zip下载按钮', '', '', '', 500, 3, '1002003', 'generator.zip', 'F', 'F', 'F', 'F', 'F', 'F', '2024-02-15 10:23:17', '2024-05-10 20:54:27', '1', '1', 'F', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', '', 'F', '2024-05-10 21:28:31', '2024-05-10 21:28:31', NULL, NULL);
INSERT INTO `sys_role` VALUES (2, '字典管理', '', 'F', '2024-05-10 21:52:39', '2024-05-10 21:52:39', NULL, NULL);
INSERT INTO `sys_role` VALUES (3, '教师统计', '', 'F', '2024-05-10 21:53:15', '2024-05-10 21:53:15', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
  `role_id` int NOT NULL COMMENT 'sys_role_id （角色表）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色-菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, '88b2e5def2ff474fa8bf3537d4a2fe5b', 1);
INSERT INTO `sys_role_menu` VALUES (2, '140c9ed43ef54542bbcdde8a5d928400', 1);
INSERT INTO `sys_role_menu` VALUES (3, '0f98b89c67e54cb0bcff2b56aa98832f', 1);
INSERT INTO `sys_role_menu` VALUES (4, '8255bac5eae748a0a8500167963b3e00', 1);
INSERT INTO `sys_role_menu` VALUES (5, 'cea01dcde9b24b5a8686bdc33c438cd7', 1);
INSERT INTO `sys_role_menu` VALUES (6, '6e25a716c1a646009a9be90b16f0a682', 1);
INSERT INTO `sys_role_menu` VALUES (7, '330a1a0a857c4ad1a95327db5134e420', 1);
INSERT INTO `sys_role_menu` VALUES (8, 'e91eeaea8f1546d3921839469fe247b6', 1);
INSERT INTO `sys_role_menu` VALUES (9, '686a5522b0334d4da51aa15b3fd1a303', 1);
INSERT INTO `sys_role_menu` VALUES (10, 'c6dd479d5b304731be403d7551c60d70', 1);
INSERT INTO `sys_role_menu` VALUES (11, 'df2894b4c06e47cab84142d81edc494d', 1);
INSERT INTO `sys_role_menu` VALUES (12, '0933b165ffc14d558e8de43ccb6687f6', 1);
INSERT INTO `sys_role_menu` VALUES (13, '445b73dda9a34ad681d2705a7abcf2f6', 1);
INSERT INTO `sys_role_menu` VALUES (14, '5b5fb3748c6a4ed5a4dda3877508c3a7', 1);
INSERT INTO `sys_role_menu` VALUES (15, '99c2ee7b882749e597bcd62385f368fb', 1);
INSERT INTO `sys_role_menu` VALUES (16, '9338bf2f57984825bc227bb618f9db81', 1);
INSERT INTO `sys_role_menu` VALUES (17, '05194ef5fa7a4a308a44f6f5c6791c3a', 1);
INSERT INTO `sys_role_menu` VALUES (18, 'fa0c65ad783d4bf9b919a6db02ef1428', 1);
INSERT INTO `sys_role_menu` VALUES (19, 'ee36ad68586e42fa8a896215c544cb76', 1);
INSERT INTO `sys_role_menu` VALUES (20, 'dcb6aabcd910469ebf3efbc7e43282d4', 1);
INSERT INTO `sys_role_menu` VALUES (21, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 1);
INSERT INTO `sys_role_menu` VALUES (22, '818cc6e1889d46579525ad8ab921eeb8', 1);
INSERT INTO `sys_role_menu` VALUES (23, '9830d86487184961b90fc527c9604720', 1);
INSERT INTO `sys_role_menu` VALUES (24, '81647226a2d047e8ab0b70472350ee69', 1);
INSERT INTO `sys_role_menu` VALUES (25, '97f11d74c98047ba80f011a3da9d882c', 1);
INSERT INTO `sys_role_menu` VALUES (26, '1a86a9d2b3ca49439277fff9f499c7cd', 1);
INSERT INTO `sys_role_menu` VALUES (27, '29d33eba6b73420287d8f7e64aea62b3', 1);
INSERT INTO `sys_role_menu` VALUES (28, 'ede76f5e60b640aa9de2ba7216b90ceb', 1);
INSERT INTO `sys_role_menu` VALUES (29, '3a54d488132b4331bf3cd5e6d86ffcf4', 1);
INSERT INTO `sys_role_menu` VALUES (30, 'b428eba3f9a34025a46c394df5390b88', 1);
INSERT INTO `sys_role_menu` VALUES (31, '9e731ff422184fc1be2022c5c985735e', 1);
INSERT INTO `sys_role_menu` VALUES (32, '6c46fd01faf042fc9dd4a9c9b9ef2c5a', 1);
INSERT INTO `sys_role_menu` VALUES (33, '5b33ac3d630543d09d1388fae4d13fc0', 1);
INSERT INTO `sys_role_menu` VALUES (34, '8d0b8b57a58e41a5a5e840cc2b3703f4', 1);
INSERT INTO `sys_role_menu` VALUES (35, '8354d626cc65487594a7c38e98de1bad', 1);
INSERT INTO `sys_role_menu` VALUES (36, '8fd6721941494fd5bbe16bec82b235be', 1);
INSERT INTO `sys_role_menu` VALUES (37, '006bdbacd71a481f88b6acf895529acd', 1);
INSERT INTO `sys_role_menu` VALUES (38, '3ba9407560a1490583fefa10b22bc74f', 1);
INSERT INTO `sys_role_menu` VALUES (39, '85b54322630f43a39296488a5e76ba16', 1);
INSERT INTO `sys_role_menu` VALUES (40, 'cb3500315dba4c2d83e4d92edf36dff7', 1);
INSERT INTO `sys_role_menu` VALUES (41, '7391f12ad51049c2b86d231d39708c71', 1);
INSERT INTO `sys_role_menu` VALUES (42, '73d312f4fa8949ddba3d9807c0c56f00', 1);
INSERT INTO `sys_role_menu` VALUES (43, '91ccb13b5c174583803a4c492a5dfdb6', 1);
INSERT INTO `sys_role_menu` VALUES (44, '8061d8e79be744bf91b7b438f8e8e887', 1);
INSERT INTO `sys_role_menu` VALUES (45, 'da1b46db642f42978f83ed5eb34870ce', 1);
INSERT INTO `sys_role_menu` VALUES (46, '0e529e8a9dbf450898b695e051c36d48', 1);
INSERT INTO `sys_role_menu` VALUES (47, 'b5ce6412c26447348a7267de3ea11a21', 1);
INSERT INTO `sys_role_menu` VALUES (48, '3d7eed8398d3457c897b2e8bf838e9c6', 1);
INSERT INTO `sys_role_menu` VALUES (49, '310d02bb121645d1b7a7f949f48c981b', 1);
INSERT INTO `sys_role_menu` VALUES (50, '2d6b78ad03de4cf1a3899f25cd7fe0ee', 1);
INSERT INTO `sys_role_menu` VALUES (51, 'fbdcbcc0ccf547b4b78a4fc2cf303236', 1);
INSERT INTO `sys_role_menu` VALUES (52, '012efc4ef8d24304a8562534f319524a', 1);
INSERT INTO `sys_role_menu` VALUES (53, 'dcb6aabcd910469ebf3efbc7e43282d4', 2);
INSERT INTO `sys_role_menu` VALUES (54, '4f39ef0fd2f748f6ab7d6d20d98bc4af', 2);
INSERT INTO `sys_role_menu` VALUES (55, '818cc6e1889d46579525ad8ab921eeb8', 2);
INSERT INTO `sys_role_menu` VALUES (56, '9830d86487184961b90fc527c9604720', 2);
INSERT INTO `sys_role_menu` VALUES (57, '81647226a2d047e8ab0b70472350ee69', 2);
INSERT INTO `sys_role_menu` VALUES (58, '97f11d74c98047ba80f011a3da9d882c', 2);
INSERT INTO `sys_role_menu` VALUES (59, '1a86a9d2b3ca49439277fff9f499c7cd', 2);
INSERT INTO `sys_role_menu` VALUES (60, '88b2e5def2ff474fa8bf3537d4a2fe5b', 2);
INSERT INTO `sys_role_menu` VALUES (61, '85b54322630f43a39296488a5e76ba16', 3);
INSERT INTO `sys_role_menu` VALUES (62, 'cb3500315dba4c2d83e4d92edf36dff7', 3);
INSERT INTO `sys_role_menu` VALUES (63, '7391f12ad51049c2b86d231d39708c71', 3);
INSERT INTO `sys_role_menu` VALUES (64, '73d312f4fa8949ddba3d9807c0c56f00', 3);
INSERT INTO `sys_role_menu` VALUES (65, '91ccb13b5c174583803a4c492a5dfdb6', 3);
INSERT INTO `sys_role_menu` VALUES (66, '8061d8e79be744bf91b7b438f8e8e887', 3);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$EZ9YQ.qlyrpTLs5FaIBS/uFdXQKrVXvWSwwUT7H3AjHx2aMiH3LVm', '19988887777', '系统管理员', 1, '2022-01-01', 'http://59.110.220.106:9001/sz-admin-test/user/20240420/b2f4c7d4-cf47-4a26-a714-4974005b789d.jpg', 1, '', '', '1000001', '1001003', '2024-02-02 13:36:04', '2023-08-18 11:15:10', '2024-05-10 22:24:53', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$Km4pU/DdW/.LXRYgR446S.HCdcjIHkp7uFisXtCVoaXyXfveBHjlO', NULL, '测试用户', 1, '2024-01-01', NULL, 0, NULL, NULL, '1000001', '1001003', NULL, '2024-05-09 21:50:02', '2024-05-10 22:30:54', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (3, 'anqishi609', '', '289185733', '石安琪', 0, '2008-01-24', 'https://image.jamieort629.xyz/CellPhonesAccessories', 39, '5395996248624422', 'anqi45@hotmail.com', '1000001', '1001003', '2019-02-05 03:47:18', '2024-05-12 17:52:11', '2024-05-10 22:24:53', 'F', 440, 606);
INSERT INTO `sys_user` VALUES (4, 'yunxitang', '', '75544298327', '汤云熙', 0, '2020-06-11', 'http://drive.kafailoui10.org/Handcrafts', 75, '6227697151319628', 'yunxitang@mail.com', '1000001', '1001003', '2010-12-27 10:28:18', '2024-05-10 14:50:09', '2024-05-10 22:24:53', 'T', 441, 614);
INSERT INTO `sys_user` VALUES (5, 'hoa818', '', '19130079363', '侯安琪', 0, '2011-12-02', 'http://image.pan43.us/ClothingShoesandJewelry', 34, '373194120764212', 'ahou@icloud.com', '1000001', '1001003', '2022-06-15 00:35:50', '2024-05-16 10:44:57', '2024-05-10 22:24:53', 'F', 794, 843);
INSERT INTO `sys_user` VALUES (6, 'zhennt1204', '', '2131572348', '汤震南', 1, '2018-05-26', 'http://image.kazumasa.net/ToysGames', 35, '4211480643548846', 'tzhennan@mail.com', '1000001', '1001003', '2020-12-03 09:58:21', '2024-05-18 04:15:39', '2024-05-10 22:24:53', 'T', 563, 484);
INSERT INTO `sys_user` VALUES (7, 'xuy', '', '17717671922', '徐宇宁', 1, '2023-08-11', 'http://auth.sandersjosephine.org/SportsOutdoor', 76, '4362222037231903', 'xuy@mail.com', '1000001', '1001003', '2007-08-21 17:58:24', '2024-05-31 16:41:33', '2024-05-10 22:24:53', 'T', 621, 581);
INSERT INTO `sys_user` VALUES (8, 'lurui', '', '15742835010', '陆睿', 0, '2018-08-10', 'http://auth.ahe.jp/ArtsHandicraftsSewing', 16, '375791789582260', 'ruilu@mail.com', '1000001', '1001003', '2003-06-09 20:05:49', '2024-06-02 14:50:34', '2024-05-10 22:24:53', 'F', 708, 285);
INSERT INTO `sys_user` VALUES (9, 'jyu', '', '76999780362', '江云熙', 1, '2023-06-05', 'https://drive.shikaru3.net/BeautyPersonalCare', 73, '3559492522320843', 'jiayunx1112@gmail.com', '1000001', '1001003', '2001-09-28 18:30:42', '2024-05-11 15:29:17', '2024-05-10 22:24:53', 'F', 428, 431);
INSERT INTO `sys_user` VALUES (10, 'zengshihan201', '', '17944178962', '曾詩涵', 1, '2003-04-16', 'http://video.mosk.xyz/CellPhonesAccessories', 62, '371275526796973', 'shihanzeng@yahoo.com', '1000001', '1001003', '2002-10-08 05:54:56', '2024-06-04 19:03:46', '2024-05-10 22:24:53', 'T', 928, 210);
INSERT INTO `sys_user` VALUES (11, 'ruish4', '', '15286961569', '史睿', 1, '2002-10-26', 'http://drive.cn14.net/AppsGames', 53, '4426488000585013', 'ruish@outlook.com', '1000001', '1001003', '2018-04-05 15:37:02', '2024-05-23 01:54:38', '2024-05-10 22:24:53', 'T', 48, 207);
INSERT INTO `sys_user` VALUES (12, 'liuji729', '', '2844575642', '刘嘉伦', 0, '2020-09-03', 'https://auth.john1212.xyz/Books', 22, '6221062506855352', 'liji@icloud.com', '1000001', '1001003', '2023-05-30 08:05:43', '2024-05-23 13:06:25', '2024-05-10 22:24:53', 'T', 687, 634);
INSERT INTO `sys_user` VALUES (13, 'shihanyin', '', '107035138', '尹詩涵', 0, '2021-05-26', 'https://video.tszchingchow.biz/ComputersElectronics', 67, '4255992899428025', 'yin4@outlook.com', '1000001', '1001003', '2006-05-04 18:07:30', '2024-05-29 14:16:05', '2024-05-10 22:24:53', 'F', 261, 559);
INSERT INTO `sys_user` VALUES (14, 'jgao', '', '18695497557', '高杰宏', 1, '2010-04-06', 'https://image.aoshi6.co.jp/HouseholdKitchenAppliances', 42, '3545908499131373', 'gaojiehong2@hotmail.com', '1000001', '1001003', '2023-02-01 21:01:45', '2024-05-10 13:36:49', '2024-05-10 22:24:53', 'F', 45, 858);
INSERT INTO `sys_user` VALUES (15, 'liuyu68', '', '19832254326', '刘宇宁', 0, '2010-05-09', 'https://drive.kaows3.info/ComputersElectronics', 70, '3588901464171621', 'liy6@mail.com', '1000001', '1001003', '2019-02-01 20:12:31', '2024-06-10 12:48:58', '2024-05-10 22:24:53', 'T', 724, 378);
INSERT INTO `sys_user` VALUES (16, 'ma6', '', '76912538387', '马嘉伦', 1, '2012-10-19', 'http://auth.yqin.org/ComputersElectronics', 60, '342848598687225', 'jialuma66@icloud.com', '1000001', '1001003', '2019-07-02 05:07:00', '2024-05-31 09:32:53', '2024-05-10 22:24:53', 'T', 635, 563);
INSERT INTO `sys_user` VALUES (17, 'qiuziyi51', '', '19208777160', '邱子异', 1, '2014-09-26', 'https://drive.linxiaoming4.biz/ComputersElectronics', 35, '5370644872368739', 'qziyi911@gmail.com', '1000001', '1001003', '2004-11-21 15:52:18', '2024-05-19 01:54:27', '2024-05-10 22:24:53', 'F', 106, 957);
INSERT INTO `sys_user` VALUES (18, 'zitding', '', '2884609303', '丁子韬', 1, '2008-05-13', 'http://image.okamhana.jp/ToysGames', 52, '3589347871651327', 'dingzita414@outlook.com', '1000001', '1001003', '2003-09-22 14:41:18', '2024-05-19 05:35:36', '2024-05-10 22:24:53', 'F', 124, 967);
INSERT INTO `sys_user` VALUES (19, 'lul', '', '19191775296', '廖璐', 1, '2006-05-13', 'https://video.lamsiuwai42.jp/AppsGames', 93, '341254455308552', 'liaolu2@hotmail.com', '1000001', '1001003', '2021-02-21 04:38:22', '2024-05-30 20:38:22', '2024-05-10 22:24:53', 'T', 293, 222);
INSERT INTO `sys_user` VALUES (20, 'yinl422', '', '14947345688', '尹璐', 1, '2016-03-27', 'https://image.jiehongtian04.org/ToolsHomeDecoration', 52, '3542847684726451', 'ylu@mail.com', '1000001', '1001003', '2015-03-14 22:18:09', '2024-05-27 06:52:37', '2024-05-10 22:24:53', 'T', 332, 333);
INSERT INTO `sys_user` VALUES (21, 'wangshihan78', '', '18306379300', '汪詩涵', 1, '2017-01-02', 'http://auth.guzi409.jp/IndustrialScientificSupplies', 93, '4235494965399672', 'shihanwang@gmail.com', '1000001', '1001003', '2012-02-07 09:25:15', '2024-05-17 13:00:19', '2024-05-10 22:24:53', 'F', 811, 726);
INSERT INTO `sys_user` VALUES (22, 'shzheng', '', '1097029692', '郑詩涵', 0, '2003-01-02', 'http://video.tchiehlun.org/CDsVinyl', 41, '341431194930207', 'zshiha1995@outlook.com', '1000001', '1001003', '2011-11-07 12:13:03', '2024-06-04 01:03:23', '2024-05-10 22:24:53', 'F', 915, 203);
INSERT INTO `sys_user` VALUES (23, 'ziyx', '', '13180035485', '夏子异', 0, '2005-01-19', 'http://drive.harai84.org/HouseholdKitchenAppliances', 43, '5420008515242125', 'xiaziyi@yahoo.com', '1000001', '1001003', '2019-07-10 04:32:46', '2024-06-08 13:28:12', '2024-05-10 22:24:53', 'T', 744, 178);
INSERT INTO `sys_user` VALUES (24, 'lji1203', '', '13739376174', '梁杰宏', 0, '2001-11-07', 'http://video.takeka1001.info/HealthBabyCare', 50, '6225431676629846', 'ljiehong68@gmail.com', '1000001', '1001003', '2022-09-29 01:32:37', '2024-05-30 08:40:30', '2024-05-10 22:24:53', 'T', 76, 852);
INSERT INTO `sys_user` VALUES (25, 'xiuyfa', '', '13959904301', '范秀英', 1, '2001-06-21', 'https://www.yx00.us/SportsOutdoor', 93, '4336886977805844', 'xiufan@gmail.com', '1000001', '1001003', '2008-09-21 06:32:17', '2024-05-22 02:41:38', '2024-05-10 22:24:53', 'F', 74, 47);
INSERT INTO `sys_user` VALUES (26, 'dong20', '', '216499009', '董云熙', 1, '2021-08-16', 'http://www.weam.cn/VideoGames', 42, '3544028777017474', 'yunxi1125@icloud.com', '1000001', '1001003', '2003-06-04 08:11:59', '2024-06-01 09:17:52', '2024-05-10 22:24:53', 'F', 702, 579);
INSERT INTO `sys_user` VALUES (27, 'houa', '', '17990110729', '侯安琪', 0, '2014-03-17', 'http://video.slan309.us/AppsGames', 84, '347908362268035', 'hou51@icloud.com', '1000001', '1001003', '2006-10-17 08:29:57', '2024-05-30 08:05:28', '2024-05-10 22:24:53', 'T', 264, 659);
INSERT INTO `sys_user` VALUES (28, 'jialxue', '', '14437444501', '薛嘉伦', 0, '2001-08-13', 'https://drive.wingkuen20.org/BeautyPersonalCare', 60, '4631587987034936', 'jxue@outlook.com', '1000001', '1001003', '2020-07-02 05:15:30', '2024-06-10 22:18:11', '2024-05-10 22:24:53', 'T', 681, 670);
INSERT INTO `sys_user` VALUES (29, 'jiehonggong', '', '202773901', '龚杰宏', 1, '2007-09-26', 'https://drive.anqizha7.org/HouseholdKitchenAppliances', 73, '5487541825832920', 'jiegong@gmail.com', '1000001', '1001003', '2004-08-27 20:37:36', '2024-05-31 23:52:19', '2024-05-10 22:24:53', 'F', 767, 822);
INSERT INTO `sys_user` VALUES (30, 'zhennan1216', '', '7551539353', '江震南', 0, '2016-07-03', 'https://auth.campbell7.us/BaggageTravelEquipment', 79, '5346678462119533', 'zhennanjiang@icloud.com', '1000001', '1001003', '2000-05-01 02:23:11', '2024-05-26 23:03:47', '2024-05-10 22:24:53', 'T', 585, 270);
INSERT INTO `sys_user` VALUES (31, 'lu58', '', '7555341017', '余璐', 1, '2013-04-30', 'https://video.antonio328.info/ComputersElectronics', 55, '3581616487029211', 'yl3@gmail.com', '1000001', '1001003', '2004-03-20 01:16:04', '2024-06-01 11:56:32', '2024-05-10 22:24:53', 'F', 858, 452);
INSERT INTO `sys_user` VALUES (32, 'taolu', '', '76969477825', '陶璐', 0, '2000-11-10', 'https://image.aito.xyz/ClothingShoesandJewelry', 100, '4936350292729407', 'tao87@icloud.com', '1000001', '1001003', '2004-06-22 09:59:58', '2024-06-06 15:30:17', '2024-05-10 22:24:53', 'F', 917, 353);
INSERT INTO `sys_user` VALUES (33, 'xiaoming6', '', '16921925400', '阎晓明', 1, '2002-07-11', 'https://image.hoyinwon.cn/VideoGames', 87, '6254426864310492', 'yan1115@outlook.com', '1000001', '1001003', '2016-03-22 17:30:19', '2024-05-31 23:28:18', '2024-05-10 22:24:53', 'F', 348, 413);
INSERT INTO `sys_user` VALUES (34, 'yuning6', '', '1022646244', '朱宇宁', 1, '2001-08-12', 'http://video.jordane10.xyz/MusicalInstrument', 28, '6246881631364159', 'zhu1954@hotmail.com', '1000001', '1001003', '2001-09-08 13:31:34', '2024-06-10 01:39:32', '2024-05-10 22:24:53', 'F', 78, 479);
INSERT INTO `sys_user` VALUES (35, 'zhu6', '', '18029098702', '朱杰宏', 0, '2008-12-03', 'https://image.sc07.cn/Handcrafts', 32, '6227075583827411', 'jiehongzh@mail.com', '1000001', '1001003', '2007-10-06 03:22:42', '2024-05-27 13:08:22', '2024-05-10 22:24:53', 'T', 109, 534);
INSERT INTO `sys_user` VALUES (36, 'yujia', '', '14384111658', '于嘉伦', 0, '2001-01-03', 'https://video.kytsui.net/CDsVinyl', 37, '4026717178147766', 'jialyu@gmail.com', '1000001', '1001003', '2004-07-31 05:00:04', '2024-05-19 11:08:55', '2024-05-10 22:24:53', 'T', 267, 483);
INSERT INTO `sys_user` VALUES (37, 'lupan', '', '287042625', '潘璐', 1, '2017-08-24', 'https://video.sarah802.us/PetSupplies', 95, '5235409822632153', 'lpan@gmail.com', '1000001', '1001003', '2003-03-13 17:51:36', '2024-05-23 12:25:17', '2024-05-10 22:24:53', 'F', 934, 565);
INSERT INTO `sys_user` VALUES (38, 'fangziyi', '', '13666139576', '方子异', 0, '2004-02-17', 'https://video.hungwaihan.co.jp/AutomotivePartsAccessories', 38, '6238794543050974', 'faz@icloud.com', '1000001', '1001003', '2005-03-26 06:04:24', '2024-06-06 07:02:18', '2024-05-10 22:24:53', 'F', 870, 498);
INSERT INTO `sys_user` VALUES (39, 'yxiaom', '', '205650618', '于晓明', 1, '2022-04-03', 'https://auth.tochoyee.jp/ComputersElectronics', 78, '6254246742416554', 'yu514@hotmail.com', '1000001', '1001003', '2016-07-22 20:10:56', '2024-05-23 22:29:19', '2024-05-10 22:24:53', 'T', 709, 424);
INSERT INTO `sys_user` VALUES (40, 'fenzhe1966', '', '18273261640', '冯震南', 1, '2010-01-17', 'https://auth.kongtw2.cn/FilmSupplies', 78, '4569581255468435', 'zhennanfen@icloud.com', '1000001', '1001003', '2023-09-21 01:55:50', '2024-05-29 14:06:04', '2024-05-10 22:24:53', 'F', 927, 713);
INSERT INTO `sys_user` VALUES (41, 'jialunh', '', '17204515877', '韩嘉伦', 1, '2007-05-21', 'https://drive.brenda5.biz/CollectiblesFineArt', 22, '4517054427306845', 'jiahan1@mail.com', '1000001', '1001003', '2015-05-21 10:50:52', '2024-05-28 12:18:49', '2024-05-10 22:24:53', 'F', 161, 381);
INSERT INTO `sys_user` VALUES (42, 'zhennanwu', '', '14392129738', '吴震南', 0, '2008-03-06', 'https://auth.lany905.info/ToysGames', 48, '3554741509552603', 'zhennanwu@gmail.com', '1000001', '1001003', '2013-08-05 16:20:34', '2024-05-19 13:38:23', '2024-05-10 22:24:53', 'T', 412, 479);
INSERT INTO `sys_user` VALUES (43, 'mozhiyuan5', '', '2182629224', '莫致远', 0, '2011-03-01', 'http://auth.aoiwad.net/VideoGames', 78, '3529831912752866', 'mozhiy@gmail.com', '1000001', '1001003', '2017-10-17 22:09:58', '2024-06-03 23:25:18', '2024-05-10 22:24:53', 'T', 13, 804);
INSERT INTO `sys_user` VALUES (44, 'yuningshen2', '', '2049373061', '沈宇宁', 1, '2020-08-13', 'https://drive.nakayamayuto.biz/Food', 95, '4562285820181872', 'shen09@yahoo.com', '1000001', '1001003', '2016-12-28 23:16:23', '2024-06-08 23:45:18', '2024-05-10 22:24:53', 'T', 870, 152);
INSERT INTO `sys_user` VALUES (45, 'doj89', '', '18297330728', '董杰宏', 0, '2014-01-25', 'https://drive.heung9.net/Beauty', 96, '5461313425977814', 'dongj@gmail.com', '1000001', '1001003', '2009-08-30 20:49:50', '2024-05-24 10:04:28', '2024-05-10 22:24:53', 'F', 927, 869);
INSERT INTO `sys_user` VALUES (46, 'ys80', '', '76008170441', '严詩涵', 0, '2008-02-16', 'https://www.shi82.jp/BeautyPersonalCare', 91, '4563610812192208', 'yan5@gmail.com', '1000001', '1001003', '2013-02-25 13:53:23', '2024-06-06 12:36:17', '2024-05-10 22:24:53', 'T', 163, 711);
INSERT INTO `sys_user` VALUES (47, 'yunxit', '', '2112094241', '唐云熙', 0, '2009-09-09', 'https://auth.jporter4.com/HealthBabyCare', 98, '3529652010649113', 'ytang2@hotmail.com', '1000001', '1001003', '2020-12-10 22:12:21', '2024-06-07 13:55:36', '2024-05-10 22:24:53', 'F', 548, 546);
INSERT INTO `sys_user` VALUES (48, 'zhennan14', '', '2040929090', '贾震南', 1, '2017-02-28', 'http://auth.saoi1988.info/Handcrafts', 50, '3577801618036329', 'zhennan2@gmail.com', '1000001', '1001003', '2023-02-19 06:54:43', '2024-05-29 05:42:03', '2024-05-10 22:24:53', 'F', 307, 776);
INSERT INTO `sys_user` VALUES (49, 'xr69', '', '101472048', '任晓明', 0, '2001-01-24', 'http://image.suj.info/HealthBabyCare', 88, '3573511260199572', 'xren@gmail.com', '1000001', '1001003', '2008-09-30 08:30:34', '2024-06-05 22:25:21', '2024-05-10 22:24:53', 'F', 846, 853);
INSERT INTO `sys_user` VALUES (50, 'xiaoming713', '', '75581715970', '韦晓明', 0, '2011-09-06', 'http://image.duans.org/IndustrialScientificSupplies', 85, '373458481101300', 'wxiaoming@gmail.com', '1000001', '1001003', '2000-11-18 14:14:20', '2024-05-20 13:40:45', '2024-05-10 22:24:53', 'F', 45, 916);
INSERT INTO `sys_user` VALUES (51, 'xiaoa02', '', '18612266110', '萧安琪', 1, '2004-12-05', 'https://www.cych.us/BaggageTravelEquipment', 23, '378517613764387', 'xiaoan@gmail.com', '1000001', '1001003', '2002-03-04 16:04:35', '2024-05-10 12:06:43', '2024-05-10 22:24:53', 'T', 626, 997);
INSERT INTO `sys_user` VALUES (52, 'gonshihan2', '', '13786132696', '龚詩涵', 0, '2021-04-09', 'http://auth.dunnr69.biz/BeautyPersonalCare', 60, '342781628324800', 'gongshih@outlook.com', '1000001', '1001003', '2002-08-21 11:09:57', '2024-06-07 16:09:29', '2024-05-10 22:24:53', 'F', 896, 636);
INSERT INTO `sys_user` VALUES (53, 'ziyma', '', '1060768429', '马子异', 0, '2015-04-26', 'http://auth.yingkaryan1.us/BeautyPersonalCare', 31, '5195519321834239', 'maziyi10@icloud.com', '1000001', '1001003', '2006-10-31 17:39:28', '2024-05-29 08:55:02', '2024-05-10 22:24:53', 'T', 92, 324);
INSERT INTO `sys_user` VALUES (54, 'shxiuy83', '', '14561631624', '石秀英', 0, '2015-05-29', 'http://auth.lanfu7.jp/MusicalInstrument', 49, '6206053169275821', 'xiuyingshi@gmail.com', '1000001', '1001003', '2020-03-05 09:38:02', '2024-05-10 17:14:20', '2024-05-10 22:24:53', 'F', 962, 370);
INSERT INTO `sys_user` VALUES (55, 'wanglu17', '', '17256208408', '王璐', 0, '2020-01-02', 'http://www.yuz.cn/CDsVinyl', 75, '6296842431298194', 'lu207@outlook.com', '1000001', '1001003', '2010-06-28 21:44:41', '2024-06-09 08:38:34', '2024-05-10 22:24:53', 'F', 419, 663);
INSERT INTO `sys_user` VALUES (56, 'fulu', '', '201653422', '傅璐', 0, '2011-07-26', 'https://drive.saumanlee402.info/ComputersElectronics', 27, '6257857713811974', 'fulu@gmail.com', '1000001', '1001003', '2013-08-11 07:02:03', '2024-06-04 05:12:50', '2024-05-10 22:24:53', 'F', 546, 757);
INSERT INTO `sys_user` VALUES (57, 'duanjiehong8', '', '18579111592', '段杰宏', 0, '2015-12-09', 'http://video.mcarte61.net/BaggageTravelEquipment', 20, '4984516473063896', 'jiehong1@gmail.com', '1000001', '1001003', '2019-09-18 21:28:37', '2024-06-10 20:09:32', '2024-05-10 22:24:53', 'T', 982, 599);
INSERT INTO `sys_user` VALUES (58, 'zch', '', '215968299', '程震南', 1, '2015-05-03', 'https://video.ah517.com/HouseholdKitchenAppliances', 55, '373861665667204', 'cheng1960@gmail.com', '1000001', '1001003', '2024-03-01 22:49:12', '2024-05-31 23:39:36', '2024-05-10 22:24:53', 'F', 596, 220);
INSERT INTO `sys_user` VALUES (59, 'guoyu8', '', '15349613154', '郭宇宁', 1, '2002-01-18', 'http://drive.angelam.jp/AppsGames', 47, '6233024347099463', 'guoy1012@icloud.com', '1000001', '1001003', '2008-11-11 05:18:07', '2024-05-28 00:27:59', '2024-05-10 22:24:53', 'F', 248, 286);
INSERT INTO `sys_user` VALUES (60, 'qians1949', '', '104822188', '钱詩涵', 1, '2007-12-10', 'http://video.jj47.cn/PetSupplies', 73, '6223574033290091', 'qishihan@outlook.com', '1000001', '1001003', '2008-10-30 16:21:13', '2024-05-10 18:47:50', '2024-05-10 22:24:53', 'T', 797, 309);
INSERT INTO `sys_user` VALUES (61, 'zeng522', '', '13388770567', '曾安琪', 0, '2015-01-10', 'https://drive.sofs94.co.jp/CenturionGardenOutdoor', 74, '3556604615856110', 'azeng1@outlook.com', '1000001', '1001003', '2020-12-19 18:59:28', '2024-05-15 03:50:31', '2024-05-10 22:24:53', 'T', 983, 390);
INSERT INTO `sys_user` VALUES (62, 'rujin', '', '76938250525', '金睿', 1, '2001-05-05', 'http://video.nikki5.info/ComputersElectronics', 98, '5039255664749797', 'ruij@outlook.com', '1000001', '1001003', '2018-06-12 18:23:56', '2024-06-08 00:51:24', '2024-05-10 22:24:53', 'T', 355, 812);
INSERT INTO `sys_user` VALUES (63, 'long9', '', '15840384800', '龙秀英', 0, '2006-03-05', 'https://image.lchi6.cn/BeautyPersonalCare', 33, '3552605807328341', 'xiuyinglon@hotmail.com', '1000001', '1001003', '2011-05-11 20:01:47', '2024-06-05 13:54:24', '2024-05-10 22:24:53', 'T', 262, 90);
INSERT INTO `sys_user` VALUES (64, 'haoyunxi16', '', '76047308048', '郝云熙', 0, '2022-04-24', 'http://image.wingszechan.info/ComputersElectronics', 83, '375323342798702', 'yunxha507@outlook.com', '1000001', '1001003', '2008-03-29 13:26:08', '2024-06-08 22:41:15', '2024-05-10 22:24:53', 'T', 979, 233);
INSERT INTO `sys_user` VALUES (65, 'yang04', '', '18881573157', '杨秀英', 1, '2010-09-28', 'http://image.millanne.com/ArtsHandicraftsSewing', 52, '6275850410140787', 'yangxiuy@outlook.com', '1000001', '1001003', '2009-02-26 00:29:49', '2024-05-17 05:04:30', '2024-05-10 22:24:53', 'T', 227, 830);
INSERT INTO `sys_user` VALUES (66, 'lu411', '', '19865976281', '钟璐', 1, '2018-12-03', 'https://auth.mitsukis1979.xyz/BaggageTravelEquipment', 23, '5020986548121750', 'zlu@icloud.com', '1000001', '1001003', '2001-12-29 10:21:54', '2024-06-05 00:33:35', '2024-05-10 22:24:53', 'F', 17, 682);
INSERT INTO `sys_user` VALUES (67, 'fangjie', '', '109822076', '方杰宏', 0, '2006-03-11', 'http://video.choi8.co.jp/CDsVinyl', 56, '5470575658869798', 'jfang@yahoo.com', '1000001', '1001003', '2020-02-07 08:05:31', '2024-06-03 13:54:19', '2024-05-10 22:24:53', 'T', 279, 834);
INSERT INTO `sys_user` VALUES (68, 'xie3', '', '14276276370', '谢云熙', 1, '2010-11-22', 'http://auth.tang623.org/AppsGames', 93, '5425714405639883', 'xieyu@outlook.com', '1000001', '1001003', '2014-07-05 18:59:49', '2024-06-01 03:58:09', '2024-05-10 22:24:53', 'T', 166, 386);
INSERT INTO `sys_user` VALUES (69, 'yunxis', '', '13008510919', '邵云熙', 1, '2023-09-07', 'http://image.sukyeech.jp/Books', 69, '6213657571080990', 'yshao51@yahoo.com', '1000001', '1001003', '2003-06-19 16:09:27', '2024-05-13 05:35:47', '2024-05-10 22:24:53', 'F', 973, 921);
INSERT INTO `sys_user` VALUES (70, 'linyunxi', '', '13038441703', '林云熙', 1, '2014-07-03', 'http://image.jialunsong.co.jp/BaggageTravelEquipment', 23, '6219232162202512', 'yunxili10@outlook.com', '1000001', '1001003', '2022-02-11 04:36:11', '2024-05-10 04:45:09', '2024-05-10 22:24:53', 'T', 42, 865);
INSERT INTO `sys_user` VALUES (71, 'luzhiyuan415', '', '14941725599', '陆致远', 1, '2017-10-28', 'http://video.yuning07.com/Others', 18, '6235244766004194', 'zhiyuan5@mail.com', '1000001', '1001003', '2000-12-21 19:13:11', '2024-06-03 18:21:26', '2024-05-10 22:24:53', 'F', 626, 816);
INSERT INTO `sys_user` VALUES (72, 'txiaoming1222', '', '76958452370', '陶晓明', 0, '2019-02-27', 'http://video.yx522.info/Others', 80, '4402052463372570', 'xiaoming1942@gmail.com', '1000001', '1001003', '2009-08-18 12:54:54', '2024-05-25 14:12:49', '2024-05-10 22:24:53', 'T', 135, 28);
INSERT INTO `sys_user` VALUES (73, 'xiuyingh', '', '76070416792', '黄秀英', 0, '2016-07-22', 'http://drive.tste.com/Others', 71, '4831501085217424', 'huangxiu@icloud.com', '1000001', '1001003', '2008-02-29 00:56:29', '2024-05-20 02:11:52', '2024-05-10 22:24:53', 'F', 274, 932);
INSERT INTO `sys_user` VALUES (74, 'weiru', '', '15524677471', '韦睿', 0, '2008-08-03', 'http://drive.so814.org/BaggageTravelEquipment', 51, '6218563640363505', 'weirui3@gmail.com', '1000001', '1001003', '2005-07-07 10:40:59', '2024-05-18 03:53:45', '2024-05-10 22:24:53', 'F', 901, 929);
INSERT INTO `sys_user` VALUES (75, 'peng9', '', '14693428748', '彭云熙', 0, '2015-12-08', 'https://drive.yuto10.us/HealthBabyCare', 35, '3551600286718790', 'py1@mail.com', '1000001', '1001003', '2012-12-31 18:28:52', '2024-06-01 23:33:54', '2024-05-10 22:24:53', 'T', 457, 859);
INSERT INTO `sys_user` VALUES (76, 'hoyunxi', '', '17035135174', '侯云熙', 1, '2021-09-23', 'https://video.xta.org/AutomotivePartsAccessories', 25, '5279235892612545', 'houyunxi@outlook.com', '1000001', '1001003', '2005-08-14 06:33:38', '2024-06-08 17:31:42', '2024-05-10 22:24:53', 'F', 333, 481);
INSERT INTO `sys_user` VALUES (77, 'jyu55', '', '2041402987', '余嘉伦', 1, '2019-12-30', 'http://image.martsc.us/ClothingShoesandJewelry', 37, '379555006341552', 'yu9@outlook.com', '1000001', '1001003', '2004-11-11 20:32:21', '2024-05-24 17:00:50', '2024-05-10 22:24:53', 'F', 222, 804);
INSERT INTO `sys_user` VALUES (78, 'daish', '', '17217172277', '戴詩涵', 0, '2000-10-30', 'https://image.yuningcai616.xyz/HouseholdKitchenAppliances', 36, '3544499849407623', 'daishiha@gmail.com', '1000001', '1001003', '2008-11-30 17:11:23', '2024-05-29 22:40:48', '2024-05-10 22:24:53', 'F', 710, 93);
INSERT INTO `sys_user` VALUES (79, 'ruix', '', '7552662239', '许睿', 1, '2007-03-31', 'https://image.annealva.info/Books', 30, '4672140259117417', 'xur@icloud.com', '1000001', '1001003', '2011-05-06 13:55:03', '2024-05-27 00:29:12', '2024-05-10 22:24:53', 'T', 744, 266);
INSERT INTO `sys_user` VALUES (80, 'jialu424', '', '15261707476', '贾璐', 0, '2022-08-03', 'http://drive.parsylvia220.cn/Food', 27, '6208919496642800', 'jialu@outlook.com', '1000001', '1001003', '2010-02-09 17:09:23', '2024-05-10 02:32:58', '2024-05-10 22:24:53', 'T', 190, 82);
INSERT INTO `sys_user` VALUES (81, 'pengjie3', '', '7558710661', '彭杰宏', 1, '2015-01-19', 'http://image.xiang3.cn/CenturionGardenOutdoor', 94, '5540475659804652', 'jiepeng60@icloud.com', '1000001', '1001003', '2005-10-22 22:45:08', '2024-05-14 16:47:11', '2024-05-10 22:24:53', 'F', 445, 847);
INSERT INTO `sys_user` VALUES (82, 'xiaomlin1', '', '17592712150', '林晓明', 1, '2016-08-08', 'http://drive.robertsrodney70.org/CDsVinyl', 55, '6236412532591484', 'linx@gmail.com', '1000001', '1001003', '2015-11-18 03:13:29', '2024-05-14 06:25:57', '2024-05-10 22:24:53', 'T', 644, 122);
INSERT INTO `sys_user` VALUES (83, 'tangziyi', '', '206273886', '唐子异', 1, '2008-06-05', 'https://image.gardnergrac7.info/Food', 77, '3531109246487582', 'zitang@mail.com', '1000001', '1001003', '2005-10-28 09:38:24', '2024-05-23 11:14:35', '2024-05-10 22:24:53', 'T', 514, 30);
INSERT INTO `sys_user` VALUES (84, 'zitaos51', '', '7608785690', '宋子韬', 1, '2009-03-26', 'https://video.uenosakura.us/Handcrafts', 34, '340535568347144', 'songzitao58@outlook.com', '1000001', '1001003', '2011-09-29 11:14:51', '2024-05-11 01:48:49', '2024-05-10 22:24:53', 'T', 917, 996);
INSERT INTO `sys_user` VALUES (85, 'luziyi', '', '13888475389', '吕子异', 0, '2016-11-24', 'http://drive.takedaair1007.cn/Handcrafts', 22, '3571418552939719', 'zilu@outlook.com', '1000001', '1001003', '2020-04-07 01:29:09', '2024-06-05 16:33:13', '2024-05-10 22:24:53', 'F', 705, 967);
INSERT INTO `sys_user` VALUES (86, 'shihanqi', '', '7553015600', '钱詩涵', 0, '2009-11-25', 'http://drive.yunxi802.co.jp/HealthBabyCare', 17, '4220500147717040', 'qians@outlook.com', '1000001', '1001003', '2014-09-29 12:55:39', '2024-05-28 00:49:25', '2024-05-10 22:24:53', 'T', 328, 284);
INSERT INTO `sys_user` VALUES (87, 'zhiyy1953', '', '14944079141', '阎致远', 0, '2009-02-08', 'http://auth.wrui.jp/FilmSupplies', 34, '3545868028422380', 'yzhiyuan@outlook.com', '1000001', '1001003', '2013-12-31 15:54:51', '2024-05-30 06:06:13', '2024-05-10 22:24:53', 'T', 484, 534);
INSERT INTO `sys_user` VALUES (88, 'pengxiaoming110', '', '2803361299', '彭晓明', 0, '2019-03-09', 'http://video.kaisakam.org/ClothingShoesandJewelry', 59, '341164351245917', 'xiaomingpeng725@outlook.com', '1000001', '1001003', '2013-08-27 05:21:27', '2024-05-31 10:51:56', '2024-05-10 22:24:53', 'F', 43, 482);
INSERT INTO `sys_user` VALUES (89, 'yao71', '', '76931987422', '姚睿', 0, '2021-06-28', 'http://video.khy.co.jp/CellPhonesAccessories', 26, '346448750563052', 'yao20@icloud.com', '1000001', '1001003', '2001-03-10 02:20:32', '2024-05-10 15:45:58', '2024-05-10 22:24:53', 'T', 615, 681);
INSERT INTO `sys_user` VALUES (90, 'rliang46', '', '16208189957', '梁睿', 0, '2017-11-12', 'http://www.ziyi10.com/CenturionGardenOutdoor', 51, '3575641020507232', 'rliang@hotmail.com', '1000001', '1001003', '2020-06-09 00:23:49', '2024-05-11 10:58:49', '2024-05-10 22:24:53', 'T', 242, 40);
INSERT INTO `sys_user` VALUES (91, 'zhisha72', '', '76058834979', '邵致远', 0, '2007-01-03', 'http://auth.michealward523.net/HealthBabyCare', 97, '5323150355363567', 'zhiyuans@gmail.com', '1000001', '1001003', '2021-10-05 01:01:09', '2024-05-20 17:49:00', '2024-05-10 22:24:53', 'F', 912, 478);
INSERT INTO `sys_user` VALUES (92, 'dziyi2', '', '7692361078', '段子异', 0, '2022-08-06', 'http://drive.xiuying5.info/VideoGames', 53, '3541238292155526', 'dziy@icloud.com', '1000001', '1001003', '2014-03-24 08:32:55', '2024-05-12 14:16:33', '2024-05-10 22:24:53', 'T', 800, 145);
INSERT INTO `sys_user` VALUES (93, 'lir216', '', '75591041109', '黎睿', 0, '2020-12-14', 'https://auth.zitaopeng.info/Beauty', 23, '6249199800020594', 'ruli408@icloud.com', '1000001', '1001003', '2009-05-13 14:07:00', '2024-06-09 04:56:55', '2024-05-10 22:24:53', 'T', 660, 861);
INSERT INTO `sys_user` VALUES (94, 'luzou501', '', '1016425526', '邹璐', 0, '2013-12-07', 'https://auth.bangela50.us/Beauty', 21, '6260099730608068', 'zoulu103@gmail.com', '1000001', '1001003', '2023-11-23 03:18:31', '2024-05-14 09:28:39', '2024-05-10 22:24:53', 'F', 28, 975);
INSERT INTO `sys_user` VALUES (95, 'fan901', '', '18154708548', '范秀英', 1, '2020-09-24', 'http://www.zenglan80.biz/HouseholdKitchenAppliances', 69, '5228500153561810', 'xiuying77@gmail.com', '1000001', '1001003', '2001-02-19 16:46:46', '2024-06-04 01:54:03', '2024-05-10 22:24:53', 'F', 396, 322);
INSERT INTO `sys_user` VALUES (96, 'yuz', '', '16322632528', '周宇宁', 0, '2023-01-24', 'http://www.koo86.net/CellPhonesAccessories', 19, '3585688054751267', 'zhou410@icloud.com', '1000001', '1001003', '2008-02-24 07:54:48', '2024-05-20 08:09:12', '2024-05-10 22:24:53', 'T', 420, 748);
INSERT INTO `sys_user` VALUES (97, 'heziyi', '', '14408988892', '何子异', 0, '2002-02-19', 'https://video.millercraig.cn/CenturionGardenOutdoor', 80, '347071928391443', 'ziyi1992@yahoo.com', '1000001', '1001003', '2003-01-01 14:47:07', '2024-05-31 22:16:11', '2024-05-10 22:24:53', 'F', 606, 150);
INSERT INTO `sys_user` VALUES (98, 'yxiuying9', '', '75508940661', '姚秀英', 1, '2013-01-08', 'https://image.kaitis.jp/Books', 33, '5118503263747041', 'xiuying3@icloud.com', '1000001', '1001003', '2020-03-10 05:50:21', '2024-05-22 16:38:33', '2024-05-10 22:24:53', 'T', 246, 473);
INSERT INTO `sys_user` VALUES (99, 'xia2002', '', '15111411208', '夏安琪', 0, '2002-06-16', 'http://drive.haroldfoste.us/CDsVinyl', 91, '3589446636264952', 'anqix42@gmail.com', '1000001', '1001003', '2002-06-30 16:31:31', '2024-05-26 16:48:49', '2024-05-10 22:24:53', 'F', 152, 529);
INSERT INTO `sys_user` VALUES (100, 'jianzitao', '', '201455598', '蒋子韬', 1, '2011-02-06', 'https://image.hughes707.jp/PetSupplies', 54, '4365640605332943', 'jiang923@yahoo.com', '1000001', '1001003', '2021-11-10 21:03:02', '2024-05-15 07:25:48', '2024-05-10 22:24:53', 'T', 214, 684);
INSERT INTO `sys_user` VALUES (101, 'jfan7', '', '75591434287', '范杰宏', 0, '2016-10-05', 'http://drive.lamky.xyz/AutomotivePartsAccessories', 23, '5543458752447410', 'jiehong5@hotmail.com', '1000001', '1001003', '2017-07-30 11:11:35', '2024-05-25 03:44:58', '2024-05-10 22:24:53', 'T', 284, 568);
INSERT INTO `sys_user` VALUES (102, 'xiuying8', '', '14216032308', '黄秀英', 0, '2014-09-07', 'https://www.jefferym.org/Beauty', 69, '4248658344281950', 'huangxi@gmail.com', '1000001', '1001003', '2015-01-04 02:05:40', '2024-05-26 15:26:42', '2024-05-10 22:24:53', 'F', 35, 564);
INSERT INTO `sys_user` VALUES (103, 'cziyi', '', '1096356987', '崔子异', 1, '2018-10-26', 'http://drive.wonkk.com/SportsOutdoor', 98, '5517369701203667', 'zcu1947@icloud.com', '1000001', '1001003', '2013-12-02 02:42:34', '2024-05-10 10:55:38', '2024-05-10 22:24:53', 'T', 584, 930);
INSERT INTO `sys_user` VALUES (104, 'yayunxi', '', '280962377', '严云熙', 0, '2011-10-26', 'https://www.janehar43.co.jp/MusicalInstrument', 55, '3578742844286180', 'yanyunxi87@icloud.com', '1000001', '1001003', '2016-02-06 14:17:37', '2024-06-01 15:41:00', '2024-05-10 22:24:53', 'T', 174, 134);
INSERT INTO `sys_user` VALUES (105, 'hanjiehong', '', '2056132251', '韩杰宏', 1, '2002-07-20', 'http://drive.matsuda7.jp/BaggageTravelEquipment', 22, '3531517758591080', 'jiehonghan@icloud.com', '1000001', '1001003', '2000-05-13 23:31:58', '2024-05-27 21:55:27', '2024-05-10 22:24:53', 'F', 836, 75);
INSERT INTO `sys_user` VALUES (106, 'yunxi331', '', '16924107623', '梁云熙', 1, '2009-07-17', 'http://drive.stewartwillie.org/HealthBabyCare', 94, '5511774492950184', 'liang61@gmail.com', '1000001', '1001003', '2011-03-09 09:52:59', '2024-05-20 08:41:00', '2024-05-10 22:24:53', 'T', 900, 682);
INSERT INTO `sys_user` VALUES (107, 'kong96', '', '14595192124', '孔詩涵', 1, '2017-09-20', 'http://www.wingfloui.co.jp/CDsVinyl', 39, '5386965654221537', 'shiko1965@outlook.com', '1000001', '1001003', '2014-05-14 12:11:52', '2024-06-03 17:33:03', '2024-05-10 22:24:53', 'F', 616, 641);
INSERT INTO `sys_user` VALUES (108, 'long96', '', '7603502657', '龙致远', 1, '2011-03-06', 'https://drive.chlopez43.info/IndustrialScientificSupplies', 55, '3569312281435665', 'longzhiyuan1970@gmail.com', '1000001', '1001003', '2008-04-01 17:22:00', '2024-06-07 01:49:02', '2024-05-10 22:24:53', 'T', 685, 42);
INSERT INTO `sys_user` VALUES (109, 'lulu', '', '15134359221', '吕璐', 1, '2019-10-05', 'http://video.xieziyi121.us/SportsOutdoor', 87, '4534686703787302', 'lu1020@gmail.com', '1000001', '1001003', '2019-11-04 22:35:33', '2024-06-04 08:50:04', '2024-05-10 22:24:53', 'T', 666, 34);
INSERT INTO `sys_user` VALUES (110, 'jialunhuang', '', '202472097', '黄嘉伦', 0, '2012-09-21', 'http://www.fshi68.info/PetSupplies', 62, '348012696117569', 'jihuang2@mail.com', '1000001', '1001003', '2009-08-16 20:19:09', '2024-06-05 03:59:41', '2024-05-10 22:24:53', 'F', 16, 754);
INSERT INTO `sys_user` VALUES (111, 'yunxiding', '', '281663722', '丁云熙', 1, '2023-12-21', 'https://image.hmomoka.net/CDsVinyl', 76, '5355742681991979', 'yunxidi@gmail.com', '1000001', '1001003', '2024-03-04 21:40:06', '2024-06-10 00:15:21', '2024-05-10 22:24:53', 'F', 203, 162);
INSERT INTO `sys_user` VALUES (112, 'weiziyi', '', '18776346174', '魏子异', 0, '2001-11-26', 'https://www.emilyk7.org/CDsVinyl', 39, '5180983462967744', 'ziyiwei5@gmail.com', '1000001', '1001003', '2011-08-10 12:00:32', '2024-05-18 05:19:49', '2024-05-10 22:24:53', 'F', 191, 473);
INSERT INTO `sys_user` VALUES (113, 'yjialun', '', '14867761634', '尹嘉伦', 1, '2015-03-18', 'https://video.yhuimei.cn/ComputersElectronics', 15, '3547113420275229', 'yjialun78@yahoo.com', '1000001', '1001003', '2012-04-17 17:19:51', '2024-05-29 07:08:49', '2024-05-10 22:24:53', 'T', 619, 623);
INSERT INTO `sys_user` VALUES (114, 'ruizh128', '', '19019337941', '周睿', 1, '2007-08-14', 'http://www.natsu.jp/AppsGames', 43, '4296000939678681', 'rz73@icloud.com', '1000001', '1001003', '2020-07-25 12:53:51', '2024-06-01 19:27:00', '2024-05-10 22:24:53', 'T', 383, 756);
INSERT INTO `sys_user` VALUES (115, 'shihan8', '', '7697205984', '邓詩涵', 1, '2020-09-04', 'https://image.warded601.biz/ToysGames', 21, '341880218969908', 'shihandeng@gmail.com', '1000001', '1001003', '2023-05-29 22:01:24', '2024-06-01 03:26:15', '2024-05-10 22:24:53', 'F', 774, 792);
INSERT INTO `sys_user` VALUES (116, 'tang73', '', '7600399119', '汤嘉伦', 0, '2019-03-25', 'http://drive.fujiirin1.us/Handcrafts', 23, '6273589070217137', 'tanjialun@icloud.com', '1000001', '1001003', '2008-01-03 22:12:16', '2024-06-02 16:46:30', '2024-05-10 22:24:53', 'F', 5, 563);
INSERT INTO `sys_user` VALUES (117, 'zhangyunxi3', '', '7698976569', '张云熙', 1, '2013-05-16', 'https://image.miya726.com/AppsGames', 56, '5255022298272993', 'yunxi8@gmail.com', '1000001', '1001003', '2016-11-24 00:08:49', '2024-06-03 03:29:11', '2024-05-10 22:24:53', 'T', 604, 886);
INSERT INTO `sys_user` VALUES (118, 'lizhiyuan', '', '7607468723', '李致远', 1, '2007-06-12', 'https://image.tang3.net/CDsVinyl', 42, '6290513305789248', 'zhiyuanli4@gmail.com', '1000001', '1001003', '2002-04-27 10:17:16', '2024-06-10 15:00:35', '2024-05-10 22:24:53', 'T', 169, 950);
INSERT INTO `sys_user` VALUES (119, 'luy419', '', '2124755088', '袁璐', 0, '2011-06-13', 'https://drive.ishis.co.jp/Others', 59, '3557457094785618', 'luyuan@mail.com', '1000001', '1001003', '2007-01-21 05:05:42', '2024-06-01 12:45:37', '2024-05-10 22:24:53', 'T', 89, 290);
INSERT INTO `sys_user` VALUES (120, 'luliang', '', '2854582758', '梁璐', 0, '2019-01-29', 'https://auth.hes202.info/Others', 96, '4865316432858997', 'lialu1980@gmail.com', '1000001', '1001003', '2014-11-21 06:14:57', '2024-05-24 10:25:43', '2024-05-10 22:24:53', 'T', 692, 622);
INSERT INTO `sys_user` VALUES (121, 'hjiehong85', '', '17219525691', '何杰宏', 0, '2009-05-11', 'http://auth.kaitoendo.xyz/ToysGames', 94, '3543069741168325', 'hejiehong@gmail.com', '1000001', '1001003', '2012-01-21 05:12:40', '2024-05-23 09:34:28', '2024-05-10 22:24:53', 'F', 427, 761);
INSERT INTO `sys_user` VALUES (122, 'far', '', '7606402600', '范睿', 1, '2017-03-21', 'https://drive.makin.co.jp/HouseholdKitchenAppliances', 51, '3564347054097115', 'ruifan10@icloud.com', '1000001', '1001003', '2015-09-03 08:51:30', '2024-05-31 13:36:14', '2024-05-10 22:24:53', 'T', 49, 250);
INSERT INTO `sys_user` VALUES (123, 'zwa1', '', '14658786383', '王子异', 1, '2017-05-12', 'http://drive.cky.jp/CellPhonesAccessories', 31, '5082927871299776', 'zwang@icloud.com', '1000001', '1001003', '2011-08-12 05:48:34', '2024-06-03 04:16:36', '2024-05-10 22:24:53', 'F', 298, 312);
INSERT INTO `sys_user` VALUES (124, 'sh1113', '', '75573164930', '韩詩涵', 0, '2006-03-14', 'http://image.yunsh827.info/HealthBabyCare', 71, '5076788614978433', 'han5@gmail.com', '1000001', '1001003', '2012-10-30 01:14:29', '2024-06-09 09:35:28', '2024-05-10 22:24:53', 'F', 941, 43);
INSERT INTO `sys_user` VALUES (125, 'liurui2006', '', '75585783169', '刘睿', 0, '2010-08-22', 'https://image.rino1949.us/IndustrialScientificSupplies', 65, '5588602025251663', 'liu327@mail.com', '1000001', '1001003', '2003-01-30 01:03:24', '2024-05-23 07:25:21', '2024-05-10 22:24:53', 'T', 750, 17);
INSERT INTO `sys_user` VALUES (126, 'zhiyuanh5', '', '15463317651', '郝致远', 1, '2000-02-05', 'https://image.ruizeng1966.net/IndustrialScientificSupplies', 48, '378802825304523', 'zhiyuan8@outlook.com', '1000001', '1001003', '2011-03-24 12:14:58', '2024-05-11 08:35:58', '2024-05-10 22:24:53', 'F', 498, 234);
INSERT INTO `sys_user` VALUES (127, 'yuningmao', '', '19512419253', '毛宇宁', 0, '2012-12-19', 'https://drive.barneslaura.com/ComputersElectronics', 32, '6205006283028162', 'yuningm@gmail.com', '1000001', '1001003', '2008-08-16 07:47:38', '2024-05-15 01:33:19', '2024-05-10 22:24:53', 'F', 419, 492);
INSERT INTO `sys_user` VALUES (128, 'xiaomingt', '', '7559182409', '谭晓明', 0, '2018-11-15', 'http://www.rikuwata.us/Beauty', 17, '6248906427492274', 'tanxiaoming@yahoo.com', '1000001', '1001003', '2009-04-03 13:34:20', '2024-06-10 07:32:37', '2024-05-10 22:24:53', 'T', 314, 161);
INSERT INTO `sys_user` VALUES (129, 'gongshihan', '', '18983002083', '龚詩涵', 1, '2007-09-25', 'http://auth.syhsuan2.co.jp/Books', 16, '5267600685954921', 'gosh@icloud.com', '1000001', '1001003', '2019-06-16 07:15:09', '2024-05-19 20:08:18', '2024-05-10 22:24:53', 'F', 940, 262);
INSERT INTO `sys_user` VALUES (130, 'ziyx7', '', '2873628734', '熊子异', 0, '2017-12-22', 'https://image.minatomurat402.co.jp/ComputersElectronics', 95, '371377687050578', 'xiongzi@outlook.com', '1000001', '1001003', '2007-02-04 11:05:26', '2024-06-04 09:20:39', '2024-05-10 22:24:53', 'F', 158, 967);
INSERT INTO `sys_user` VALUES (131, 'jiayuning', '', '14705450439', '贾宇宁', 0, '2004-07-09', 'https://www.yji.com/CDsVinyl', 96, '6251929758500386', 'jyuni@gmail.com', '1000001', '1001003', '2008-05-04 19:00:05', '2024-05-29 18:22:47', '2024-05-10 22:24:53', 'F', 481, 617);
INSERT INTO `sys_user` VALUES (132, 'jiaanqi', '', '2154215978', '贾安琪', 1, '2005-07-01', 'https://auth.garcia96.co.jp/ToolsHomeDecoration', 36, '4198584542181604', 'jianqi@outlook.com', '1000001', '1001003', '2000-03-04 05:30:26', '2024-05-28 15:56:57', '2024-05-10 22:24:53', 'F', 559, 62);
INSERT INTO `sys_user` VALUES (133, 'zholu', '', '7698051603', '钟璐', 1, '2010-07-14', 'https://image.hos.org/HouseholdKitchenAppliances', 67, '6282720263631722', 'zl1@hotmail.com', '1000001', '1001003', '2021-03-14 14:11:01', '2024-05-27 11:28:08', '2024-05-10 22:24:53', 'F', 731, 887);
INSERT INTO `sys_user` VALUES (134, 'caoshi', '', '18000176399', '曹詩涵', 0, '2004-08-17', 'http://auth.ritasmith4.info/ToolsHomeDecoration', 77, '4216601199862407', 'cashihan@icloud.com', '1000001', '1001003', '2018-07-01 00:59:43', '2024-05-18 05:46:01', '2024-05-10 22:24:53', 'T', 320, 144);
INSERT INTO `sys_user` VALUES (135, 'zhao06', '', '1066610160', '郝子韬', 0, '2018-11-19', 'http://drive.meyerjohnny.cn/ComputersElectronics', 88, '5478641890309969', 'haozit@gmail.com', '1000001', '1001003', '2019-02-26 08:27:21', '2024-05-21 07:26:19', '2024-05-10 22:24:53', 'F', 581, 705);
INSERT INTO `sys_user` VALUES (136, 'zhwe2', '', '75553517520', '韦致远', 1, '2011-03-23', 'https://video.zhennanfu913.com/BaggageTravelEquipment', 86, '3552472649233597', 'zwei7@hotmail.com', '1000001', '1001003', '2015-06-17 07:46:03', '2024-05-28 16:19:59', '2024-05-10 22:24:53', 'T', 737, 159);
INSERT INTO `sys_user` VALUES (137, 'lshih114', '', '76921776787', '林詩涵', 0, '2024-04-27', 'https://auth.kasakurai1109.jp/Handcrafts', 49, '5422069416454241', 'linsh3@yahoo.com', '1000001', '1001003', '2012-04-15 23:39:19', '2024-06-01 04:23:36', '2024-05-10 22:24:53', 'T', 752, 110);
INSERT INTO `sys_user` VALUES (138, 'yunk', '', '14110629184', '孔云熙', 0, '2019-05-11', 'https://video.hahazuki.net/BaggageTravelEquipment', 36, '5099502518434697', 'yunxikon8@outlook.com', '1000001', '1001003', '2023-11-24 12:30:27', '2024-05-23 16:01:48', '2024-05-10 22:24:53', 'F', 435, 721);
INSERT INTO `sys_user` VALUES (139, 'jieht', '', '76913196955', '陶杰宏', 1, '2017-03-03', 'https://image.wingszepang.biz/BaggageTravelEquipment', 21, '3564697473437460', 'jiehongta@gmail.com', '1000001', '1001003', '2011-07-08 09:08:01', '2024-06-02 12:20:53', '2024-05-10 22:24:53', 'T', 113, 316);
INSERT INTO `sys_user` VALUES (140, 'zhennanzhao', '', '75535660391', '赵震南', 1, '2006-12-31', 'http://video.jiehonglei209.com/FilmSupplies', 25, '6239633501410987', 'zhao8@outlook.com', '1000001', '1001003', '2013-11-25 23:52:21', '2024-05-15 14:45:40', '2024-05-10 22:24:53', 'T', 287, 223);
INSERT INTO `sys_user` VALUES (141, 'shifu5', '', '2085412043', '傅詩涵', 0, '2022-02-25', 'http://drive.suy.net/Baby', 87, '372341244584726', 'fus@outlook.com', '1000001', '1001003', '2004-07-18 01:32:46', '2024-05-27 14:11:29', '2024-05-10 22:24:53', 'F', 140, 162);
INSERT INTO `sys_user` VALUES (142, 'wangr', '', '15635719407', '汪睿', 0, '2008-04-30', 'https://drive.anqiyan201.info/CenturionGardenOutdoor', 87, '379551715157587', 'ruiwang@gmail.com', '1000001', '1001003', '2001-04-25 12:00:34', '2024-06-03 06:59:55', '2024-05-10 22:24:53', 'F', 586, 840);
INSERT INTO `sys_user` VALUES (143, 'chengl', '', '14696687067', '程岚', 0, '2000-12-15', 'http://auth.evans1978.xyz/Appliances', 49, '3571089217997838', 'cheng3@gmail.com', '1000001', '1001003', '2010-07-23 15:58:21', '2024-05-27 19:33:56', '2024-05-10 22:24:53', 'F', 425, 364);
INSERT INTO `sys_user` VALUES (144, 'zhziyi227', '', '208730175', '赵子异', 0, '2008-01-03', 'http://www.momoeimai3.co.jp/PetSupplies', 92, '373758675182002', 'ziyi8@outlook.com', '1000001', '1001003', '2023-05-31 17:10:47', '2024-06-04 23:54:04', '2024-05-10 22:24:53', 'F', 786, 540);
INSERT INTO `sys_user` VALUES (145, 'zhaoy', '', '15718302893', '赵云熙', 1, '2003-01-25', 'http://image.zitaowe.co.jp/Handcrafts', 57, '3549836313251547', 'yzhao7@icloud.com', '1000001', '1001003', '2005-10-02 06:05:03', '2024-05-23 00:38:14', '2024-05-10 22:24:53', 'T', 597, 296);
INSERT INTO `sys_user` VALUES (146, 'lul912', '', '18009749383', '刘璐', 0, '2005-04-30', 'http://www.liaook.info/Appliances', 60, '5159547078320171', 'lliu804@mail.com', '1000001', '1001003', '2019-05-06 19:26:48', '2024-06-03 05:55:16', '2024-05-10 22:24:53', 'T', 583, 106);
INSERT INTO `sys_user` VALUES (147, 'wuzhiy4', '', '19989125222', '武致远', 0, '2008-12-25', 'http://www.chiconkay1112.biz/MusicalInstrument', 49, '5309851337297793', 'wuzhiyuan@gmail.com', '1000001', '1001003', '2014-10-24 20:02:07', '2024-05-27 11:19:32', '2024-05-10 22:24:53', 'F', 852, 214);
INSERT INTO `sys_user` VALUES (148, 'zzh', '', '2001852293', '郑震南', 0, '2007-08-28', 'http://drive.nakajima01.xyz/SportsOutdoor', 87, '5131614626736804', 'zheng3@gmail.com', '1000001', '1001003', '2014-05-03 06:07:12', '2024-05-30 21:53:42', '2024-05-10 22:24:53', 'F', 412, 683);
INSERT INTO `sys_user` VALUES (149, 'hux1004', '', '7699498072', '胡晓明', 1, '2002-11-29', 'https://www.yamatoyamashita13.us/CellPhonesAccessories', 31, '3568613228071153', 'xiaomhu9@gmail.com', '1000001', '1001003', '2017-06-27 18:01:27', '2024-05-10 01:23:22', '2024-05-10 22:24:53', 'F', 278, 790);
INSERT INTO `sys_user` VALUES (150, 'zitao5', '', '18645461745', '萧子韬', 1, '2008-07-09', 'http://www.tiok315.info/FilmSupplies', 20, '342181926458766', 'zitao5@gmail.com', '1000001', '1001003', '2015-06-21 23:35:14', '2024-06-05 13:28:17', '2024-05-10 22:24:53', 'F', 973, 591);
INSERT INTO `sys_user` VALUES (151, 'lan224', '', '14341799779', '陆岚', 1, '2000-09-24', 'https://auth.chziyi.us/Baby', 83, '3539404172498014', 'lanlu@gmail.com', '1000001', '1001003', '2020-11-05 02:15:15', '2024-05-15 03:44:17', '2024-05-10 22:24:53', 'F', 308, 436);
INSERT INTO `sys_user` VALUES (152, 'zitaox', '', '2153273894', '熊子韬', 0, '2014-09-01', 'http://drive.mak7.org/VideoGames', 50, '5042269300251501', 'zitaoxion50@gmail.com', '1000001', '1001003', '2018-04-11 13:07:51', '2024-05-28 18:21:00', '2024-05-10 22:24:53', 'F', 582, 945);
INSERT INTO `sys_user` VALUES (153, 'shihah', '', '7606375219', '黄詩涵', 1, '2021-08-13', 'http://auth.pms.cn/CDsVinyl', 29, '348610219789424', 'shhua@icloud.com', '1000001', '1001003', '2007-03-13 17:58:24', '2024-05-28 18:10:57', '2024-05-10 22:24:53', 'T', 914, 386);
INSERT INTO `sys_user` VALUES (154, 'jialun1227', '', '16013682500', '蒋嘉伦', 0, '2009-09-16', 'https://drive.mn1028.com/Food', 37, '3565063851353432', 'jiajiang@gmail.com', '1000001', '1001003', '2006-12-28 02:44:13', '2024-06-07 08:29:47', '2024-05-10 22:24:53', 'F', 855, 947);
INSERT INTO `sys_user` VALUES (155, 'chenga828', '', '13684784648', '程安琪', 0, '2003-07-28', 'http://image.seikohaya.co.jp/BaggageTravelEquipment', 70, '5066642755453734', 'anqicheng@gmail.com', '1000001', '1001003', '2017-11-26 16:32:31', '2024-05-23 05:28:44', '2024-05-10 22:24:53', 'F', 708, 793);
INSERT INTO `sys_user` VALUES (156, 'wyuning', '', '75563399923', '王宇宁', 0, '2003-10-24', 'http://auth.shibata9.org/ToysGames', 20, '4662299344249460', 'yuning1@icloud.com', '1000001', '1001003', '2021-05-06 23:16:39', '2024-05-19 05:53:02', '2024-05-10 22:24:53', 'T', 219, 28);
INSERT INTO `sys_user` VALUES (157, 'anqi217', '', '15806645557', '董安琪', 0, '2014-09-04', 'https://www.tasm221.info/IndustrialScientificSupplies', 67, '4605797421695752', 'anqi213@hotmail.com', '1000001', '1001003', '2002-10-14 01:23:09', '2024-06-04 22:28:32', '2024-05-10 22:24:53', 'F', 983, 2);
INSERT INTO `sys_user` VALUES (158, 'yzhiyu3', '', '76927872540', '余致远', 0, '2011-10-25', 'https://drive.onkayy.co.jp/AutomotivePartsAccessories', 68, '4784183536489774', 'zhiyuan54@outlook.com', '1000001', '1001003', '2003-04-10 16:29:12', '2024-06-08 17:11:05', '2024-05-10 22:24:53', 'T', 550, 260);
INSERT INTO `sys_user` VALUES (159, 'ruipan1009', '', '15888167065', '潘睿', 1, '2014-01-28', 'https://auth.duayu3.net/AppsGames', 58, '6299142861509369', 'ruipa@icloud.com', '1000001', '1001003', '2002-06-09 21:54:07', '2024-05-15 13:16:53', '2024-05-10 22:24:53', 'F', 488, 296);
INSERT INTO `sys_user` VALUES (160, 'lonzitao1111', '', '15752748956', '龙子韬', 1, '2015-06-18', 'http://drive.shhikari.jp/Others', 41, '3530270561939184', 'longz1@outlook.com', '1000001', '1001003', '2016-02-23 02:27:42', '2024-05-19 02:59:45', '2024-05-10 22:24:54', 'F', 227, 458);
INSERT INTO `sys_user` VALUES (161, 'zitalong', '', '18232769147', '龙子韬', 0, '2012-05-02', 'http://video.lll2.us/HealthBabyCare', 22, '4973531913979406', 'zitlong10@yahoo.com', '1000001', '1001003', '2007-11-23 09:49:11', '2024-05-26 01:55:29', '2024-05-10 22:24:54', 'F', 705, 241);
INSERT INTO `sys_user` VALUES (162, 'huj', '', '7604341297', '胡杰宏', 0, '2000-10-22', 'https://image.sugdaisuke1994.net/SportsOutdoor', 32, '5241537082054890', 'hj913@outlook.com', '1000001', '1001003', '2005-03-07 09:48:02', '2024-05-11 09:47:43', '2024-05-10 22:24:54', 'T', 56, 988);
INSERT INTO `sys_user` VALUES (163, 'jiajia903', '', '76040868404', '贾嘉伦', 1, '2001-03-15', 'https://image.yunh49.us/Beauty', 49, '340360189602045', 'jjia@gmail.com', '1000001', '1001003', '2006-02-02 07:04:20', '2024-06-01 13:06:12', '2024-05-10 22:24:54', 'T', 827, 374);
INSERT INTO `sys_user` VALUES (164, 'zhang1972', '', '2064612544', '张杰宏', 0, '2015-02-20', 'https://image.ruicindy.cn/FilmSupplies', 23, '4007582311586533', 'jiehzh5@icloud.com', '1000001', '1001003', '2021-09-11 15:22:19', '2024-06-06 22:35:34', '2024-05-10 22:24:54', 'F', 459, 343);
INSERT INTO `sys_user` VALUES (165, 'xsu922', '', '17043565108', '孙秀英', 0, '2006-10-24', 'http://drive.leslip80.us/Beauty', 37, '4377442862448850', 'xsun322@outlook.com', '1000001', '1001003', '2009-01-26 22:26:29', '2024-06-08 01:17:17', '2024-05-10 22:24:54', 'F', 280, 559);
INSERT INTO `sys_user` VALUES (166, 'yuningwei4', '', '19952672339', '魏宇宁', 0, '2000-11-26', 'https://www.noguchisakura.cn/PetSupplies', 61, '4197270409405606', 'yuning401@icloud.com', '1000001', '1001003', '2024-01-08 02:09:19', '2024-05-15 07:54:46', '2024-05-10 22:24:54', 'T', 371, 846);
INSERT INTO `sys_user` VALUES (167, 'xiuyiduan', '', '106485841', '段秀英', 0, '2022-12-06', 'http://video.jesse116.co.jp/ArtsHandicraftsSewing', 55, '4687310869200043', 'duanxiuy5@outlook.com', '1000001', '1001003', '2017-12-11 07:43:58', '2024-05-24 07:02:56', '2024-05-10 22:24:54', 'T', 237, 214);
INSERT INTO `sys_user` VALUES (168, 'shiy', '', '76074146454', '史宇宁', 1, '2023-06-22', 'https://drive.takadam.biz/Handcrafts', 41, '348363854824789', 'sy1204@yahoo.com', '1000001', '1001003', '2007-07-18 01:34:59', '2024-05-11 23:12:17', '2024-05-10 22:24:54', 'F', 441, 588);
INSERT INTO `sys_user` VALUES (169, 'jiehongj9', '', '19207349721', '贾杰宏', 0, '2003-11-09', 'https://image.jiehong2020.biz/PetSupplies', 26, '371442995803702', 'jiehji@mail.com', '1000001', '1001003', '2016-04-07 10:19:21', '2024-06-01 06:58:51', '2024-05-10 22:24:54', 'F', 965, 640);
INSERT INTO `sys_user` VALUES (170, 'longz', '', '14129486615', '龙子异', 0, '2023-11-09', 'https://www.cyyeung1218.cn/BaggageTravelEquipment', 88, '5159945272867197', 'lonz57@outlook.com', '1000001', '1001003', '2001-06-21 05:15:25', '2024-05-22 23:10:35', '2024-05-10 22:24:54', 'T', 575, 147);
INSERT INTO `sys_user` VALUES (171, 'peng706', '', '17576758903', '彭嘉伦', 0, '2015-03-21', 'http://www.qijialun7.com/Books', 87, '3565919375456912', 'jialupe@gmail.com', '1000001', '1001003', '2023-09-29 22:55:25', '2024-06-02 03:18:15', '2024-05-10 22:24:54', 'T', 184, 245);
INSERT INTO `sys_user` VALUES (172, 'cyun', '', '17609071518', '崔云熙', 1, '2000-07-15', 'http://auth.yamhana10.us/HealthBabyCare', 70, '4988967097431439', 'cuy2@gmail.com', '1000001', '1001003', '2008-05-18 02:33:02', '2024-05-17 06:36:26', '2024-05-10 22:24:54', 'F', 208, 89);
INSERT INTO `sys_user` VALUES (173, 'layuan', '', '7691632142', '袁岚', 0, '2005-08-28', 'http://image.vasquez59.co.jp/PetSupplies', 59, '5076995420307489', 'ylan@mail.com', '1000001', '1001003', '2012-04-23 22:22:28', '2024-05-28 13:40:30', '2024-05-10 22:24:54', 'F', 76, 627);
INSERT INTO `sys_user` VALUES (174, 'tianrui8', '', '14506390063', '田睿', 1, '2012-06-09', 'https://auth.moorek.co.jp/Others', 40, '379567482882736', 'tianrui@gmail.com', '1000001', '1001003', '2002-05-31 11:23:08', '2024-05-14 21:42:06', '2024-05-10 22:24:54', 'T', 861, 889);
INSERT INTO `sys_user` VALUES (175, 'zhiyqiu', '', '19187526995', '邱致远', 1, '2024-01-17', 'https://drive.hawkinstiff.info/FilmSupplies', 41, '4112412204908374', 'zhiqi324@icloud.com', '1000001', '1001003', '2021-07-21 20:23:16', '2024-05-15 13:11:23', '2024-05-10 22:24:54', 'F', 989, 644);
INSERT INTO `sys_user` VALUES (176, 'djiehong912', '', '1034197825', '杜杰宏', 0, '2020-04-11', 'http://video.yhir.us/PetSupplies', 67, '3588271224061920', 'jiehongdu2009@gmail.com', '1000001', '1001003', '2013-03-24 05:44:26', '2024-05-28 07:06:39', '2024-05-10 22:24:54', 'T', 43, 736);
INSERT INTO `sys_user` VALUES (177, 'ziyiyao', '', '16389850960', '姚子异', 0, '2014-08-31', 'http://video.tanj.net/Books', 73, '5368546864739476', 'yao9@mail.com', '1000001', '1001003', '2002-11-03 08:53:17', '2024-05-14 13:39:43', '2024-05-10 22:24:54', 'T', 446, 380);
INSERT INTO `sys_user` VALUES (178, 'xiyunin7', '', '17238386201', '谢宇宁', 1, '2012-05-31', 'https://www.helen8.xyz/Baby', 66, '3529439014085746', 'xyun@gmail.com', '1000001', '1001003', '2023-04-20 02:20:03', '2024-06-04 20:36:08', '2024-05-10 22:24:54', 'T', 782, 257);
INSERT INTO `sys_user` VALUES (179, 'jyunxi', '', '7609563013', '金云熙', 0, '2008-03-16', 'http://drive.crjo.us/IndustrialScientificSupplies', 93, '5073133626935720', 'yjin58@gmail.com', '1000001', '1001003', '2011-06-12 14:54:43', '2024-05-18 18:21:28', '2024-05-10 22:24:54', 'F', 861, 519);
INSERT INTO `sys_user` VALUES (180, 'xielan', '', '16800813412', '谢岚', 0, '2017-07-05', 'http://video.lru410.biz/CDsVinyl', 66, '370936129961238', 'lanx40@yahoo.com', '1000001', '1001003', '2006-11-12 13:18:35', '2024-05-25 08:03:25', '2024-05-10 22:24:54', 'F', 275, 190);
INSERT INTO `sys_user` VALUES (181, 'yunxi426', '', '19862991550', '李云熙', 1, '2017-06-04', 'https://video.nchiyuen.jp/Handcrafts', 90, '4779896346811104', 'liy@gmail.com', '1000001', '1001003', '2001-10-07 22:14:01', '2024-05-18 20:23:37', '2024-05-10 22:24:54', 'F', 235, 726);
INSERT INTO `sys_user` VALUES (182, 'cxiaoming', '', '2032782206', '曹晓明', 0, '2018-06-21', 'https://image.hab.net/ComputersElectronics', 19, '347602712405062', 'cxiaoming111@icloud.com', '1000001', '1001003', '2004-07-03 19:18:58', '2024-06-06 22:08:34', '2024-05-10 22:24:54', 'F', 835, 527);
INSERT INTO `sys_user` VALUES (183, 'zhiyuan3', '', '200214657', '田致远', 0, '2023-03-24', 'http://image.manwaiman.net/Beauty', 63, '4769094441083264', 'tzhiy731@icloud.com', '1000001', '1001003', '2010-11-25 18:29:12', '2024-06-05 21:17:02', '2024-05-10 22:24:54', 'F', 994, 619);
INSERT INTO `sys_user` VALUES (184, 'zitaf', '', '109811021', '方子韬', 1, '2019-06-28', 'http://image.hashrin.co.jp/CollectiblesFineArt', 92, '3540552814828977', 'fzitao@gmail.com', '1000001', '1001003', '2022-01-22 19:28:35', '2024-05-19 07:53:45', '2024-05-10 22:24:54', 'T', 879, 242);
INSERT INTO `sys_user` VALUES (185, 'ruixia', '', '103639743', '夏睿', 1, '2013-06-16', 'https://video.masuda1210.xyz/CellPhonesAccessories', 61, '3550126467640349', 'ruixia@outlook.com', '1000001', '1001003', '2023-10-17 03:51:40', '2024-06-03 08:23:49', '2024-05-10 22:24:54', 'F', 478, 27);
INSERT INTO `sys_user` VALUES (186, 'jiehongli', '', '2083653743', '李杰宏', 1, '2012-03-14', 'http://image.robit59.cn/ToolsHomeDecoration', 40, '370835633948436', 'jiehong10@outlook.com', '1000001', '1001003', '2015-01-27 05:53:33', '2024-05-14 15:35:19', '2024-05-10 22:24:54', 'F', 109, 196);
INSERT INTO `sys_user` VALUES (187, 'ruiwa', '', '76059997221', '王睿', 1, '2024-02-01', 'https://www.ayano50.com/CellPhonesAccessories', 78, '4911856115886181', 'wangr1943@gmail.com', '1000001', '1001003', '2015-12-01 03:35:58', '2024-05-18 11:27:36', '2024-05-10 22:24:54', 'F', 231, 899);
INSERT INTO `sys_user` VALUES (188, 'yangzitao10', '', '7600055788', '杨子韬', 0, '2021-02-05', 'https://www.mitsuki18.co.jp/Beauty', 82, '6299386758483366', 'zitaoyang@icloud.com', '1000001', '1001003', '2014-10-05 19:44:11', '2024-05-19 03:51:33', '2024-05-10 22:24:54', 'T', 224, 825);
INSERT INTO `sys_user` VALUES (189, 'yhou', '', '76012530640', '侯云熙', 1, '2010-06-07', 'http://image.chibayuna.cn/HealthBabyCare', 22, '343787747362927', 'houy@outlook.com', '1000001', '1001003', '2017-12-12 06:39:11', '2024-05-14 17:16:22', '2024-05-10 22:24:54', 'F', 73, 777);
INSERT INTO `sys_user` VALUES (190, 'shilan', '', '16640622535', '史岚', 1, '2012-03-10', 'http://auth.mmio3.biz/CenturionGardenOutdoor', 17, '6226316550890458', 'shilan@gmail.com', '1000001', '1001003', '2023-01-20 17:23:39', '2024-05-14 22:05:55', '2024-05-10 22:24:54', 'T', 700, 844);
INSERT INTO `sys_user` VALUES (191, 'xiuying803', '', '76036754107', '吴秀英', 1, '2024-02-07', 'http://video.houyu.xyz/CellPhonesAccessories', 39, '6229520742580904', 'wuxiu@gmail.com', '1000001', '1001003', '2004-12-06 22:13:32', '2024-06-10 18:31:21', '2024-05-10 22:24:54', 'T', 902, 409);
INSERT INTO `sys_user` VALUES (192, 'ruitang806', '', '215637747', '汤睿', 1, '2006-09-08', 'https://video.ishida8.jp/Handcrafts', 95, '6268092339896616', 'tarui@yahoo.com', '1000001', '1001003', '2005-12-03 00:15:05', '2024-05-20 21:46:56', '2024-05-10 22:24:54', 'F', 91, 48);
INSERT INTO `sys_user` VALUES (193, 'zyu1971', '', '212518081', '周云熙', 1, '2004-06-04', 'http://image.zhiyuan1124.biz/BaggageTravelEquipment', 49, '5181219635453026', 'yunxizhou@hotmail.com', '1000001', '1001003', '2005-06-08 13:42:51', '2024-05-16 08:27:59', '2024-05-10 22:24:54', 'F', 378, 783);
INSERT INTO `sys_user` VALUES (194, 'zitao2', '', '16424315756', '许子韬', 1, '2020-06-19', 'https://auth.russellpatt.org/Beauty', 88, '3559580661604306', 'zitaoxu@hotmail.com', '1000001', '1001003', '2018-01-03 15:58:57', '2024-05-12 14:33:19', '2024-05-10 22:24:54', 'F', 136, 102);
INSERT INTO `sys_user` VALUES (195, 'yrui', '', '13534041418', '严睿', 1, '2020-12-10', 'http://image.reyokoyama82.com/CDsVinyl', 71, '5457965025283558', 'yanru@icloud.com', '1000001', '1001003', '2019-07-16 22:18:16', '2024-06-05 20:50:11', '2024-05-10 22:24:54', 'F', 457, 338);
INSERT INTO `sys_user` VALUES (196, 'wshihan', '', '13241788066', '魏詩涵', 1, '2001-04-24', 'https://image.as3.info/ComputersElectronics', 26, '3538297476294464', 'weishihan@icloud.com', '1000001', '1001003', '2018-12-03 18:58:54', '2024-05-30 06:20:17', '2024-05-10 22:24:54', 'F', 212, 261);
INSERT INTO `sys_user` VALUES (197, 'zhiyuan7', '', '18619969410', '张致远', 1, '2002-04-27', 'http://auth.manwingsze9.info/Baby', 39, '4709855240193923', 'zhanzhiyu@yahoo.com', '1000001', '1001003', '2001-01-16 07:13:07', '2024-05-14 03:23:15', '2024-05-10 22:24:54', 'T', 856, 804);
INSERT INTO `sys_user` VALUES (198, 'dxiaoming', '', '2131187580', '丁晓明', 1, '2020-07-01', 'https://video.htakeuchi702.com/HouseholdKitchenAppliances', 60, '347757069874441', 'xiadi@gmail.com', '1000001', '1001003', '2008-07-30 00:14:05', '2024-05-14 16:38:21', '2024-05-10 22:24:54', 'T', 933, 824);
INSERT INTO `sys_user` VALUES (199, 'yuningliao', '', '14811817346', '廖宇宁', 1, '2018-05-10', 'https://drive.tlt86.com/AutomotivePartsAccessories', 45, '3537804589482570', 'liaoyuning7@yahoo.com', '1000001', '1001003', '2004-03-07 04:29:38', '2024-05-30 06:53:30', '2024-05-10 22:24:54', 'T', 528, 355);
INSERT INTO `sys_user` VALUES (200, 'antian', '', '76039842631', '田安琪', 1, '2011-11-24', 'https://auth.lui7.org/Appliances', 56, '6286978973247785', 'anti7@icloud.com', '1000001', '1001003', '2023-09-15 20:47:58', '2024-06-08 16:09:06', '2024-05-10 22:24:54', 'F', 6, 100);
INSERT INTO `sys_user` VALUES (201, 'xiangjiehong1', '', '76992943398', '向杰宏', 1, '2000-11-28', 'https://auth.shihan1982.com/AppsGames', 34, '3576071756032677', 'jiehxiang@gmail.com', '1000001', '1001003', '2013-12-30 20:42:29', '2024-06-01 06:04:47', '2024-05-10 22:24:54', 'T', 998, 546);
INSERT INTO `sys_user` VALUES (202, 'zitao60', '', '14002238061', '戴子韬', 1, '2001-06-07', 'http://video.kwokwingk.com/Food', 46, '6204832596138419', 'dzit@hotmail.com', '1000001', '1001003', '2006-05-15 16:41:07', '2024-06-01 08:20:38', '2024-05-10 22:24:54', 'F', 527, 807);
INSERT INTO `sys_user` VALUES (203, 'qiuziyi216', '', '2064498196', '邱子异', 1, '2018-10-15', 'http://video.qil.co.jp/ToysGames', 66, '5504777497132210', 'qiuz@yahoo.com', '1000001', '1001003', '2016-02-19 06:17:23', '2024-05-24 09:51:52', '2024-05-10 22:24:54', 'T', 254, 96);
INSERT INTO `sys_user` VALUES (204, 'zhengzhiyuan514', '', '14518093762', '郑致远', 1, '2007-05-22', 'https://auth.inan.jp/BeautyPersonalCare', 53, '6230151616416301', 'zzh@hotmail.com', '1000001', '1001003', '2004-02-04 08:39:58', '2024-06-03 02:24:38', '2024-05-10 22:24:54', 'T', 412, 817);
INSERT INTO `sys_user` VALUES (205, 'tang616', '', '18247250246', '汤震南', 1, '2014-06-06', 'http://video.allenruby2.us/ArtsHandicraftsSewing', 43, '6203325986394412', 'tzhennan1955@gmail.com', '1000001', '1001003', '2004-12-18 07:46:11', '2024-05-28 20:24:52', '2024-05-10 22:24:54', 'F', 153, 328);
INSERT INTO `sys_user` VALUES (206, 'yajialu', '', '14064886141', '阎嘉伦', 0, '2015-06-07', 'https://image.choikwo311.net/CellPhonesAccessories', 81, '3547555921930258', 'jya5@icloud.com', '1000001', '1001003', '2005-02-25 05:39:24', '2024-05-25 04:04:20', '2024-05-10 22:24:54', 'F', 432, 843);
INSERT INTO `sys_user` VALUES (207, 'zengziy', '', '1000375940', '曾子异', 1, '2000-03-08', 'https://drive.hokyauchu.biz/IndustrialScientificSupplies', 82, '3578573634291597', 'zzeng6@gmail.com', '1000001', '1001003', '2004-12-01 02:52:23', '2024-06-09 05:55:26', '2024-05-10 22:24:54', 'T', 320, 83);
INSERT INTO `sys_user` VALUES (208, 'zitaosun504', '', '2825076304', '孙子韬', 0, '2006-05-31', 'https://www.ryota10.jp/SportsOutdoor', 50, '6279861149239838', 'zitao7@mail.com', '1000001', '1001003', '2023-02-23 08:15:38', '2024-05-22 03:53:33', '2024-05-10 22:24:54', 'T', 280, 285);
INSERT INTO `sys_user` VALUES (209, 'zhoxiuying715', '', '17564040723', '钟秀英', 0, '2014-04-14', 'https://video.qinzit1203.xyz/Books', 100, '4591737713282865', 'xz46@icloud.com', '1000001', '1001003', '2017-02-01 06:53:13', '2024-05-26 14:44:19', '2024-05-10 22:24:54', 'F', 336, 87);
INSERT INTO `sys_user` VALUES (210, 'fanjialun62', '', '218815076', '方嘉伦', 1, '2010-08-02', 'https://auth.shemo6.co.jp/BaggageTravelEquipment', 75, '4771227410974552', 'jialunfang731@hotmail.com', '1000001', '1001003', '2004-10-04 03:38:43', '2024-05-31 12:40:59', '2024-05-10 22:24:54', 'F', 212, 470);
INSERT INTO `sys_user` VALUES (211, 'yunxishi', '', '13416841921', '史云熙', 1, '2012-06-27', 'https://drive.jeblack1946.cn/FilmSupplies', 64, '6237871213003128', 'syun@icloud.com', '1000001', '1001003', '2023-10-24 13:05:24', '2024-05-19 08:46:24', '2024-05-10 22:24:54', 'T', 813, 93);
INSERT INTO `sys_user` VALUES (212, 'zhennangu1105', '', '13285310152', '郭震南', 1, '2008-05-12', 'https://video.garzalarr.us/HouseholdKitchenAppliances', 70, '6264907441007162', 'guoz6@icloud.com', '1000001', '1001003', '2017-12-20 22:44:45', '2024-05-10 09:12:55', '2024-05-10 22:24:54', 'T', 756, 914);
INSERT INTO `sys_user` VALUES (213, 'zhaxiaoming5', '', '16717478714', '张晓明', 0, '2014-12-26', 'https://auth.km1102.us/Appliances', 84, '6259646739804941', 'xiaozhang@gmail.com', '1000001', '1001003', '2016-10-20 20:13:49', '2024-05-22 09:02:51', '2024-05-10 22:24:54', 'F', 394, 702);
INSERT INTO `sys_user` VALUES (214, 'luzou02', '', '16666800076', '邹璐', 1, '2017-07-31', 'https://video.conniest.xyz/ToysGames', 54, '4312008818108118', 'luzo@hotmail.com', '1000001', '1001003', '2008-06-24 10:38:15', '2024-05-30 09:47:49', '2024-05-10 22:24:54', 'F', 92, 419);
INSERT INTO `sys_user` VALUES (215, 'zhanglu92', '', '19384260756', '张璐', 0, '2010-02-11', 'https://www.mome.info/ToolsHomeDecoration', 86, '348605741055153', 'luzhang826@mail.com', '1000001', '1001003', '2021-01-02 01:35:09', '2024-06-01 22:29:52', '2024-05-10 22:24:54', 'F', 817, 554);
INSERT INTO `sys_user` VALUES (216, 'zhuz', '', '14318933071', '朱致远', 0, '2013-12-06', 'https://image.ishim.us/CenturionGardenOutdoor', 54, '6235265910230716', 'zhzhu3@gmail.com', '1000001', '1001003', '2004-08-04 10:14:47', '2024-05-17 11:20:20', '2024-05-10 22:24:54', 'T', 69, 227);
INSERT INTO `sys_user` VALUES (217, 'mao1965', '', '13776986595', '毛宇宁', 0, '2009-02-10', 'https://auth.pedwar40.xyz/Handcrafts', 82, '3532900650458179', 'maoy@gmail.com', '1000001', '1001003', '2008-11-09 10:33:13', '2024-05-23 23:33:13', '2024-05-10 22:24:54', 'T', 663, 151);
INSERT INTO `sys_user` VALUES (218, 'ziyilong', '', '7552231865', '龙子异', 0, '2018-10-21', 'http://image.kaminghun94.info/HouseholdKitchenAppliances', 57, '5204376990205905', 'zlong@hotmail.com', '1000001', '1001003', '2003-11-27 18:45:20', '2024-05-23 03:58:23', '2024-05-10 22:24:54', 'T', 39, 16);
INSERT INTO `sys_user` VALUES (219, 'cai1', '', '2059399111', '蔡詩涵', 0, '2002-06-17', 'https://video.xiayuning4.net/HouseholdKitchenAppliances', 90, '6237586571278654', 'cai8@gmail.com', '1000001', '1001003', '2022-11-10 05:59:00', '2024-06-10 23:01:36', '2024-05-10 22:24:54', 'F', 508, 165);
INSERT INTO `sys_user` VALUES (220, 'shenxia', '', '208048143', '沈晓明', 0, '2019-11-08', 'https://auth.yuenls.com/CollectiblesFineArt', 88, '4375708528228254', 'shenxi@gmail.com', '1000001', '1001003', '2000-08-24 00:45:25', '2024-06-09 08:09:56', '2024-05-10 22:24:54', 'T', 62, 944);
INSERT INTO `sys_user` VALUES (221, 'xuxiuyi', '', '13078992821', '徐秀英', 1, '2001-08-31', 'http://www.yungfs605.biz/AutomotivePartsAccessories', 59, '348450762853891', 'xu54@outlook.com', '1000001', '1001003', '2016-08-16 11:02:32', '2024-06-05 00:37:40', '2024-05-10 22:24:54', 'F', 530, 991);
INSERT INTO `sys_user` VALUES (222, 'qianjiehong', '', '18339948657', '钱杰宏', 0, '2000-02-14', 'http://video.rematsui415.xyz/HealthBabyCare', 67, '344470719226359', 'qian02@gmail.com', '1000001', '1001003', '2019-08-09 09:55:48', '2024-05-30 10:10:31', '2024-05-10 22:24:54', 'F', 518, 695);
INSERT INTO `sys_user` VALUES (223, 'chengji', '', '17525959797', '程杰宏', 1, '2015-12-30', 'https://drive.makina.co.jp/SportsOutdoor', 48, '6265141366468477', 'chengjieh@outlook.com', '1000001', '1001003', '2018-04-21 11:50:30', '2024-06-04 04:07:56', '2024-05-10 22:24:54', 'T', 138, 497);
INSERT INTO `sys_user` VALUES (224, 'lujieho', '', '7697153252', '吕杰宏', 1, '2024-05-08', 'http://image.ikkiiw.co.jp/AutomotivePartsAccessories', 95, '4203689894994380', 'lujiehong@outlook.com', '1000001', '1001003', '2005-01-25 23:50:35', '2024-05-12 09:11:54', '2024-05-10 22:24:54', 'T', 422, 229);
INSERT INTO `sys_user` VALUES (225, 'zitao67', '', '7695052565', '邵子韬', 1, '2013-09-19', 'http://www.shirleysa84.org/CollectiblesFineArt', 60, '5469342504205625', 'zitash@mail.com', '1000001', '1001003', '2005-03-09 13:15:10', '2024-05-12 05:09:57', '2024-05-10 22:24:54', 'F', 71, 163);
INSERT INTO `sys_user` VALUES (226, 'shguo1', '', '18808366380', '郭詩涵', 0, '2017-07-17', 'http://auth.kmfu.org/VideoGames', 34, '6234784633657675', 'gus@gmail.com', '1000001', '1001003', '2014-09-01 23:31:21', '2024-05-20 14:27:01', '2024-05-10 22:24:54', 'T', 296, 876);
INSERT INTO `sys_user` VALUES (227, 'rc16', '', '2088957489', '程睿', 0, '2000-08-20', 'http://drive.cychang9.xyz/ToysGames', 20, '345815608645540', 'ruic@gmail.com', '1000001', '1001003', '2012-04-26 06:33:19', '2024-06-03 01:51:56', '2024-05-10 22:24:54', 'F', 678, 190);
INSERT INTO `sys_user` VALUES (228, 'zitao98', '', '76916156068', '姚子韬', 0, '2017-10-09', 'https://auth.rodneyreed.biz/AppsGames', 48, '5164986043180091', 'yaozitao@outlook.com', '1000001', '1001003', '2009-12-27 11:13:31', '2024-05-27 04:38:22', '2024-05-10 22:24:54', 'F', 959, 908);
INSERT INTO `sys_user` VALUES (229, 'anqixu', '', '76004107628', '许安琪', 0, '2000-09-14', 'http://video.luhe.jp/ToolsHomeDecoration', 57, '4917761730074193', 'xa74@gmail.com', '1000001', '1001003', '2010-06-01 04:30:55', '2024-05-15 01:47:18', '2024-05-10 22:24:54', 'F', 755, 156);
INSERT INTO `sys_user` VALUES (230, 'qius1989', '', '16816730748', '邱詩涵', 0, '2002-06-14', 'https://www.wt86.info/BaggageTravelEquipment', 56, '4209969628708932', 'qiu1221@outlook.com', '1000001', '1001003', '2014-03-03 20:41:14', '2024-05-25 17:58:29', '2024-05-10 22:24:54', 'F', 853, 204);
INSERT INTO `sys_user` VALUES (231, 'xianzitao1105', '', '2895279772', '向子韬', 1, '2019-01-24', 'https://image.rm69.jp/ArtsHandicraftsSewing', 71, '4503622583740142', 'xiangzit95@outlook.com', '1000001', '1001003', '2007-08-14 14:26:43', '2024-06-01 22:28:38', '2024-05-10 22:24:54', 'T', 59, 42);
INSERT INTO `sys_user` VALUES (232, 'zhennanli', '', '1011781870', '黎震南', 1, '2003-05-21', 'https://drive.yunxi1231.xyz/Others', 54, '6263370330649613', 'zhennan61@icloud.com', '1000001', '1001003', '2021-09-03 04:03:42', '2024-05-17 23:55:52', '2024-05-10 22:24:54', 'F', 807, 125);
INSERT INTO `sys_user` VALUES (233, 'weiyuning', '', '287077750', '魏宇宁', 0, '2002-06-01', 'https://www.fushing99.cn/HealthBabyCare', 99, '3549908966304194', 'wei1009@yahoo.com', '1000001', '1001003', '2015-01-10 17:16:03', '2024-05-20 14:05:08', '2024-05-10 22:24:54', 'T', 670, 954);
INSERT INTO `sys_user` VALUES (234, 'calu', '', '16283618157', '蔡璐', 1, '2008-08-10', 'https://drive.mgot.com/CellPhonesAccessories', 35, '3558438012340727', 'cai6@mail.com', '1000001', '1001003', '2005-11-30 00:19:00', '2024-05-29 05:45:12', '2024-05-10 22:24:54', 'F', 944, 504);
INSERT INTO `sys_user` VALUES (235, 'xiuyingl', '', '2152478452', '吕秀英', 0, '2024-03-25', 'https://auth.guxiuying.biz/Baby', 100, '5593147144268498', 'xilu816@icloud.com', '1000001', '1001003', '2016-05-05 09:19:34', '2024-05-21 09:41:29', '2024-05-10 22:24:54', 'T', 30, 44);
INSERT INTO `sys_user` VALUES (236, 'xxiuying', '', '2865142773', '谢秀英', 0, '2021-04-24', 'https://www.holf.biz/ToolsHomeDecoration', 46, '6249765107940682', 'xxi1@icloud.com', '1000001', '1001003', '2022-01-05 22:54:38', '2024-05-19 12:41:32', '2024-05-10 22:24:54', 'F', 104, 852);
INSERT INTO `sys_user` VALUES (237, 'zhiyuan9', '', '18386403054', '龚致远', 0, '2018-02-12', 'https://drive.yzitao.net/Books', 95, '349739234880816', 'zhigong712@icloud.com', '1000001', '1001003', '2023-06-01 16:33:34', '2024-05-27 13:31:19', '2024-05-10 22:24:54', 'T', 31, 476);
INSERT INTO `sys_user` VALUES (238, 'atao', '', '14637159541', '陶安琪', 1, '2000-07-05', 'https://video.miuws.net/SportsOutdoor', 58, '5013700748177283', 'anqit@icloud.com', '1000001', '1001003', '2012-05-04 03:56:13', '2024-05-13 01:57:56', '2024-05-10 22:24:54', 'T', 220, 148);
INSERT INTO `sys_user` VALUES (239, 'yunxluo', '', '75538866808', '罗云熙', 0, '2020-06-21', 'https://auth.amu.com/Beauty', 47, '3531216847253576', 'luoyunxi@yahoo.com', '1000001', '1001003', '2003-09-19 18:35:15', '2024-06-03 07:23:12', '2024-05-10 22:24:54', 'T', 521, 179);
INSERT INTO `sys_user` VALUES (240, 'zhfu', '', '15427276002', '傅致远', 0, '2001-04-17', 'https://video.jialuli.jp/Books', 84, '4372631583042373', 'fu72@gmail.com', '1000001', '1001003', '2016-03-06 09:33:19', '2024-06-02 01:21:34', '2024-05-10 22:24:54', 'F', 467, 755);
INSERT INTO `sys_user` VALUES (241, 'lzh', '', '19964674187', '赵璐', 0, '2023-07-15', 'https://auth.wdavid.xyz/AppsGames', 78, '6210068875468407', 'lu52@outlook.com', '1000001', '1001003', '2022-12-03 13:33:52', '2024-05-21 05:39:24', '2024-05-10 22:24:54', 'T', 941, 69);
INSERT INTO `sys_user` VALUES (242, 'ziyi427', '', '7557769614', '高子异', 1, '2017-12-04', 'https://image.chimingheung2.jp/CDsVinyl', 87, '5010001215460424', 'ziyigao05@gmail.com', '1000001', '1001003', '2004-10-14 19:48:00', '2024-05-15 20:58:08', '2024-05-10 22:24:54', 'T', 640, 414);
INSERT INTO `sys_user` VALUES (243, 'yunxdai', '', '18110956495', '戴云熙', 0, '2004-05-21', 'http://drive.tracydaniels1201.jp/AutomotivePartsAccessories', 53, '377741305378394', 'ydai@icloud.com', '1000001', '1001003', '2017-05-22 04:26:05', '2024-06-10 18:17:08', '2024-05-10 22:24:54', 'T', 332, 647);
INSERT INTO `sys_user` VALUES (244, 'yzhiyuan526', '', '76945192700', '叶致远', 1, '2004-04-05', 'http://video.ff1974.cn/HouseholdKitchenAppliances', 73, '5430572287740036', 'yezh@icloud.com', '1000001', '1001003', '2008-10-17 07:10:01', '2024-05-16 16:49:45', '2024-05-10 22:24:54', 'F', 619, 572);
INSERT INTO `sys_user` VALUES (245, 'xue8', '', '7604470333', '薛秀英', 1, '2013-09-19', 'https://www.chunkm8.info/Beauty', 92, '345702078431165', 'xiuyxue3@outlook.com', '1000001', '1001003', '2008-11-22 14:11:37', '2024-06-07 09:17:10', '2024-05-10 22:24:54', 'F', 755, 631);
INSERT INTO `sys_user` VALUES (246, 'guzhiyuan', '', '1030965479', '顾致远', 0, '2021-09-15', 'https://drive.kimuraaos.biz/Handcrafts', 50, '3560197394878318', 'guzhi3@yahoo.com', '1000001', '1001003', '2016-02-01 15:11:24', '2024-05-25 06:20:33', '2024-05-10 22:24:54', 'T', 678, 527);
INSERT INTO `sys_user` VALUES (247, 'mo503', '', '2156877872', '莫致远', 0, '2013-12-12', 'https://video.hinama3.xyz/AutomotivePartsAccessories', 47, '5452700764664972', 'mozhiy@outlook.com', '1000001', '1001003', '2015-11-01 11:24:42', '2024-06-08 11:56:43', '2024-05-10 22:24:54', 'F', 898, 233);
INSERT INTO `sys_user` VALUES (248, 'xiaoz', '', '1088046091', '萧子韬', 1, '2009-11-20', 'https://auth.ziyisun.us/AppsGames', 67, '6289085831203252', 'zitaox@gmail.com', '1000001', '1001003', '2020-08-15 05:33:22', '2024-05-18 04:45:41', '2024-05-10 22:24:54', 'T', 92, 147);
INSERT INTO `sys_user` VALUES (249, 'zhxia', '', '206993802', '郑晓明', 1, '2005-07-25', 'http://auth.kwantinlok.us/Baby', 57, '6213139107757508', 'zhengxiaoming3@outlook.com', '1000001', '1001003', '2005-03-13 03:45:02', '2024-05-27 23:08:43', '2024-05-10 22:24:54', 'F', 925, 296);
INSERT INTO `sys_user` VALUES (250, 'zziyi', '', '16943357176', '邹子异', 0, '2006-12-04', 'http://auth.welchar41.net/MusicalInstrument', 94, '6205022215292495', 'zziyi@outlook.com', '1000001', '1001003', '2019-09-09 04:28:24', '2024-05-29 06:49:56', '2024-05-10 22:24:54', 'F', 619, 670);
INSERT INTO `sys_user` VALUES (251, 'lanliu', '', '7695628377', '刘岚', 1, '2001-07-19', 'http://www.hiutungchu.xyz/VideoGames', 17, '4097876317405735', 'liul@icloud.com', '1000001', '1001003', '2007-12-07 03:36:51', '2024-05-19 11:25:14', '2024-05-10 22:24:54', 'F', 393, 750);
INSERT INTO `sys_user` VALUES (252, 'mash10', '', '15069264958', '马詩涵', 1, '2018-07-13', 'http://www.she.us/Baby', 48, '5344729637167097', 'mshihan@hotmail.com', '1000001', '1001003', '2002-05-01 18:50:44', '2024-05-20 09:31:17', '2024-05-10 22:24:54', 'T', 303, 383);
INSERT INTO `sys_user` VALUES (253, 'maozitao', '', '17655608503', '毛子韬', 1, '2018-07-16', 'https://drive.araiyuto.biz/Others', 90, '5450014664109985', 'zimao@hotmail.com', '1000001', '1001003', '2010-03-28 00:44:05', '2024-06-01 01:39:30', '2024-05-10 22:24:54', 'F', 425, 435);
INSERT INTO `sys_user` VALUES (254, 'xiangxiu', '', '17643171171', '向秀英', 0, '2020-03-06', 'https://drive.yulis.cn/ToolsHomeDecoration', 48, '5244429197357524', 'xiuying1014@gmail.com', '1000001', '1001003', '2013-04-24 06:52:47', '2024-06-07 08:05:50', '2024-05-10 22:24:54', 'T', 61, 172);
INSERT INTO `sys_user` VALUES (255, 'zhiyuan219', '', '1057282550', '郝致远', 0, '2003-07-04', 'http://image.chb6.biz/HealthBabyCare', 98, '5126131366843028', 'zhh@hotmail.com', '1000001', '1001003', '2022-11-13 03:45:22', '2024-06-01 19:51:30', '2024-05-10 22:24:54', 'T', 391, 416);
INSERT INTO `sys_user` VALUES (256, 'zitazheng', '', '19897507578', '郑子韬', 0, '2013-04-14', 'https://www.fcw.us/CDsVinyl', 39, '4170444364533212', 'zheng1230@icloud.com', '1000001', '1001003', '2005-06-02 21:56:03', '2024-06-01 21:52:59', '2024-05-10 22:24:54', 'T', 563, 946);
INSERT INTO `sys_user` VALUES (257, 'xiuyingxu', '', '14458528036', '徐秀英', 1, '2016-07-02', 'https://drive.morro.info/Food', 69, '4667857582968565', 'xuxiuyi@outlook.com', '1000001', '1001003', '2022-07-03 07:15:16', '2024-06-04 06:00:17', '2024-05-10 22:24:54', 'F', 178, 420);
INSERT INTO `sys_user` VALUES (258, 'qziyi', '', '15995280955', '邱子异', 0, '2008-12-19', 'http://drive.kyam.info/Others', 84, '4035868323965336', 'zqiu1978@outlook.com', '1000001', '1001003', '2015-05-29 15:44:18', '2024-06-06 00:10:17', '2024-05-10 22:24:54', 'T', 770, 452);
INSERT INTO `sys_user` VALUES (259, 'yujialun', '', '216839902', '于嘉伦', 1, '2018-12-01', 'http://image.sakhika8.jp/CellPhonesAccessories', 75, '377950167664938', 'jyu3@yahoo.com', '1000001', '1001003', '2022-07-27 03:17:47', '2024-06-10 06:50:08', '2024-05-10 22:24:54', 'T', 657, 777);
INSERT INTO `sys_user` VALUES (260, 'ruizeng', '', '15684476963', '曾睿', 1, '2021-05-10', 'https://video.meyer205.info/IndustrialScientificSupplies', 70, '3572973026721803', 'zenru@yahoo.com', '1000001', '1001003', '2008-07-30 09:34:05', '2024-05-10 01:30:20', '2024-05-10 22:24:54', 'T', 703, 198);
INSERT INTO `sys_user` VALUES (261, 'zcui', '', '14332207446', '崔子韬', 1, '2012-04-17', 'http://image.ylchi.us/Handcrafts', 45, '4091386024825809', 'zicui88@mail.com', '1000001', '1001003', '2014-03-26 19:20:42', '2024-05-15 09:31:31', '2024-05-10 22:24:54', 'T', 480, 81);
INSERT INTO `sys_user` VALUES (262, 'xxiuy2', '', '203510461', '熊秀英', 0, '2021-08-16', 'https://image.hicar2.biz/AutomotivePartsAccessories', 69, '4948221370885823', 'xxion@icloud.com', '1000001', '1001003', '2006-07-03 01:38:48', '2024-05-26 18:20:31', '2024-05-10 22:24:54', 'T', 510, 82);
INSERT INTO `sys_user` VALUES (263, 'shihan528', '', '17814803678', '萧詩涵', 1, '2016-08-21', 'http://www.aguilarjos.biz/ArtsHandicraftsSewing', 33, '5292722577992691', 'xiash@gmail.com', '1000001', '1001003', '2018-07-17 21:34:43', '2024-05-23 17:02:01', '2024-05-10 22:24:54', 'F', 424, 666);
INSERT INTO `sys_user` VALUES (264, 'liziyi', '', '76931536320', '廖子异', 1, '2002-03-30', 'http://auth.luyu15.net/ToysGames', 63, '6273276819756175', 'lziyi9@mail.com', '1000001', '1001003', '2014-11-22 05:59:47', '2024-06-07 16:10:50', '2024-05-10 22:24:54', 'F', 406, 939);
INSERT INTO `sys_user` VALUES (265, 'cuiyunxi', '', '18575407848', '崔云熙', 1, '2017-06-05', 'https://image.lwa.org/BaggageTravelEquipment', 90, '347989786241832', 'cuiyunxi7@gmail.com', '1000001', '1001003', '2012-12-31 02:24:04', '2024-05-13 02:42:42', '2024-05-10 22:24:54', 'T', 108, 603);
INSERT INTO `sys_user` VALUES (266, 'fan4', '', '1045956071', '范宇宁', 1, '2012-12-11', 'http://drive.hikari121.org/SportsOutdoor', 36, '376838219070124', 'fany@gmail.com', '1000001', '1001003', '2010-06-02 20:38:09', '2024-05-27 20:35:46', '2024-05-10 22:24:54', 'F', 24, 986);
INSERT INTO `sys_user` VALUES (267, 'sxiuying', '', '205876114', '沈秀英', 1, '2012-08-10', 'https://www.aoi46.biz/CenturionGardenOutdoor', 71, '5160220070149678', 'shen4@yahoo.com', '1000001', '1001003', '2007-08-04 08:47:20', '2024-05-19 23:04:03', '2024-05-10 22:24:54', 'F', 581, 898);
INSERT INTO `sys_user` VALUES (268, 'qialan4', '', '17626005993', '钱岚', 1, '2006-11-01', 'https://www.itsuki1.net/Books', 45, '4479430079226729', 'qianlan1218@gmail.com', '1000001', '1001003', '2018-01-17 09:35:40', '2024-05-20 01:22:23', '2024-05-10 22:24:54', 'T', 892, 678);
INSERT INTO `sys_user` VALUES (269, 'xtang1980', '', '17780846756', '汤晓明', 1, '2010-07-17', 'https://image.mokwin.com/Beauty', 57, '4353583720666270', 'tangxiao@outlook.com', '1000001', '1001003', '2014-02-04 12:17:16', '2024-06-07 00:17:09', '2024-05-10 22:24:54', 'T', 147, 166);
INSERT INTO `sys_user` VALUES (270, 'jyu16', '', '76049650855', '余嘉伦', 1, '2000-09-18', 'http://image.kkmeng.xyz/Books', 65, '3544924509015713', 'jialun10@icloud.com', '1000001', '1001003', '2002-09-15 12:48:44', '2024-05-17 13:44:38', '2024-05-10 22:24:54', 'F', 276, 91);
INSERT INTO `sys_user` VALUES (271, 'xis', '', '18066410527', '孙晓明', 1, '2005-09-22', 'http://drive.jiehha.co.jp/Handcrafts', 25, '4554058575306881', 'sunxiaom@outlook.com', '1000001', '1001003', '2012-12-10 19:35:21', '2024-05-21 17:32:45', '2024-05-10 22:24:54', 'F', 885, 694);
INSERT INTO `sys_user` VALUES (272, 'jiehongx', '', '19461413323', '徐杰宏', 0, '2006-07-04', 'http://image.chu6.biz/ComputersElectronics', 66, '5172883531794683', 'xuji@hotmail.com', '1000001', '1001003', '2021-07-09 07:44:26', '2024-05-16 09:28:02', '2024-05-10 22:24:54', 'F', 808, 432);
INSERT INTO `sys_user` VALUES (273, 'xiaomingjia10', '', '14044510644', '贾晓明', 1, '2017-01-23', 'http://drive.yuetf.cn/HealthBabyCare', 45, '4054590163928820', 'xiaomingji@mail.com', '1000001', '1001003', '2005-10-09 01:28:21', '2024-06-09 17:57:36', '2024-05-10 22:24:54', 'T', 218, 252);
INSERT INTO `sys_user` VALUES (274, 'yelan', '', '2000133085', '叶岚', 0, '2015-12-07', 'https://www.choyeem921.org/ClothingShoesandJewelry', 22, '4307472095818921', 'yel1974@gmail.com', '1000001', '1001003', '2008-08-23 18:14:24', '2024-06-06 05:14:21', '2024-05-10 22:24:54', 'F', 298, 956);
INSERT INTO `sys_user` VALUES (275, 'xiazhennan916', '', '280799384', '夏震南', 1, '2021-02-15', 'http://video.sakum1204.cn/FilmSupplies', 67, '5006496734195319', 'xiazhenn3@outlook.com', '1000001', '1001003', '2020-04-01 08:56:54', '2024-06-06 12:50:05', '2024-05-10 22:24:54', 'T', 367, 566);
INSERT INTO `sys_user` VALUES (276, 'rga1953', '', '19633998142', '高睿', 0, '2019-07-05', 'http://drive.renasaito.co.jp/AutomotivePartsAccessories', 40, '5534714585018518', 'rui2@hotmail.com', '1000001', '1001003', '2012-10-24 15:16:19', '2024-05-29 08:07:35', '2024-05-10 22:24:54', 'F', 28, 623);
INSERT INTO `sys_user` VALUES (277, 'ya9', '', '285571838', '尹安琪', 1, '2013-04-27', 'http://drive.onnaleun322.co.jp/CDsVinyl', 31, '6288679838206515', 'anqiyi309@icloud.com', '1000001', '1001003', '2000-03-26 03:51:33', '2024-05-28 04:20:32', '2024-05-10 22:24:54', 'T', 63, 876);
INSERT INTO `sys_user` VALUES (278, 'pjieh', '', '14273354858', '潘杰宏', 0, '2009-06-01', 'http://auth.harrygonzalez.cn/Food', 70, '4260287923202279', 'jiehongpan@outlook.com', '1000001', '1001003', '2018-10-31 18:39:34', '2024-05-31 15:15:38', '2024-05-10 22:24:54', 'F', 607, 336);
INSERT INTO `sys_user` VALUES (279, 'hu50', '', '216931970', '胡詩涵', 1, '2013-10-15', 'https://drive.xiuying923.net/SportsOutdoor', 36, '4249595661945743', 'hu8@gmail.com', '1000001', '1001003', '2011-01-23 14:44:24', '2024-06-01 23:31:58', '2024-05-10 22:24:54', 'T', 965, 854);
INSERT INTO `sys_user` VALUES (280, 'plu1203', '', '219386287', '潘璐', 0, '2007-12-20', 'https://image.lindahi.jp/Baby', 97, '3562160598611206', 'lup1007@gmail.com', '1000001', '1001003', '2017-03-16 05:27:28', '2024-05-27 03:51:11', '2024-05-10 22:24:54', 'F', 833, 362);
INSERT INTO `sys_user` VALUES (281, 'lu54', '', '19148762636', '卢詩涵', 0, '2021-03-02', 'http://www.kwokmingyau3.jp/SportsOutdoor', 55, '6256669090132333', 'lus@hotmail.com', '1000001', '1001003', '2020-09-25 19:52:20', '2024-05-16 20:00:33', '2024-05-10 22:24:54', 'F', 534, 334);
INSERT INTO `sys_user` VALUES (282, 'ziyilei', '', '203131200', '雷子异', 0, '2012-08-04', 'https://auth.ren55.com/IndustrialScientificSupplies', 74, '372812926386218', 'ziyle@mail.com', '1000001', '1001003', '2017-08-19 07:21:59', '2024-05-18 22:18:28', '2024-05-10 22:24:54', 'T', 144, 42);
INSERT INTO `sys_user` VALUES (283, 'tan1980', '', '282616199', '谭杰宏', 1, '2006-05-07', 'https://auth.robertson714.jp/ToolsHomeDecoration', 52, '5028824899668594', 'jiehongtan@icloud.com', '1000001', '1001003', '2008-07-05 20:24:52', '2024-05-17 12:29:05', '2024-05-10 22:24:54', 'F', 765, 420);
INSERT INTO `sys_user` VALUES (284, 'qianlu16', '', '15690226629', '钱璐', 1, '2013-01-09', 'https://auth.renakay.biz/SportsOutdoor', 45, '6216077249898367', 'qianlu@gmail.com', '1000001', '1001003', '2007-04-08 04:27:53', '2024-05-27 19:16:22', '2024-05-10 22:24:54', 'F', 182, 757);
INSERT INTO `sys_user` VALUES (285, 'kongx', '', '16972079194', '孔晓明', 0, '2018-02-05', 'https://www.emp.net/CDsVinyl', 86, '3573647046356126', 'kong2003@outlook.com', '1000001', '1001003', '2003-09-19 01:32:00', '2024-06-04 18:15:25', '2024-05-10 22:24:54', 'F', 151, 822);
INSERT INTO `sys_user` VALUES (286, 'mo6', '', '18386599859', '莫秀英', 1, '2017-07-13', 'http://drive.zou2.cn/CDsVinyl', 91, '4731276118230642', 'xiumo@gmail.com', '1000001', '1001003', '2022-04-28 04:45:11', '2024-05-28 07:46:38', '2024-05-10 22:24:54', 'F', 432, 153);
INSERT INTO `sys_user` VALUES (287, 'qin9', '', '14080765615', '秦宇宁', 0, '2013-01-01', 'https://video.mcdjack125.co.jp/PetSupplies', 19, '6225602732221923', 'qinyuning@icloud.com', '1000001', '1001003', '2018-11-19 02:12:03', '2024-05-26 13:51:54', '2024-05-10 22:24:54', 'F', 886, 418);
INSERT INTO `sys_user` VALUES (288, 'fulu1958', '', '17349406273', '傅璐', 1, '2013-06-14', 'https://image.myuling.org/ComputersElectronics', 94, '5156385351237967', 'fulu1959@outlook.com', '1000001', '1001003', '2019-02-18 08:52:22', '2024-06-10 00:07:32', '2024-05-10 22:24:54', 'F', 404, 952);
INSERT INTO `sys_user` VALUES (289, 'zlu2', '', '216004404', '曾璐', 0, '2013-01-14', 'https://drive.kimmunoz80.cn/ToysGames', 97, '5291765096909796', 'lu7@gmail.com', '1000001', '1001003', '2003-10-13 20:08:30', '2024-06-06 04:31:11', '2024-05-10 22:24:54', 'T', 927, 13);
INSERT INTO `sys_user` VALUES (290, 'jix', '', '205878028', '夏嘉伦', 1, '2014-04-19', 'https://drive.gonzitao10.co.jp/MusicalInstrument', 62, '347030610083544', 'jialun93@gmail.com', '1000001', '1001003', '2006-05-07 18:55:52', '2024-05-20 08:22:09', '2024-05-10 22:24:54', 'T', 970, 519);
INSERT INTO `sys_user` VALUES (291, 'lanjin', '', '76998642011', '金岚', 1, '2011-01-01', 'http://image.ywl1110.co.jp/FilmSupplies', 90, '3543087592354315', 'lan306@outlook.com', '1000001', '1001003', '2005-12-28 13:54:45', '2024-05-12 16:26:38', '2024-05-10 22:24:54', 'T', 317, 672);
INSERT INTO `sys_user` VALUES (292, 'zhennan6', '', '16426466563', '高震南', 0, '2014-03-20', 'http://drive.xru1967.com/ToolsHomeDecoration', 90, '5067487589940616', 'gaoz1943@icloud.com', '1000001', '1001003', '2017-02-28 17:31:28', '2024-05-23 18:31:34', '2024-05-10 22:24:54', 'F', 26, 212);
INSERT INTO `sys_user` VALUES (293, 'ruiyuan', '', '19588311278', '袁睿', 0, '2001-04-15', 'https://drive.fongkwokwing.us/Others', 21, '6276284665257865', 'yuan11@hotmail.com', '1000001', '1001003', '2022-10-14 17:17:11', '2024-05-26 01:39:30', '2024-05-10 22:24:54', 'T', 663, 515);
INSERT INTO `sys_user` VALUES (294, 'zl2', '', '13195820372', '吕震南', 1, '2014-08-15', 'http://auth.at66.us/CDsVinyl', 71, '347690443209226', 'luzhenn@outlook.com', '1000001', '1001003', '2008-04-30 20:42:20', '2024-05-31 15:14:16', '2024-05-10 22:24:54', 'T', 599, 720);
INSERT INTO `sys_user` VALUES (295, 'jiang3', '', '13830475670', '姜子异', 0, '2009-06-03', 'https://www.bri2016.jp/Others', 36, '5541615781201462', 'ziyijia@hotmail.com', '1000001', '1001003', '2007-07-18 03:20:25', '2024-06-09 13:52:39', '2024-05-10 22:24:54', 'F', 599, 405);
INSERT INTO `sys_user` VALUES (296, 'jinanqi', '', '13807537961', '金安琪', 0, '2003-09-11', 'http://www.fuwingsuen.info/Others', 28, '4216617047829737', 'anqijin3@icloud.com', '1000001', '1001003', '2006-10-27 14:19:41', '2024-05-26 08:43:49', '2024-05-10 22:24:54', 'T', 951, 853);
INSERT INTO `sys_user` VALUES (297, 'layang', '', '76945039984', '杨岚', 0, '2017-04-12', 'http://auth.luzhennan1990.net/ArtsHandicraftsSewing', 90, '6285595962627396', 'yanglan@mail.com', '1000001', '1001003', '2008-01-09 00:41:46', '2024-06-04 15:06:28', '2024-05-10 22:24:54', 'F', 886, 112);
INSERT INTO `sys_user` VALUES (298, 'zhsu5', '', '209601206', '苏致远', 1, '2000-11-11', 'https://auth.janetr.jp/ClothingShoesandJewelry', 89, '376990222433730', 'szhiyuan1996@hotmail.com', '1000001', '1001003', '2018-05-04 20:20:27', '2024-06-07 16:02:43', '2024-05-10 22:24:54', 'F', 658, 349);
INSERT INTO `sys_user` VALUES (299, 'mayu', '', '2866415260', '毛宇宁', 0, '2009-05-17', 'http://www.du4.info/ToysGames', 16, '3543826102522592', 'mayunin@gmail.com', '1000001', '1001003', '2017-02-18 06:10:27', '2024-05-28 03:52:00', '2024-05-10 22:24:54', 'T', 238, 941);
INSERT INTO `sys_user` VALUES (300, 'hou9', '', '7604377640', '侯致远', 0, '2004-08-26', 'https://drive.zhennanya.biz/Handcrafts', 73, '5006321485288115', 'zhiyuanho@icloud.com', '1000001', '1001003', '2017-05-28 15:19:59', '2024-06-07 15:36:58', '2024-05-10 22:24:54', 'T', 280, 806);
INSERT INTO `sys_user` VALUES (301, 'ruilin', '', '76010072605', '林睿', 0, '2004-06-27', 'http://www.yunxi223.cn/BeautyPersonalCare', 86, '3574409319751619', 'rlin@outlook.com', '1000001', '1001003', '2001-05-31 19:17:18', '2024-06-04 12:04:52', '2024-05-10 22:24:54', 'F', 366, 566);
INSERT INTO `sys_user` VALUES (302, 'zhenganq', '', '76059341645', '郑安琪', 0, '2023-01-13', 'https://www.anthony5.cn/Beauty', 28, '5105606369782757', 'anqiz926@outlook.com', '1000001', '1001003', '2009-01-28 00:10:48', '2024-05-17 04:02:26', '2024-05-10 22:24:54', 'F', 107, 557);
INSERT INTO `sys_user` VALUES (303, 'lu307', '', '18838791979', '熊璐', 1, '2004-03-29', 'http://image.socl.xyz/FilmSupplies', 46, '6297202136977569', 'luxiong@icloud.com', '1000001', '1001003', '2004-07-23 17:09:03', '2024-06-01 13:36:13', '2024-05-10 22:24:54', 'T', 480, 204);
INSERT INTO `sys_user` VALUES (304, 'yuan817', '', '216811460', '袁云熙', 1, '2009-11-17', 'https://auth.aoshinakamura915.biz/PetSupplies', 94, '340197639157265', 'yuanyunxi@mail.com', '1000001', '1001003', '2022-06-19 11:14:08', '2024-06-06 21:50:36', '2024-05-10 22:24:54', 'F', 814, 875);
INSERT INTO `sys_user` VALUES (305, 'songyunxi12', '', '15390790466', '宋云熙', 1, '2019-08-08', 'http://www.alromero129.cn/VideoGames', 92, '377839864943861', 'sonyun40@gmail.com', '1000001', '1001003', '2017-11-22 08:48:03', '2024-06-05 18:14:32', '2024-05-10 22:24:54', 'F', 389, 559);
INSERT INTO `sys_user` VALUES (306, 'xianziyi', '', '2813672367', '向子异', 1, '2022-05-02', 'https://drive.awf.co.jp/ArtsHandicraftsSewing', 34, '4648783481131648', 'xiang06@icloud.com', '1000001', '1001003', '2019-01-16 13:24:11', '2024-05-12 12:43:50', '2024-05-10 22:24:54', 'F', 392, 567);
INSERT INTO `sys_user` VALUES (307, 'lwang', '', '17571618037', '王岚', 0, '2008-04-10', 'https://image.yuechiyuen.us/VideoGames', 77, '3539974628058729', 'lanwang@gmail.com', '1000001', '1001003', '2022-05-14 13:09:56', '2024-05-17 10:23:17', '2024-05-10 22:24:54', 'F', 96, 170);
INSERT INTO `sys_user` VALUES (308, 'jtang', '', '7690843664', '汤杰宏', 0, '2004-01-08', 'https://image.zitzh94.biz/BaggageTravelEquipment', 43, '5581791384755534', 'tajiehong9@mail.com', '1000001', '1001003', '2009-01-16 23:26:57', '2024-05-22 21:00:07', '2024-05-10 22:24:54', 'T', 143, 460);
INSERT INTO `sys_user` VALUES (309, 'qiu1', '', '2888409968', '邱晓明', 1, '2011-05-30', 'http://www.yamtw.info/SportsOutdoor', 60, '371421039093405', 'qiux@icloud.com', '1000001', '1001003', '2013-01-18 14:25:39', '2024-05-21 09:29:00', '2024-05-10 22:24:54', 'F', 38, 157);
INSERT INTO `sys_user` VALUES (310, 'luhu', '', '14821342924', '胡璐', 1, '2005-11-17', 'https://video.yuitono505.com/BaggageTravelEquipment', 49, '343044802751325', 'hlu@gmail.com', '1000001', '1001003', '2009-09-22 12:17:42', '2024-06-10 19:28:24', '2024-05-10 22:24:54', 'T', 823, 763);
INSERT INTO `sys_user` VALUES (311, 'xiuying2', '', '201231784', '韦秀英', 1, '2018-11-01', 'http://drive.williamj.org/CDsVinyl', 44, '5046182738732214', 'wx9@icloud.com', '1000001', '1001003', '2001-08-01 17:41:21', '2024-05-18 12:38:20', '2024-05-10 22:24:54', 'F', 621, 376);
INSERT INTO `sys_user` VALUES (312, 'xiaomingwang20', '', '2122957610', '王晓明', 0, '2015-12-11', 'https://auth.zhennanxi.com/CDsVinyl', 49, '5489666511776350', 'xiawang@mail.com', '1000001', '1001003', '2003-07-18 08:20:05', '2024-05-27 10:12:42', '2024-05-10 22:24:54', 'F', 773, 479);
INSERT INTO `sys_user` VALUES (313, 'rlu17', '', '1065623876', '吕睿', 1, '2019-05-11', 'http://video.em212.xyz/Handcrafts', 89, '5476437075594315', 'lurui@outlook.com', '1000001', '1001003', '2002-11-15 04:14:19', '2024-05-24 11:15:13', '2024-05-10 22:24:54', 'F', 548, 510);
INSERT INTO `sys_user` VALUES (314, 'wezi', '', '16951754688', '韦子异', 0, '2012-07-27', 'http://drive.auky.xyz/CDsVinyl', 63, '4801739188396341', 'weiziyi8@gmail.com', '1000001', '1001003', '2008-09-18 04:54:20', '2024-06-05 06:25:02', '2024-05-10 22:24:54', 'T', 966, 420);
INSERT INTO `sys_user` VALUES (315, 'zitaq', '', '218170001', '钱子韬', 0, '2019-07-31', 'https://drive.akinaaoki.us/Books', 30, '5457826560324837', 'ziqia@outlook.com', '1000001', '1001003', '2003-01-02 19:45:56', '2024-05-11 10:17:32', '2024-05-10 22:24:54', 'T', 455, 702);
INSERT INTO `sys_user` VALUES (316, 'yuningt81', '', '13005142511', '谭宇宁', 1, '2015-06-18', 'https://image.jialuxia517.biz/HouseholdKitchenAppliances', 59, '5233862368756483', 'tyuni5@icloud.com', '1000001', '1001003', '2021-10-14 23:40:44', '2024-05-23 21:38:21', '2024-05-10 22:24:54', 'F', 174, 662);
INSERT INTO `sys_user` VALUES (317, 'anqyu75', '', '18547362862', '于安琪', 0, '2018-09-16', 'https://video.kamliksun.com/Appliances', 92, '5201095662888268', 'anqi2@gmail.com', '1000001', '1001003', '2015-07-12 23:48:14', '2024-06-08 07:50:28', '2024-05-10 22:24:54', 'T', 257, 888);
INSERT INTO `sys_user` VALUES (318, 'zc5', '', '19806172047', '蔡子韬', 1, '2014-03-30', 'http://drive.chungwingkuen.cn/BaggageTravelEquipment', 29, '5192663855229433', 'zicai@icloud.com', '1000001', '1001003', '2005-01-31 03:22:56', '2024-05-25 07:51:12', '2024-05-10 22:24:54', 'F', 530, 101);
INSERT INTO `sys_user` VALUES (319, 'shihqiu', '', '15166196393', '邱詩涵', 1, '2009-10-16', 'https://drive.roy927.co.jp/ArtsHandicraftsSewing', 94, '5159304569232246', 'qshihan424@gmail.com', '1000001', '1001003', '2007-07-10 09:59:10', '2024-06-03 05:30:25', '2024-05-10 22:24:54', 'T', 256, 889);
INSERT INTO `sys_user` VALUES (320, 'zhuzhiy', '', '18893857749', '朱致远', 1, '2005-04-27', 'http://drive.onoa.jp/ClothingShoesandJewelry', 63, '3538344327209984', 'zhiyuan5@mail.com', '1000001', '1001003', '2018-06-18 00:59:49', '2024-06-03 18:35:25', '2024-05-10 22:24:54', 'T', 387, 175);
INSERT INTO `sys_user` VALUES (321, 'xshih', '', '75575301124', '夏詩涵', 1, '2003-03-02', 'http://auth.rinsakamoto.info/Handcrafts', 57, '6269349389795869', 'shihxia@gmail.com', '1000001', '1001003', '2014-04-22 16:01:16', '2024-05-20 16:01:49', '2024-05-10 22:24:54', 'F', 354, 824);
INSERT INTO `sys_user` VALUES (322, 'yxu', '', '19389344899', '许云熙', 0, '2003-01-26', 'http://image.cw7.us/Baby', 52, '5036350172224814', 'yx68@outlook.com', '1000001', '1001003', '2016-12-14 18:46:42', '2024-05-19 08:52:12', '2024-05-10 22:24:54', 'T', 675, 227);
INSERT INTO `sys_user` VALUES (323, 'yunxigong', '', '18948199266', '龚云熙', 0, '2019-12-28', 'https://drive.satotsubasa806.cn/Others', 62, '4283878574789185', 'gyun01@mail.com', '1000001', '1001003', '2000-09-11 21:16:14', '2024-06-06 18:10:26', '2024-05-10 22:24:54', 'F', 274, 727);
INSERT INTO `sys_user` VALUES (324, 'dlan7', '', '19098714369', '段岚', 0, '2004-10-04', 'http://www.yishi.com/Beauty', 51, '4626166419720169', 'duanlan@gmail.com', '1000001', '1001003', '2013-03-28 10:47:32', '2024-06-06 06:15:10', '2024-05-10 22:24:54', 'F', 47, 499);
INSERT INTO `sys_user` VALUES (325, 'xuanqi', '', '7607137158', '徐安琪', 0, '2000-08-07', 'http://www.ruijiang1116.net/Appliances', 81, '3578211155127235', 'anxu@gmail.com', '1000001', '1001003', '2019-01-02 13:59:24', '2024-06-09 18:00:36', '2024-05-10 22:24:54', 'T', 516, 21);
INSERT INTO `sys_user` VALUES (326, 'zhiyuanyin', '', '75506267774', '尹致远', 0, '2014-12-07', 'http://video.carol701.net/BaggageTravelEquipment', 52, '3565095852963462', 'yin2004@hotmail.com', '1000001', '1001003', '2000-01-15 17:05:27', '2024-05-29 06:42:41', '2024-05-10 22:24:54', 'F', 362, 158);
INSERT INTO `sys_user` VALUES (327, 'ljin9', '', '209862683', '金璐', 1, '2003-04-29', 'http://drive.joseph814.info/Baby', 45, '347681324710564', 'luji@gmail.com', '1000001', '1001003', '2021-01-28 06:35:03', '2024-06-06 21:39:28', '2024-05-10 22:24:54', 'T', 291, 907);
INSERT INTO `sys_user` VALUES (328, 'jialwa', '', '16183054486', '汪嘉伦', 0, '2008-12-03', 'https://drive.mjordan.info/Books', 19, '5289377190509565', 'wanj2@icloud.com', '1000001', '1001003', '2005-11-23 16:49:29', '2024-05-31 13:27:57', '2024-05-10 22:24:54', 'F', 379, 258);
INSERT INTO `sys_user` VALUES (329, 'anqiy', '', '17689523176', '严安琪', 0, '2022-05-09', 'http://www.garciaray.cn/MusicalInstrument', 35, '4842662776477490', 'ay58@icloud.com', '1000001', '1001003', '2021-01-06 17:12:56', '2024-06-01 02:34:59', '2024-05-10 22:24:54', 'T', 100, 833);
INSERT INTO `sys_user` VALUES (330, 'wangxiuying116', '', '1094968898', '王秀英', 0, '2007-06-17', 'http://auth.seikomi.info/ToysGames', 32, '344919932611680', 'xwan@gmail.com', '1000001', '1001003', '2010-10-24 19:46:13', '2024-05-29 07:22:24', '2024-05-10 22:24:54', 'T', 279, 562);
INSERT INTO `sys_user` VALUES (331, 'xiuyinglu1952', '', '7605638974', '吕秀英', 0, '2022-11-22', 'http://video.miosan.cn/MusicalInstrument', 56, '371314721968259', 'lu1108@gmail.com', '1000001', '1001003', '2010-07-19 12:34:02', '2024-06-01 20:59:46', '2024-05-10 22:24:54', 'T', 181, 194);
INSERT INTO `sys_user` VALUES (332, 'wu508', '', '7605911943', '吴杰宏', 0, '2019-11-13', 'http://www.momoekondo.net/HouseholdKitchenAppliances', 49, '6243276013417985', 'jiehongwu43@icloud.com', '1000001', '1001003', '2009-09-18 03:09:08', '2024-05-24 04:21:56', '2024-05-10 22:24:54', 'F', 397, 136);
INSERT INTO `sys_user` VALUES (333, 'guo9', '', '7553630234', '郭安琪', 0, '2002-09-08', 'https://video.jiehongzhong.xyz/Books', 44, '372049330054336', 'anqigu@icloud.com', '1000001', '1001003', '2016-10-12 17:11:29', '2024-06-02 13:42:53', '2024-05-10 22:24:54', 'F', 771, 853);
INSERT INTO `sys_user` VALUES (334, 'cuilu7', '', '19274966973', '崔璐', 0, '2020-10-07', 'http://image.kwokyintang41.net/Handcrafts', 67, '4971724492731627', 'clu319@gmail.com', '1000001', '1001003', '2018-05-19 03:12:16', '2024-05-20 04:10:09', '2024-05-10 22:24:54', 'T', 710, 157);
INSERT INTO `sys_user` VALUES (335, 'yuyan430', '', '7608402900', '严云熙', 0, '2013-05-11', 'http://image.zyuan.info/BaggageTravelEquipment', 74, '6261760146091533', 'yanyunxi@gmail.com', '1000001', '1001003', '2002-10-07 17:43:03', '2024-05-20 15:15:26', '2024-05-10 22:24:54', 'F', 332, 436);
INSERT INTO `sys_user` VALUES (336, 'll703', '', '16668571627', '龙璐', 1, '2009-04-08', 'https://drive.ireclark.jp/ClothingShoesandJewelry', 60, '6221654369091438', 'llong@outlook.com', '1000001', '1001003', '2001-08-11 21:16:48', '2024-05-23 13:11:00', '2024-05-10 22:24:54', 'F', 8, 545);
INSERT INTO `sys_user` VALUES (337, 'wei4', '', '16889995436', '魏震南', 0, '2001-08-15', 'http://www.iwasakitsubasa.jp/PetSupplies', 15, '374604842995893', 'wei814@outlook.com', '1000001', '1001003', '2006-12-19 05:36:26', '2024-05-21 22:12:17', '2024-05-10 22:24:54', 'F', 313, 657);
INSERT INTO `sys_user` VALUES (338, 'tangxi', '', '16232333238', '汤秀英', 0, '2024-03-31', 'https://video.kaitoiwasaki14.org/AppsGames', 53, '3551777768651192', 'xta@gmail.com', '1000001', '1001003', '2014-05-20 22:09:32', '2024-05-30 05:06:23', '2024-05-10 22:24:54', 'F', 80, 640);
INSERT INTO `sys_user` VALUES (339, 'xiaoming214', '', '2873107723', '史晓明', 1, '2022-08-15', 'https://video.liaji1991.com/SportsOutdoor', 20, '4265649883588994', 'xiaos@gmail.com', '1000001', '1001003', '2001-03-15 15:00:50', '2024-06-10 16:03:28', '2024-05-10 22:24:54', 'F', 810, 169);
INSERT INTO `sys_user` VALUES (340, 'tangr501', '', '17788428967', '唐睿', 1, '2001-08-28', 'http://auth.paulwells6.org/VideoGames', 35, '3540842547124822', 'tang411@gmail.com', '1000001', '1001003', '2012-06-19 07:39:00', '2024-05-31 21:05:51', '2024-05-10 22:24:54', 'T', 585, 57);
INSERT INTO `sys_user` VALUES (341, 'ziyixie', '', '2067886706', '谢子异', 1, '2012-09-18', 'http://image.cmunoz5.xyz/AutomotivePartsAccessories', 35, '6262501064437590', 'ziyixie@icloud.com', '1000001', '1001003', '2024-01-09 00:51:18', '2024-06-01 11:53:14', '2024-05-10 22:24:54', 'T', 703, 141);
INSERT INTO `sys_user` VALUES (342, 'shenxia405', '', '17131414852', '沈晓明', 0, '2011-01-30', 'http://drive.fujiwara10.jp/MusicalInstrument', 31, '347848806978361', 'xiaomingshen@hotmail.com', '1000001', '1001003', '2023-05-05 22:07:09', '2024-05-18 16:50:22', '2024-05-10 22:24:54', 'T', 522, 102);
INSERT INTO `sys_user` VALUES (343, 'zitaotao03', '', '19061752363', '陶子韬', 0, '2003-01-26', 'https://auth.onkay05.co.jp/Books', 94, '6234379995824417', 'zitao96@icloud.com', '1000001', '1001003', '2002-08-06 06:42:02', '2024-06-09 10:00:05', '2024-05-10 22:24:54', 'T', 353, 963);
INSERT INTO `sys_user` VALUES (344, 'luz7', '', '217245585', '曾璐', 1, '2010-06-13', 'https://www.xiuyicai.jp/PetSupplies', 91, '370784597149831', 'lzeng@icloud.com', '1000001', '1001003', '2012-02-26 21:02:40', '2024-05-21 17:03:13', '2024-05-10 22:24:54', 'F', 120, 382);
INSERT INTO `sys_user` VALUES (345, 'lei1216', '', '76988694941', '雷睿', 0, '2008-04-24', 'https://auth.rj8.org/CollectiblesFineArt', 63, '342857944905595', 'ruilei@icloud.com', '1000001', '1001003', '2018-05-04 04:13:06', '2024-06-10 01:40:30', '2024-05-10 22:24:54', 'T', 879, 228);
INSERT INTO `sys_user` VALUES (346, 'xiaomzou', '', '75572830899', '邹晓明', 0, '2000-02-29', 'https://auth.zou6.us/CellPhonesAccessories', 46, '4408195886357571', 'xiaozou@icloud.com', '1000001', '1001003', '2014-03-17 15:25:47', '2024-05-12 09:52:02', '2024-05-10 22:24:54', 'F', 72, 638);
INSERT INTO `sys_user` VALUES (347, 'jialuqian10', '', '76937801556', '钱嘉伦', 0, '2006-07-15', 'http://drive.jialuwu.org/SportsOutdoor', 66, '4709289172403888', 'qianj@hotmail.com', '1000001', '1001003', '2017-02-16 01:56:12', '2024-05-24 16:44:30', '2024-05-10 22:24:54', 'F', 962, 63);
INSERT INTO `sys_user` VALUES (348, 'huangyuni2', '', '208007595', '黄宇宁', 0, '2021-06-27', 'https://www.jhele103.net/CollectiblesFineArt', 30, '5079201003122628', 'huang1982@outlook.com', '1000001', '1001003', '2023-03-16 03:22:56', '2024-05-18 08:28:48', '2024-05-10 22:24:54', 'F', 583, 735);
INSERT INTO `sys_user` VALUES (349, 'guzhen1', '', '217747146', '郭震南', 0, '2005-12-10', 'http://video.nomuraa720.us/Handcrafts', 94, '5190634622296296', 'zhennanguo@yahoo.com', '1000001', '1001003', '2008-03-14 06:01:21', '2024-05-28 19:50:57', '2024-05-10 22:24:54', 'F', 510, 880);
INSERT INTO `sys_user` VALUES (350, 'ziyifeng96', '', '14314551131', '冯子异', 1, '2004-08-16', 'http://drive.nanami404.org/PetSupplies', 93, '5062140713116711', 'ziyf426@mail.com', '1000001', '1001003', '2021-09-18 09:30:23', '2024-05-22 12:45:45', '2024-05-10 22:24:54', 'F', 947, 682);
INSERT INTO `sys_user` VALUES (351, 'linzh', '', '16140811398', '林致远', 1, '2014-07-17', 'https://video.matsumoto18.jp/PetSupplies', 22, '6247364802324756', 'linz3@hotmail.com', '1000001', '1001003', '2015-06-30 16:05:08', '2024-05-19 19:24:01', '2024-05-10 22:24:54', 'T', 139, 588);
INSERT INTO `sys_user` VALUES (352, 'hanj', '', '7553362675', '韩杰宏', 0, '2010-06-21', 'http://www.tracyrichardson6.jp/VideoGames', 46, '4842393912128991', 'jiehong90@yahoo.com', '1000001', '1001003', '2018-06-01 09:03:56', '2024-05-14 01:15:49', '2024-05-10 22:24:54', 'T', 537, 714);
INSERT INTO `sys_user` VALUES (353, 'song606', '', '17212650519', '宋璐', 1, '2012-07-20', 'https://image.taox615.us/Appliances', 65, '4039744655367621', 'song5@outlook.com', '1000001', '1001003', '2011-02-23 01:58:33', '2024-05-24 15:35:17', '2024-05-10 22:24:54', 'F', 385, 999);
INSERT INTO `sys_user` VALUES (354, 'jialun4', '', '14909737764', '宋嘉伦', 0, '2001-06-10', 'https://www.onohana4.info/BaggageTravelEquipment', 59, '3533029751807156', 'sjialun9@icloud.com', '1000001', '1001003', '2010-06-15 20:02:30', '2024-05-11 19:28:00', '2024-05-10 22:24:54', 'T', 744, 138);
INSERT INTO `sys_user` VALUES (355, 'heji', '', '2180736928', '贺杰宏', 0, '2020-08-18', 'https://video.momoeokada56.info/Handcrafts', 67, '6283748037098877', 'jhe@icloud.com', '1000001', '1001003', '2021-05-03 03:27:15', '2024-05-13 06:52:18', '2024-05-10 22:24:54', 'T', 168, 749);
INSERT INTO `sys_user` VALUES (356, 'zhiyuanfu', '', '76028339252', '傅致远', 1, '2010-03-20', 'https://auth.joe17.net/Beauty', 63, '6284206599719165', 'fuzhi@outlook.com', '1000001', '1001003', '2018-09-26 11:42:44', '2024-05-13 18:26:23', '2024-05-10 22:24:54', 'F', 901, 813);
INSERT INTO `sys_user` VALUES (357, 'zouanqi93', '', '219755728', '邹安琪', 1, '2022-12-18', 'http://auth.jiaz806.xyz/CenturionGardenOutdoor', 99, '4583223573469181', 'anqiz52@mail.com', '1000001', '1001003', '2009-10-09 15:46:37', '2024-06-06 09:16:24', '2024-05-10 22:24:54', 'F', 901, 874);
INSERT INTO `sys_user` VALUES (358, 'wang802', '', '1034388992', '汪子异', 0, '2012-12-11', 'https://video.yunxiz.com/ToolsHomeDecoration', 58, '4659237516724782', 'ziyiw@icloud.com', '1000001', '1001003', '2014-07-16 20:30:03', '2024-05-24 05:02:25', '2024-05-10 22:24:54', 'T', 192, 374);
INSERT INTO `sys_user` VALUES (359, 'peng7', '', '2061591817', '彭安琪', 1, '2000-08-03', 'http://drive.hikarukato.cn/ToysGames', 43, '6285845261479175', 'pea@mail.com', '1000001', '1001003', '2003-03-14 01:29:15', '2024-05-26 00:48:42', '2024-05-10 22:24:54', 'F', 348, 530);
INSERT INTO `sys_user` VALUES (360, 'zitaotan', '', '17959860544', '谭子韬', 1, '2003-07-06', 'http://auth.uchidam.xyz/FilmSupplies', 66, '345001516787761', 'zitaota4@outlook.com', '1000001', '1001003', '2008-12-06 22:57:20', '2024-05-15 16:13:22', '2024-05-10 22:24:54', 'T', 373, 335);
INSERT INTO `sys_user` VALUES (361, 'dong2', '', '2864299814', '董宇宁', 0, '2021-01-28', 'http://video.youngd2.biz/ToolsHomeDecoration', 56, '4758974097644761', 'yudong606@gmail.com', '1000001', '1001003', '2004-10-11 09:27:07', '2024-06-05 04:39:00', '2024-05-10 22:24:54', 'F', 921, 154);
INSERT INTO `sys_user` VALUES (362, 'zouzhiyu507', '', '7695515094', '邹致远', 1, '2019-02-13', 'http://www.peterperez1003.com/ToolsHomeDecoration', 68, '5133116609987931', 'zhiyuanzo@hotmail.com', '1000001', '1001003', '2011-08-25 04:12:31', '2024-05-21 03:33:15', '2024-05-10 22:24:54', 'F', 304, 973);
INSERT INTO `sys_user` VALUES (363, 'xf328', '', '286502304', '傅晓明', 1, '2009-05-23', 'http://drive.joel5.cn/VideoGames', 81, '4713309349776489', 'xiafu1975@mail.com', '1000001', '1001003', '2012-09-27 13:40:38', '2024-05-27 06:05:39', '2024-05-10 22:24:54', 'T', 952, 655);
INSERT INTO `sys_user` VALUES (364, 'anqi4', '', '205326777', '龙安琪', 0, '2024-04-12', 'http://auth.zhiyuan2.com/VideoGames', 16, '4037174555144106', 'anqil@hotmail.com', '1000001', '1001003', '2022-10-14 00:45:56', '2024-05-21 02:53:30', '2024-05-10 22:24:54', 'T', 449, 433);
INSERT INTO `sys_user` VALUES (365, 'shens', '', '13095318111', '沈詩涵', 1, '2018-05-16', 'https://auth.lau75.co.jp/ToysGames', 42, '6288097920015761', 'shihanshen1999@yahoo.com', '1000001', '1001003', '2015-02-15 05:25:04', '2024-05-27 15:32:25', '2024-05-10 22:24:54', 'T', 803, 106);
INSERT INTO `sys_user` VALUES (366, 'yuxi', '', '7555933225', '萧宇宁', 1, '2018-04-19', 'http://www.hana7.us/ToysGames', 29, '3538441616815990', 'yuning805@outlook.com', '1000001', '1001003', '2006-06-30 23:39:59', '2024-05-29 09:43:25', '2024-05-10 22:24:54', 'F', 446, 678);
INSERT INTO `sys_user` VALUES (367, 'zhao40', '', '7558759257', '赵云熙', 0, '2011-08-18', 'https://drive.dyunxi.cn/FilmSupplies', 29, '4824423565884640', 'zhayunxi10@outlook.com', '1000001', '1001003', '2015-04-30 20:43:57', '2024-06-05 18:45:38', '2024-05-10 22:24:54', 'T', 596, 513);
INSERT INTO `sys_user` VALUES (368, 'ysun', '', '16317901644', '孙云熙', 1, '2004-06-01', 'http://image.takwahsh2.biz/ComputersElectronics', 37, '6239834151631993', 'sunyunx@gmail.com', '1000001', '1001003', '2006-01-20 10:12:56', '2024-05-22 19:10:13', '2024-05-10 22:24:54', 'T', 351, 480);
INSERT INTO `sys_user` VALUES (369, 'xiangyun', '', '7600899518', '向宇宁', 0, '2011-01-03', 'http://drive.onoshino428.net/MusicalInstrument', 97, '3575795068279557', 'xiangyuning1127@gmail.com', '1000001', '1001003', '2022-05-03 13:19:02', '2024-06-09 08:34:02', '2024-05-10 22:24:54', 'F', 655, 687);
INSERT INTO `sys_user` VALUES (370, 'li9', '', '2011140591', '李子韬', 0, '2004-02-20', 'http://video.washington407.jp/VideoGames', 21, '4068506997299216', 'zitaol1127@mail.com', '1000001', '1001003', '2005-03-25 23:44:56', '2024-05-28 22:43:32', '2024-05-10 22:24:54', 'T', 519, 361);
INSERT INTO `sys_user` VALUES (371, 'lshao', '', '17575493206', '邵岚', 0, '2001-05-28', 'http://auth.zh321.jp/Baby', 62, '3579204513534068', 'lansh@outlook.com', '1000001', '1001003', '2019-11-26 04:08:15', '2024-05-13 02:37:59', '2024-05-10 22:24:54', 'T', 358, 89);
INSERT INTO `sys_user` VALUES (372, 'zhiyuanzheng', '', '17383138405', '郑致远', 1, '2016-04-23', 'http://auth.matsui2.com/AutomotivePartsAccessories', 91, '6224735188665325', 'zhengzhiyuan@gmail.com', '1000001', '1001003', '2014-06-04 08:14:00', '2024-05-11 10:01:47', '2024-05-10 22:24:54', 'F', 725, 717);
INSERT INTO `sys_user` VALUES (373, 'deng4', '', '206451571', '邓安琪', 0, '2018-11-09', 'https://video.yamato90.us/Beauty', 23, '4659034218229669', 'adeng96@icloud.com', '1000001', '1001003', '2011-12-02 14:37:00', '2024-05-19 09:47:52', '2024-05-10 22:24:54', 'F', 794, 414);
INSERT INTO `sys_user` VALUES (374, 'xiaoming424', '', '15694447175', '冯晓明', 1, '2016-12-03', 'https://auth.walkerch.net/Appliances', 25, '3543075731736427', 'fexiaoming@gmail.com', '1000001', '1001003', '2006-05-04 10:27:57', '2024-05-11 15:13:25', '2024-05-10 22:24:54', 'T', 779, 973);
INSERT INTO `sys_user` VALUES (375, 'zq512', '', '13823256148', '钱致远', 1, '2007-01-22', 'http://drive.renn.com/VideoGames', 38, '4940911623042663', 'qianzhiy@icloud.com', '1000001', '1001003', '2020-02-07 19:03:51', '2024-05-29 12:02:00', '2024-05-10 22:24:54', 'F', 897, 57);
INSERT INTO `sys_user` VALUES (376, 'xiaoming1', '', '101442758', '马晓明', 0, '2003-10-12', 'https://image.ayato2010.org/ToysGames', 63, '340678341490938', 'mxiaoming91@outlook.com', '1000001', '1001003', '2008-05-05 16:45:39', '2024-05-13 02:51:00', '2024-05-10 22:24:54', 'F', 158, 843);
INSERT INTO `sys_user` VALUES (377, 'lanzeng', '', '215718361', '曾岚', 0, '2017-04-19', 'http://www.reo75.cn/ToolsHomeDecoration', 38, '6200969154382882', 'lanzeng@gmail.com', '1000001', '1001003', '2002-09-30 01:12:28', '2024-06-04 23:47:41', '2024-05-10 22:24:54', 'T', 62, 254);
INSERT INTO `sys_user` VALUES (378, 'zhiyusu9', '', '281166368', '孙致远', 1, '2021-01-28', 'https://video.masuda1945.biz/ArtsHandicraftsSewing', 22, '348968614945243', 'zhiyusun923@gmail.com', '1000001', '1001003', '2000-09-08 07:56:02', '2024-05-16 12:53:08', '2024-05-10 22:24:54', 'F', 393, 414);
INSERT INTO `sys_user` VALUES (379, 'haoshiha', '', '281441371', '郝詩涵', 0, '2002-03-30', 'https://image.katore.jp/PetSupplies', 77, '6213027009261515', 'haoshihan@hotmail.com', '1000001', '1001003', '2004-11-29 07:44:50', '2024-05-19 09:23:27', '2024-05-10 22:24:54', 'T', 115, 114);
INSERT INTO `sys_user` VALUES (380, 'jmen', '', '17654918242', '孟杰宏', 0, '2004-03-19', 'https://drive.tamurahi3.com/PetSupplies', 57, '5067756210251449', 'mjieh10@yahoo.com', '1000001', '1001003', '2018-12-21 09:45:18', '2024-05-23 17:40:54', '2024-05-10 22:24:54', 'F', 4, 719);
INSERT INTO `sys_user` VALUES (381, 'ziyi56', '', '13487318926', '傅子异', 0, '2020-06-21', 'http://image.xuyun.biz/AppsGames', 56, '4923140603959586', 'ziyifu@outlook.com', '1000001', '1001003', '2010-04-24 02:20:02', '2024-05-14 02:31:26', '2024-05-10 22:24:54', 'F', 730, 184);
INSERT INTO `sys_user` VALUES (382, 'zlu', '', '14343614861', '卢子韬', 0, '2000-12-01', 'https://image.wangzhi4.com/CellPhonesAccessories', 37, '343152973432318', 'lzita425@gmail.com', '1000001', '1001003', '2004-09-10 03:59:21', '2024-06-05 20:50:28', '2024-05-10 22:24:54', 'F', 580, 31);
INSERT INTO `sys_user` VALUES (383, 'dongzhe', '', '15612448950', '董震南', 1, '2015-12-21', 'http://www.rinnomu.info/CenturionGardenOutdoor', 53, '6233728539240939', 'dzhen@yahoo.com', '1000001', '1001003', '2009-11-10 13:02:18', '2024-05-24 12:07:28', '2024-05-10 22:24:54', 'F', 288, 71);
INSERT INTO `sys_user` VALUES (384, 'ziyitan', '', '7605423204', '汤子异', 0, '2000-12-09', 'http://video.houanq1121.jp/FilmSupplies', 54, '6290020965031770', 'ziyi1@icloud.com', '1000001', '1001003', '2017-01-26 03:31:49', '2024-05-17 21:32:40', '2024-05-10 22:24:54', 'F', 40, 493);
INSERT INTO `sys_user` VALUES (385, 'xzhi', '', '288146961', '谢致远', 0, '2018-11-12', 'http://auth.yunlu.biz/CDsVinyl', 54, '5537040176220850', 'xie729@gmail.com', '1000001', '1001003', '2019-04-11 08:26:36', '2024-06-03 15:49:19', '2024-05-10 22:24:54', 'T', 995, 642);
INSERT INTO `sys_user` VALUES (386, 'xiuyingj', '', '19796507918', '姜秀英', 1, '2021-06-15', 'https://drive.traashley5.biz/ToysGames', 42, '3587508469471103', 'xj75@mail.com', '1000001', '1001003', '2005-06-11 21:53:37', '2024-05-19 06:46:14', '2024-05-10 22:24:54', 'T', 705, 536);
INSERT INTO `sys_user` VALUES (387, 'sun203', '', '19015042872', '孙璐', 1, '2023-02-08', 'http://www.marcus1018.co.jp/FilmSupplies', 30, '379193956333138', 'lusu1@hotmail.com', '1000001', '1001003', '2011-11-05 10:27:22', '2024-06-04 17:34:47', '2024-05-10 22:24:54', 'F', 878, 994);
INSERT INTO `sys_user` VALUES (388, 'ziyisun5', '', '16484662752', '孙子异', 1, '2011-10-16', 'http://image.gziyi.biz/Appliances', 23, '6285520724276696', 'ziyi806@outlook.com', '1000001', '1001003', '2015-11-03 12:36:49', '2024-06-01 03:41:52', '2024-05-10 22:24:54', 'F', 154, 800);
INSERT INTO `sys_user` VALUES (389, 'yanshihan', '', '1015609114', '阎詩涵', 1, '2012-07-15', 'https://video.bryam.us/ComputersElectronics', 17, '377636201187404', 'sy111@outlook.com', '1000001', '1001003', '2010-08-06 07:49:50', '2024-06-07 03:03:31', '2024-05-10 22:24:54', 'F', 52, 798);
INSERT INTO `sys_user` VALUES (390, 'sunyunxi90', '', '2136728036', '孙云熙', 0, '2018-07-18', 'https://www.noguct.org/VideoGames', 58, '6267027433503289', 'yunxisu2001@outlook.com', '1000001', '1001003', '2013-12-05 21:34:27', '2024-05-31 06:55:31', '2024-05-10 22:24:54', 'F', 707, 492);
INSERT INTO `sys_user` VALUES (391, 'zhennxu', '', '203160825', '许震南', 0, '2007-08-05', 'http://auth.anqifu209.cn/HouseholdKitchenAppliances', 43, '5211027361428623', 'xuzhen4@icloud.com', '1000001', '1001003', '2020-12-30 05:59:29', '2024-06-10 17:02:21', '2024-05-10 22:24:54', 'T', 961, 849);
INSERT INTO `sys_user` VALUES (392, 'zhaanqi', '', '7607814163', '赵安琪', 0, '2011-10-03', 'http://auth.yuningshao1.net/PetSupplies', 17, '5598890677360712', 'anqz@gmail.com', '1000001', '1001003', '2017-08-22 23:39:37', '2024-06-02 13:25:38', '2024-05-10 22:24:54', 'T', 208, 290);
INSERT INTO `sys_user` VALUES (393, 'jialuzheng', '', '75529118887', '郑嘉伦', 0, '2021-09-06', 'http://drive.waiman2.cn/Appliances', 18, '3582213490807426', 'zhengj9@mail.com', '1000001', '1001003', '2023-07-08 23:43:58', '2024-06-02 00:10:00', '2024-05-10 22:24:54', 'F', 358, 140);
INSERT INTO `sys_user` VALUES (394, 'tiayunxi', '', '280962240', '田云熙', 1, '2019-03-09', 'https://video.lokm.jp/AutomotivePartsAccessories', 42, '4436083492781739', 'tianyunx4@outlook.com', '1000001', '1001003', '2009-06-19 07:46:37', '2024-05-17 13:15:42', '2024-05-10 22:24:54', 'F', 324, 847);
INSERT INTO `sys_user` VALUES (395, 'cui9', '', '100289426', '崔杰宏', 0, '2002-12-14', 'https://video.komiu.net/FilmSupplies', 20, '376955478621247', 'jc8@gmail.com', '1000001', '1001003', '2014-10-05 20:42:40', '2024-06-06 10:34:41', '2024-05-10 22:24:54', 'T', 825, 985);
INSERT INTO `sys_user` VALUES (396, 'yuningj', '', '13828445099', '蒋宇宁', 0, '2012-05-31', 'http://auth.sumwingmok10.org/FilmSupplies', 25, '6260101361595810', 'jiy@gmail.com', '1000001', '1001003', '2022-02-26 13:21:16', '2024-05-21 16:05:06', '2024-05-10 22:24:54', 'T', 796, 104);
INSERT INTO `sys_user` VALUES (397, 'jialo', '', '208520892', '龙嘉伦', 1, '2005-09-22', 'http://www.huimeik.xyz/ArtsHandicraftsSewing', 28, '4743857817805234', 'jl805@icloud.com', '1000001', '1001003', '2004-06-07 11:33:41', '2024-05-16 12:28:57', '2024-05-10 22:24:54', 'F', 792, 735);
INSERT INTO `sys_user` VALUES (398, 'liaoyunxi', '', '210890463', '廖云熙', 0, '2020-06-05', 'http://drive.yuttan.org/HealthBabyCare', 90, '372905320398365', 'ly322@mail.com', '1000001', '1001003', '2018-01-14 00:57:40', '2024-05-14 07:38:09', '2024-05-10 22:24:54', 'F', 319, 892);
INSERT INTO `sys_user` VALUES (399, 'yyunxi1989', '', '13510259733', '袁云熙', 1, '2003-05-06', 'https://video.cheunghuimei.info/CenturionGardenOutdoor', 29, '6250224332670009', 'yyunx@gmail.com', '1000001', '1001003', '2021-06-13 17:10:09', '2024-06-03 13:27:29', '2024-05-10 22:24:54', 'T', 186, 326);
INSERT INTO `sys_user` VALUES (400, 'xiaoming79', '', '13818870879', '汪晓明', 0, '2015-02-22', 'http://www.shihxia102.xyz/MusicalInstrument', 90, '5400896077136071', 'wanxiaoming@yahoo.com', '1000001', '1001003', '2007-10-19 13:41:11', '2024-06-02 14:16:53', '2024-05-10 22:24:54', 'T', 804, 282);
INSERT INTO `sys_user` VALUES (401, 'anqiwu', '', '14518951285', '武安琪', 1, '2018-09-24', 'https://drive.yutootsuka823.info/ClothingShoesandJewelry', 24, '344162607523688', 'anqiwu713@gmail.com', '1000001', '1001003', '2002-10-11 06:26:34', '2024-05-15 06:40:58', '2024-05-10 22:24:54', 'F', 75, 685);
INSERT INTO `sys_user` VALUES (402, 'yuning1018', '', '76969338587', '彭宇宁', 0, '2014-02-26', 'http://video.yuling10.com/PetSupplies', 27, '4094959608560229', 'penyunin69@gmail.com', '1000001', '1001003', '2010-01-18 14:20:31', '2024-06-03 04:32:48', '2024-05-10 22:24:54', 'T', 585, 823);
INSERT INTO `sys_user` VALUES (403, 'aren', '', '76023816385', '任安琪', 0, '2015-04-03', 'https://www.zitao1119.net/CollectiblesFineArt', 77, '375764715516595', 'renanqi@outlook.com', '1000001', '1001003', '2020-04-10 02:23:11', '2024-05-30 12:39:44', '2024-05-10 22:24:54', 'F', 983, 59);
INSERT INTO `sys_user` VALUES (404, 'xiashiha7', '', '2848721435', '夏詩涵', 1, '2015-02-04', 'https://drive.jefa1963.co.jp/FilmSupplies', 24, '6217891865091704', 'shihan2004@gmail.com', '1000001', '1001003', '2010-07-25 18:04:35', '2024-06-10 22:55:44', '2024-05-10 22:24:54', 'T', 121, 164);
INSERT INTO `sys_user` VALUES (405, 'jiehongfu', '', '15686981484', '傅杰宏', 1, '2001-03-23', 'http://video.sakumiy.co.jp/CellPhonesAccessories', 73, '5329702840388122', 'fujieho1954@gmail.com', '1000001', '1001003', '2015-09-29 02:33:10', '2024-05-24 08:51:56', '2024-05-10 22:24:54', 'T', 518, 832);
INSERT INTO `sys_user` VALUES (406, 'ziyi1017', '', '14114210973', '邵子异', 1, '2011-11-09', 'http://www.tashino.xyz/HealthBabyCare', 100, '6285286952298303', 'ziyishao@mail.com', '1000001', '1001003', '2009-11-17 21:16:07', '2024-05-22 17:36:24', '2024-05-10 22:24:54', 'F', 300, 169);
INSERT INTO `sys_user` VALUES (407, 'zhiz1209', '', '14622724160', '邹致远', 0, '2021-11-29', 'https://image.daih.co.jp/MusicalInstrument', 52, '6256713457018147', 'zhzou@gmail.com', '1000001', '1001003', '2000-09-12 06:53:42', '2024-05-10 21:38:27', '2024-05-10 22:24:54', 'F', 425, 832);
INSERT INTO `sys_user` VALUES (408, 'shihan1', '', '14012053109', '胡詩涵', 1, '2019-12-12', 'http://auth.fangziyi.com/AutomotivePartsAccessories', 43, '5520287391520775', 'hshih@gmail.com', '1000001', '1001003', '2010-06-14 19:05:25', '2024-06-05 17:41:12', '2024-05-10 22:24:54', 'F', 514, 356);
INSERT INTO `sys_user` VALUES (409, 'tanz519', '', '19886755152', '谭子异', 0, '2001-01-04', 'https://image.sakurhazu.info/ToolsHomeDecoration', 80, '3538254168054680', 'ztan719@outlook.com', '1000001', '1001003', '2002-12-12 12:07:32', '2024-05-25 03:52:42', '2024-05-10 22:24:54', 'F', 550, 344);
INSERT INTO `sys_user` VALUES (410, 'cuixiaoming', '', '76950465215', '崔晓明', 1, '2009-04-16', 'https://auth.jiangxiaoming.biz/ToysGames', 76, '4809014920009244', 'cuixia@outlook.com', '1000001', '1001003', '2010-08-13 23:19:51', '2024-05-15 20:35:49', '2024-05-10 22:24:54', 'T', 757, 843);
INSERT INTO `sys_user` VALUES (411, 'changjieho', '', '18752576073', '常杰宏', 1, '2010-05-02', 'https://www.yunsh.com/BaggageTravelEquipment', 49, '5046538634178036', 'chjieho@gmail.com', '1000001', '1001003', '2023-10-09 14:01:52', '2024-05-23 04:41:42', '2024-05-10 22:24:54', 'F', 869, 296);
INSERT INTO `sys_user` VALUES (412, 'lachang', '', '14091869905', '常岚', 0, '2012-01-14', 'https://www.wuxiaoming.jp/Others', 69, '6208508907297567', 'changlan@mail.com', '1000001', '1001003', '2021-06-15 08:37:26', '2024-06-02 10:29:08', '2024-05-10 22:24:54', 'T', 372, 561);
INSERT INTO `sys_user` VALUES (413, 'shfa', '', '16292087050', '方詩涵', 0, '2022-10-04', 'http://www.gomezroy.com/MusicalInstrument', 32, '348417576812918', 'fangs5@outlook.com', '1000001', '1001003', '2019-11-18 14:34:24', '2024-06-08 00:26:47', '2024-05-10 22:24:54', 'F', 968, 749);
INSERT INTO `sys_user` VALUES (414, 'guxia', '', '16075484019', '顾晓明', 0, '2018-09-04', 'http://video.jospe1231.net/ClothingShoesandJewelry', 24, '3539404688633096', 'xiaominggu10@icloud.com', '1000001', '1001003', '2021-11-29 15:47:19', '2024-05-28 22:05:33', '2024-05-10 22:24:54', 'T', 1000, 270);
INSERT INTO `sys_user` VALUES (415, 'luzeng', '', '1060390641', '曾璐', 0, '2006-06-29', 'http://video.cindyh11.org/CDsVinyl', 67, '371821562671503', 'lzen@mail.com', '1000001', '1001003', '2015-03-08 21:16:46', '2024-06-07 09:06:21', '2024-05-10 22:24:54', 'F', 592, 247);
INSERT INTO `sys_user` VALUES (416, 'xias', '', '2867364284', '宋晓明', 0, '2016-11-16', 'https://drive.scheng04.cn/CellPhonesAccessories', 18, '4160641332809267', 'songx@hotmail.com', '1000001', '1001003', '2019-10-01 15:44:07', '2024-06-03 09:51:54', '2024-05-10 22:24:54', 'T', 448, 124);
INSERT INTO `sys_user` VALUES (417, 'jialun7', '', '7550378261', '于嘉伦', 1, '2019-03-11', 'https://www.rojason.com/ComputersElectronics', 26, '4645974802187491', 'yuj804@gmail.com', '1000001', '1001003', '2023-10-13 01:57:09', '2024-06-08 14:25:27', '2024-05-10 22:24:54', 'F', 881, 995);
INSERT INTO `sys_user` VALUES (418, 'yuning74', '', '76951834263', '田宇宁', 0, '2007-10-10', 'https://video.royj.xyz/Handcrafts', 21, '6289033415281419', 'yunintian2@icloud.com', '1000001', '1001003', '2005-10-20 13:30:20', '2024-05-25 10:29:13', '2024-05-10 22:24:54', 'F', 266, 190);
INSERT INTO `sys_user` VALUES (419, 'lan824', '', '2888659825', '曾岚', 1, '2012-08-25', 'https://video.xiuying7.biz/IndustrialScientificSupplies', 87, '3577268328075932', 'zenglan@outlook.com', '1000001', '1001003', '2015-08-23 01:25:49', '2024-06-04 18:23:07', '2024-05-10 22:24:54', 'T', 804, 595);
INSERT INTO `sys_user` VALUES (420, 'guolan', '', '13265286371', '郭岚', 1, '2012-01-08', 'https://drive.wskong.info/IndustrialScientificSupplies', 36, '6292690478497193', 'gulan@gmail.com', '1000001', '1001003', '2015-08-11 21:29:37', '2024-05-29 21:53:59', '2024-05-10 22:24:54', 'T', 591, 190);
INSERT INTO `sys_user` VALUES (421, 'her', '', '1015847507', '何睿', 0, '2019-06-16', 'https://auth.evans7.jp/Baby', 60, '3541986493358527', 'ruih405@icloud.com', '1000001', '1001003', '2008-09-04 01:41:51', '2024-05-14 13:14:47', '2024-05-10 22:24:54', 'F', 747, 449);
INSERT INTO `sys_user` VALUES (422, 'zitaowei1966', '', '2116710923', '韦子韬', 1, '2001-05-15', 'http://image.ludu.net/IndustrialScientificSupplies', 53, '4091872141409379', 'wzitao@hotmail.com', '1000001', '1001003', '2014-06-25 18:23:59', '2024-05-24 20:14:16', '2024-05-10 22:24:54', 'F', 966, 482);
INSERT INTO `sys_user` VALUES (423, 'ziyfa48', '', '2051153512', '范子异', 0, '2002-08-20', 'http://video.yunait.co.jp/AutomotivePartsAccessories', 28, '4393154403403753', 'ziyif@icloud.com', '1000001', '1001003', '2021-12-23 09:28:45', '2024-06-04 21:07:32', '2024-05-10 22:24:54', 'F', 15, 625);
INSERT INTO `sys_user` VALUES (424, 'zhmao', '', '18825878084', '毛震南', 0, '2005-11-01', 'http://video.ls10.jp/CollectiblesFineArt', 53, '3547993848769824', 'mzhe@gmail.com', '1000001', '1001003', '2018-02-17 06:31:51', '2024-05-24 03:45:46', '2024-05-10 22:24:54', 'F', 77, 611);
INSERT INTO `sys_user` VALUES (425, 'jiangr', '', '19347691188', '江睿', 0, '2002-08-13', 'http://drive.kathyga.info/CollectiblesFineArt', 84, '5019133772083645', 'ruijiang68@mail.com', '1000001', '1001003', '2022-08-05 21:41:30', '2024-05-30 23:10:43', '2024-05-10 22:24:54', 'F', 799, 5);
INSERT INTO `sys_user` VALUES (426, 'yuningdu', '', '18869429830', '杜宇宁', 0, '2014-12-24', 'https://drive.yam1.info/BaggageTravelEquipment', 48, '6244049307255018', 'duyunin@gmail.com', '1000001', '1001003', '2005-08-30 02:18:04', '2024-05-19 05:26:57', '2024-05-10 22:24:54', 'F', 661, 673);
INSERT INTO `sys_user` VALUES (427, 'shihan05', '', '2843003323', '彭詩涵', 1, '2023-02-12', 'http://www.gotosara.com/Appliances', 84, '374200100803265', 'pengshihan5@outlook.com', '1000001', '1001003', '2015-12-05 09:51:19', '2024-05-18 06:18:17', '2024-05-10 22:24:54', 'F', 37, 50);
INSERT INTO `sys_user` VALUES (428, 'xiuyingsu', '', '13947649122', '孙秀英', 1, '2016-09-26', 'http://www.bennett51.biz/Beauty', 78, '5352314209097037', 'sunxi@gmail.com', '1000001', '1001003', '2023-12-16 18:32:16', '2024-06-02 12:10:55', '2024-05-10 22:24:54', 'F', 611, 724);
INSERT INTO `sys_user` VALUES (429, 'luzhou', '', '19776049834', '周璐', 1, '2021-06-18', 'https://drive.liksun74.cn/AutomotivePartsAccessories', 35, '6218779164546271', 'luzho@outlook.com', '1000001', '1001003', '2023-11-21 21:19:03', '2024-05-30 11:41:54', '2024-05-10 22:24:54', 'T', 474, 622);
INSERT INTO `sys_user` VALUES (430, 'lizhiyuan1211', '', '18981741841', '李致远', 0, '2003-01-08', 'https://video.shihkong.org/BeautyPersonalCare', 24, '4384918607998136', 'liz@mail.com', '1000001', '1001003', '2015-08-21 02:32:18', '2024-05-23 04:17:57', '2024-05-10 22:24:54', 'T', 490, 246);
INSERT INTO `sys_user` VALUES (431, 'shyuning', '', '1037775580', '邵宇宁', 1, '2012-12-21', 'http://video.qiushiha.cn/CellPhonesAccessories', 94, '4253903901715483', 'yuning211@outlook.com', '1000001', '1001003', '2010-10-08 07:12:35', '2024-06-10 08:34:22', '2024-05-10 22:24:54', 'F', 917, 660);
INSERT INTO `sys_user` VALUES (432, 'xiaoziyi728', '', '14673667278', '萧子异', 1, '2006-03-05', 'http://video.zitaohou.info/ToysGames', 69, '371530901987310', 'ziyi4@gmail.com', '1000001', '1001003', '2006-08-23 07:36:21', '2024-06-03 18:16:50', '2024-05-10 22:24:54', 'F', 998, 338);
INSERT INTO `sys_user` VALUES (433, 'zhengziyi', '', '7555887542', '郑子异', 1, '2006-01-14', 'http://auth.herrera2003.cn/ClothingShoesandJewelry', 71, '377604322648449', 'zhengziy1986@icloud.com', '1000001', '1001003', '2002-09-11 22:46:58', '2024-05-22 04:08:47', '2024-05-10 22:24:54', 'T', 872, 182);
INSERT INTO `sys_user` VALUES (434, 'xiaomlu7', '', '209165574', '吕晓明', 0, '2008-05-13', 'http://drive.xiazita.net/SportsOutdoor', 51, '3576281803049869', 'luxia@outlook.com', '1000001', '1001003', '2009-07-25 00:16:19', '2024-05-12 05:48:02', '2024-05-10 22:24:54', 'F', 941, 869);
INSERT INTO `sys_user` VALUES (435, 'weiziyi405', '', '19188469510', '魏子异', 0, '2018-10-30', 'https://auth.waiman102.cn/HealthBabyCare', 32, '5598853844327261', 'weiziyi@outlook.com', '1000001', '1001003', '2013-05-26 00:48:19', '2024-05-14 11:18:33', '2024-05-10 22:24:54', 'T', 794, 775);
INSERT INTO `sys_user` VALUES (436, 'zheny', '', '76919121858', '郑云熙', 1, '2011-05-28', 'https://www.takuym.co.jp/AutomotivePartsAccessories', 71, '4811560218234254', 'zyunxi1945@yahoo.com', '1000001', '1001003', '2003-07-17 05:04:05', '2024-06-06 23:55:33', '2024-05-10 22:24:54', 'F', 951, 431);
INSERT INTO `sys_user` VALUES (437, 'chenr', '', '14055291259', '程睿', 1, '2003-11-02', 'http://drive.daichiokamoto318.net/HealthBabyCare', 99, '5577319505720264', 'ruich@gmail.com', '1000001', '1001003', '2019-08-03 14:12:31', '2024-05-13 10:25:43', '2024-05-10 22:24:54', 'F', 276, 791);
INSERT INTO `sys_user` VALUES (438, 'zitaoluo', '', '200429586', '罗子韬', 0, '2018-06-11', 'http://video.mai1112.info/AppsGames', 81, '3577898702957113', 'luo1944@mail.com', '1000001', '1001003', '2002-03-23 15:49:45', '2024-05-16 16:21:26', '2024-05-10 22:24:54', 'F', 264, 5);
INSERT INTO `sys_user` VALUES (439, 'zhennansu', '', '14511319249', '苏震南', 1, '2010-05-31', 'https://auth.lu10.net/ComputersElectronics', 28, '6284630755217492', 'suz3@outlook.com', '1000001', '1001003', '2013-11-24 07:14:04', '2024-05-12 01:01:37', '2024-05-10 22:24:54', 'F', 542, 652);
INSERT INTO `sys_user` VALUES (440, 'dongjialu', '', '7607260087', '董嘉伦', 1, '2003-08-26', 'https://video.klw10.cn/Appliances', 84, '5293783250293160', 'jdong@gmail.com', '1000001', '1001003', '2010-03-13 14:08:03', '2024-05-20 01:21:13', '2024-05-10 22:24:54', 'T', 240, 142);
INSERT INTO `sys_user` VALUES (441, 'lujiehong8', '', '76978195198', '卢杰宏', 1, '2004-09-19', 'http://www.cysiu.org/PetSupplies', 50, '4386152853782600', 'jlu@outlook.com', '1000001', '1001003', '2015-07-08 07:07:53', '2024-06-03 12:22:39', '2024-05-10 22:24:54', 'F', 497, 846);
INSERT INTO `sys_user` VALUES (442, 'cheng1943', '', '2804104801', '程杰宏', 1, '2008-07-21', 'http://auth.ikkikimura3.co.jp/PetSupplies', 60, '5444116465119243', 'chengjiehong54@gmail.com', '1000001', '1001003', '2006-01-20 05:23:00', '2024-06-03 03:51:42', '2024-05-10 22:24:54', 'F', 187, 425);
INSERT INTO `sys_user` VALUES (443, 'zhennan5', '', '288517251', '郑震南', 1, '2003-07-29', 'https://video.siuonn10.org/Food', 67, '3551837813959741', 'zzhe@mail.com', '1000001', '1001003', '2016-04-04 14:28:05', '2024-06-10 01:58:17', '2024-05-10 22:24:54', 'F', 829, 942);
INSERT INTO `sys_user` VALUES (444, 'anqiw', '', '7554464626', '武安琪', 0, '2017-06-08', 'https://www.xiaoming503.biz/Handcrafts', 77, '3535421227710791', 'wanqi@icloud.com', '1000001', '1001003', '2018-10-03 23:41:30', '2024-06-08 20:32:07', '2024-05-10 22:24:54', 'F', 150, 328);
INSERT INTO `sys_user` VALUES (445, 'shi1220', '', '76917315206', '石云熙', 0, '2013-02-01', 'http://image.wsng3.com/ClothingShoesandJewelry', 26, '4170152512521332', 'shiyun3@yahoo.com', '1000001', '1001003', '2015-05-22 22:24:55', '2024-05-15 18:34:36', '2024-05-10 22:24:54', 'F', 872, 263);
INSERT INTO `sys_user` VALUES (446, 'zitx1202', '', '1076149418', '徐子韬', 1, '2022-08-07', 'https://auth.kwokyinfu10.net/Books', 29, '3537150887835949', 'xuz329@gmail.com', '1000001', '1001003', '2010-11-09 03:00:58', '2024-05-19 04:40:52', '2024-05-10 22:24:54', 'F', 557, 900);
INSERT INTO `sys_user` VALUES (447, 'zhiyuand', '', '16166079852', '段致远', 0, '2006-07-03', 'http://www.jacquelineedwards8.xyz/ToolsHomeDecoration', 87, '3576143484540634', 'zhiyuduan@gmail.com', '1000001', '1001003', '2014-01-19 19:27:49', '2024-05-11 05:31:16', '2024-05-10 22:24:54', 'T', 568, 973);
INSERT INTO `sys_user` VALUES (448, 'wuxiu20', '', '19475927411', '吴秀英', 1, '2004-12-06', 'http://www.kwokmingt1.org/BaggageTravelEquipment', 47, '3562909538693876', 'xwu@mail.com', '1000001', '1001003', '2022-10-31 06:36:19', '2024-06-08 08:26:50', '2024-05-10 22:24:54', 'F', 647, 319);
INSERT INTO `sys_user` VALUES (449, 'jiehohe', '', '14050540391', '贺杰宏', 1, '2020-08-01', 'https://video.tsutingfung.co.jp/ToysGames', 86, '4773506736767925', 'he10@gmail.com', '1000001', '1001003', '2009-10-02 03:10:17', '2024-05-26 15:13:42', '2024-05-10 22:24:54', 'T', 727, 191);
INSERT INTO `sys_user` VALUES (450, 'hjiehong', '', '16586368822', '黄杰宏', 0, '2010-12-22', 'http://drive.alfred6.co.jp/ComputersElectronics', 87, '5353742797036364', 'jiehoh112@yahoo.com', '1000001', '1001003', '2004-07-02 07:40:51', '2024-05-21 06:32:06', '2024-05-10 22:24:54', 'T', 709, 540);
INSERT INTO `sys_user` VALUES (451, 'zitao1107', '', '16363501411', '毛子韬', 1, '2006-08-23', 'http://image.liurui2002.us/Food', 60, '5119731686668473', 'maz1010@gmail.com', '1000001', '1001003', '2018-05-15 20:51:07', '2024-05-29 19:51:17', '2024-05-10 22:24:54', 'F', 118, 430);
INSERT INTO `sys_user` VALUES (452, 'pejie', '', '7555815691', '彭杰宏', 0, '2003-02-22', 'https://auth.ziyij.cn/ToolsHomeDecoration', 23, '3585153715078022', 'jpeng6@icloud.com', '1000001', '1001003', '2018-08-14 03:53:57', '2024-06-01 07:27:46', '2024-05-10 22:24:54', 'T', 960, 488);
INSERT INTO `sys_user` VALUES (453, 'yunxiliao49', '', '75505003301', '廖云熙', 0, '2011-05-15', 'http://www.zitao1.biz/VideoGames', 48, '3554167299544651', 'yunxliao2@gmail.com', '1000001', '1001003', '2001-10-30 08:04:55', '2024-06-02 10:41:13', '2024-05-10 22:24:54', 'T', 991, 622);
INSERT INTO `sys_user` VALUES (454, 'jialy1997', '', '2028195024', '袁嘉伦', 1, '2020-04-28', 'https://drive.eddie908.biz/Beauty', 62, '4816527092751849', 'yuanjialun@outlook.com', '1000001', '1001003', '2006-01-29 06:33:36', '2024-05-25 02:29:06', '2024-05-10 22:24:54', 'T', 556, 275);
INSERT INTO `sys_user` VALUES (455, 'lu5', '', '1006142198', '卢璐', 1, '2008-02-20', 'https://video.tfy.biz/PetSupplies', 27, '4479664802904547', 'lulu@gmail.com', '1000001', '1001003', '2022-07-20 10:26:42', '2024-06-02 09:28:28', '2024-05-10 22:24:54', 'T', 458, 924);
INSERT INTO `sys_user` VALUES (456, 'lan8', '', '15068915208', '毛岚', 0, '2017-05-02', 'http://www.kk4.co.jp/Beauty', 77, '3574837246815968', 'maol@icloud.com', '1000001', '1001003', '2024-03-05 06:37:44', '2024-05-28 06:11:15', '2024-05-10 22:24:54', 'T', 552, 855);
INSERT INTO `sys_user` VALUES (457, 'wxia', '', '13328599599', '武晓明', 0, '2005-08-23', 'http://auth.airiueda.biz/PetSupplies', 77, '4256181492719910', 'xiaomingwu2@gmail.com', '1000001', '1001003', '2020-06-20 11:33:31', '2024-05-17 18:38:27', '2024-05-10 22:24:54', 'F', 137, 798);
INSERT INTO `sys_user` VALUES (458, 'anqi505', '', '216248726', '胡安琪', 0, '2009-10-11', 'http://video.akinanaka10.co.jp/ToolsHomeDecoration', 39, '379122851305453', 'ahu@icloud.com', '1000001', '1001003', '2020-05-24 21:45:18', '2024-05-10 02:03:09', '2024-05-10 22:24:54', 'T', 660, 663);
INSERT INTO `sys_user` VALUES (459, 'yejialun', '', '7605005298', '叶嘉伦', 0, '2000-06-18', 'http://image.liawl.biz/Beauty', 69, '4776606554083434', 'yejialun8@hotmail.com', '1000001', '1001003', '2001-01-15 16:20:29', '2024-06-05 06:19:10', '2024-05-10 22:24:54', 'F', 525, 804);
INSERT INTO `sys_user` VALUES (460, 'shixiaoming', '', '2868471563', '石晓明', 0, '2003-05-31', 'https://drive.sandersrog.xyz/Baby', 93, '6286007624217359', 'shxiaoming@yahoo.com', '1000001', '1001003', '2019-09-14 14:40:48', '2024-06-08 11:27:57', '2024-05-10 22:24:54', 'T', 993, 241);
INSERT INTO `sys_user` VALUES (461, 'ma1003', '', '283473890', '马嘉伦', 1, '2021-06-28', 'http://video.kr1.co.jp/Appliances', 72, '3556606404682326', 'jma@yahoo.com', '1000001', '1001003', '2005-08-16 00:44:32', '2024-05-15 08:44:31', '2024-05-10 22:24:54', 'T', 954, 974);
INSERT INTO `sys_user` VALUES (462, 'zwu', '', '13285220768', '吴震南', 1, '2017-02-10', 'http://drive.csha1225.biz/Books', 45, '373012551142106', 'zw10@outlook.com', '1000001', '1001003', '2016-04-27 00:02:41', '2024-06-03 04:17:30', '2024-05-10 22:24:54', 'F', 837, 402);
INSERT INTO `sys_user` VALUES (463, 'yuning813', '', '15803710511', '顾宇宁', 0, '2022-05-12', 'http://drive.fordrand1201.net/MusicalInstrument', 50, '349060725002640', 'ygu@gmail.com', '1000001', '1001003', '2020-09-30 20:49:20', '2024-05-13 20:54:33', '2024-05-10 22:24:54', 'F', 358, 630);
INSERT INTO `sys_user` VALUES (464, 'sziyi802', '', '14017182116', '孙子异', 0, '2000-01-05', 'http://www.sahikari7.jp/IndustrialScientificSupplies', 27, '4484264743882374', 'ziyis76@icloud.com', '1000001', '1001003', '2017-08-09 15:53:10', '2024-05-11 22:50:35', '2024-05-10 22:24:54', 'F', 400, 150);
INSERT INTO `sys_user` VALUES (465, 'xiaominghu5', '', '7550323405', '胡晓明', 1, '2003-08-13', 'http://image.tao519.org/AppsGames', 58, '6234759558093577', 'xiaominghu8@outlook.com', '1000001', '1001003', '2023-09-24 22:42:27', '2024-05-11 06:16:00', '2024-05-10 22:24:54', 'T', 886, 598);
INSERT INTO `sys_user` VALUES (466, 'ziyima7', '', '210467742', '马子异', 1, '2008-11-23', 'https://drive.baker10.biz/CenturionGardenOutdoor', 100, '4673530297165650', 'ziyi9@mail.com', '1000001', '1001003', '2020-11-05 15:01:06', '2024-05-12 08:58:14', '2024-05-10 22:24:54', 'T', 507, 474);
INSERT INTO `sys_user` VALUES (467, 'huzit5', '', '14484853871', '胡子韬', 1, '2016-10-11', 'https://drive.yamadamom820.com/ComputersElectronics', 66, '4064135496358986', 'huzitao@mail.com', '1000001', '1001003', '2001-07-01 08:12:50', '2024-05-13 11:06:51', '2024-05-10 22:24:54', 'F', 386, 83);
INSERT INTO `sys_user` VALUES (468, 'yunxiduan921', '', '19270041366', '段云熙', 0, '2009-01-25', 'http://auth.chungwin.jp/ClothingShoesandJewelry', 43, '3544990983215434', 'dyunxi@icloud.com', '1000001', '1001003', '2000-06-06 19:35:51', '2024-05-16 00:17:15', '2024-05-10 22:24:54', 'T', 665, 526);
INSERT INTO `sys_user` VALUES (469, 'xiuyingta', '', '15786145968', '谭秀英', 1, '2005-11-05', 'https://www.dai87.jp/CellPhonesAccessories', 34, '5012041322034140', 'xiuyingta5@mail.com', '1000001', '1001003', '2017-04-10 21:07:12', '2024-05-28 19:52:54', '2024-05-10 22:24:54', 'T', 258, 211);
INSERT INTO `sys_user` VALUES (470, 'xiang10', '', '18678525740', '向宇宁', 1, '2001-07-20', 'http://image.ethwe.cn/ArtsHandicraftsSewing', 61, '5440532857122567', 'yuning1202@gmail.com', '1000001', '1001003', '2001-11-13 07:09:20', '2024-05-19 20:59:53', '2024-05-10 22:24:54', 'F', 887, 139);
INSERT INTO `sys_user` VALUES (471, 'dings80', '', '7553114521', '丁詩涵', 0, '2001-12-09', 'http://drive.turnerme.xyz/Food', 74, '3530669183497580', 'dshiha@gmail.com', '1000001', '1001003', '2009-07-15 06:16:05', '2024-05-24 16:15:54', '2024-05-10 22:24:54', 'F', 529, 582);
INSERT INTO `sys_user` VALUES (472, 'wangjialun703', '', '106041004', '汪嘉伦', 0, '2001-05-15', 'http://auth.dwagner.info/Appliances', 48, '3533126688954497', 'wj404@icloud.com', '1000001', '1001003', '2009-05-28 17:55:26', '2024-05-27 04:18:15', '2024-05-10 22:24:54', 'F', 596, 673);
INSERT INTO `sys_user` VALUES (473, 'zhz', '', '207919201', '赵致远', 1, '2001-07-18', 'https://video.jiehongsha.com/BaggageTravelEquipment', 62, '5293773837954459', 'zhizha@outlook.com', '1000001', '1001003', '2000-06-26 18:05:31', '2024-05-11 12:45:32', '2024-05-10 22:24:54', 'F', 671, 833);
INSERT INTO `sys_user` VALUES (474, 'majiehong422', '', '14872072032', '马杰宏', 1, '2007-11-17', 'https://www.seiko7.org/HouseholdKitchenAppliances', 70, '373868699004510', 'jma9@gmail.com', '1000001', '1001003', '2004-06-24 05:04:31', '2024-05-31 21:35:47', '2024-05-10 22:24:54', 'F', 319, 805);
INSERT INTO `sys_user` VALUES (475, 'lyuni', '', '15099221700', '吕宇宁', 0, '2019-08-03', 'http://image.hyl.net/VideoGames', 36, '348869978759057', 'lyunin50@gmail.com', '1000001', '1001003', '2019-08-07 05:09:42', '2024-05-16 20:41:06', '2024-05-10 22:24:54', 'T', 326, 200);
INSERT INTO `sys_user` VALUES (476, 'maxia79', '', '75555599209', '马晓明', 1, '2022-09-11', 'https://auth.zitaohou812.us/HealthBabyCare', 50, '6205168515948185', 'maxi1@outlook.com', '1000001', '1001003', '2001-09-05 05:00:16', '2024-05-25 14:29:50', '2024-05-10 22:24:54', 'F', 701, 358);
INSERT INTO `sys_user` VALUES (477, 'ruyin', '', '287676347', '尹睿', 1, '2019-11-27', 'http://auth.jia929.biz/AutomotivePartsAccessories', 91, '5385401844579056', 'yirui@mail.com', '1000001', '1001003', '2011-09-23 07:40:36', '2024-06-08 11:48:17', '2024-05-10 22:24:54', 'F', 396, 127);
INSERT INTO `sys_user` VALUES (478, 'zoxiaoming74', '', '2855911288', '邹晓明', 1, '2000-08-06', 'https://auth.rin83.com/Beauty', 86, '5249511366541221', 'zoux1@outlook.com', '1000001', '1001003', '2012-05-01 07:01:46', '2024-06-07 23:38:40', '2024-05-10 22:24:54', 'F', 362, 988);
INSERT INTO `sys_user` VALUES (479, 'yunxiren', '', '7607936869', '任云熙', 0, '2012-03-25', 'http://www.tsubasaimai.us/MusicalInstrument', 28, '6220695734460965', 'yr6@outlook.com', '1000001', '1001003', '2023-12-17 04:24:09', '2024-05-25 04:01:32', '2024-05-10 22:24:54', 'T', 247, 487);
INSERT INTO `sys_user` VALUES (480, 'rdeng', '', '18652933251', '邓睿', 1, '2000-11-29', 'http://drive.mmiyam.jp/HouseholdKitchenAppliances', 43, '5315102329085032', 'ruideng@gmail.com', '1000001', '1001003', '2007-05-11 19:48:43', '2024-06-04 14:47:56', '2024-05-10 22:24:54', 'F', 177, 345);
INSERT INTO `sys_user` VALUES (481, 'jishihan', '', '205682073', '贾詩涵', 1, '2005-03-07', 'http://image.chibm96.jp/IndustrialScientificSupplies', 85, '3583253407890031', 'jias@hotmail.com', '1000001', '1001003', '2022-07-19 04:35:07', '2024-06-05 01:01:15', '2024-05-10 22:24:54', 'F', 483, 296);
INSERT INTO `sys_user` VALUES (482, 'jiehong7', '', '15835642698', '梁杰宏', 1, '2021-01-13', 'http://www.imkent.us/AutomotivePartsAccessories', 72, '4252328284423722', 'lijieho04@yahoo.com', '1000001', '1001003', '2008-06-18 04:05:09', '2024-05-29 18:34:45', '2024-05-10 22:24:54', 'F', 530, 795);
INSERT INTO `sys_user` VALUES (483, 'fanlan9', '', '76075600482', '范岚', 1, '2009-09-03', 'http://www.brybilly18.us/SportsOutdoor', 53, '6290887163349349', 'flan9@icloud.com', '1000001', '1001003', '2001-09-19 19:27:03', '2024-05-18 05:29:29', '2024-05-10 22:24:54', 'F', 272, 264);
INSERT INTO `sys_user` VALUES (484, 'zhouxi8', '', '76051465215', '周秀英', 0, '2007-10-03', 'https://drive.peggybarnes.biz/Beauty', 33, '6243856612308329', 'zhoxiuying221@gmail.com', '1000001', '1001003', '2005-06-22 20:03:16', '2024-06-01 21:38:55', '2024-05-10 22:24:54', 'F', 866, 245);
INSERT INTO `sys_user` VALUES (485, 'tang6', '', '16973938978', '唐安琪', 1, '2001-11-20', 'https://www.rena74.xyz/ComputersElectronics', 86, '5085299104424135', 'at1106@gmail.com', '1000001', '1001003', '2022-08-21 08:43:48', '2024-05-10 10:24:57', '2024-05-10 22:24:54', 'F', 236, 607);
INSERT INTO `sys_user` VALUES (486, 'ziyiwei', '', '212061030', '韦子异', 1, '2011-08-01', 'http://image.kmku.net/ToolsHomeDecoration', 45, '372053798676564', 'weiziyi89@hotmail.com', '1000001', '1001003', '2009-01-07 11:24:09', '2024-05-13 16:07:31', '2024-05-10 22:24:54', 'T', 347, 273);
INSERT INTO `sys_user` VALUES (487, 'ruyao', '', '18228466550', '姚睿', 0, '2021-03-14', 'https://image.xue64.jp/CDsVinyl', 78, '6210774421780597', 'yarui@gmail.com', '1000001', '1001003', '2000-01-07 16:08:06', '2024-05-23 23:13:02', '2024-05-10 22:24:54', 'T', 697, 551);
INSERT INTO `sys_user` VALUES (488, 'mzitao', '', '18672565644', '毛子韬', 1, '2006-04-20', 'https://auth.simar805.net/CenturionGardenOutdoor', 75, '4382995290094953', 'maozitao@gmail.com', '1000001', '1001003', '2000-07-25 14:10:29', '2024-06-10 23:02:12', '2024-05-10 22:24:54', 'T', 781, 737);
INSERT INTO `sys_user` VALUES (489, 'jiehong219', '', '19292744541', '周杰宏', 1, '2011-04-12', 'https://www.lee7.com/CollectiblesFineArt', 84, '4251225473904515', 'zjie@gmail.com', '1000001', '1001003', '2016-11-30 17:48:26', '2024-05-17 14:06:37', '2024-05-10 22:24:54', 'T', 463, 627);
INSERT INTO `sys_user` VALUES (490, 'jiacai', '', '13760458806', '蔡嘉伦', 0, '2009-10-24', 'https://drive.saumanso6.cn/Appliances', 72, '5198710115006814', 'jcai@gmail.com', '1000001', '1001003', '2011-09-09 18:39:44', '2024-06-03 19:31:10', '2024-05-10 22:24:54', 'F', 104, 680);
INSERT INTO `sys_user` VALUES (491, 'renxiaoming', '', '7551972533', '任晓明', 1, '2014-09-09', 'http://www.mosw928.jp/IndustrialScientificSupplies', 34, '373199905092754', 'renxiaoming3@icloud.com', '1000001', '1001003', '2017-01-19 01:52:01', '2024-06-10 10:41:05', '2024-05-10 22:24:54', 'F', 673, 551);
INSERT INTO `sys_user` VALUES (492, 'xiuliu9', '', '19567937019', '刘秀英', 0, '2012-02-25', 'https://www.satomai76.biz/ToysGames', 89, '5351827153975925', 'liuxiuy15@yahoo.com', '1000001', '1001003', '2012-03-11 22:50:02', '2024-05-22 20:09:08', '2024-05-10 22:24:54', 'F', 677, 385);
INSERT INTO `sys_user` VALUES (493, 'rui703', '', '16565794007', '段睿', 0, '2006-01-24', 'https://drive.kltang.net/CenturionGardenOutdoor', 79, '4366032215485718', 'duan1@hotmail.com', '1000001', '1001003', '2003-05-05 03:55:59', '2024-05-22 17:21:17', '2024-05-10 22:24:54', 'T', 651, 911);
INSERT INTO `sys_user` VALUES (494, 'xixie69', '', '109903652', '谢晓明', 0, '2007-02-10', 'https://video.emily1.info/Others', 34, '6210001910726307', 'xiaox@gmail.com', '1000001', '1001003', '2005-09-25 13:45:43', '2024-05-14 20:28:55', '2024-05-10 22:24:54', 'F', 730, 28);
INSERT INTO `sys_user` VALUES (495, 'zouan', '', '7693360675', '邹安琪', 0, '2016-01-08', 'http://drive.pengz1.co.jp/VideoGames', 73, '5047771059230995', 'anqizou1@gmail.com', '1000001', '1001003', '2023-07-16 08:07:07', '2024-06-05 11:35:12', '2024-05-10 22:24:54', 'F', 416, 441);
INSERT INTO `sys_user` VALUES (496, 'dong1007', '', '15193070752', '董杰宏', 1, '2011-11-22', 'http://video.yamaguchi2.jp/Baby', 57, '4145028645864132', 'dongjiehong@icloud.com', '1000001', '1001003', '2007-06-19 19:01:28', '2024-06-08 16:35:07', '2024-05-10 22:24:54', 'F', 326, 28);
INSERT INTO `sys_user` VALUES (497, 'xiuyiyu', '', '7559393494', '余秀英', 1, '2007-04-02', 'https://image.moky.net/AppsGames', 70, '3540191158429686', 'yu9@gmail.com', '1000001', '1001003', '2006-05-29 08:34:41', '2024-05-25 07:11:01', '2024-05-10 22:24:54', 'F', 432, 280);
INSERT INTO `sys_user` VALUES (498, 'xiangy', '', '19650119243', '向宇宁', 0, '2017-06-03', 'http://image.hikaruhiran.info/Beauty', 98, '3568046430678830', 'yuxia03@gmail.com', '1000001', '1001003', '2006-03-10 14:47:27', '2024-06-01 17:38:26', '2024-05-10 22:24:54', 'T', 370, 52);
INSERT INTO `sys_user` VALUES (499, 'shihanzhao', '', '18191421805', '赵詩涵', 1, '2013-10-19', 'https://video.mevict.biz/Food', 91, '348678810593552', 'shihanzha1010@hotmail.com', '1000001', '1001003', '2006-11-04 14:34:26', '2024-05-26 09:38:02', '2024-05-10 22:24:54', 'F', 53, 238);
INSERT INTO `sys_user` VALUES (500, 'zouxi', '', '7691441351', '邹晓明', 1, '2016-04-28', 'http://auth.hayasakina.com/ToysGames', 64, '3582390085639313', 'xiaomingzou@gmail.com', '1000001', '1001003', '2013-01-16 23:19:23', '2024-06-10 06:13:26', '2024-05-10 22:24:54', 'T', 984, 633);
INSERT INTO `sys_user` VALUES (501, 'zkong', '', '15834343244', '孔致远', 0, '2007-10-24', 'https://video.mawingk97.co.jp/Others', 21, '4166520687694470', 'kong5@icloud.com', '1000001', '1001003', '2015-12-29 05:14:22', '2024-05-23 23:53:37', '2024-05-10 22:24:54', 'T', 831, 321);
INSERT INTO `sys_user` VALUES (502, 'wangji', '', '15166148251', '汪嘉伦', 0, '2013-06-10', 'https://auth.fushingche1.org/Baby', 30, '6282012239073985', 'jiawang@icloud.com', '1000001', '1001003', '2017-03-12 11:12:35', '2024-06-02 21:34:09', '2024-05-10 22:24:54', 'F', 142, 883);
INSERT INTO `sys_user` VALUES (503, 'jiwu', '', '2843189085', '武杰宏', 0, '2007-09-06', 'http://image.kwkm5.xyz/ClothingShoesandJewelry', 49, '3539279724114685', 'jiehwu@icloud.com', '1000001', '1001003', '2019-10-30 07:31:34', '2024-05-19 15:29:36', '2024-05-10 22:24:54', 'F', 510, 9);
INSERT INTO `sys_user` VALUES (504, 'zhaoj', '', '13584178520', '赵杰宏', 1, '2018-09-06', 'http://www.hazukiotsuka.co.jp/Others', 81, '3556084706028897', 'zhaojiehong@icloud.com', '1000001', '1001003', '2015-09-17 06:03:29', '2024-05-13 18:15:53', '2024-05-10 22:24:54', 'T', 273, 932);
INSERT INTO `sys_user` VALUES (505, 'jialutian1111', '', '7551491627', '田嘉伦', 0, '2002-07-14', 'http://video.ziyiliu.us/CenturionGardenOutdoor', 60, '3535493892875399', 'tiajialun@gmail.com', '1000001', '1001003', '2006-10-10 07:39:03', '2024-05-27 12:00:59', '2024-05-10 22:24:54', 'F', 772, 943);
INSERT INTO `sys_user` VALUES (506, 'yilu', '', '13887366981', '尹璐', 1, '2015-12-13', 'http://drive.liksun1943.us/CDsVinyl', 84, '3561175859886751', 'yin426@hotmail.com', '1000001', '1001003', '2021-07-16 09:01:52', '2024-06-03 10:17:29', '2024-05-10 22:24:54', 'T', 775, 877);
INSERT INTO `sys_user` VALUES (507, 'shianqi8', '', '19236045762', '石安琪', 1, '2013-09-18', 'http://drive.bernardhowa5.jp/CollectiblesFineArt', 55, '6251671776459738', 'anqi1004@outlook.com', '1000001', '1001003', '2004-12-20 14:00:00', '2024-05-18 21:35:27', '2024-05-10 22:24:54', 'T', 634, 784);
INSERT INTO `sys_user` VALUES (508, 'xma10', '', '7604653683', '马秀英', 0, '2005-03-03', 'http://auth.kamats.jp/Books', 80, '345431021484887', 'mx3@icloud.com', '1000001', '1001003', '2011-09-26 10:01:40', '2024-05-11 23:06:33', '2024-05-10 22:24:54', 'T', 963, 353);
INSERT INTO `sys_user` VALUES (509, 'hany92', '', '15530436847', '韩云熙', 1, '2005-01-09', 'http://image.nakagawa10.biz/ToysGames', 32, '4305522688089603', 'yunxi2@yahoo.com', '1000001', '1001003', '2018-01-30 03:16:37', '2024-05-11 12:01:55', '2024-05-10 22:24:54', 'F', 894, 340);
INSERT INTO `sys_user` VALUES (510, 'dyuning', '', '14439495452', '杜宇宁', 0, '2024-03-09', 'https://image.zhanyuning.com/AutomotivePartsAccessories', 64, '6230249316617101', 'duyuning@mail.com', '1000001', '1001003', '2020-11-15 09:25:12', '2024-05-10 04:30:52', '2024-05-10 22:24:54', 'T', 165, 716);
INSERT INTO `sys_user` VALUES (511, 'luhe', '', '19680645009', '贺璐', 1, '2014-09-12', 'https://drive.zhwang.net/VideoGames', 51, '6232750510815159', 'lu909@gmail.com', '1000001', '1001003', '2023-02-05 15:38:22', '2024-05-10 14:27:12', '2024-05-10 22:24:54', 'F', 498, 475);
INSERT INTO `sys_user` VALUES (512, 'tan109', '', '76011151567', '谭睿', 0, '2020-12-02', 'http://video.mws.biz/Others', 96, '347415896454974', 'tan9@gmail.com', '1000001', '1001003', '2011-10-29 06:38:21', '2024-06-06 03:37:20', '2024-05-10 22:24:54', 'F', 834, 46);
INSERT INTO `sys_user` VALUES (513, 'anqi1973', '', '7692064367', '阎安琪', 0, '2022-04-05', 'https://www.jiso77.us/Handcrafts', 29, '4825768236723539', 'anqiya@gmail.com', '1000001', '1001003', '2000-08-15 02:58:31', '2024-05-15 03:17:57', '2024-05-10 22:24:54', 'T', 406, 500);
INSERT INTO `sys_user` VALUES (514, 'ma531', '', '14971729037', '马震南', 1, '2011-04-14', 'http://www.kimurmio.cn/BeautyPersonalCare', 60, '342646272005221', 'zma822@outlook.com', '1000001', '1001003', '2011-11-19 17:38:05', '2024-05-17 13:39:32', '2024-05-10 22:24:54', 'F', 662, 474);
INSERT INTO `sys_user` VALUES (515, 'ysh122', '', '17105990323', '阎詩涵', 0, '2017-09-11', 'http://drive.hashiyamato.net/Food', 43, '4510663849975487', 'yashi10@icloud.com', '1000001', '1001003', '2016-06-05 21:56:31', '2024-05-11 15:30:10', '2024-05-10 22:24:54', 'T', 137, 747);
INSERT INTO `sys_user` VALUES (516, 'shihanche1028', '', '16489018364', '陈詩涵', 1, '2020-05-04', 'http://video.kaitogo.biz/CenturionGardenOutdoor', 24, '379284318998880', 'shihanchen@gmail.com', '1000001', '1001003', '2022-05-20 17:55:54', '2024-05-13 16:32:33', '2024-05-10 22:24:54', 'F', 782, 885);
INSERT INTO `sys_user` VALUES (517, 'duzi', '', '76945638660', '段子韬', 0, '2000-09-24', 'http://www.anqi309.jp/Books', 22, '4547308253977523', 'duanzitao1971@outlook.com', '1000001', '1001003', '2005-12-25 15:16:10', '2024-05-14 12:01:16', '2024-05-10 22:24:54', 'T', 620, 976);
INSERT INTO `sys_user` VALUES (518, 'xujieho', '', '2894195172', '徐杰宏', 0, '2001-10-13', 'http://image.daiishida.com/IndustrialScientificSupplies', 29, '5116174563213274', 'jiehxu@icloud.com', '1000001', '1001003', '2011-07-16 05:00:50', '2024-06-10 04:54:57', '2024-05-10 22:24:54', 'F', 985, 480);
INSERT INTO `sys_user` VALUES (519, 'yuanzhen4', '', '7602656719', '袁震南', 1, '2001-08-05', 'https://image.tinta.com/ArtsHandicraftsSewing', 94, '3540141235372185', 'zhennan3@outlook.com', '1000001', '1001003', '2021-08-13 06:53:05', '2024-05-17 18:58:15', '2024-05-10 22:24:54', 'T', 631, 950);
INSERT INTO `sys_user` VALUES (520, 'longrui', '', '18002946727', '龙睿', 0, '2000-05-13', 'https://auth.tsui87.us/AppsGames', 90, '347850697964849', 'ruilong@icloud.com', '1000001', '1001003', '2006-05-19 11:26:47', '2024-05-21 22:56:43', '2024-05-10 22:24:54', 'T', 442, 484);
INSERT INTO `sys_user` VALUES (521, 'yuzhu816', '', '76916907516', '朱宇宁', 1, '2002-11-08', 'https://video.so04.co.jp/Food', 99, '4820607141875130', 'yunzhu1119@icloud.com', '1000001', '1001003', '2015-06-07 18:48:52', '2024-06-08 03:59:25', '2024-05-10 22:24:54', 'F', 283, 705);
INSERT INTO `sys_user` VALUES (522, 'kongyuning1', '', '287936035', '孔宇宁', 0, '2008-07-15', 'https://drive.gonzales8.org/CDsVinyl', 30, '4956026160590270', 'yunko310@outlook.com', '1000001', '1001003', '2003-05-25 11:10:31', '2024-06-04 18:52:44', '2024-05-10 22:24:54', 'T', 715, 481);
INSERT INTO `sys_user` VALUES (523, 'shis', '', '1053167875', '邵詩涵', 0, '2005-12-06', 'https://image.yunxixie.co.jp/ArtsHandicraftsSewing', 96, '4215863216750731', 'shaoshihan8@yahoo.com', '1000001', '1001003', '2014-11-28 18:09:25', '2024-05-15 23:30:52', '2024-05-10 22:24:54', 'F', 675, 822);
INSERT INTO `sys_user` VALUES (524, 'lshiha', '', '2014993897', '梁詩涵', 0, '2009-09-07', 'http://image.yiht.info/ToysGames', 64, '3556482478094826', 'liangshihan815@outlook.com', '1000001', '1001003', '2002-09-25 02:23:28', '2024-05-25 22:16:38', '2024-05-10 22:24:54', 'T', 97, 764);
INSERT INTO `sys_user` VALUES (525, 'ytian', '', '7557076488', '田宇宁', 0, '2008-01-03', 'https://image.deniw.cn/Others', 62, '3561774157151031', 'yuningtian@outlook.com', '1000001', '1001003', '2016-03-03 11:25:10', '2024-05-28 15:26:16', '2024-05-10 22:24:54', 'F', 177, 672);
INSERT INTO `sys_user` VALUES (526, 'wrui', '', '17169092144', '韦睿', 0, '2000-07-04', 'https://auth.yamws10.jp/FilmSupplies', 69, '3575187652917125', 'ruwe@outlook.com', '1000001', '1001003', '2016-04-21 11:54:01', '2024-05-28 18:39:02', '2024-05-10 22:24:54', 'T', 35, 765);
INSERT INTO `sys_user` VALUES (527, 'yuningg1981', '', '2102027029', '顾宇宁', 0, '2001-02-05', 'http://image.ngwin.xyz/Appliances', 99, '6255601622001398', 'yuning1@outlook.com', '1000001', '1001003', '2000-12-31 21:44:58', '2024-05-14 06:42:52', '2024-05-10 22:24:54', 'T', 535, 449);
INSERT INTO `sys_user` VALUES (528, 'ziywei', '', '76035357741', '魏子异', 1, '2015-01-11', 'https://video.pangyl02.co.jp/AppsGames', 99, '6239739513271828', 'ziyi827@hotmail.com', '1000001', '1001003', '2006-05-28 19:42:33', '2024-05-18 00:07:20', '2024-05-10 22:24:54', 'F', 479, 542);
INSERT INTO `sys_user` VALUES (529, 'leziy1226', '', '16715903981', '雷子异', 1, '2018-03-25', 'https://video.yokoyama5.jp/IndustrialScientificSupplies', 72, '6258509398024469', 'ziyile@outlook.com', '1000001', '1001003', '2013-09-20 19:49:45', '2024-06-03 10:02:36', '2024-05-10 22:24:54', 'T', 908, 973);
INSERT INTO `sys_user` VALUES (530, 'zhennan620', '', '208433091', '蒋震南', 0, '2002-01-21', 'http://www.itsuki1955.jp/HealthBabyCare', 61, '5532916556916859', 'jzhennan@yahoo.com', '1000001', '1001003', '2012-02-06 09:21:43', '2024-05-17 15:15:37', '2024-05-10 22:24:54', 'F', 998, 706);
INSERT INTO `sys_user` VALUES (531, 'lushi', '', '7553497510', '史璐', 0, '2013-03-18', 'http://video.kinohikari.com/CellPhonesAccessories', 96, '5454754292428580', 'shilu@gmail.com', '1000001', '1001003', '2000-08-05 11:05:36', '2024-06-09 00:22:18', '2024-05-10 22:24:54', 'T', 394, 204);
INSERT INTO `sys_user` VALUES (532, 'yunicai1225', '', '14529005100', '蔡宇宁', 1, '2005-03-03', 'https://image.fungth01.info/AppsGames', 25, '3558526273951479', 'yunca@gmail.com', '1000001', '1001003', '2011-08-19 05:28:45', '2024-05-16 07:56:42', '2024-05-10 22:24:54', 'F', 654, 433);
INSERT INTO `sys_user` VALUES (533, 'xyunxi82', '', '211854347', '薛云熙', 1, '2007-02-27', 'https://drive.eugenefis2.cn/BeautyPersonalCare', 16, '4036504469531967', 'xuy@outlook.com', '1000001', '1001003', '2010-04-28 11:04:09', '2024-05-30 15:42:45', '2024-05-10 22:24:54', 'F', 107, 392);
INSERT INTO `sys_user` VALUES (534, 'xiuying707', '', '1047554659', '黄秀英', 1, '2011-04-01', 'https://www.renu.org/CenturionGardenOutdoor', 79, '3541101442124885', 'xiuyingh9@yahoo.com', '1000001', '1001003', '2011-02-12 15:16:14', '2024-05-14 12:25:01', '2024-05-10 22:24:54', 'F', 921, 396);
INSERT INTO `sys_user` VALUES (535, 'jialun9', '', '15234711098', '陈嘉伦', 0, '2002-11-28', 'http://video.yiws.net/BeautyPersonalCare', 66, '3555756097807275', 'jich@yahoo.com', '1000001', '1001003', '2021-04-03 15:31:03', '2024-05-17 07:58:39', '2024-05-10 22:24:54', 'T', 430, 778);
INSERT INTO `sys_user` VALUES (536, 'wuj', '', '1008129286', '武嘉伦', 1, '2005-07-27', 'https://auth.garzano10.jp/Food', 94, '4874809750935628', 'wuj7@yahoo.com', '1000001', '1001003', '2006-03-15 15:24:50', '2024-05-19 03:34:42', '2024-05-10 22:24:54', 'T', 337, 397);
INSERT INTO `sys_user` VALUES (537, 'sulan314', '', '15503176606', '孙岚', 0, '2010-09-23', 'http://image.huikw1030.biz/SportsOutdoor', 34, '5249323467662553', 'sunla1102@icloud.com', '1000001', '1001003', '2014-12-10 04:43:48', '2024-05-29 20:17:15', '2024-05-10 22:24:54', 'T', 584, 627);
INSERT INTO `sys_user` VALUES (538, 'longz623', '', '212229489', '龙子韬', 1, '2013-04-23', 'http://www.lsun.us/VideoGames', 45, '346468453344576', 'zitaolong@gmail.com', '1000001', '1001003', '2010-04-02 11:38:17', '2024-06-03 08:16:04', '2024-05-10 22:24:54', 'F', 1000, 711);
INSERT INTO `sys_user` VALUES (539, 'caoshihan', '', '13884252136', '曹詩涵', 0, '2001-11-25', 'http://drive.arimurayun1214.cn/SportsOutdoor', 36, '3577296772441441', 'shihca@gmail.com', '1000001', '1001003', '2009-03-24 13:56:01', '2024-06-10 09:41:42', '2024-05-10 22:24:54', 'T', 410, 133);
INSERT INTO `sys_user` VALUES (540, 'lshihan', '', '282686454', '廖詩涵', 0, '2016-09-13', 'https://drive.marjorieg.us/Food', 51, '5193220885676916', 'shihanlia@gmail.com', '1000001', '1001003', '2006-12-03 21:55:54', '2024-06-09 16:40:21', '2024-05-10 22:24:54', 'F', 284, 508);
INSERT INTO `sys_user` VALUES (541, 'ruisha', '', '200647940', '邵睿', 1, '2006-09-02', 'http://image.renkoyama.org/CDsVinyl', 56, '5118049758331891', 'shru@mail.com', '1000001', '1001003', '2003-06-14 17:01:22', '2024-05-23 03:41:47', '2024-05-10 22:24:54', 'T', 369, 292);
INSERT INTO `sys_user` VALUES (542, 'lanwu', '', '1025200262', '吴岚', 0, '2003-04-14', 'https://image.yunxf1997.cn/FilmSupplies', 61, '4745769808585576', 'lanwu3@hotmail.com', '1000001', '1001003', '2016-03-30 07:10:19', '2024-05-26 22:40:23', '2024-05-10 22:24:54', 'F', 304, 708);
INSERT INTO `sys_user` VALUES (543, 'ziyiren2', '', '16801145294', '任子异', 1, '2022-01-05', 'https://image.yamadamisa9.net/Beauty', 73, '376899055948278', 'ren308@mail.com', '1000001', '1001003', '2001-03-26 14:41:19', '2024-06-02 09:32:09', '2024-05-10 22:24:54', 'F', 343, 116);
INSERT INTO `sys_user` VALUES (544, 'yjialu', '', '16546789287', '叶嘉伦', 1, '2006-05-18', 'http://www.rinishimura20.com/Others', 78, '346737599740834', 'yeji60@yahoo.com', '1000001', '1001003', '2021-04-04 07:51:28', '2024-06-01 12:21:30', '2024-05-10 22:24:54', 'F', 108, 760);
INSERT INTO `sys_user` VALUES (545, 'yuan1990', '', '76032873588', '袁晓明', 1, '2016-03-29', 'https://auth.yuen9.biz/FilmSupplies', 53, '4458595324083755', 'xiaoming402@outlook.com', '1000001', '1001003', '2015-09-28 15:58:28', '2024-05-12 11:23:52', '2024-05-10 22:24:54', 'F', 50, 371);
INSERT INTO `sys_user` VALUES (546, 'cuizh', '', '19736868233', '崔致远', 0, '2012-03-09', 'http://www.silwillie.jp/FilmSupplies', 36, '4529661259000225', 'cui831@hotmail.com', '1000001', '1001003', '2002-03-18 13:15:25', '2024-06-03 03:35:38', '2024-05-10 22:24:54', 'T', 548, 329);
INSERT INTO `sys_user` VALUES (547, 'yeyu', '', '15355512139', '叶宇宁', 0, '2022-01-17', 'https://www.clifford1969.co.jp/Others', 68, '348225851290381', 'yeyuning115@gmail.com', '1000001', '1001003', '2004-08-07 05:01:24', '2024-06-08 19:24:35', '2024-05-10 22:24:54', 'F', 379, 779);
INSERT INTO `sys_user` VALUES (548, 'shjiang', '', '2193259694', '姜詩涵', 1, '2015-01-28', 'https://auth.kanekmitsuki.jp/CollectiblesFineArt', 65, '3554627851234470', 'jiang96@yahoo.com', '1000001', '1001003', '2020-03-12 18:55:49', '2024-05-15 07:19:00', '2024-05-10 22:24:54', 'T', 748, 166);
INSERT INTO `sys_user` VALUES (549, 'daixiuy1', '', '13378599564', '戴秀英', 1, '2022-04-12', 'https://image.jiehongtang.biz/Baby', 95, '6267809542406862', 'xiuying1955@icloud.com', '1000001', '1001003', '2019-03-26 13:04:21', '2024-05-14 13:58:20', '2024-05-10 22:24:54', 'T', 624, 601);
INSERT INTO `sys_user` VALUES (550, 'weishihan', '', '18526736103', '魏詩涵', 0, '2022-05-15', 'http://image.chcy.us/Handcrafts', 39, '344621772832893', 'wei6@yahoo.com', '1000001', '1001003', '2010-12-15 03:29:31', '2024-06-08 09:20:52', '2024-05-10 22:24:54', 'T', 977, 575);
INSERT INTO `sys_user` VALUES (551, 'luliao', '', '13366863839', '廖璐', 0, '2009-09-20', 'https://www.cabryant1948.biz/BeautyPersonalCare', 85, '6226540849551932', 'luli514@outlook.com', '1000001', '1001003', '2006-07-08 10:20:35', '2024-05-19 20:17:08', '2024-05-10 22:24:54', 'T', 77, 836);
INSERT INTO `sys_user` VALUES (552, 'shenyunxi11', '', '289381234', '沈云熙', 0, '2018-08-27', 'https://auth.yotao.xyz/AppsGames', 88, '6299929387727958', 'shen5@gmail.com', '1000001', '1001003', '2004-02-03 08:00:25', '2024-05-18 21:00:00', '2024-05-10 22:24:54', 'F', 254, 483);
INSERT INTO `sys_user` VALUES (553, 'lanhe', '', '17480710674', '何岚', 1, '2003-11-21', 'http://image.jrui.cn/BeautyPersonalCare', 73, '4886433121482578', 'hela1943@icloud.com', '1000001', '1001003', '2019-01-01 07:06:16', '2024-05-20 06:00:32', '2024-05-10 22:24:54', 'F', 538, 891);
INSERT INTO `sys_user` VALUES (554, 'lu9', '', '19484711286', '卢宇宁', 1, '2023-07-03', 'https://auth.zhonxiuy.com/CDsVinyl', 39, '4905523363320424', 'yunlu86@yahoo.com', '1000001', '1001003', '2006-05-28 15:37:01', '2024-05-31 11:39:55', '2024-05-10 22:24:54', 'T', 739, 138);
INSERT INTO `sys_user` VALUES (555, 'reshihan', '', '7600047247', '任詩涵', 0, '2001-08-03', 'https://auth.scurtis20.co.jp/ToolsHomeDecoration', 48, '6264657628783719', 'shiren12@gmail.com', '1000001', '1001003', '2018-03-29 11:46:33', '2024-05-16 15:40:55', '2024-05-10 22:24:54', 'F', 864, 35);
INSERT INTO `sys_user` VALUES (556, 'lucao', '', '19404616919', '曹璐', 1, '2000-01-02', 'http://www.daixiaom.info/CollectiblesFineArt', 23, '348346084945813', 'calu@gmail.com', '1000001', '1001003', '2015-07-24 12:05:04', '2024-06-01 20:06:21', '2024-05-10 22:24:54', 'T', 966, 900);
INSERT INTO `sys_user` VALUES (557, 'wx520', '', '1009780041', '魏晓明', 1, '2021-10-01', 'https://video.jiashihan.org/SportsOutdoor', 44, '371139253331752', 'xwei@icloud.com', '1000001', '1001003', '2000-02-26 22:23:19', '2024-05-10 23:58:09', '2024-05-10 22:24:54', 'F', 198, 288);
INSERT INTO `sys_user` VALUES (558, 'xiuyipe719', '', '7698831127', '彭秀英', 1, '2017-04-14', 'https://image.zda8.xyz/PetSupplies', 28, '6289210928408073', 'xiuying10@gmail.com', '1000001', '1001003', '2021-10-14 04:33:35', '2024-05-12 07:00:27', '2024-05-10 22:24:54', 'F', 838, 928);
INSERT INTO `sys_user` VALUES (559, 'shizhiyu505', '', '15674847491', '石致远', 1, '2011-10-02', 'http://www.hikarufujii302.info/CellPhonesAccessories', 43, '6279874517581343', 'zhiyuan622@icloud.com', '1000001', '1001003', '2001-02-23 06:37:47', '2024-05-29 08:25:00', '2024-05-10 22:24:54', 'F', 439, 248);
INSERT INTO `sys_user` VALUES (560, 'luxiaom1005', '', '15567728837', '吕晓明', 0, '2022-01-27', 'http://www.seikom.com/CDsVinyl', 88, '345221800425510', 'xilu1017@gmail.com', '1000001', '1001003', '2002-09-14 16:49:01', '2024-05-12 19:51:18', '2024-05-10 22:24:54', 'T', 169, 164);
INSERT INTO `sys_user` VALUES (561, 'weirui95', '', '282254720', '魏睿', 0, '2019-07-26', 'http://image.asug1115.co.jp/Appliances', 72, '5001551549428134', 'wei10@mail.com', '1000001', '1001003', '2020-02-23 20:06:29', '2024-05-30 11:49:42', '2024-05-10 22:24:54', 'T', 568, 439);
INSERT INTO `sys_user` VALUES (562, 'tzitao', '', '16051830653', '谭子韬', 0, '2000-03-12', 'http://auth.yzitao.xyz/Food', 45, '4319730534779764', 'tazit4@mail.com', '1000001', '1001003', '2012-07-24 18:13:32', '2024-06-04 01:36:03', '2024-05-10 22:24:54', 'T', 263, 670);
INSERT INTO `sys_user` VALUES (563, 'zhennan9', '', '18830140280', '严震南', 1, '2020-08-27', 'https://drive.jiayao7.xyz/IndustrialScientificSupplies', 73, '6237912350925994', 'zyan1957@outlook.com', '1000001', '1001003', '2001-11-26 20:06:45', '2024-05-29 19:35:25', '2024-05-10 22:24:54', 'F', 391, 557);
INSERT INTO `sys_user` VALUES (564, 'dongyunxi', '', '76047304436', '董云熙', 0, '2006-07-02', 'https://image.nht.com/PetSupplies', 55, '5126076541125397', 'yunxido125@yahoo.com', '1000001', '1001003', '2012-11-13 18:07:21', '2024-06-06 17:41:42', '2024-05-10 22:24:54', 'F', 331, 809);
INSERT INTO `sys_user` VALUES (565, 'hrui', '', '13248765549', '贺睿', 0, '2000-07-06', 'https://www.fostersar.info/CenturionGardenOutdoor', 15, '378413614225745', 'her@hotmail.com', '1000001', '1001003', '2013-03-06 13:38:18', '2024-06-01 23:37:26', '2024-05-10 22:24:54', 'F', 263, 460);
INSERT INTO `sys_user` VALUES (566, 'lgao', '', '7550448156', '高璐', 1, '2018-12-06', 'http://drive.lzhang.us/Handcrafts', 46, '4904076689349859', 'gaolu@outlook.com', '1000001', '1001003', '2021-01-29 12:14:18', '2024-05-15 09:44:38', '2024-05-10 22:24:54', 'F', 430, 277);
INSERT INTO `sys_user` VALUES (567, 'jiehozhao', '', '14895220971', '赵杰宏', 1, '2005-11-10', 'http://auth.itsukinishi.info/Baby', 70, '341342419571777', 'zhao18@hotmail.com', '1000001', '1001003', '2011-02-13 05:03:07', '2024-05-27 00:09:30', '2024-05-10 22:24:54', 'F', 824, 856);
INSERT INTO `sys_user` VALUES (568, 'dishihan', '', '217553521', '丁詩涵', 1, '2004-01-24', 'https://auth.ayanakamura.jp/Others', 93, '348209726101353', 'dshihan@gmail.com', '1000001', '1001003', '2001-04-08 17:17:07', '2024-05-11 04:32:51', '2024-05-10 22:24:54', 'F', 978, 300);
INSERT INTO `sys_user` VALUES (569, 'yuningx2018', '', '206148984', '徐宇宁', 1, '2009-12-22', 'https://video.fsyung707.com/Appliances', 33, '5326068599368837', 'xuyun2@mail.com', '1000001', '1001003', '2009-07-21 09:51:38', '2024-05-30 11:28:39', '2024-05-10 22:24:54', 'T', 265, 803);
INSERT INTO `sys_user` VALUES (570, 'changxiuying', '', '7550882179', '常秀英', 1, '2013-09-28', 'http://image.kasumi46.xyz/BaggageTravelEquipment', 48, '5425417696415605', 'changxiuying16@yahoo.com', '1000001', '1001003', '2019-08-01 20:57:09', '2024-05-19 12:55:56', '2024-05-10 22:24:54', 'F', 360, 139);
INSERT INTO `sys_user` VALUES (571, 'zitz', '', '108546230', '钟子韬', 0, '2023-10-11', 'https://auth.jordanph.xyz/HouseholdKitchenAppliances', 67, '4732220137318672', 'zitao704@hotmail.com', '1000001', '1001003', '2014-03-22 05:41:01', '2024-05-10 14:29:04', '2024-05-10 22:24:54', 'T', 49, 572);
INSERT INTO `sys_user` VALUES (572, 'yuninghe10', '', '14720081913', '何宇宁', 0, '2000-08-11', 'https://video.heungfat5.us/PetSupplies', 19, '348502520077948', 'yunihe@hotmail.com', '1000001', '1001003', '2008-04-13 21:40:59', '2024-06-06 20:55:01', '2024-05-10 22:24:54', 'T', 53, 196);
INSERT INTO `sys_user` VALUES (573, 'yunyang55', '', '17622992792', '杨宇宁', 0, '2016-11-05', 'http://www.ziyijin811.cn/ToysGames', 57, '4861417479499853', 'yuningyang3@icloud.com', '1000001', '1001003', '2002-12-02 09:26:14', '2024-05-11 16:59:22', '2024-05-10 22:24:54', 'T', 351, 548);
INSERT INTO `sys_user` VALUES (574, 'ziliu1979', '', '7607097975', '刘子韬', 1, '2014-12-05', 'https://auth.sya.info/BaggageTravelEquipment', 100, '4545230892656726', 'zliu@outlook.com', '1000001', '1001003', '2019-11-18 15:48:05', '2024-06-02 15:45:03', '2024-05-10 22:24:54', 'F', 548, 569);
INSERT INTO `sys_user` VALUES (575, 'jiaanqi8', '', '19589323602', '贾安琪', 1, '2001-12-10', 'http://www.lagao.xyz/MusicalInstrument', 68, '4363365694068511', 'aji@gmail.com', '1000001', '1001003', '2010-02-04 09:37:40', '2024-05-31 21:10:53', '2024-05-10 22:24:54', 'T', 733, 297);
INSERT INTO `sys_user` VALUES (576, 'leiz701', '', '15444638554', '雷致远', 1, '2016-11-26', 'http://auth.swt.xyz/BaggageTravelEquipment', 83, '4104715281693473', 'lezhiy@outlook.com', '1000001', '1001003', '2023-07-01 11:57:43', '2024-05-31 17:10:48', '2024-05-10 22:24:54', 'T', 668, 675);
INSERT INTO `sys_user` VALUES (577, 'anqiding3', '', '16111791201', '丁安琪', 1, '2022-04-25', 'http://image.wxia.xyz/ToolsHomeDecoration', 49, '4392889544242732', 'dan@hotmail.com', '1000001', '1001003', '2014-03-04 12:45:35', '2024-06-05 14:44:52', '2024-05-10 22:24:54', 'T', 341, 121);
INSERT INTO `sys_user` VALUES (578, 'xiuytan', '', '2813604791', '汤秀英', 0, '2009-06-05', 'https://drive.anqiwu.net/Food', 32, '3580435241694066', 'tangx93@hotmail.com', '1000001', '1001003', '2017-07-05 04:31:08', '2024-05-28 11:29:06', '2024-05-10 22:24:54', 'T', 909, 220);
INSERT INTO `sys_user` VALUES (579, 'lexia', '', '18252093438', '雷晓明', 0, '2012-02-20', 'https://drive.rennomu.biz/SportsOutdoor', 45, '6246362452236230', 'xlei@yahoo.com', '1000001', '1001003', '2013-04-14 02:06:19', '2024-05-13 19:56:16', '2024-05-10 22:24:54', 'T', 553, 789);
INSERT INTO `sys_user` VALUES (580, 'xiuyishi', '', '13594543785', '史秀英', 1, '2009-04-07', 'http://image.koyami.cn/ToysGames', 88, '349798386560238', 'shixiuyi@mail.com', '1000001', '1001003', '2022-09-06 13:07:42', '2024-05-15 23:10:19', '2024-05-10 22:24:54', 'T', 627, 476);
INSERT INTO `sys_user` VALUES (581, 'surui42', '', '14737207536', '孙睿', 0, '2017-05-25', 'http://www.zhzi.us/Books', 44, '5056265059451758', 'ruisu72@outlook.com', '1000001', '1001003', '2021-11-21 18:45:39', '2024-06-10 12:36:40', '2024-05-10 22:24:54', 'F', 693, 856);
INSERT INTO `sys_user` VALUES (582, 'xshiha', '', '289391713', '夏詩涵', 1, '2000-12-27', 'http://image.manshaw1949.biz/MusicalInstrument', 79, '5000783589068659', 'xshihan@mail.com', '1000001', '1001003', '2007-01-02 07:48:21', '2024-06-01 07:49:33', '2024-05-10 22:24:54', 'T', 43, 990);
INSERT INTO `sys_user` VALUES (583, 'li1968', '', '19159015222', '李致远', 0, '2002-07-14', 'https://auth.randydaniels1963.info/HouseholdKitchenAppliances', 25, '4148450382885830', 'zhiyuli520@gmail.com', '1000001', '1001003', '2009-01-28 12:09:21', '2024-05-22 13:02:49', '2024-05-10 22:24:54', 'T', 921, 509);
INSERT INTO `sys_user` VALUES (584, 'fes', '', '75502645003', '冯詩涵', 1, '2019-04-15', 'https://image.zlu.biz/Books', 64, '5393855657839123', 'fengshihan@icloud.com', '1000001', '1001003', '2003-12-18 17:51:59', '2024-06-09 09:16:11', '2024-05-10 22:24:54', 'T', 227, 167);
INSERT INTO `sys_user` VALUES (585, 'liuyunxi', '', '13535124356', '刘云熙', 0, '2015-06-22', 'http://www.lll724.com/CellPhonesAccessories', 23, '4322965442128721', 'liuyunx@gmail.com', '1000001', '1001003', '2019-12-17 06:50:23', '2024-05-20 05:06:47', '2024-05-10 22:24:54', 'F', 583, 609);
INSERT INTO `sys_user` VALUES (586, 'yaoziyi6', '', '285088572', '姚子异', 0, '2011-10-25', 'http://auth.tlchic.org/ClothingShoesandJewelry', 37, '3586261388079562', 'yziyi929@outlook.com', '1000001', '1001003', '2013-01-04 11:24:57', '2024-05-10 21:11:18', '2024-05-10 22:24:54', 'F', 299, 426);
INSERT INTO `sys_user` VALUES (587, 'yan225', '', '17772647854', '严璐', 1, '2003-01-20', 'http://video.joe12.info/VideoGames', 73, '3582862585273942', 'yan1118@icloud.com', '1000001', '1001003', '2001-04-30 11:13:39', '2024-05-22 15:09:42', '2024-05-10 22:24:54', 'F', 616, 584);
INSERT INTO `sys_user` VALUES (588, 'lyunin8', '', '2007534684', '林宇宁', 1, '2010-10-11', 'http://www.zitao63.us/CDsVinyl', 92, '4237576861448567', 'yuning17@gmail.com', '1000001', '1001003', '2023-09-02 23:54:49', '2024-05-19 06:54:41', '2024-05-10 22:24:54', 'T', 775, 289);
INSERT INTO `sys_user` VALUES (589, 'zhenhu', '', '7692992181', '胡震南', 1, '2003-05-08', 'https://auth.sasaki56.cn/Appliances', 23, '4939938224380419', 'hzhe@gmail.com', '1000001', '1001003', '2000-06-26 20:48:24', '2024-05-14 23:54:55', '2024-05-10 22:24:54', 'F', 107, 556);
INSERT INTO `sys_user` VALUES (590, 'suyunxi9', '', '13714135516', '苏云熙', 0, '2020-10-17', 'https://www.wl1222.cn/PetSupplies', 74, '6261051760775662', 'yunxsu409@icloud.com', '1000001', '1001003', '2021-09-13 06:00:07', '2024-06-08 17:45:58', '2024-05-10 22:24:54', 'T', 184, 403);
INSERT INTO `sys_user` VALUES (591, 'fayu', '', '1054353827', '范宇宁', 1, '2014-10-16', 'https://drive.kmtam.biz/BeautyPersonalCare', 34, '373987676346148', 'yuningf@hotmail.com', '1000001', '1001003', '2008-11-16 00:53:08', '2024-06-06 18:15:02', '2024-05-10 22:24:54', 'F', 12, 729);
INSERT INTO `sys_user` VALUES (592, 'shaoshih5', '', '208670624', '邵詩涵', 1, '2020-02-18', 'https://www.hanayamamoto.org/Others', 34, '3535891722941085', 'shao1988@gmail.com', '1000001', '1001003', '2022-12-28 00:09:57', '2024-05-19 21:03:16', '2024-05-10 22:24:54', 'T', 510, 853);
INSERT INTO `sys_user` VALUES (593, 'shihangon3', '', '16124264470', '龚詩涵', 1, '2003-04-11', 'https://www.yau1947.biz/ClothingShoesandJewelry', 59, '5076291609402634', 'gs3@outlook.com', '1000001', '1001003', '2007-09-02 00:02:03', '2024-05-29 14:25:01', '2024-05-10 22:24:54', 'F', 921, 482);
INSERT INTO `sys_user` VALUES (594, 'zhiyuanshen2', '', '17040959662', '沈致远', 1, '2013-08-16', 'https://drive.wschow.com/CenturionGardenOutdoor', 77, '5160706735730076', 'shzhiyu@hotmail.com', '1000001', '1001003', '2002-09-12 14:18:31', '2024-05-28 01:58:26', '2024-05-10 22:24:54', 'F', 261, 275);
INSERT INTO `sys_user` VALUES (595, 'yding66', '', '2141339945', '丁云熙', 1, '2022-01-15', 'https://auth.yulingchiang1986.net/Books', 82, '347868402975462', 'dingyunx@icloud.com', '1000001', '1001003', '2010-06-03 17:06:49', '2024-05-15 09:12:31', '2024-05-10 22:24:54', 'F', 623, 346);
INSERT INTO `sys_user` VALUES (596, 'jialun02', '', '2143852972', '谢嘉伦', 0, '2019-09-25', 'https://video.sauman912.co.jp/PetSupplies', 25, '340415631355699', 'xiejial831@gmail.com', '1000001', '1001003', '2003-10-22 03:56:46', '2024-06-06 10:07:36', '2024-05-10 22:24:54', 'F', 490, 85);
INSERT INTO `sys_user` VALUES (597, 'fang6', '', '19798808730', '方子韬', 0, '2017-10-04', 'https://www.nicole325.net/MusicalInstrument', 32, '5508625377035035', 'fang43@icloud.com', '1000001', '1001003', '2024-02-02 15:33:19', '2024-05-12 20:15:37', '2024-05-10 22:24:54', 'F', 605, 763);
INSERT INTO `sys_user` VALUES (598, 'luorui7', '', '16840477893', '罗睿', 1, '2008-04-19', 'http://image.turnere.xyz/CDsVinyl', 45, '373220888306928', 'luorui@mail.com', '1000001', '1001003', '2001-02-16 16:34:56', '2024-05-20 09:47:14', '2024-05-10 22:24:54', 'T', 88, 294);
INSERT INTO `sys_user` VALUES (599, 'qin1969', '', '17524990114', '秦云熙', 0, '2012-09-15', 'http://video.sasakiseiko1211.cn/HouseholdKitchenAppliances', 24, '5348200987325384', 'yunxiq@gmail.com', '1000001', '1001003', '2004-05-13 19:12:46', '2024-06-01 16:58:28', '2024-05-10 22:24:54', 'F', 399, 774);
INSERT INTO `sys_user` VALUES (600, 'anqiden', '', '14239276449', '邓安琪', 0, '2011-05-26', 'http://video.lanchang.co.jp/IndustrialScientificSupplies', 27, '4432766734137991', 'dean@outlook.com', '1000001', '1001003', '2001-05-08 13:13:52', '2024-05-23 14:28:12', '2024-05-10 22:24:54', 'F', 117, 502);
INSERT INTO `sys_user` VALUES (601, 'guo7', '', '16154984468', '郭嘉伦', 0, '2001-01-16', 'https://video.lizhiyu10.co.jp/ArtsHandicraftsSewing', 98, '373672045181265', 'jguo10@mail.com', '1000001', '1001003', '2000-09-14 22:03:28', '2024-05-15 08:16:21', '2024-05-10 22:24:54', 'F', 162, 249);
INSERT INTO `sys_user` VALUES (602, 'shenrui', '', '76961388494', '沈睿', 0, '2006-08-26', 'http://auth.yujia7.biz/PetSupplies', 83, '347347323308525', 'ruishe@hotmail.com', '1000001', '1001003', '2005-08-28 02:33:45', '2024-05-11 12:23:48', '2024-05-10 22:24:54', 'F', 310, 213);
INSERT INTO `sys_user` VALUES (603, 'lu504', '', '204302383', '陆宇宁', 0, '2000-02-07', 'http://www.rays.info/Beauty', 99, '4580046578074265', 'lu04@icloud.com', '1000001', '1001003', '2004-08-30 15:03:51', '2024-05-24 16:30:26', '2024-05-10 22:24:54', 'F', 889, 652);
INSERT INTO `sys_user` VALUES (604, 'lyu501', '', '7698805511', '雷云熙', 0, '2003-03-26', 'http://www.llt824.xyz/ArtsHandicraftsSewing', 59, '4015644201382306', 'leiy@yahoo.com', '1000001', '1001003', '2006-05-20 17:24:37', '2024-06-05 16:45:04', '2024-05-10 22:24:54', 'F', 926, 7);
INSERT INTO `sys_user` VALUES (605, 'yuning7', '', '215719760', '贺宇宁', 1, '2023-07-02', 'http://auth.miyaminat.us/BeautyPersonalCare', 98, '341228743540051', 'heyu@mail.com', '1000001', '1001003', '2008-10-21 00:59:22', '2024-06-05 07:05:23', '2024-05-10 22:24:54', 'F', 181, 30);
INSERT INTO `sys_user` VALUES (606, 'zhiyuli10', '', '202395894', '林致远', 1, '2017-02-28', 'http://video.tsangsy.com/Beauty', 55, '3546104796406176', 'lzhiyuan@gmail.com', '1000001', '1001003', '2020-07-28 08:11:02', '2024-05-25 07:39:53', '2024-05-10 22:24:54', 'F', 962, 814);
INSERT INTO `sys_user` VALUES (607, 'xuzitao801', '', '215894475', '薛子韬', 0, '2019-06-11', 'http://auth.yuitom.biz/AutomotivePartsAccessories', 77, '5283571442349095', 'xuez@icloud.com', '1000001', '1001003', '2012-05-13 02:27:21', '2024-05-22 04:55:54', '2024-05-10 22:24:54', 'T', 736, 790);
INSERT INTO `sys_user` VALUES (608, 'ziyi06', '', '14216490092', '陶子异', 0, '2015-04-19', 'http://www.anqliao.org/Handcrafts', 35, '6297011085725774', 'taoz6@gmail.com', '1000001', '1001003', '2004-11-13 09:54:27', '2024-06-02 17:46:28', '2024-05-10 22:24:54', 'F', 49, 819);
INSERT INTO `sys_user` VALUES (609, 'luzh10', '', '13675324981', '朱璐', 0, '2019-06-21', 'https://auth.tolopez.org/Books', 28, '3548154487454116', 'zlu6@gmail.com', '1000001', '1001003', '2021-06-06 05:19:21', '2024-06-02 18:02:42', '2024-05-10 22:24:54', 'F', 470, 201);
INSERT INTO `sys_user` VALUES (610, 'rc73', '', '211815451', '曹睿', 0, '2003-02-26', 'http://video.sakamotoa11.net/Food', 39, '3584947868188996', 'rui5@icloud.com', '1000001', '1001003', '2024-01-04 22:58:50', '2024-05-23 16:19:26', '2024-05-10 22:24:54', 'T', 891, 225);
INSERT INTO `sys_user` VALUES (611, 'zhouanqi', '', '7697057777', '周安琪', 1, '2013-09-18', 'https://video.kkloui2.net/Appliances', 81, '6269413645951748', 'zhouan@gmail.com', '1000001', '1001003', '2004-01-08 05:53:03', '2024-05-25 01:54:31', '2024-05-10 22:24:54', 'T', 655, 358);
INSERT INTO `sys_user` VALUES (612, 'jiehong02', '', '1032639446', '龙杰宏', 1, '2018-05-27', 'http://drive.jane719.com/MusicalInstrument', 77, '6246511893103041', 'jlong@gmail.com', '1000001', '1001003', '2013-12-23 03:07:28', '2024-06-10 20:19:04', '2024-05-10 22:24:54', 'T', 58, 405);
INSERT INTO `sys_user` VALUES (613, 'haoziy', '', '18629508346', '郝子异', 1, '2021-05-25', 'https://image.zouz77.co.jp/Food', 51, '6203324922846403', 'zhao@mail.com', '1000001', '1001003', '2002-02-14 20:55:32', '2024-06-02 17:00:50', '2024-05-10 22:24:54', 'F', 476, 485);
INSERT INTO `sys_user` VALUES (614, 'languo', '', '76014568398', '郭岚', 0, '2008-03-21', 'https://image.nakano2.net/SportsOutdoor', 16, '5402178934508240', 'laguo1985@icloud.com', '1000001', '1001003', '2000-12-29 19:23:53', '2024-06-07 10:45:27', '2024-05-10 22:24:54', 'F', 19, 139);
INSERT INTO `sys_user` VALUES (615, 'jialund', '', '7605806754', '戴嘉伦', 1, '2006-12-18', 'https://image.anqhuang.cn/AutomotivePartsAccessories', 95, '4745524596825589', 'jda@yahoo.com', '1000001', '1001003', '2004-03-15 13:11:15', '2024-05-21 14:50:23', '2024-05-10 22:24:54', 'T', 525, 395);
INSERT INTO `sys_user` VALUES (616, 'anqilu2018', '', '76945956207', '吕安琪', 0, '2014-01-23', 'http://drive.likyung2004.us/IndustrialScientificSupplies', 28, '6218347686345844', 'anqilu5@gmail.com', '1000001', '1001003', '2001-03-04 21:57:58', '2024-05-21 05:28:16', '2024-05-10 22:24:54', 'F', 866, 973);
INSERT INTO `sys_user` VALUES (617, 'zjialun', '', '14607427139', '朱嘉伦', 0, '2004-02-17', 'https://auth.yuan430.us/FilmSupplies', 64, '3541389750990407', 'jz915@icloud.com', '1000001', '1001003', '2006-04-09 01:20:36', '2024-05-21 10:35:08', '2024-05-10 22:24:54', 'F', 888, 439);
INSERT INTO `sys_user` VALUES (618, 'wangji2001', '', '15278918715', '汪嘉伦', 1, '2001-05-01', 'http://auth.hwailam.org/Food', 57, '5500861305519638', 'wjialun1@icloud.com', '1000001', '1001003', '2020-10-16 23:35:33', '2024-05-12 09:40:57', '2024-05-10 22:24:54', 'T', 192, 755);
INSERT INTO `sys_user` VALUES (619, 'zitao1976', '', '14110532073', '戴子韬', 0, '2012-03-11', 'https://auth.wingfl.us/AppsGames', 78, '3566040135499826', 'zitaoda@icloud.com', '1000001', '1001003', '2013-07-06 14:59:14', '2024-05-24 22:32:35', '2024-05-10 22:24:54', 'F', 771, 22);
INSERT INTO `sys_user` VALUES (620, 'hao3', '', '17921953529', '郝岚', 0, '2006-03-29', 'http://auth.saitryota.co.jp/ToysGames', 93, '341454330184000', 'lanhao@hotmail.com', '1000001', '1001003', '2023-10-02 15:00:35', '2024-05-19 10:17:49', '2024-05-10 22:24:54', 'F', 830, 97);
INSERT INTO `sys_user` VALUES (621, 'wrui50', '', '19542067121', '吴睿', 1, '2019-09-19', 'https://video.yungkk.jp/Handcrafts', 72, '6237210271012665', 'wr1971@mail.com', '1000001', '1001003', '2007-04-22 13:27:45', '2024-05-13 02:23:45', '2024-05-10 22:24:54', 'F', 33, 125);
INSERT INTO `sys_user` VALUES (622, 'jiehonggao909', '', '219787100', '高杰宏', 1, '2014-10-13', 'http://image.shpeng.co.jp/SportsOutdoor', 20, '4237871001767723', 'gaj9@yahoo.com', '1000001', '1001003', '2009-08-19 11:56:23', '2024-06-07 16:33:15', '2024-05-10 22:24:54', 'F', 587, 296);
INSERT INTO `sys_user` VALUES (623, 'jiehongjin40', '', '17250799794', '金杰宏', 1, '2011-04-03', 'https://drive.kws218.xyz/Handcrafts', 89, '340994486771630', 'jji@yahoo.com', '1000001', '1001003', '2002-01-28 02:30:54', '2024-05-14 09:53:08', '2024-05-10 22:24:54', 'T', 2, 191);
INSERT INTO `sys_user` VALUES (624, 'gonl', '', '19685800489', '龚岚', 0, '2013-07-01', 'https://auth.hall10.jp/Beauty', 24, '342324599594612', 'lan2@outlook.com', '1000001', '1001003', '2022-12-15 10:08:47', '2024-05-29 17:23:55', '2024-05-10 22:24:54', 'T', 448, 31);
INSERT INTO `sys_user` VALUES (625, 'yaozhe6', '', '15577581680', '姚震南', 0, '2009-03-14', 'https://auth.misakikudo4.org/FilmSupplies', 25, '6209071875140086', 'yaozhennan517@gmail.com', '1000001', '1001003', '2005-02-25 13:53:04', '2024-05-21 04:40:34', '2024-05-10 22:24:54', 'F', 869, 186);
INSERT INTO `sys_user` VALUES (626, 'taxiaoming330', '', '2890991473', '陶晓明', 0, '2016-11-25', 'https://www.tlkong87.co.jp/Handcrafts', 48, '6253191675742207', 'tao1221@icloud.com', '1000001', '1001003', '2019-01-31 02:14:18', '2024-05-25 10:00:02', '2024-05-10 22:24:54', 'T', 507, 732);
INSERT INTO `sys_user` VALUES (627, 'lan6', '', '13784149025', '毛岚', 1, '2015-07-04', 'http://video.myerjuanita109.us/ComputersElectronics', 44, '4039301447108704', 'maolan@icloud.com', '1000001', '1001003', '2008-11-13 23:03:56', '2024-05-22 15:20:34', '2024-05-10 22:24:54', 'F', 229, 536);
INSERT INTO `sys_user` VALUES (628, 'zitaog', '', '15516319042', '高子韬', 1, '2001-05-14', 'https://auth.chang923.info/HealthBabyCare', 64, '375161972106352', 'gao6@gmail.com', '1000001', '1001003', '2007-06-13 22:37:28', '2024-05-17 21:32:22', '2024-05-10 22:24:54', 'F', 89, 448);
INSERT INTO `sys_user` VALUES (629, 'ziyixue', '', '19756782535', '薛子异', 1, '2018-01-20', 'http://auth.aoiha413.org/ComputersElectronics', 100, '376856372534967', 'ziyi101@outlook.com', '1000001', '1001003', '2017-02-28 20:08:44', '2024-05-19 05:17:13', '2024-05-10 22:24:54', 'F', 216, 834);
INSERT INTO `sys_user` VALUES (630, 'lhe', '', '1060774364', '贺璐', 0, '2000-05-10', 'https://video.janichawkins4.cn/MusicalInstrument', 24, '6245965996028169', 'luh@outlook.com', '1000001', '1001003', '2001-02-14 00:20:07', '2024-06-08 20:02:11', '2024-05-10 22:24:54', 'T', 428, 382);
INSERT INTO `sys_user` VALUES (631, 'yuningl', '', '1034319510', '吕宇宁', 1, '2005-11-19', 'http://video.juliael.com/ClothingShoesandJewelry', 24, '373514095824354', 'yunilu5@gmail.com', '1000001', '1001003', '2010-09-25 20:43:03', '2024-06-05 03:57:40', '2024-05-10 22:24:54', 'T', 646, 209);
INSERT INTO `sys_user` VALUES (632, 'zhennan47', '', '19657798580', '侯震南', 1, '2022-07-04', 'https://image.lu1025.com/HealthBabyCare', 33, '5066841590599285', 'hou50@hotmail.com', '1000001', '1001003', '2023-08-30 15:25:21', '2024-05-17 17:21:38', '2024-05-10 22:24:54', 'T', 425, 281);
INSERT INTO `sys_user` VALUES (633, 'zhiylu', '', '103136702', '卢致远', 1, '2012-10-03', 'http://auth.takeuchiyota2008.biz/VideoGames', 26, '3581163961499628', 'lu3@gmail.com', '1000001', '1001003', '2013-09-13 23:16:28', '2024-06-09 16:58:39', '2024-05-10 22:24:54', 'F', 395, 521);
INSERT INTO `sys_user` VALUES (634, 'anqil', '', '19174573304', '廖安琪', 1, '2017-10-02', 'http://image.waiyeeyue.co.jp/MusicalInstrument', 41, '6246779610827289', 'anqili@outlook.com', '1000001', '1001003', '2015-08-21 02:49:01', '2024-05-11 10:06:44', '2024-05-10 22:24:54', 'F', 325, 319);
INSERT INTO `sys_user` VALUES (635, 'gy1978', '', '17098049612', '郭云熙', 0, '2016-06-24', 'https://image.ziyi85.net/Appliances', 99, '5481904757564166', 'guoyunxi@hotmail.com', '1000001', '1001003', '2016-08-17 09:50:28', '2024-05-19 11:40:17', '2024-05-10 22:24:54', 'F', 702, 628);
INSERT INTO `sys_user` VALUES (636, 'jia720', '', '7553833400', '贾嘉伦', 0, '2006-08-09', 'http://auth.yuito3.biz/IndustrialScientificSupplies', 39, '3575644683159069', 'jiajialun@yahoo.com', '1000001', '1001003', '2023-03-09 08:27:15', '2024-05-20 19:49:29', '2024-05-10 22:24:54', 'F', 518, 978);
INSERT INTO `sys_user` VALUES (637, 'zhiyuanz4', '', '76940647097', '周致远', 0, '2015-04-15', 'https://drive.richardyoung7.jp/BaggageTravelEquipment', 51, '4907686704960857', 'zhiyuanz5@gmail.com', '1000001', '1001003', '2010-08-22 07:11:28', '2024-05-13 13:15:01', '2024-05-10 22:24:54', 'T', 906, 849);
INSERT INTO `sys_user` VALUES (638, 'ziyiwang2011', '', '76953720087', '王子异', 1, '2017-03-24', 'https://auth.rui3.us/SportsOutdoor', 53, '6217324955115472', 'ziyiwang@gmail.com', '1000001', '1001003', '2005-10-05 22:53:50', '2024-06-05 06:21:37', '2024-05-10 22:24:54', 'F', 532, 917);
INSERT INTO `sys_user` VALUES (639, 'chenlan', '', '216728574', '陈岚', 0, '2001-05-25', 'http://video.fredhawkins.co.jp/ToysGames', 68, '3560919517605348', 'lch@icloud.com', '1000001', '1001003', '2006-01-31 15:13:22', '2024-06-06 16:02:43', '2024-05-10 22:24:54', 'T', 927, 414);
INSERT INTO `sys_user` VALUES (640, 'dongzh', '', '7604518537', '董致远', 0, '2004-05-02', 'http://www.patriciacr09.us/Baby', 70, '5229890057449280', 'dong81@gmail.com', '1000001', '1001003', '2017-11-26 14:27:10', '2024-05-19 07:04:28', '2024-05-10 22:24:54', 'F', 431, 483);
INSERT INTO `sys_user` VALUES (641, 'ay831', '', '14499676187', '于安琪', 0, '2019-08-02', 'http://www.wmm10.co.jp/ToysGames', 21, '4124610465784709', 'any@icloud.com', '1000001', '1001003', '2020-04-20 19:47:15', '2024-06-06 02:12:53', '2024-05-10 22:24:54', 'T', 521, 716);
INSERT INTO `sys_user` VALUES (642, 'jiguo3', '', '7696274543', '郭杰宏', 0, '2006-09-06', 'http://www.zit.cn/CollectiblesFineArt', 50, '5476472001161343', 'jguo@yahoo.com', '1000001', '1001003', '2018-03-01 07:05:46', '2024-06-03 16:21:48', '2024-05-10 22:24:54', 'F', 774, 181);
INSERT INTO `sys_user` VALUES (643, 'zhlu', '', '13930208707', '赵璐', 0, '2009-08-03', 'https://auth.carriram4.jp/Handcrafts', 52, '348206600832905', 'luzhao518@outlook.com', '1000001', '1001003', '2000-10-28 16:14:49', '2024-05-19 14:46:54', '2024-05-10 22:24:54', 'T', 964, 914);
INSERT INTO `sys_user` VALUES (644, 'xiaotang', '', '2022353330', '唐晓明', 0, '2015-05-31', 'http://image.medinaphillip.xyz/VideoGames', 31, '379991016729082', 'xiaoming4@mail.com', '1000001', '1001003', '2018-04-05 22:09:42', '2024-06-09 22:47:31', '2024-05-10 22:24:54', 'T', 215, 96);
INSERT INTO `sys_user` VALUES (645, 'yan6', '', '2153528754', '阎詩涵', 1, '2015-09-06', 'http://drive.pam.xyz/ArtsHandicraftsSewing', 84, '3549001736213729', 'shihyan@gmail.com', '1000001', '1001003', '2009-01-23 07:24:59', '2024-05-27 02:14:57', '2024-05-10 22:24:54', 'T', 396, 143);
INSERT INTO `sys_user` VALUES (646, 'yuan510', '', '7698541021', '袁震南', 1, '2015-03-24', 'http://www.anitabennett.co.jp/ToysGames', 58, '5329108695215065', 'yuan10@gmail.com', '1000001', '1001003', '2006-01-24 11:54:15', '2024-05-25 10:09:01', '2024-05-10 22:24:54', 'T', 884, 289);
INSERT INTO `sys_user` VALUES (647, 'ac1024', '', '75592559729', '常安琪', 0, '2016-02-28', 'https://video.kimbjames.biz/HealthBabyCare', 43, '5209137102430574', 'anqichang@outlook.com', '1000001', '1001003', '2007-07-03 08:37:47', '2024-06-10 22:42:11', '2024-05-10 22:24:54', 'F', 503, 571);
INSERT INTO `sys_user` VALUES (648, 'ye1', '', '76907171143', '叶岚', 1, '2019-03-25', 'http://www.hok8.biz/BeautyPersonalCare', 51, '3553119051586751', 'yelan@yahoo.com', '1000001', '1001003', '2009-11-21 17:47:46', '2024-06-09 10:46:10', '2024-05-10 22:24:54', 'F', 746, 110);
INSERT INTO `sys_user` VALUES (649, 'zcao', '', '107065371', '曹致远', 1, '2010-03-02', 'http://image.clkat302.co.jp/Books', 38, '6270136529480818', 'caozhi@hotmail.com', '1000001', '1001003', '2005-01-02 17:39:33', '2024-05-15 17:06:35', '2024-05-10 22:24:54', 'F', 708, 446);
INSERT INTO `sys_user` VALUES (650, 'xiaanqi7', '', '16357653449', '夏安琪', 0, '2010-10-14', 'http://auth.hsuanwaiman.xyz/HealthBabyCare', 92, '6243903833669236', 'xiaanqi@gmail.com', '1000001', '1001003', '2023-06-03 13:52:11', '2024-05-17 04:35:09', '2024-05-10 22:24:54', 'T', 51, 950);
INSERT INTO `sys_user` VALUES (651, 'ziyi4', '', '16113833329', '贾子异', 1, '2016-03-15', 'http://image.wingfatpak60.xyz/IndustrialScientificSupplies', 28, '377810635115436', 'jiaziyi@icloud.com', '1000001', '1001003', '2006-04-16 10:50:05', '2024-06-09 00:48:56', '2024-05-10 22:24:54', 'T', 513, 455);
INSERT INTO `sys_user` VALUES (652, 'xjiehong', '', '214833106', '许杰宏', 1, '2023-09-21', 'https://image.okamotomio88.net/Handcrafts', 69, '349382605489667', 'xjieh@gmail.com', '1000001', '1001003', '2013-01-24 03:04:33', '2024-05-30 04:46:36', '2024-05-10 22:24:54', 'F', 335, 925);
INSERT INTO `sys_user` VALUES (653, 'zhennans510', '', '2149384279', '宋震南', 0, '2015-09-19', 'http://video.yuning8.com/AutomotivePartsAccessories', 71, '4652154118835477', 'sozhen@mail.com', '1000001', '1001003', '2024-01-08 09:09:35', '2024-06-07 11:54:50', '2024-05-10 22:24:54', 'F', 537, 692);
INSERT INTO `sys_user` VALUES (654, 'yuningyin1115', '', '16192638029', '尹宇宁', 0, '2000-01-31', 'http://auth.lmarshall.xyz/VideoGames', 61, '3575619531506356', 'yinyuni2@gmail.com', '1000001', '1001003', '2013-09-25 11:18:52', '2024-05-15 01:34:47', '2024-05-10 22:24:54', 'T', 494, 809);
INSERT INTO `sys_user` VALUES (655, 'hou405', '', '76093630130', '侯云熙', 0, '2004-01-08', 'http://auth.tz1122.org/ArtsHandicraftsSewing', 83, '6249491773691257', 'houyunxi9@gmail.com', '1000001', '1001003', '2007-04-20 21:29:35', '2024-06-01 21:42:04', '2024-05-10 22:24:54', 'F', 657, 929);
INSERT INTO `sys_user` VALUES (656, 'anqix', '', '13044902487', '谢安琪', 1, '2008-09-15', 'https://www.patrik1025.co.jp/IndustrialScientificSupplies', 71, '4006979258765321', 'xieanq@yahoo.com', '1000001', '1001003', '2007-12-09 23:13:46', '2024-05-20 08:44:31', '2024-05-10 22:24:54', 'T', 543, 812);
INSERT INTO `sys_user` VALUES (657, 'zhennan57', '', '7553279578', '郝震南', 1, '2002-10-14', 'https://drive.takahashirin806.xyz/Others', 82, '3535736140881399', 'zhennanhao@gmail.com', '1000001', '1001003', '2004-09-16 16:31:46', '2024-05-18 08:48:07', '2024-05-10 22:24:54', 'T', 302, 871);
INSERT INTO `sys_user` VALUES (658, 'jz62', '', '13347941241', '江子韬', 0, '2010-01-13', 'http://www.mminat.info/Books', 89, '4279008545041641', 'zij@icloud.com', '1000001', '1001003', '2019-06-28 13:33:46', '2024-06-07 21:24:06', '2024-05-10 22:24:54', 'T', 289, 820);
INSERT INTO `sys_user` VALUES (659, 'yanan', '', '205231708', '阎安琪', 0, '2002-02-04', 'https://auth.leizi.biz/IndustrialScientificSupplies', 26, '4712324383196024', 'ayan8@icloud.com', '1000001', '1001003', '2008-02-14 09:56:58', '2024-05-19 13:01:53', '2024-05-10 22:24:54', 'F', 246, 603);
INSERT INTO `sys_user` VALUES (660, 'jialucu9', '', '16797840065', '崔嘉伦', 1, '2009-10-05', 'http://video.maries.biz/BaggageTravelEquipment', 32, '4312360229880631', 'cuijialun@outlook.com', '1000001', '1001003', '2021-05-09 06:52:09', '2024-05-14 14:12:54', '2024-05-10 22:24:54', 'F', 580, 860);
INSERT INTO `sys_user` VALUES (661, 'xiuyixu', '', '14108370340', '徐秀英', 0, '2011-03-22', 'http://image.simpsonj.net/Appliances', 78, '347452267274111', 'xuxiuyi@gmail.com', '1000001', '1001003', '2023-06-13 05:59:51', '2024-05-17 22:48:31', '2024-05-10 22:24:54', 'F', 289, 511);
INSERT INTO `sys_user` VALUES (662, 'yunxixie910', '', '2156738190', '谢云熙', 1, '2013-09-12', 'http://drive.liksun1211.cn/Books', 90, '341038919271492', 'xie07@gmail.com', '1000001', '1001003', '2007-05-14 22:47:26', '2024-05-28 07:27:13', '2024-05-10 22:24:55', 'T', 193, 733);
INSERT INTO `sys_user` VALUES (663, 'zz2', '', '19056587177', '曾震南', 1, '2012-04-13', 'http://www.pricnor1121.net/AppsGames', 64, '6237811539558124', 'zhennzeng@gmail.com', '1000001', '1001003', '2003-02-04 03:24:44', '2024-06-02 08:29:42', '2024-05-10 22:24:55', 'F', 489, 105);
INSERT INTO `sys_user` VALUES (664, 'ypa', '', '104532157', '潘宇宁', 1, '2020-04-11', 'https://auth.fong421.cn/Food', 70, '3555173839335344', 'panyunin@icloud.com', '1000001', '1001003', '2019-01-04 01:36:43', '2024-05-28 16:12:53', '2024-05-10 22:24:55', 'F', 661, 533);
INSERT INTO `sys_user` VALUES (665, 'wz10', '', '2199225785', '汪子异', 0, '2013-12-13', 'https://auth.bradleyharrison.biz/ClothingShoesandJewelry', 30, '4061738340435026', 'ziwang@icloud.com', '1000001', '1001003', '2008-10-23 09:37:01', '2024-05-14 20:29:47', '2024-05-10 22:24:55', 'F', 266, 106);
INSERT INTO `sys_user` VALUES (666, 'xiuhu1112', '', '1012285764', '胡秀英', 0, '2002-09-10', 'https://www.sakaya.info/BeautyPersonalCare', 54, '6253657261191892', 'xiuyingh@icloud.com', '1000001', '1001003', '2005-07-22 01:33:55', '2024-05-20 17:39:30', '2024-05-10 22:24:55', 'F', 471, 785);
INSERT INTO `sys_user` VALUES (667, 'szh', '', '13110860272', '沈致远', 1, '2017-01-26', 'https://video.waihanc2.org/HouseholdKitchenAppliances', 74, '3539666658955259', 'zhiyshen@hotmail.com', '1000001', '1001003', '2018-06-03 23:55:35', '2024-06-07 23:49:18', '2024-05-10 22:24:55', 'F', 511, 447);
INSERT INTO `sys_user` VALUES (668, 'shenzitao69', '', '18812509476', '沈子韬', 0, '2019-01-07', 'https://drive.alvarez711.xyz/CellPhonesAccessories', 23, '373820359099637', 'zitao56@outlook.com', '1000001', '1001003', '2022-03-15 23:46:36', '2024-05-15 20:13:31', '2024-05-10 22:24:55', 'T', 608, 865);
INSERT INTO `sys_user` VALUES (669, 'yizhiyuan', '', '7553122145', '尹致远', 0, '2019-02-05', 'https://video.wang2.cn/CenturionGardenOutdoor', 55, '5207936656397986', 'yin97@hotmail.com', '1000001', '1001003', '2011-02-17 00:57:50', '2024-06-10 01:00:30', '2024-05-10 22:24:55', 'T', 995, 745);
INSERT INTO `sys_user` VALUES (670, 'yjiang74', '', '1088218730', '姜云熙', 0, '2019-08-04', 'https://auth.fp323.net/ToolsHomeDecoration', 90, '4639778339889406', 'yunxijiang@gmail.com', '1000001', '1001003', '2006-10-24 12:34:14', '2024-05-23 20:27:23', '2024-05-10 22:24:55', 'T', 175, 888);
INSERT INTO `sys_user` VALUES (671, 'xiuyiyang517', '', '15400915845', '杨秀英', 1, '2012-07-08', 'http://image.yull.xyz/ArtsHandicraftsSewing', 43, '5322403680950082', 'xiuying6@gmail.com', '1000001', '1001003', '2001-05-28 12:11:55', '2024-05-18 10:37:15', '2024-05-10 22:24:55', 'T', 243, 881);
INSERT INTO `sys_user` VALUES (672, 'zhennan4', '', '204415942', '严震南', 0, '2003-01-20', 'https://auth.kelley3.org/SportsOutdoor', 35, '4986914710115512', 'zhennan4@hotmail.com', '1000001', '1001003', '2012-08-10 15:11:55', '2024-05-10 06:10:08', '2024-05-10 22:24:55', 'F', 425, 561);
INSERT INTO `sys_user` VALUES (673, 'zxia', '', '16850089190', '夏致远', 0, '2015-02-10', 'https://drive.chiyuenkwo.info/VideoGames', 40, '6230974995695306', 'zhiyuanxia@icloud.com', '1000001', '1001003', '2016-07-13 18:37:58', '2024-06-04 08:59:25', '2024-05-10 22:24:55', 'F', 457, 277);
INSERT INTO `sys_user` VALUES (674, 'cxiu6', '', '13941377000', '蔡秀英', 1, '2002-01-16', 'https://auth.tchristine725.com/CollectiblesFineArt', 27, '373188570642134', 'xcai16@outlook.com', '1000001', '1001003', '2018-02-04 10:09:18', '2024-05-30 05:48:50', '2024-05-10 22:24:55', 'F', 370, 743);
INSERT INTO `sys_user` VALUES (675, 'farui', '', '15449787970', '方睿', 1, '2022-04-24', 'http://video.simpsonellen.us/Baby', 83, '6259871467815238', 'rui4@mail.com', '1000001', '1001003', '2010-11-28 20:50:05', '2024-06-07 10:30:41', '2024-05-10 22:24:55', 'T', 88, 16);
INSERT INTO `sys_user` VALUES (676, 'linjialun10', '', '7553631619', '林嘉伦', 0, '2012-11-03', 'https://video.br49.us/ToolsHomeDecoration', 50, '4303151578953728', 'jiallin5@gmail.com', '1000001', '1001003', '2017-06-20 03:25:00', '2024-06-10 09:01:55', '2024-05-10 22:24:55', 'T', 856, 781);
INSERT INTO `sys_user` VALUES (677, 'zengjie', '', '16913747514', '曾杰宏', 1, '2017-02-22', 'https://image.marilyn1980.us/Baby', 83, '4196063351872738', 'jzeng58@gmail.com', '1000001', '1001003', '2013-02-09 20:48:29', '2024-06-05 13:30:03', '2024-05-10 22:24:55', 'F', 102, 665);
INSERT INTO `sys_user` VALUES (678, 'xiuyj6', '', '17588714581', '江秀英', 1, '2023-11-16', 'https://auth.du8.jp/Handcrafts', 80, '4365172086150669', 'xiuyijiang@outlook.com', '1000001', '1001003', '2023-04-07 11:54:43', '2024-05-13 04:28:30', '2024-05-10 22:24:55', 'F', 123, 54);
INSERT INTO `sys_user` VALUES (679, 'wezhiy', '', '19367099226', '魏致远', 1, '2013-08-28', 'https://video.wonwingfat.com/Handcrafts', 23, '6233626342309109', 'wz6@yahoo.com', '1000001', '1001003', '2022-11-22 22:34:10', '2024-06-07 18:52:19', '2024-05-10 22:24:55', 'F', 163, 125);
INSERT INTO `sys_user` VALUES (680, 'suanq', '', '2843942730', '苏安琪', 1, '2017-04-12', 'https://auth.chang3.net/BeautyPersonalCare', 56, '6231704467855636', 'su7@mail.com', '1000001', '1001003', '2012-08-14 22:30:48', '2024-05-24 12:27:35', '2024-05-10 22:24:55', 'T', 911, 752);
INSERT INTO `sys_user` VALUES (681, 'hayuning', '', '76990674419', '郝宇宁', 1, '2009-10-26', 'https://auth.qianqi2.jp/FilmSupplies', 59, '3564755882984983', 'hao1942@gmail.com', '1000001', '1001003', '2009-11-02 13:48:58', '2024-05-18 14:17:15', '2024-05-10 22:24:55', 'F', 46, 66);
INSERT INTO `sys_user` VALUES (682, 'lanyan', '', '17254757035', '杨岚', 1, '2023-10-22', 'https://auth.jijialun.org/CollectiblesFineArt', 99, '5525081184817919', 'lany@outlook.com', '1000001', '1001003', '2000-12-23 18:19:08', '2024-05-24 19:42:07', '2024-05-10 22:24:55', 'T', 199, 579);
INSERT INTO `sys_user` VALUES (683, 'shihan206', '', '2064465153', '徐詩涵', 0, '2007-06-14', 'https://auth.chuwin3.xyz/BaggageTravelEquipment', 69, '6279993686317022', 'shihxu@outlook.com', '1000001', '1001003', '2019-11-17 23:00:39', '2024-06-07 12:07:47', '2024-05-10 22:24:55', 'F', 523, 486);
INSERT INTO `sys_user` VALUES (684, 'chenzh3', '', '13673268886', '陈震南', 1, '2007-10-13', 'http://image.yuningf.xyz/ComputersElectronics', 43, '4566563394266188', 'chenz@gmail.com', '1000001', '1001003', '2007-09-02 02:59:24', '2024-05-26 06:13:40', '2024-05-10 22:24:55', 'F', 606, 644);
INSERT INTO `sys_user` VALUES (685, 'ziyan9', '', '16316621080', '杨子异', 0, '2004-02-15', 'http://auth.ishikawaren1999.info/CDsVinyl', 64, '3550177899283620', 'ziyya@icloud.com', '1000001', '1001003', '2014-08-06 12:02:49', '2024-05-18 03:26:49', '2024-05-10 22:24:55', 'F', 358, 708);
INSERT INTO `sys_user` VALUES (686, 'lanz', '', '18875141917', '朱岚', 0, '2012-09-10', 'https://drive.panwh.jp/Food', 15, '4129905072139619', 'zhu2@icloud.com', '1000001', '1001003', '2017-01-13 04:45:54', '2024-06-06 19:33:44', '2024-05-10 22:24:55', 'F', 552, 110);
INSERT INTO `sys_user` VALUES (687, 'yuningxia', '', '76024676304', '夏宇宁', 1, '2023-11-19', 'https://drive.hahokya4.net/BaggageTravelEquipment', 99, '3575976196893952', 'xiyun@icloud.com', '1000001', '1001003', '2009-09-09 20:02:26', '2024-05-12 06:07:48', '2024-05-10 22:24:55', 'T', 851, 579);
INSERT INTO `sys_user` VALUES (688, 'yuningshao', '', '217954459', '邵宇宁', 0, '2016-01-17', 'https://image.smith14.jp/SportsOutdoor', 21, '6215650861580306', 'sy45@gmail.com', '1000001', '1001003', '2022-11-11 06:47:03', '2024-05-27 17:18:28', '2024-05-10 22:24:55', 'T', 69, 828);
INSERT INTO `sys_user` VALUES (689, 'zzhenn2002', '', '2109971138', '朱震南', 1, '2008-07-16', 'http://video.nirosa4.co.jp/ToolsHomeDecoration', 79, '6287001413840686', 'zhuzhenn@mail.com', '1000001', '1001003', '2013-02-06 13:05:02', '2024-05-23 10:56:15', '2024-05-10 22:24:55', 'T', 948, 294);
INSERT INTO `sys_user` VALUES (690, 'zicao', '', '206381009', '曹子异', 0, '2015-02-19', 'https://auth.sheilabell915.cn/BaggageTravelEquipment', 60, '6290910100499975', 'zicao@mail.com', '1000001', '1001003', '2017-03-26 12:56:44', '2024-05-16 06:58:55', '2024-05-10 22:24:55', 'F', 624, 565);
INSERT INTO `sys_user` VALUES (691, 'yunxiduan', '', '2825261039', '段云熙', 1, '2019-06-05', 'http://image.tszchingt608.org/PetSupplies', 61, '343906525550012', 'duayunxi9@gmail.com', '1000001', '1001003', '2016-01-16 15:15:38', '2024-05-10 11:55:47', '2024-05-10 22:24:55', 'F', 971, 487);
INSERT INTO `sys_user` VALUES (692, 'ziyili', '', '14207712504', '黎子异', 1, '2010-11-06', 'http://drive.lau116.xyz/CollectiblesFineArt', 68, '341082412204600', 'ziyili@gmail.com', '1000001', '1001003', '2020-10-01 13:05:05', '2024-05-31 07:02:15', '2024-05-10 22:24:55', 'F', 144, 930);
INSERT INTO `sys_user` VALUES (693, 'anqiga', '', '19705174373', '高安琪', 1, '2003-09-20', 'https://auth.brbaker.net/SportsOutdoor', 86, '5445479884617299', 'ganqi@icloud.com', '1000001', '1001003', '2003-08-31 18:34:32', '2024-06-02 22:26:37', '2024-05-10 22:24:55', 'T', 346, 936);
INSERT INTO `sys_user` VALUES (694, 'jiang619', '', '100122653', '江璐', 0, '2012-06-18', 'http://image.taylorva10.co.jp/FilmSupplies', 47, '341182630352122', 'jlu@outlook.com', '1000001', '1001003', '2015-05-18 19:58:46', '2024-06-04 14:22:07', '2024-05-10 22:24:55', 'F', 351, 236);
INSERT INTO `sys_user` VALUES (695, 'lanyi', '', '7692971835', '尹岚', 1, '2014-06-17', 'http://drive.wer8.net/AutomotivePartsAccessories', 28, '5286338483238695', 'yinlan@mail.com', '1000001', '1001003', '2005-05-30 11:16:43', '2024-06-03 06:18:43', '2024-05-10 22:24:55', 'T', 172, 110);
INSERT INTO `sys_user` VALUES (696, 'luzo6', '', '2816220483', '邹璐', 1, '2012-01-13', 'https://auth.hikarta706.org/CollectiblesFineArt', 27, '347306257833148', 'luzou@hotmail.com', '1000001', '1001003', '2011-09-14 11:11:52', '2024-06-07 19:21:54', '2024-05-10 22:24:55', 'T', 232, 699);
INSERT INTO `sys_user` VALUES (697, 'azhe', '', '201403519', '郑安琪', 1, '2019-06-07', 'https://auth.suznanami8.jp/Books', 52, '4694440855769825', 'anzhe@outlook.com', '1000001', '1001003', '2010-04-25 05:20:47', '2024-06-05 22:06:47', '2024-05-10 22:24:55', 'T', 382, 406);
INSERT INTO `sys_user` VALUES (698, 'zitaocu', '', '17805019993', '崔子韬', 1, '2017-06-15', 'https://www.chpaul.cn/CDsVinyl', 93, '378318346001068', 'cui5@mail.com', '1000001', '1001003', '2020-07-02 09:12:56', '2024-05-20 23:49:28', '2024-05-10 22:24:55', 'T', 474, 1);
INSERT INTO `sys_user` VALUES (699, 'hj728', '', '14569002435', '何嘉伦', 0, '2010-09-17', 'https://image.hiog.com/IndustrialScientificSupplies', 42, '3582708430173870', 'jialunhe@yahoo.com', '1000001', '1001003', '2014-01-16 06:00:05', '2024-06-01 20:49:45', '2024-05-10 22:24:55', 'T', 150, 766);
INSERT INTO `sys_user` VALUES (700, 'zhongj', '', '281367024', '钟杰宏', 1, '2016-06-10', 'https://video.kimunan1972.xyz/PetSupplies', 45, '3560043335886335', 'jiehongzh@gmail.com', '1000001', '1001003', '2014-06-07 04:32:17', '2024-05-13 21:23:25', '2024-05-10 22:24:55', 'F', 567, 494);
INSERT INTO `sys_user` VALUES (701, 'zitaoshao11', '', '17952780873', '邵子韬', 0, '2009-11-13', 'https://auth.mev2.co.jp/PetSupplies', 97, '344380029202620', 'shaozitao@gmail.com', '1000001', '1001003', '2016-04-13 09:36:25', '2024-06-05 08:09:45', '2024-05-10 22:24:55', 'T', 80, 255);
INSERT INTO `sys_user` VALUES (702, 'fzhiyu', '', '7607589110', '冯致远', 0, '2012-12-29', 'https://drive.chuws9.biz/ToolsHomeDecoration', 76, '3555876358657878', 'fengz@mail.com', '1000001', '1001003', '2006-04-09 21:13:32', '2024-05-17 11:50:52', '2024-05-10 22:24:55', 'F', 272, 224);
INSERT INTO `sys_user` VALUES (703, 'fan', '', '16647195607', '方安琪', 1, '2005-09-20', 'http://drive.fuwing822.info/HouseholdKitchenAppliances', 40, '4166731704343799', 'anqifang@yahoo.com', '1000001', '1001003', '2001-06-26 06:44:44', '2024-06-01 14:02:16', '2024-05-10 22:24:55', 'T', 388, 356);
INSERT INTO `sys_user` VALUES (704, 'wulan930', '', '76057716141', '吴岚', 0, '2001-06-12', 'http://image.laicy3.jp/BeautyPersonalCare', 72, '4337187825630081', 'lanwu1@gmail.com', '1000001', '1001003', '2002-11-03 19:45:07', '2024-05-12 10:04:55', '2024-05-10 22:24:55', 'T', 288, 796);
INSERT INTO `sys_user` VALUES (705, 'yufan802', '', '17029985534', '范云熙', 1, '2023-09-08', 'http://www.ngchiehlun2.cn/ToysGames', 48, '5504784507964095', 'fyu905@hotmail.com', '1000001', '1001003', '2006-08-01 22:57:35', '2024-05-21 23:37:33', '2024-05-10 22:24:55', 'F', 503, 784);
INSERT INTO `sys_user` VALUES (706, 'yche', '', '7558225992', '陈云熙', 1, '2022-04-27', 'https://auth.kwokkuen927.xyz/SportsOutdoor', 96, '5491275516822916', 'ychen1208@gmail.com', '1000001', '1001003', '2022-09-22 19:42:01', '2024-06-05 11:19:40', '2024-05-10 22:24:55', 'F', 30, 483);
INSERT INTO `sys_user` VALUES (707, 'zhrui05', '', '17868052766', '周睿', 0, '2005-06-26', 'http://drive.lz6.cn/VideoGames', 27, '5043922951724882', 'zhorui@gmail.com', '1000001', '1001003', '2012-07-15 13:29:47', '2024-05-30 10:15:09', '2024-05-10 22:24:55', 'F', 705, 439);
INSERT INTO `sys_user` VALUES (708, 'zitli', '', '14652362780', '黎子韬', 1, '2002-06-23', 'http://video.kathy3.org/BeautyPersonalCare', 81, '4162342224838138', 'zitaoli04@outlook.com', '1000001', '1001003', '2006-03-10 02:06:19', '2024-06-05 13:39:15', '2024-05-10 22:24:55', 'T', 857, 467);
INSERT INTO `sys_user` VALUES (709, 'lan53', '', '13577452917', '戴岚', 0, '2016-01-29', 'http://image.moreno5.co.jp/MusicalInstrument', 73, '370061693462701', 'ladai@mail.com', '1000001', '1001003', '2022-11-02 15:03:22', '2024-05-11 22:44:10', '2024-05-10 22:24:55', 'T', 486, 326);
INSERT INTO `sys_user` VALUES (710, 'jx2', '', '16491067908', '姜晓明', 1, '2022-01-26', 'https://drive.lu926.info/CellPhonesAccessories', 59, '5182590760576388', 'xiaoming1028@mail.com', '1000001', '1001003', '2019-01-14 18:48:26', '2024-05-27 20:56:53', '2024-05-10 22:24:55', 'F', 485, 344);
INSERT INTO `sys_user` VALUES (711, 'fenxiaoming', '', '16232286753', '冯晓明', 1, '2000-07-25', 'http://drive.hazmaruyama.cn/BeautyPersonalCare', 23, '371894280941805', 'fxiaoming@outlook.com', '1000001', '1001003', '2015-12-08 06:18:11', '2024-05-27 14:51:59', '2024-05-10 22:24:55', 'F', 40, 835);
INSERT INTO `sys_user` VALUES (712, 'luzhiyuan', '', '1069653607', '陆致远', 0, '2022-03-01', 'https://auth.atakagi309.info/HouseholdKitchenAppliances', 74, '3568204717032557', 'luz3@outlook.com', '1000001', '1001003', '2020-07-17 04:14:09', '2024-05-28 21:14:56', '2024-05-10 22:24:55', 'F', 536, 609);
INSERT INTO `sys_user` VALUES (713, 'yunxiqian', '', '7696214037', '钱云熙', 1, '2012-09-09', 'http://www.momokasakurai.cn/CenturionGardenOutdoor', 56, '6268857662142691', 'yunxi6@yahoo.com', '1000001', '1001003', '2022-07-25 22:35:05', '2024-05-16 19:39:55', '2024-05-10 22:24:55', 'T', 15, 415);
INSERT INTO `sys_user` VALUES (714, 'zy1114', '', '19844326526', '杨子韬', 0, '2010-12-03', 'https://auth.dennni804.org/FilmSupplies', 46, '375764978088316', 'zitaoyang@mail.com', '1000001', '1001003', '2022-01-29 17:42:50', '2024-05-31 10:59:33', '2024-05-10 22:24:55', 'T', 883, 794);
INSERT INTO `sys_user` VALUES (715, 'jiehya', '', '203487840', '阎杰宏', 0, '2018-05-03', 'https://image.rikugoto10.com/Appliances', 80, '347696984033206', 'yan7@gmail.com', '1000001', '1001003', '2003-09-19 07:01:37', '2024-06-02 02:54:08', '2024-05-10 22:24:55', 'T', 613, 36);
INSERT INTO `sys_user` VALUES (716, 'slian', '', '14116586081', '梁詩涵', 0, '2004-12-28', 'https://image.petergutierrez.com/CenturionGardenOutdoor', 39, '4994873026802256', 'shihan9@icloud.com', '1000001', '1001003', '2000-10-30 15:58:34', '2024-05-12 20:16:44', '2024-05-10 22:24:55', 'T', 843, 289);
INSERT INTO `sys_user` VALUES (717, 'jialun1982', '', '76971441955', '刘嘉伦', 0, '2018-08-28', 'https://image.kellmar.info/IndustrialScientificSupplies', 84, '6243040003820768', 'liujial08@gmail.com', '1000001', '1001003', '2013-04-15 14:56:21', '2024-06-09 22:09:46', '2024-05-10 22:24:55', 'T', 680, 740);
INSERT INTO `sys_user` VALUES (718, 'lant', '', '16733116189', '陶岚', 0, '2005-09-06', 'https://auth.hashimoto802.info/VideoGames', 100, '3550442906996766', 'taol1949@hotmail.com', '1000001', '1001003', '2015-07-19 06:35:30', '2024-05-18 01:17:13', '2024-05-10 22:24:55', 'F', 132, 610);
INSERT INTO `sys_user` VALUES (719, 'qinxiuyi8', '', '19738011442', '秦秀英', 0, '2013-05-19', 'https://www.matsudaeita.org/BaggageTravelEquipment', 23, '3532536822193276', 'qinxiuyi44@outlook.com', '1000001', '1001003', '2014-08-05 20:18:16', '2024-05-21 13:30:58', '2024-05-10 22:24:55', 'F', 258, 735);
INSERT INTO `sys_user` VALUES (720, 'fuxiao7', '', '17073396062', '傅晓明', 1, '2005-10-28', 'http://image.xiuyingsh.org/Books', 36, '6222919915153928', 'xiaoming620@icloud.com', '1000001', '1001003', '2020-07-13 20:36:22', '2024-06-07 20:31:12', '2024-05-10 22:24:55', 'F', 246, 759);
INSERT INTO `sys_user` VALUES (721, 'zhenh', '', '100006838', '韩震南', 0, '2005-12-23', 'https://auth.shihanpan.com/CellPhonesAccessories', 73, '5278828153781925', 'hazhennan67@gmail.com', '1000001', '1001003', '2010-02-05 23:52:36', '2024-05-30 12:36:03', '2024-05-10 22:24:55', 'F', 471, 361);
INSERT INTO `sys_user` VALUES (722, 'xj3', '', '2867850993', '谢杰宏', 0, '2010-07-02', 'https://www.yuwf6.jp/SportsOutdoor', 35, '347529457145183', 'xiejiehong@gmail.com', '1000001', '1001003', '2017-02-26 11:29:03', '2024-05-18 23:28:51', '2024-05-10 22:24:55', 'F', 261, 756);
INSERT INTO `sys_user` VALUES (723, 'shihm', '', '7601967501', '马詩涵', 0, '2010-10-26', 'http://www.fx61.jp/Handcrafts', 19, '4556733593694397', 'shihanma@icloud.com', '1000001', '1001003', '2011-01-27 16:19:08', '2024-05-11 05:53:23', '2024-05-10 22:24:55', 'F', 653, 51);
INSERT INTO `sys_user` VALUES (724, 'jiehozh89', '', '75539944355', '朱杰宏', 0, '2006-10-13', 'https://video.dolan4.us/HealthBabyCare', 29, '6269089440077580', 'zjieh@icloud.com', '1000001', '1001003', '2012-09-24 01:12:16', '2024-05-22 09:48:07', '2024-05-10 22:24:55', 'F', 609, 894);
INSERT INTO `sys_user` VALUES (725, 'lansong', '', '102583943', '宋岚', 1, '2007-05-26', 'https://drive.kafai46.xyz/Beauty', 89, '4641786682225413', 'lan61@gmail.com', '1000001', '1001003', '2016-06-30 17:42:43', '2024-06-05 12:39:57', '2024-05-10 22:24:55', 'T', 491, 92);
INSERT INTO `sys_user` VALUES (726, 'zhelu4', '', '2137602272', '吕震南', 1, '2024-03-31', 'https://image.rohu.net/AppsGames', 29, '6235623919537541', 'zhenlu6@gmail.com', '1000001', '1001003', '2001-07-10 09:06:43', '2024-05-28 11:55:24', '2024-05-10 22:24:55', 'T', 809, 628);
INSERT INTO `sys_user` VALUES (727, 'xiaomingfu', '', '285129506', '傅晓明', 1, '2009-08-14', 'https://www.webvirginia.net/PetSupplies', 18, '6213774622265551', 'xiaoming1@icloud.com', '1000001', '1001003', '2006-03-22 11:34:28', '2024-05-16 02:29:25', '2024-05-10 22:24:55', 'T', 974, 219);
INSERT INTO `sys_user` VALUES (728, 'liaoru827', '', '16551954162', '廖睿', 1, '2004-08-19', 'https://image.sakura626.net/ArtsHandicraftsSewing', 31, '346333616977151', 'liaorui@gmail.com', '1000001', '1001003', '2015-12-09 08:48:42', '2024-05-18 22:06:01', '2024-05-10 22:24:55', 'T', 449, 533);
INSERT INTO `sys_user` VALUES (729, 'lu611', '', '17005792714', '陆宇宁', 0, '2001-09-05', 'https://video.nmio.info/IndustrialScientificSupplies', 54, '5194467707955239', 'lyuning@icloud.com', '1000001', '1001003', '2016-05-21 20:23:04', '2024-05-20 07:08:10', '2024-05-10 22:24:55', 'T', 288, 783);
INSERT INTO `sys_user` VALUES (730, 'lanyu1011', '', '2170140470', '余岚', 0, '2011-05-29', 'https://video.jimdonal625.us/FilmSupplies', 73, '5286655167502777', 'yulan@hotmail.com', '1000001', '1001003', '2022-03-12 08:09:39', '2024-05-11 13:14:28', '2024-05-10 22:24:55', 'T', 614, 952);
INSERT INTO `sys_user` VALUES (731, 'mejialun327', '', '2110205101', '孟嘉伦', 1, '2000-01-28', 'http://drive.xiaoming1024.jp/Food', 19, '6244078965885766', 'jialumeng@hotmail.com', '1000001', '1001003', '2015-05-31 05:11:21', '2024-05-15 08:39:24', '2024-05-10 22:24:55', 'T', 607, 800);
INSERT INTO `sys_user` VALUES (732, 'zxi7', '', '13861287367', '夏子韬', 1, '2022-09-23', 'https://video.wingfattang9.net/Others', 45, '5324777156900137', 'zitaoxia@gmail.com', '1000001', '1001003', '2001-08-07 22:42:03', '2024-05-27 18:14:17', '2024-05-10 22:24:55', 'T', 497, 696);
INSERT INTO `sys_user` VALUES (733, 'zhgong', '', '18467123945', '龚震南', 1, '2014-11-02', 'http://video.chang4.jp/Others', 16, '3556070566526310', 'zhennangong906@yahoo.com', '1000001', '1001003', '2016-08-29 00:49:26', '2024-05-11 06:36:29', '2024-05-10 22:24:55', 'F', 463, 99);
INSERT INTO `sys_user` VALUES (734, 'yunxli', '', '2195208558', '李云熙', 1, '2003-01-14', 'https://drive.shish1102.biz/Appliances', 83, '5378646034237415', 'yunxli803@mail.com', '1000001', '1001003', '2003-10-03 07:28:54', '2024-06-03 22:09:32', '2024-05-10 22:24:55', 'T', 40, 384);
INSERT INTO `sys_user` VALUES (735, 'yunxig', '', '15226305560', '顾云熙', 0, '2018-07-11', 'http://auth.virginiaphi930.biz/ComputersElectronics', 26, '5302837883002247', 'guyunx9@outlook.com', '1000001', '1001003', '2023-04-05 16:56:30', '2024-06-07 02:16:18', '2024-05-10 22:24:55', 'T', 250, 880);
INSERT INTO `sys_user` VALUES (736, 'liang920', '', '76945792430', '梁震南', 0, '2021-04-17', 'https://www.minatona.cn/HouseholdKitchenAppliances', 92, '4350101617942986', 'zhlia5@outlook.com', '1000001', '1001003', '2012-06-23 01:54:11', '2024-05-22 16:54:25', '2024-05-10 22:24:55', 'F', 775, 207);
INSERT INTO `sys_user` VALUES (737, 'dziyi03', '', '19362866644', '戴子异', 1, '2007-12-07', 'http://drive.payne2.us/VideoGames', 32, '6287992781052245', 'dazi@gmail.com', '1000001', '1001003', '2014-05-08 03:38:07', '2024-05-28 16:42:36', '2024-05-10 22:24:55', 'F', 203, 297);
INSERT INTO `sys_user` VALUES (738, 'slan', '', '16538666726', '孙岚', 0, '2009-02-05', 'https://video.ayano1013.us/SportsOutdoor', 80, '4106212956803189', 'lan214@gmail.com', '1000001', '1001003', '2022-12-12 18:10:49', '2024-05-26 21:35:11', '2024-05-10 22:24:55', 'F', 662, 578);
INSERT INTO `sys_user` VALUES (739, 'houzhi', '', '13437548805', '侯致远', 0, '2007-08-02', 'http://www.haoa.info/MusicalInstrument', 79, '343119067342620', 'houzhiyuan@gmail.com', '1000001', '1001003', '2023-04-24 06:37:30', '2024-06-02 08:41:14', '2024-05-10 22:24:55', 'T', 434, 543);
INSERT INTO `sys_user` VALUES (740, 'tshihan1974', '', '14380472517', '谭詩涵', 1, '2015-01-06', 'https://www.hazel729.us/Appliances', 85, '6251837357407459', 'tashihan@icloud.com', '1000001', '1001003', '2014-04-06 15:48:24', '2024-05-12 08:17:47', '2024-05-10 22:24:55', 'T', 530, 842);
INSERT INTO `sys_user` VALUES (741, 'suxiaoming', '', '2185606583', '苏晓明', 0, '2010-07-26', 'https://video.tcar1981.cn/MusicalInstrument', 66, '3543514961375936', 'xsu629@gmail.com', '1000001', '1001003', '2011-07-04 03:39:04', '2024-06-07 00:04:41', '2024-05-10 22:24:55', 'F', 896, 228);
INSERT INTO `sys_user` VALUES (742, 'czitao4', '', '13627913314', '程子韬', 0, '2001-12-25', 'https://www.kaarai5.us/Food', 57, '3535335702670702', 'chengzit@yahoo.com', '1000001', '1001003', '2013-03-19 22:44:52', '2024-05-12 00:28:36', '2024-05-10 22:24:55', 'T', 538, 493);
INSERT INTO `sys_user` VALUES (743, 'yunxiqi', '', '76009613674', '秦云熙', 0, '2015-03-26', 'http://www.chiuwailiao605.com/BaggageTravelEquipment', 20, '3583718669268381', 'yqin08@yahoo.com', '1000001', '1001003', '2010-03-31 20:20:55', '2024-05-10 15:48:35', '2024-05-10 22:24:55', 'F', 949, 639);
INSERT INTO `sys_user` VALUES (744, 'qiurui8', '', '15670710812', '邱睿', 0, '2011-02-23', 'https://www.chiba48.co.jp/CDsVinyl', 88, '3535723642226406', 'qiurui@icloud.com', '1000001', '1001003', '2008-07-09 14:20:41', '2024-05-25 07:22:06', '2024-05-10 22:24:55', 'T', 553, 591);
INSERT INTO `sys_user` VALUES (745, 'jlu', '', '17066672726', '卢嘉伦', 1, '2003-11-27', 'https://www.yuziyi.info/VideoGames', 74, '349463883987547', 'lujial@outlook.com', '1000001', '1001003', '2000-11-05 03:53:14', '2024-05-17 13:13:34', '2024-05-10 22:24:55', 'T', 448, 796);
INSERT INTO `sys_user` VALUES (746, 'zitaojiang10', '', '14824948987', '江子韬', 1, '2011-02-16', 'https://www.ls119.net/Food', 36, '343057888820681', 'zitajiang@icloud.com', '1000001', '1001003', '2006-11-01 12:23:22', '2024-06-04 02:34:29', '2024-05-10 22:24:55', 'T', 205, 784);
INSERT INTO `sys_user` VALUES (747, 'xila42', '', '75583801804', '夏岚', 0, '2013-07-13', 'https://image.sikki.us/HealthBabyCare', 90, '6291976958248748', 'lan827@mail.com', '1000001', '1001003', '2015-06-13 12:01:06', '2024-06-06 05:52:27', '2024-05-10 22:24:55', 'T', 862, 126);
INSERT INTO `sys_user` VALUES (748, 'lanc', '', '18256199253', '程岚', 0, '2010-06-14', 'https://auth.cjialun.jp/Baby', 24, '341771876369327', 'chengla@mail.com', '1000001', '1001003', '2015-04-30 18:45:27', '2024-05-20 03:20:16', '2024-05-10 22:24:55', 'T', 145, 781);
INSERT INTO `sys_user` VALUES (749, 'rfu', '', '14649805308', '傅睿', 0, '2016-03-09', 'https://auth.seit6.cn/BeautyPersonalCare', 25, '5040083728288652', 'fu62@gmail.com', '1000001', '1001003', '2011-01-24 18:23:17', '2024-05-11 23:34:36', '2024-05-10 22:24:55', 'F', 551, 977);
INSERT INTO `sys_user` VALUES (750, 'lixiaoming', '', '18806640060', '黎晓明', 1, '2024-02-17', 'http://drive.florencepay1.co.jp/HouseholdKitchenAppliances', 68, '6246567548044709', 'lixiaoming13@gmail.com', '1000001', '1001003', '2021-01-28 10:36:14', '2024-05-10 20:09:39', '2024-05-10 22:24:55', 'F', 895, 947);
INSERT INTO `sys_user` VALUES (751, 'yunxigu', '', '217214660', '顾云熙', 1, '2012-10-12', 'https://video.lo9.jp/HouseholdKitchenAppliances', 34, '6288771414062543', 'yunxi3@hotmail.com', '1000001', '1001003', '2008-10-07 10:51:47', '2024-05-30 14:53:14', '2024-05-10 22:24:55', 'T', 623, 121);
INSERT INTO `sys_user` VALUES (752, 'jiang6', '', '18258912992', '姜嘉伦', 0, '2012-01-09', 'https://image.rodneythomas.jp/ToolsHomeDecoration', 78, '6294891493013225', 'jiangjialu@outlook.com', '1000001', '1001003', '2000-08-19 00:38:35', '2024-05-18 01:50:42', '2024-05-10 22:24:55', 'F', 713, 107);
INSERT INTO `sys_user` VALUES (753, 'jianyu15', '', '7555787611', '姜宇宁', 0, '2017-05-01', 'http://auth.grifs.jp/Handcrafts', 30, '3539298258202765', 'yunijiang@gmail.com', '1000001', '1001003', '2021-06-19 23:23:13', '2024-05-31 13:54:31', '2024-05-10 22:24:55', 'F', 372, 700);
INSERT INTO `sys_user` VALUES (754, 'yish', '', '282449687', '尹詩涵', 0, '2014-08-22', 'http://www.sahana.com/Books', 38, '371487012635637', 'syi7@mail.com', '1000001', '1001003', '2022-08-28 00:04:22', '2024-05-12 10:10:52', '2024-05-10 22:24:55', 'F', 55, 432);
INSERT INTO `sys_user` VALUES (755, 'daxi2016', '', '200615343', '戴秀英', 0, '2013-08-09', 'http://video.maik415.xyz/AutomotivePartsAccessories', 67, '3546418061569009', 'daxiuying@gmail.com', '1000001', '1001003', '2017-09-08 12:02:18', '2024-05-20 00:18:53', '2024-05-10 22:24:55', 'T', 191, 21);
INSERT INTO `sys_user` VALUES (756, 'xl919', '', '19614246361', '夏岚', 1, '2003-03-20', 'https://image.lews521.info/MusicalInstrument', 18, '340213537683573', 'xia78@yahoo.com', '1000001', '1001003', '2020-06-25 01:03:59', '2024-05-17 20:53:17', '2024-05-10 22:24:55', 'T', 673, 191);
INSERT INTO `sys_user` VALUES (757, 'lajin', '', '76915689464', '金岚', 1, '2006-11-18', 'http://www.eugwright.jp/Others', 62, '6261929013188991', 'jinlan@gmail.com', '1000001', '1001003', '2013-06-11 02:25:53', '2024-05-10 04:07:15', '2024-05-10 22:24:55', 'F', 718, 344);
INSERT INTO `sys_user` VALUES (758, 'xiaoji', '', '14326512874', '萧杰宏', 0, '2010-05-26', 'https://image.kentari.info/Others', 57, '3536391663115186', 'jiehxiao7@icloud.com', '1000001', '1001003', '2001-04-12 05:58:50', '2024-05-14 00:43:03', '2024-05-10 22:24:55', 'F', 617, 566);
INSERT INTO `sys_user` VALUES (759, 'shihanjin9', '', '7557956292', '金詩涵', 0, '2015-04-15', 'http://auth.minatoishid.cn/VideoGames', 91, '3575123544475691', 'jshihan@gmail.com', '1000001', '1001003', '2016-12-08 15:03:39', '2024-06-02 17:53:14', '2024-05-10 22:24:55', 'T', 977, 367);
INSERT INTO `sys_user` VALUES (760, 'hz1006', '', '15434338192', '贺子异', 1, '2019-05-14', 'https://drive.sylvromer821.co.jp/PetSupplies', 16, '376648570674426', 'ziyi1119@hotmail.com', '1000001', '1001003', '2019-08-09 14:28:05', '2024-05-26 14:37:38', '2024-05-10 22:24:55', 'F', 483, 424);
INSERT INTO `sys_user` VALUES (761, 'dzitao', '', '282721897', '董子韬', 1, '2021-04-04', 'https://auth.ritaphill1.co.jp/Books', 81, '4707527001446566', 'dongzi@gmail.com', '1000001', '1001003', '2015-03-05 01:52:02', '2024-05-26 21:46:20', '2024-05-10 22:24:55', 'T', 633, 431);
INSERT INTO `sys_user` VALUES (762, 'cheng9', '', '15113938593', '程睿', 0, '2003-08-07', 'http://video.ziz.cn/Baby', 86, '6236017608514953', 'cher1973@gmail.com', '1000001', '1001003', '2007-11-03 08:29:37', '2024-05-11 08:32:24', '2024-05-10 22:24:55', 'T', 717, 458);
INSERT INTO `sys_user` VALUES (763, 'chlan', '', '13593837582', '常岚', 1, '2013-06-22', 'https://www.zhiyuliu.xyz/FilmSupplies', 99, '6223725642059810', 'lan9@icloud.com', '1000001', '1001003', '2007-02-19 08:15:22', '2024-05-22 00:01:34', '2024-05-10 22:24:55', 'T', 558, 595);
INSERT INTO `sys_user` VALUES (764, 'kongzita', '', '17846812083', '孔子韬', 1, '2019-05-28', 'http://www.zhang8.com/Appliances', 40, '342467734371089', 'kong56@gmail.com', '1000001', '1001003', '2018-04-06 14:49:09', '2024-06-10 02:58:27', '2024-05-10 22:24:55', 'F', 345, 276);
INSERT INTO `sys_user` VALUES (765, 'zhennancao1206', '', '76962324552', '曹震南', 0, '2023-01-02', 'https://auth.fsk.org/ComputersElectronics', 58, '379425356468472', 'cazh94@outlook.com', '1000001', '1001003', '2014-09-14 12:24:06', '2024-05-16 13:36:27', '2024-05-10 22:24:55', 'F', 592, 15);
INSERT INTO `sys_user` VALUES (766, 'ziwei1971', '', '2106654195', '魏子异', 1, '2001-06-29', 'http://auth.konzitao5.xyz/BeautyPersonalCare', 17, '6297813231950309', 'ziyiw@gmail.com', '1000001', '1001003', '2005-08-27 09:34:29', '2024-06-09 00:27:01', '2024-05-10 22:24:55', 'T', 734, 291);
INSERT INTO `sys_user` VALUES (767, 'anqi3', '', '7699181365', '彭安琪', 1, '2009-07-30', 'http://drive.kuht9.com/FilmSupplies', 25, '6206642474292958', 'anqp7@outlook.com', '1000001', '1001003', '2007-02-16 10:03:31', '2024-05-16 04:04:17', '2024-05-10 22:24:55', 'F', 913, 332);
INSERT INTO `sys_user` VALUES (768, 'yashihan', '', '14105552309', '严詩涵', 1, '2014-04-23', 'https://auth.kmfu.org/ClothingShoesandJewelry', 100, '6231631498019488', 'shiyan@mail.com', '1000001', '1001003', '2004-03-01 18:09:50', '2024-05-17 06:52:56', '2024-05-10 22:24:55', 'T', 981, 422);
INSERT INTO `sys_user` VALUES (769, 'chesh', '', '18180595911', '陈詩涵', 0, '2023-03-25', 'https://drive.kcl.com/Food', 86, '3560927244293080', 'shihanchen@outlook.com', '1000001', '1001003', '2011-08-18 15:59:15', '2024-06-03 09:19:27', '2024-05-10 22:24:55', 'T', 416, 220);
INSERT INTO `sys_user` VALUES (770, 'duzh1945', '', '13205384630', '杜震南', 1, '2009-03-25', 'https://drive.blackni13.cn/CenturionGardenOutdoor', 90, '5482703628405735', 'duz4@icloud.com', '1000001', '1001003', '2021-01-02 21:53:17', '2024-05-14 23:24:00', '2024-05-10 22:24:55', 'F', 989, 663);
INSERT INTO `sys_user` VALUES (771, 'jiehox', '', '17264124242', '熊杰宏', 0, '2015-05-29', 'https://drive.wadahana1112.us/MusicalInstrument', 23, '376621806440045', 'jiehoxiong@icloud.com', '1000001', '1001003', '2018-03-06 07:39:42', '2024-05-27 03:57:38', '2024-05-10 22:24:55', 'F', 925, 833);
INSERT INTO `sys_user` VALUES (772, 'llan122', '', '203168535', '刘岚', 1, '2003-06-14', 'http://www.muichiming.co.jp/ClothingShoesandJewelry', 43, '3548236268149823', 'lil208@outlook.com', '1000001', '1001003', '2021-03-29 22:38:39', '2024-05-26 13:45:07', '2024-05-10 22:24:55', 'T', 237, 552);
INSERT INTO `sys_user` VALUES (773, 'czhiyuan', '', '19889359285', '崔致远', 1, '2003-03-01', 'https://image.majuanita.co.jp/HouseholdKitchenAppliances', 59, '5296207478417754', 'cui1964@outlook.com', '1000001', '1001003', '2019-08-15 11:53:13', '2024-05-31 07:48:04', '2024-05-10 22:24:55', 'F', 465, 353);
INSERT INTO `sys_user` VALUES (774, 'lansu', '', '13360755449', '苏岚', 1, '2005-04-10', 'http://video.yamliksu59.co.jp/HealthBabyCare', 64, '3563744102808314', 'sul@gmail.com', '1000001', '1001003', '2023-01-12 19:32:43', '2024-05-24 13:22:09', '2024-05-10 22:24:55', 'T', 922, 400);
INSERT INTO `sys_user` VALUES (775, 'xiuyingguo', '', '13391977188', '郭秀英', 1, '2000-06-14', 'https://www.dara1992.biz/CollectiblesFineArt', 20, '374661739775523', 'guo51@yahoo.com', '1000001', '1001003', '2011-05-16 18:19:05', '2024-06-10 00:54:37', '2024-05-10 22:24:55', 'T', 690, 605);
INSERT INTO `sys_user` VALUES (776, 'jiehoha', '', '18416059861', '郝杰宏', 1, '2018-08-15', 'https://www.wokm.org/ArtsHandicraftsSewing', 40, '3557815768219679', 'hjieho9@gmail.com', '1000001', '1001003', '2008-03-18 15:06:51', '2024-06-02 18:10:18', '2024-05-10 22:24:55', 'F', 912, 944);
INSERT INTO `sys_user` VALUES (777, 'lugu51', '', '7559666781', '顾璐', 0, '2022-10-21', 'https://video.saimit13.co.jp/CDsVinyl', 87, '4420193692189233', 'gulu@hotmail.com', '1000001', '1001003', '2022-02-12 15:24:39', '2024-05-16 22:26:56', '2024-05-10 22:24:55', 'F', 189, 893);
INSERT INTO `sys_user` VALUES (778, 'zhiwu10', '', '15925988405', '武致远', 1, '2008-10-31', 'http://www.rinwatanabe.co.jp/IndustrialScientificSupplies', 21, '5517029536357183', 'zhiyuanwu@icloud.com', '1000001', '1001003', '2021-01-30 02:36:26', '2024-05-27 20:27:15', '2024-05-10 22:24:55', 'F', 520, 198);
INSERT INTO `sys_user` VALUES (779, 'jk611', '', '75584295071', '孔嘉伦', 1, '2004-08-20', 'https://www.sumwing520.cn/Handcrafts', 55, '4039380859421003', 'jialun83@gmail.com', '1000001', '1001003', '2014-10-22 07:58:26', '2024-05-13 10:50:50', '2024-05-10 22:24:55', 'T', 41, 999);
INSERT INTO `sys_user` VALUES (780, 'huangz', '', '17147183391', '黄子韬', 0, '2008-03-07', 'http://image.zxiu.net/AutomotivePartsAccessories', 17, '5129066020012759', 'huanzitao1125@outlook.com', '1000001', '1001003', '2016-08-24 07:13:08', '2024-05-31 12:55:15', '2024-05-10 22:24:55', 'F', 35, 330);
INSERT INTO `sys_user` VALUES (781, 'wei78', '', '19744337393', '韦璐', 1, '2006-10-30', 'http://www.cmku.org/VideoGames', 84, '5483120134895128', 'luw@yahoo.com', '1000001', '1001003', '2021-12-03 02:20:59', '2024-05-25 16:34:41', '2024-05-10 22:24:55', 'F', 912, 153);
INSERT INTO `sys_user` VALUES (782, 'ziguo', '', '14606945620', '郭子异', 1, '2013-10-24', 'http://auth.zhlu4.jp/CollectiblesFineArt', 30, '6296066258781930', 'ziyi1003@icloud.com', '1000001', '1001003', '2015-10-21 17:47:33', '2024-05-11 22:12:18', '2024-05-10 22:24:55', 'F', 690, 515);
INSERT INTO `sys_user` VALUES (783, 'yangsh', '', '13922974026', '杨詩涵', 1, '2012-08-21', 'https://drive.richard731.us/ToysGames', 66, '4476919850466655', 'shihan4@outlook.com', '1000001', '1001003', '2017-12-13 10:57:10', '2024-06-02 11:23:15', '2024-05-10 22:24:55', 'T', 944, 47);
INSERT INTO `sys_user` VALUES (784, 'qzhi1947', '', '17904817418', '钱致远', 1, '2021-01-19', 'https://video.waihankoo4.xyz/ToolsHomeDecoration', 85, '371910130485361', 'qzh210@outlook.com', '1000001', '1001003', '2015-11-27 04:59:56', '2024-05-28 03:15:00', '2024-05-10 22:24:55', 'T', 42, 196);
INSERT INTO `sys_user` VALUES (785, 'dingyunxi9', '', '14396999902', '丁云熙', 1, '2013-03-05', 'http://image.kf52.info/PetSupplies', 95, '4554361707375307', 'yunxidin602@hotmail.com', '1000001', '1001003', '2017-04-11 01:38:32', '2024-05-30 15:04:59', '2024-05-10 22:24:55', 'T', 399, 640);
INSERT INTO `sys_user` VALUES (786, 'lijialun', '', '2146057535', '黎嘉伦', 0, '2013-03-02', 'https://drive.eddr.us/Food', 50, '6210177176111184', 'lijialun@gmail.com', '1000001', '1001003', '2002-12-18 14:23:03', '2024-06-09 22:17:39', '2024-05-10 22:24:55', 'F', 508, 524);
INSERT INTO `sys_user` VALUES (787, 'zchen1011', '', '7603489399', '陈子韬', 1, '2024-02-11', 'https://image.gardclarence97.xyz/ToysGames', 100, '3535207295118631', 'zchen1222@outlook.com', '1000001', '1001003', '2006-11-14 09:50:32', '2024-06-01 03:29:24', '2024-05-10 22:24:55', 'T', 88, 361);
INSERT INTO `sys_user` VALUES (788, 'ziyigu15', '', '2006903580', '顾子异', 1, '2023-11-29', 'https://www.ruiz.jp/MusicalInstrument', 36, '343783379571091', 'ziyig@icloud.com', '1000001', '1001003', '2012-11-08 22:13:09', '2024-05-24 09:04:04', '2024-05-10 22:24:55', 'F', 749, 795);
INSERT INTO `sys_user` VALUES (789, 'maolu514', '', '202352151', '毛璐', 0, '2006-04-02', 'https://video.ziyiwu123.info/AutomotivePartsAccessories', 45, '3574861917300738', 'mlu@yahoo.com', '1000001', '1001003', '2015-07-01 04:50:13', '2024-06-03 22:40:47', '2024-05-10 22:24:55', 'F', 104, 48);
INSERT INTO `sys_user` VALUES (790, 'yunxilu830', '', '13634777019', '陆云熙', 0, '2005-03-21', 'http://video.su1.co.jp/Baby', 91, '341370849186676', 'luy@icloud.com', '1000001', '1001003', '2007-02-14 07:49:29', '2024-06-03 20:30:31', '2024-05-10 22:24:55', 'T', 679, 271);
INSERT INTO `sys_user` VALUES (791, 'xiaoming48', '', '76078275903', '高晓明', 1, '2001-09-09', 'http://auth.hashim.jp/HouseholdKitchenAppliances', 97, '5507440516839516', 'xiaogao1115@icloud.com', '1000001', '1001003', '2009-09-15 10:32:17', '2024-06-10 09:15:29', '2024-05-10 22:24:55', 'T', 952, 171);
INSERT INTO `sys_user` VALUES (792, 'yanzhiyuan54', '', '13717490082', '杨致远', 0, '2013-02-08', 'http://image.keithlo57.xyz/SportsOutdoor', 52, '3589470893649663', 'yanz319@mail.com', '1000001', '1001003', '2004-03-03 22:50:20', '2024-05-21 17:39:04', '2024-05-10 22:24:55', 'T', 839, 232);
INSERT INTO `sys_user` VALUES (793, 'zhennanduan', '', '14142557631', '段震南', 1, '2016-03-29', 'https://auth.byoun8.com/FilmSupplies', 43, '347509551815428', 'zhed7@hotmail.com', '1000001', '1001003', '2002-07-10 03:37:28', '2024-05-31 14:59:13', '2024-05-10 22:24:55', 'F', 997, 952);
INSERT INTO `sys_user` VALUES (794, 'zhang1208', '', '15509464547', '张晓明', 0, '2021-06-24', 'http://video.elgutierrez6.co.jp/Food', 79, '4674047448363794', 'xzh7@mail.com', '1000001', '1001003', '2005-09-23 04:03:29', '2024-05-29 17:54:50', '2024-05-10 22:24:55', 'F', 653, 592);
INSERT INTO `sys_user` VALUES (795, 'huzhe', '', '1098375735', '胡震南', 1, '2005-06-06', 'http://video.ylu44.net/Appliances', 68, '3565110768384412', 'huzh321@gmail.com', '1000001', '1001003', '2001-05-14 15:09:54', '2024-05-13 11:06:22', '2024-05-10 22:24:55', 'T', 35, 685);
INSERT INTO `sys_user` VALUES (796, 'duz84', '', '13072236634', '杜致远', 0, '2000-01-22', 'https://video.xiao430.biz/Books', 93, '5194898214497044', 'zhiyuan4@gmail.com', '1000001', '1001003', '2015-07-29 17:14:17', '2024-05-25 00:09:41', '2024-05-10 22:24:55', 'T', 630, 858);
INSERT INTO `sys_user` VALUES (797, 'liy3', '', '76017159393', '李云熙', 0, '2000-06-13', 'http://video.yugao86.co.jp/ClothingShoesandJewelry', 97, '3555416639675421', 'yunxili1991@gmail.com', '1000001', '1001003', '2002-04-27 13:35:50', '2024-05-31 11:39:25', '2024-05-10 22:24:55', 'T', 764, 571);
INSERT INTO `sys_user` VALUES (798, 'shihan2', '', '2072650174', '郑詩涵', 0, '2017-12-27', 'https://video.burnse.com/ArtsHandicraftsSewing', 86, '3529252112121853', 'shihan1112@gmail.com', '1000001', '1001003', '2011-04-21 21:14:27', '2024-05-12 21:20:59', '2024-05-10 22:24:55', 'F', 883, 64);
INSERT INTO `sys_user` VALUES (799, 'anqil1', '', '19175689740', '罗安琪', 1, '2010-10-20', 'http://www.rrobin.xyz/ComputersElectronics', 37, '3535282530371436', 'anqi715@hotmail.com', '1000001', '1001003', '2011-04-08 20:49:02', '2024-05-22 08:42:24', '2024-05-10 22:24:55', 'T', 739, 683);
INSERT INTO `sys_user` VALUES (800, 'shihago', '', '286148352', '龚詩涵', 1, '2007-06-03', 'http://drive.makina.info/CDsVinyl', 58, '344066009556792', 'sgong@gmail.com', '1000001', '1001003', '2001-06-09 17:44:28', '2024-05-18 01:51:07', '2024-05-10 22:24:55', 'T', 334, 814);
INSERT INTO `sys_user` VALUES (801, 'dinganqi', '', '13115114057', '丁安琪', 1, '2006-03-14', 'http://www.fernandezr7.us/Food', 52, '4063205836257791', 'anqiding@gmail.com', '1000001', '1001003', '2000-06-11 10:22:29', '2024-05-26 06:21:27', '2024-05-10 22:24:55', 'F', 663, 637);
INSERT INTO `sys_user` VALUES (802, 'xidong', '', '17619695457', '董晓明', 1, '2017-05-10', 'https://drive.ueda412.co.jp/Others', 83, '4384257420386330', 'dong3@icloud.com', '1000001', '1001003', '2007-11-11 20:22:59', '2024-05-26 14:28:00', '2024-05-10 22:24:55', 'F', 439, 66);
INSERT INTO `sys_user` VALUES (803, 'fuanqi', '', '7696499533', '傅安琪', 0, '2007-08-20', 'https://drive.changtakwah316.co.jp/ToysGames', 39, '6247060577824604', 'fanqi@hotmail.com', '1000001', '1001003', '2014-10-29 07:06:30', '2024-06-06 13:43:26', '2024-05-10 22:24:55', 'T', 423, 761);
INSERT INTO `sys_user` VALUES (804, 'lliao', '', '17542041711', '廖岚', 1, '2023-01-29', 'https://drive.zhoulu.co.jp/VideoGames', 62, '4235187863343368', 'lanl@icloud.com', '1000001', '1001003', '2010-12-15 01:40:44', '2024-05-20 23:09:49', '2024-05-10 22:24:55', 'F', 923, 301);
INSERT INTO `sys_user` VALUES (805, 'zitao7', '', '15250865178', '丁子韬', 1, '2003-05-11', 'https://drive.gotor.jp/ArtsHandicraftsSewing', 68, '5323340483487806', 'zitaoding@mail.com', '1000001', '1001003', '2022-03-07 04:55:31', '2024-06-01 07:52:00', '2024-05-10 22:24:55', 'F', 936, 874);
INSERT INTO `sys_user` VALUES (806, 'zqi1954', '', '17796484887', '秦子韬', 0, '2004-07-10', 'http://video.shehyf.xyz/FilmSupplies', 55, '3542423484838241', 'zitao8@hotmail.com', '1000001', '1001003', '2002-07-21 14:15:57', '2024-05-30 20:21:02', '2024-05-10 22:24:55', 'F', 59, 322);
INSERT INTO `sys_user` VALUES (807, 'ziyiyang', '', '7551203549', '杨子异', 0, '2002-05-24', 'https://drive.zzeng.cn/AutomotivePartsAccessories', 49, '6239622534432183', 'yangziyi16@outlook.com', '1000001', '1001003', '2008-03-25 01:08:03', '2024-05-18 15:58:28', '2024-05-10 22:24:55', 'T', 364, 934);
INSERT INTO `sys_user` VALUES (808, 'shenjial', '', '7608365035', '沈嘉伦', 0, '2007-12-21', 'http://www.koikki6.net/ArtsHandicraftsSewing', 39, '379682759050915', 'shen1979@gmail.com', '1000001', '1001003', '2013-10-12 08:16:47', '2024-05-11 21:34:27', '2024-05-10 22:24:55', 'F', 772, 764);
INSERT INTO `sys_user` VALUES (809, 'xziyi', '', '17035918113', '谢子异', 1, '2004-09-27', 'http://image.ziyiya.xyz/AppsGames', 68, '4492388230083566', 'zix@gmail.com', '1000001', '1001003', '2003-08-20 06:05:40', '2024-05-31 14:00:08', '2024-05-10 22:24:55', 'F', 596, 820);
INSERT INTO `sys_user` VALUES (810, 'ruli3', '', '14979213852', '廖睿', 1, '2001-08-20', 'http://www.ckam820.jp/Food', 54, '3562136785968203', 'liaoru@gmail.com', '1000001', '1001003', '2013-06-25 15:56:26', '2024-06-09 02:35:54', '2024-05-10 22:24:55', 'F', 121, 274);
INSERT INTO `sys_user` VALUES (811, 'liang413', '', '2846547235', '梁震南', 1, '2005-01-26', 'https://auth.taylornicole10.cn/HealthBabyCare', 42, '3542257125795398', 'liangzhennan@icloud.com', '1000001', '1001003', '2002-07-28 03:07:01', '2024-05-21 08:43:59', '2024-05-10 22:24:55', 'T', 851, 796);
INSERT INTO `sys_user` VALUES (812, 'ziyi86', '', '14375146646', '武子异', 0, '2002-12-26', 'http://auth.hawkinsjoanne.biz/Others', 56, '4363142037004242', 'ziyi213@mail.com', '1000001', '1001003', '2010-05-14 08:53:34', '2024-06-02 17:48:23', '2024-05-10 22:24:55', 'T', 115, 710);
INSERT INTO `sys_user` VALUES (813, 'yuning131', '', '13638828091', '王宇宁', 0, '2006-04-28', 'http://auth.kytao73.xyz/Beauty', 58, '3538523981086289', 'yuwan@icloud.com', '1000001', '1001003', '2009-02-08 20:31:58', '2024-05-10 01:52:05', '2024-05-10 22:24:55', 'T', 703, 254);
INSERT INTO `sys_user` VALUES (814, 'ziyigu', '', '1004211637', '顾子异', 1, '2008-05-13', 'https://www.robert1026.com/AutomotivePartsAccessories', 64, '6251235848676727', 'gu104@yahoo.com', '1000001', '1001003', '2012-06-20 05:12:04', '2024-05-18 06:46:24', '2024-05-10 22:24:55', 'F', 135, 170);
INSERT INTO `sys_user` VALUES (815, 'chang48', '', '76934896356', '常子异', 1, '2019-12-05', 'https://auth.gkelly.xyz/BeautyPersonalCare', 66, '372074761369250', 'changziyi@hotmail.com', '1000001', '1001003', '2016-04-15 11:57:33', '2024-05-27 05:48:57', '2024-05-10 22:24:55', 'F', 659, 15);
INSERT INTO `sys_user` VALUES (816, 'xyu', '', '15162352207', '于晓明', 1, '2003-01-07', 'http://drive.mingau.net/ComputersElectronics', 37, '3577705606487883', 'xiayu529@gmail.com', '1000001', '1001003', '2004-11-14 02:29:01', '2024-05-30 06:32:44', '2024-05-10 22:24:55', 'T', 654, 738);
INSERT INTO `sys_user` VALUES (817, 'crui43', '', '17571091680', '程睿', 1, '2014-10-03', 'https://video.cwtsui10.xyz/BaggageTravelEquipment', 18, '5502333487387828', 'rui2@gmail.com', '1000001', '1001003', '2001-10-15 16:14:30', '2024-05-12 22:49:47', '2024-05-10 22:24:55', 'F', 558, 680);
INSERT INTO `sys_user` VALUES (818, 'yuliang', '', '14170401729', '梁宇宁', 1, '2015-03-22', 'https://image.yuz.biz/Books', 66, '4545579168108329', 'yuninl901@mail.com', '1000001', '1001003', '2013-02-01 02:53:31', '2024-05-24 21:07:28', '2024-05-10 22:24:55', 'T', 71, 602);
INSERT INTO `sys_user` VALUES (819, 'hexiaoming', '', '17934734216', '贺晓明', 0, '2006-02-14', 'https://image.lai129.co.jp/ComputersElectronics', 59, '375894510368189', 'xiaohe2000@mail.com', '1000001', '1001003', '2009-03-16 12:08:38', '2024-05-25 17:21:43', '2024-05-10 22:24:55', 'F', 660, 551);
INSERT INTO `sys_user` VALUES (820, 'lua', '', '19919754421', '罗安琪', 1, '2012-05-04', 'https://drive.rentan.biz/MusicalInstrument', 95, '3569426999475233', 'anqi45@outlook.com', '1000001', '1001003', '2022-01-26 16:44:59', '2024-05-20 20:01:00', '2024-05-10 22:24:55', 'F', 579, 363);
INSERT INTO `sys_user` VALUES (821, 'lguo4', '', '7550867980', '郭璐', 0, '2019-09-22', 'https://image.yung6.net/AppsGames', 90, '6201875437141517', 'guo55@icloud.com', '1000001', '1001003', '2009-03-26 21:33:10', '2024-06-05 08:41:07', '2024-05-10 22:24:55', 'T', 35, 475);
INSERT INTO `sys_user` VALUES (822, 'xueyuning', '', '2193692267', '薛宇宁', 1, '2001-03-02', 'http://image.miu725.biz/PetSupplies', 36, '5074069476878504', 'xueyu1987@yahoo.com', '1000001', '1001003', '2010-09-08 16:43:51', '2024-05-28 00:19:35', '2024-05-10 22:24:55', 'F', 269, 41);
INSERT INTO `sys_user` VALUES (823, 'yanz', '', '75534473225', '阎致远', 1, '2023-03-05', 'http://www.xijial09.info/IndustrialScientificSupplies', 29, '372872904515949', 'yanzhiy@icloud.com', '1000001', '1001003', '2022-05-09 01:00:53', '2024-05-26 05:23:01', '2024-05-10 22:24:55', 'T', 954, 867);
INSERT INTO `sys_user` VALUES (824, 'lin1982', '', '75525867090', '林嘉伦', 0, '2001-07-07', 'https://www.much.cn/BaggageTravelEquipment', 83, '5437686594028753', 'linjialun@icloud.com', '1000001', '1001003', '2012-08-06 21:27:02', '2024-05-17 09:58:31', '2024-05-10 22:24:55', 'T', 874, 74);
INSERT INTO `sys_user` VALUES (825, 'tyunxi', '', '17328354185', '陶云熙', 0, '2015-04-21', 'http://image.aoshi3.cn/SportsOutdoor', 38, '5088427721508249', 'tao4@outlook.com', '1000001', '1001003', '2017-05-10 11:02:39', '2024-05-20 16:09:28', '2024-05-10 22:24:55', 'T', 803, 669);
INSERT INTO `sys_user` VALUES (826, 'yulon', '', '7550286560', '龙云熙', 1, '2003-11-05', 'http://video.kchingwan.org/ToysGames', 42, '6295491733261061', 'yulong@icloud.com', '1000001', '1001003', '2013-05-11 04:53:41', '2024-06-09 12:23:07', '2024-05-10 22:24:55', 'F', 182, 599);
INSERT INTO `sys_user` VALUES (827, 'jianganqi101', '', '108633682', '江安琪', 1, '2001-07-05', 'http://drive.valeriep.info/MusicalInstrument', 47, '6246573204823865', 'anqijiang@outlook.com', '1000001', '1001003', '2022-11-29 10:03:41', '2024-05-21 05:47:18', '2024-05-10 22:24:55', 'F', 313, 389);
INSERT INTO `sys_user` VALUES (828, 'xiaoming54', '', '76994298930', '曾晓明', 0, '2016-03-14', 'https://image.claanna.org/CollectiblesFineArt', 35, '4855662127354954', 'xiaomingzen7@icloud.com', '1000001', '1001003', '2013-02-19 00:20:48', '2024-05-12 13:57:47', '2024-05-10 22:24:55', 'T', 146, 571);
INSERT INTO `sys_user` VALUES (829, 'lalu', '', '2083117224', '卢岚', 0, '2020-07-18', 'http://video.romjef1953.co.jp/ClothingShoesandJewelry', 54, '3585277308635052', 'lan60@mail.com', '1000001', '1001003', '2020-04-03 13:48:01', '2024-05-19 12:49:52', '2024-05-10 22:24:55', 'T', 308, 922);
INSERT INTO `sys_user` VALUES (830, 'helan', '', '17383919651', '贺岚', 0, '2002-04-06', 'http://video.choiky.xyz/CellPhonesAccessories', 22, '5060760828321914', 'lanh@icloud.com', '1000001', '1001003', '2012-07-06 14:09:28', '2024-05-31 05:37:09', '2024-05-10 22:24:55', 'F', 465, 470);
INSERT INTO `sys_user` VALUES (831, 'zji', '', '205007418', '金震南', 0, '2018-02-11', 'https://auth.sugis.com/ToysGames', 81, '375697762009330', 'zhenji41@gmail.com', '1000001', '1001003', '2003-08-04 20:45:43', '2024-05-19 11:15:15', '2024-05-10 22:24:55', 'F', 287, 535);
INSERT INTO `sys_user` VALUES (832, 'lshi53', '', '76959471997', '吕詩涵', 0, '2009-09-14', 'https://auth.koyamahikaru.co.jp/CenturionGardenOutdoor', 57, '4330618088696655', 'lushihan309@gmail.com', '1000001', '1001003', '2007-10-19 02:29:42', '2024-05-27 17:31:56', '2024-05-10 22:24:55', 'F', 424, 124);
INSERT INTO `sys_user` VALUES (833, 'zhiyuandeng', '', '18432839128', '邓致远', 1, '2015-05-27', 'https://video.torj.com/ToysGames', 68, '3549371705631114', 'zhiydeng@gmail.com', '1000001', '1001003', '2005-11-15 00:07:55', '2024-06-02 13:30:08', '2024-05-10 22:24:55', 'T', 676, 301);
INSERT INTO `sys_user` VALUES (834, 'slu', '', '18415891504', '宋璐', 0, '2001-09-02', 'https://video.yulan8.net/BaggageTravelEquipment', 73, '6241239230357709', 'luso@gmail.com', '1000001', '1001003', '2000-10-10 04:31:16', '2024-05-28 23:54:56', '2024-05-10 22:24:55', 'F', 349, 935);
INSERT INTO `sys_user` VALUES (835, 'jsun', '', '218487481', '孙杰宏', 0, '2011-04-20', 'https://www.wadadaic.xyz/CollectiblesFineArt', 49, '347798649478360', 'sunjiehong@mail.com', '1000001', '1001003', '2001-10-08 10:03:04', '2024-06-02 02:32:56', '2024-05-10 22:24:55', 'T', 264, 703);
INSERT INTO `sys_user` VALUES (836, 'xiaomingyan', '', '19496958058', '阎晓明', 0, '2006-05-21', 'http://image.takuyayamag.cn/IndustrialScientificSupplies', 31, '6268500688797276', 'xiaoming2@outlook.com', '1000001', '1001003', '2012-01-15 02:21:55', '2024-05-29 06:14:25', '2024-05-10 22:24:55', 'T', 684, 580);
INSERT INTO `sys_user` VALUES (837, 'rui207', '', '75563309686', '罗睿', 1, '2013-06-19', 'https://image.cyyu.net/SportsOutdoor', 21, '6277852040620210', 'rui2@icloud.com', '1000001', '1001003', '2018-01-08 05:28:03', '2024-05-11 09:40:07', '2024-05-10 22:24:55', 'F', 822, 688);
INSERT INTO `sys_user` VALUES (838, 'ziyiwa108', '', '281613635', '王子异', 0, '2020-03-04', 'http://drive.tianz.co.jp/Others', 20, '5486926103666556', 'ziyiw@outlook.com', '1000001', '1001003', '2020-08-06 00:02:24', '2024-05-26 21:26:11', '2024-05-10 22:24:55', 'T', 20, 976);
INSERT INTO `sys_user` VALUES (839, 'rui8', '', '14031481089', '曾睿', 1, '2006-08-05', 'http://drive.hikand4.net/IndustrialScientificSupplies', 55, '3554326731059682', 'zenrui@yahoo.com', '1000001', '1001003', '2002-06-10 12:27:20', '2024-05-23 22:27:22', '2024-05-10 22:24:55', 'F', 882, 844);
INSERT INTO `sys_user` VALUES (840, 'zshe07', '', '7558044661', '沈子韬', 0, '2004-04-10', 'http://video.lulan.info/BeautyPersonalCare', 21, '5444422408008950', 'zish@gmail.com', '1000001', '1001003', '2017-10-16 18:38:10', '2024-05-17 12:28:49', '2024-05-10 22:24:55', 'F', 415, 923);
INSERT INTO `sys_user` VALUES (841, 'jiar', '', '18433936861', '贾睿', 0, '2004-11-18', 'http://www.luxie.cn/Others', 19, '377085986010243', 'rui228@gmail.com', '1000001', '1001003', '2015-09-22 11:24:53', '2024-05-16 03:36:11', '2024-05-10 22:24:55', 'F', 178, 846);
INSERT INTO `sys_user` VALUES (842, 'yuluo', '', '16407429909', '罗云熙', 0, '2000-09-28', 'https://drive.kuwm.xyz/Handcrafts', 81, '4836302577091475', 'luo4@outlook.com', '1000001', '1001003', '2021-02-11 08:51:01', '2024-05-26 16:02:58', '2024-05-10 22:24:55', 'F', 764, 284);
INSERT INTO `sys_user` VALUES (843, 'lanjiang', '', '16824881939', '江岚', 0, '2019-08-24', 'https://drive.ziyifan.jp/Baby', 97, '4796139403730860', 'jlan@gmail.com', '1000001', '1001003', '2002-01-17 11:37:16', '2024-05-19 11:09:29', '2024-05-10 22:24:55', 'F', 973, 56);
INSERT INTO `sys_user` VALUES (844, 'ruida', '', '2119803018', '戴睿', 1, '2011-07-10', 'https://image.ziyitang.net/SportsOutdoor', 19, '4551386235990502', 'dairui@mail.com', '1000001', '1001003', '2019-11-23 10:49:19', '2024-05-27 10:46:26', '2024-05-10 22:24:55', 'T', 481, 366);
INSERT INTO `sys_user` VALUES (845, 'xiuyingyu', '', '285875914', '余秀英', 0, '2014-04-09', 'http://video.ricada.net/VideoGames', 94, '3540009561821697', 'yx816@outlook.com', '1000001', '1001003', '2016-01-20 19:37:12', '2024-05-25 06:34:29', '2024-05-10 22:24:55', 'T', 950, 729);
INSERT INTO `sys_user` VALUES (846, 'rui7', '', '16539665965', '赵睿', 1, '2018-07-21', 'http://image.sann.com/SportsOutdoor', 26, '6228997240734939', 'zhaorui@outlook.com', '1000001', '1001003', '2023-04-27 15:07:45', '2024-05-21 23:30:17', '2024-05-10 22:24:55', 'T', 485, 732);
INSERT INTO `sys_user` VALUES (847, 'sdeng01', '', '13038371629', '邓詩涵', 0, '2019-07-10', 'https://drive.jiehong2020.us/VideoGames', 65, '6264385025532923', 'shdeng2011@outlook.com', '1000001', '1001003', '2007-05-24 11:35:03', '2024-05-28 04:00:52', '2024-05-10 22:24:55', 'T', 765, 233);
INSERT INTO `sys_user` VALUES (848, 'qiaziyi831', '', '18790643921', '钱子异', 0, '2016-04-27', 'https://auth.kennlawre.com/Books', 72, '349032006767969', 'ziyqian@hotmail.com', '1000001', '1001003', '2008-09-06 00:47:58', '2024-05-16 02:43:21', '2024-05-10 22:24:55', 'F', 14, 782);
INSERT INTO `sys_user` VALUES (849, 'jiewei', '', '202085044', '魏杰宏', 0, '2015-08-20', 'http://auth.foc.com/CollectiblesFineArt', 49, '3562709333094738', 'jwei@outlook.com', '1000001', '1001003', '2013-05-27 19:38:10', '2024-05-14 04:08:37', '2024-05-10 22:24:55', 'T', 372, 658);
INSERT INTO `sys_user` VALUES (850, 'zengziyi', '', '7554418138', '曾子异', 0, '2005-01-03', 'https://image.dm1963.info/Appliances', 88, '3558038679890053', 'ziyizeng6@mail.com', '1000001', '1001003', '2014-06-01 03:43:00', '2024-06-07 22:44:27', '2024-05-10 22:24:55', 'T', 295, 227);
INSERT INTO `sys_user` VALUES (851, 'sz2007', '', '1093133360', '沈子韬', 1, '2018-04-17', 'http://drive.lorib.info/Books', 70, '5066795525874678', 'zsh01@gmail.com', '1000001', '1001003', '2020-09-02 05:34:36', '2024-05-19 09:06:03', '2024-05-10 22:24:55', 'F', 571, 815);
INSERT INTO `sys_user` VALUES (852, 'yan4', '', '284562150', '阎震南', 1, '2012-02-07', 'https://drive.arthur8.cn/CDsVinyl', 35, '4790108057660216', 'zhya1125@gmail.com', '1000001', '1001003', '2015-12-25 20:49:12', '2024-05-18 08:37:44', '2024-05-10 22:24:55', 'T', 714, 843);
INSERT INTO `sys_user` VALUES (853, 'fuxi7', '', '13614274212', '傅晓明', 0, '2017-06-17', 'https://drive.lufu1216.com/BaggageTravelEquipment', 90, '4382769959478546', 'xiaomingf@icloud.com', '1000001', '1001003', '2012-09-09 17:26:57', '2024-05-19 01:00:55', '2024-05-10 22:24:55', 'T', 719, 160);
INSERT INTO `sys_user` VALUES (854, 'laluo', '', '7602255432', '罗岚', 1, '2001-10-28', 'https://www.butleramy.org/BeautyPersonalCare', 55, '6277471910755269', 'lanluo@outlook.com', '1000001', '1001003', '2023-03-29 15:44:51', '2024-05-24 03:56:59', '2024-05-10 22:24:55', 'T', 538, 837);
INSERT INTO `sys_user` VALUES (855, 'zhangxiuying', '', '17802507401', '张秀英', 0, '2013-12-31', 'http://video.soto4.com/FilmSupplies', 68, '6283494241936509', 'zhang1966@mail.com', '1000001', '1001003', '2012-07-11 14:38:56', '2024-05-16 09:16:27', '2024-05-10 22:24:55', 'F', 406, 771);
INSERT INTO `sys_user` VALUES (856, 'wangxiuy', '', '17785691510', '汪秀英', 0, '2009-10-21', 'https://www.okadarena.xyz/AppsGames', 80, '3568850975676970', 'xiuywan822@icloud.com', '1000001', '1001003', '2024-04-03 21:50:30', '2024-06-10 00:37:48', '2024-05-10 22:24:55', 'F', 966, 360);
INSERT INTO `sys_user` VALUES (857, 'lu09', '', '1093252118', '龙璐', 0, '2021-11-02', 'http://auth.jilu.net/MusicalInstrument', 17, '347136497040133', 'longlu@gmail.com', '1000001', '1001003', '2022-02-26 19:30:44', '2024-06-06 10:59:16', '2024-05-10 22:24:55', 'F', 367, 541);
INSERT INTO `sys_user` VALUES (858, 'ruim1030', '', '2093234426', '孟睿', 0, '2006-11-01', 'https://auth.cwheung.org/BaggageTravelEquipment', 40, '5514683529757040', 'ruimeng@mail.com', '1000001', '1001003', '2022-01-18 20:11:10', '2024-06-02 07:50:19', '2024-05-10 22:24:55', 'T', 440, 802);
INSERT INTO `sys_user` VALUES (859, 'zitaowu', '', '109126798', '武子韬', 1, '2009-04-04', 'https://www.yao.co.jp/VideoGames', 67, '4998821925289113', 'wuzitao@yahoo.com', '1000001', '1001003', '2010-02-24 10:33:16', '2024-06-05 06:16:54', '2024-05-10 22:24:55', 'T', 313, 378);
INSERT INTO `sys_user` VALUES (860, 'aq10', '', '76995685904', '秦安琪', 1, '2017-08-04', 'http://drive.shihaxi.biz/BeautyPersonalCare', 41, '3559410165003062', 'anqiqin@yahoo.com', '1000001', '1001003', '2000-10-10 04:02:08', '2024-06-04 17:10:13', '2024-05-10 22:24:55', 'T', 595, 456);
INSERT INTO `sys_user` VALUES (861, 'shen3', '', '7690883702', '沈子韬', 1, '2021-06-12', 'https://image.onh.xyz/PetSupplies', 93, '4962216188576326', 'shen72@icloud.com', '1000001', '1001003', '2000-08-28 11:04:10', '2024-06-05 09:12:21', '2024-05-10 22:24:55', 'F', 607, 14);
INSERT INTO `sys_user` VALUES (862, 'wujiehong', '', '2006874931', '武杰宏', 1, '2022-04-11', 'https://www.ziyifan617.org/MusicalInstrument', 92, '3554859237679025', 'jiehongwu@mail.com', '1000001', '1001003', '2021-08-25 21:15:48', '2024-05-10 01:43:06', '2024-05-10 22:24:55', 'F', 269, 861);
INSERT INTO `sys_user` VALUES (863, 'dlan', '', '16853722159', '董岚', 1, '2001-07-09', 'http://image.okamotosara3.net/MusicalInstrument', 37, '5317173258776824', 'dlan@gmail.com', '1000001', '1001003', '2004-07-14 07:56:41', '2024-06-09 20:05:31', '2024-05-10 22:24:55', 'F', 943, 430);
INSERT INTO `sys_user` VALUES (864, 'shenanqi10', '', '13748923327', '沈安琪', 0, '2009-05-10', 'https://image.wru.co.jp/ArtsHandicraftsSewing', 61, '5385832835572272', 'shen10@icloud.com', '1000001', '1001003', '2007-06-14 04:24:04', '2024-05-31 14:24:47', '2024-05-10 22:24:55', 'T', 198, 176);
INSERT INTO `sys_user` VALUES (865, 'zhao1109', '', '76003469514', '赵秀英', 0, '2010-06-24', 'http://video.caixi9.net/ClothingShoesandJewelry', 29, '5265334703877516', 'zhaoxiuying1015@gmail.com', '1000001', '1001003', '2010-08-17 20:51:30', '2024-05-18 16:48:53', '2024-05-10 22:24:55', 'F', 571, 798);
INSERT INTO `sys_user` VALUES (866, 'anqi6', '', '19668661281', '顾安琪', 0, '2010-09-16', 'https://image.joycecollins.co.jp/HealthBabyCare', 91, '5460997421105833', 'gu83@mail.com', '1000001', '1001003', '2011-04-05 08:51:19', '2024-06-08 11:58:37', '2024-05-10 22:24:55', 'F', 423, 479);
INSERT INTO `sys_user` VALUES (867, 'tanlu', '', '2109632326', '谭璐', 1, '2003-09-18', 'https://auth.tlsiu4.cn/BeautyPersonalCare', 31, '4462624326085552', 'lutan@yahoo.com', '1000001', '1001003', '2002-01-09 19:54:21', '2024-05-18 10:13:29', '2024-05-10 22:24:55', 'T', 460, 176);
INSERT INTO `sys_user` VALUES (868, 'xiongrui10', '', '101325296', '熊睿', 1, '2024-02-07', 'https://auth.chadp6.com/Beauty', 30, '3541385720261119', 'ruxion@gmail.com', '1000001', '1001003', '2005-08-27 05:59:24', '2024-05-22 00:13:36', '2024-05-10 22:24:55', 'T', 495, 960);
INSERT INTO `sys_user` VALUES (869, 'xiuyingzhu52', '', '76068280255', '朱秀英', 0, '2013-01-01', 'https://auth.zitsh.net/FilmSupplies', 65, '344626075686441', 'zxiuyi@outlook.com', '1000001', '1001003', '2019-10-10 17:39:48', '2024-05-12 10:35:41', '2024-05-10 22:24:55', 'T', 659, 16);
INSERT INTO `sys_user` VALUES (870, 'chang65', '', '2033804586', '常子异', 1, '2021-12-07', 'http://auth.janice208.com/ToysGames', 15, '3556127916284774', 'chanziyi41@hotmail.com', '1000001', '1001003', '2017-12-09 02:46:33', '2024-05-15 06:05:51', '2024-05-10 22:24:55', 'T', 985, 127);
INSERT INTO `sys_user` VALUES (871, 'hziyi', '', '18596978579', '黄子异', 1, '2022-03-24', 'http://auth.sji7.net/SportsOutdoor', 84, '378284721049553', 'huangziyi@outlook.com', '1000001', '1001003', '2006-11-03 14:50:45', '2024-06-02 15:11:33', '2024-05-10 22:24:55', 'T', 822, 465);
INSERT INTO `sys_user` VALUES (872, 'cheng10', '', '15813402743', '程杰宏', 0, '2010-06-22', 'https://drive.kaitomae1.biz/IndustrialScientificSupplies', 36, '3554371217865418', 'jiehong4@gmail.com', '1000001', '1001003', '2024-03-31 09:37:39', '2024-05-18 20:33:31', '2024-05-10 22:24:55', 'F', 695, 590);
INSERT INTO `sys_user` VALUES (873, 'zhiyuangao10', '', '76090625413', '高致远', 1, '2015-08-07', 'https://www.jreynolds13.biz/CellPhonesAccessories', 72, '6229184300631458', 'zhiyuangao10@mail.com', '1000001', '1001003', '2014-03-09 18:45:04', '2024-06-10 04:32:25', '2024-05-10 22:24:55', 'T', 958, 597);
INSERT INTO `sys_user` VALUES (874, 'xiuying724', '', '19448663801', '杨秀英', 1, '2010-03-14', 'http://video.uedasar.us/BaggageTravelEquipment', 87, '4291582861689567', 'xiuyingyan@gmail.com', '1000001', '1001003', '2022-02-05 12:29:37', '2024-05-17 12:34:55', '2024-05-10 22:24:55', 'F', 429, 423);
INSERT INTO `sys_user` VALUES (875, 'js4', '', '14869949588', '贾詩涵', 0, '2012-08-02', 'https://www.daila.xyz/CellPhonesAccessories', 17, '4174375349997013', 'sjia@outlook.com', '1000001', '1001003', '2015-07-11 17:35:15', '2024-06-09 14:52:47', '2024-05-10 22:24:55', 'T', 92, 54);
INSERT INTO `sys_user` VALUES (876, 'lan1951', '', '1029619687', '邱岚', 1, '2018-03-23', 'https://video.isa816.cn/CenturionGardenOutdoor', 44, '3559473568939019', 'qiul2@gmail.com', '1000001', '1001003', '2005-05-07 20:43:08', '2024-05-28 05:22:44', '2024-05-10 22:24:55', 'T', 525, 53);
INSERT INTO `sys_user` VALUES (877, 'xiuyingxue', '', '19251309275', '薛秀英', 1, '2022-11-12', 'http://www.jji.xyz/BeautyPersonalCare', 52, '6231809779080292', 'xiuyingx4@gmail.com', '1000001', '1001003', '2016-09-04 12:00:58', '2024-06-08 18:24:17', '2024-05-10 22:24:55', 'T', 66, 489);
INSERT INTO `sys_user` VALUES (878, 'jialunren', '', '2187592834', '任嘉伦', 1, '2006-06-01', 'http://auth.tiyuning.net/Handcrafts', 39, '5444209840590384', 'rjia@outlook.com', '1000001', '1001003', '2011-05-26 22:58:30', '2024-05-15 13:05:55', '2024-05-10 22:24:55', 'F', 544, 463);
INSERT INTO `sys_user` VALUES (879, 'luy', '', '16114496632', '阎璐', 1, '2020-07-26', 'http://image.shimadatsubasa930.info/Others', 20, '5547669592920327', 'ylu4@outlook.com', '1000001', '1001003', '2016-07-20 00:27:03', '2024-05-29 23:25:54', '2024-05-10 22:24:55', 'T', 923, 114);
INSERT INTO `sys_user` VALUES (880, 'jisu1985', '', '2034012357', '苏杰宏', 1, '2019-11-02', 'http://video.ziyihou.info/Beauty', 16, '6273444856702920', 'sujiehong721@outlook.com', '1000001', '1001003', '2008-08-15 08:59:22', '2024-05-19 02:35:53', '2024-05-10 22:24:55', 'T', 254, 511);
INSERT INTO `sys_user` VALUES (881, 'xiaomingjin4', '', '13150436874', '金晓明', 0, '2016-10-29', 'http://drive.takadair.us/Beauty', 22, '6233957076528949', 'jinxiaom78@icloud.com', '1000001', '1001003', '2000-11-08 08:11:45', '2024-06-07 19:15:09', '2024-05-10 22:24:55', 'F', 492, 618);
INSERT INTO `sys_user` VALUES (882, 'zhennanyu', '', '200253693', '余震南', 0, '2004-12-10', 'https://www.sumwingche2014.org/ToolsHomeDecoration', 69, '376592458604610', 'yuzhen2013@gmail.com', '1000001', '1001003', '2020-07-15 07:00:06', '2024-05-21 10:42:02', '2024-05-10 22:24:55', 'T', 664, 901);
INSERT INTO `sys_user` VALUES (883, 'fananqi', '', '7601949583', '范安琪', 1, '2004-02-12', 'https://drive.wingfatd.biz/ClothingShoesandJewelry', 68, '3558818316251094', 'fan10@gmail.com', '1000001', '1001003', '2003-01-25 12:46:37', '2024-05-24 10:04:14', '2024-05-10 22:24:55', 'T', 744, 314);
INSERT INTO `sys_user` VALUES (884, 'xiexiuying', '', '13770144305', '谢秀英', 1, '2012-02-11', 'https://auth.waihank.jp/HouseholdKitchenAppliances', 55, '5381395154287504', 'xiuying5@mail.com', '1000001', '1001003', '2014-08-15 13:22:49', '2024-05-10 14:29:28', '2024-05-10 22:24:55', 'T', 274, 861);
INSERT INTO `sys_user` VALUES (885, 'lu55', '', '75517237075', '萧璐', 0, '2002-06-08', 'http://auth.rumedina.xyz/SportsOutdoor', 60, '5345805278729897', 'lux7@outlook.com', '1000001', '1001003', '2019-05-25 00:39:27', '2024-05-13 14:13:34', '2024-05-10 22:24:55', 'T', 355, 680);
INSERT INTO `sys_user` VALUES (886, 'lan18', '', '213318028', '杨岚', 1, '2013-12-05', 'https://auth.aha709.jp/IndustrialScientificSupplies', 24, '349578335505405', 'lanyang@icloud.com', '1000001', '1001003', '2023-04-09 23:59:45', '2024-06-04 09:05:22', '2024-05-10 22:24:55', 'T', 700, 87);
INSERT INTO `sys_user` VALUES (887, 'xiaosun', '', '7695047755', '孙晓明', 1, '2014-02-03', 'http://video.wandareynolds917.xyz/CDsVinyl', 91, '5075027187730231', 'xiaomings@gmail.com', '1000001', '1001003', '2005-03-15 01:41:24', '2024-05-27 03:29:19', '2024-05-10 22:24:55', 'F', 226, 969);
INSERT INTO `sys_user` VALUES (888, 'weiyuning1006', '', '75521757535', '魏宇宁', 1, '2007-03-09', 'http://www.huitl.xyz/ArtsHandicraftsSewing', 22, '5421707673692133', 'wyuning@gmail.com', '1000001', '1001003', '2008-07-02 23:05:34', '2024-06-03 01:55:14', '2024-05-10 22:24:55', 'T', 217, 990);
INSERT INTO `sys_user` VALUES (889, 'mo2', '', '217641374', '莫詩涵', 0, '2010-04-28', 'https://auth.xili.com/ToysGames', 33, '6257005399889928', 'shihan714@gmail.com', '1000001', '1001003', '2022-09-26 22:20:55', '2024-06-03 03:07:48', '2024-05-10 22:24:55', 'F', 690, 520);
INSERT INTO `sys_user` VALUES (890, 'anshi', '', '13420139568', '石安琪', 1, '2000-10-18', 'http://auth.dougr.biz/CellPhonesAccessories', 55, '5289832077605118', 'shi1127@gmail.com', '1000001', '1001003', '2000-07-20 08:37:59', '2024-05-11 23:14:12', '2024-05-10 22:24:55', 'F', 499, 163);
INSERT INTO `sys_user` VALUES (891, 'qial', '', '1037140616', '钱岚', 0, '2011-03-03', 'http://video.miyamoto1962.co.jp/CellPhonesAccessories', 25, '6219079990439737', 'qlan@outlook.com', '1000001', '1001003', '2007-06-24 18:04:52', '2024-05-15 04:05:58', '2024-05-10 22:24:55', 'F', 128, 402);
INSERT INTO `sys_user` VALUES (892, 'wujialun909', '', '14477854239', '武嘉伦', 1, '2014-03-20', 'http://drive.ayato1966.info/Appliances', 20, '3540829057325519', 'jialunwu6@icloud.com', '1000001', '1001003', '2013-08-09 13:24:45', '2024-05-26 18:58:29', '2024-05-10 22:24:55', 'F', 349, 61);
INSERT INTO `sys_user` VALUES (893, 'ziyixi', '', '18554062625', '萧子异', 1, '2013-11-23', 'http://drive.washtimothy1989.com/CenturionGardenOutdoor', 56, '3585509272990547', 'xizi@outlook.com', '1000001', '1001003', '2021-08-14 05:24:33', '2024-05-28 12:44:09', '2024-05-10 22:24:55', 'F', 13, 712);
INSERT INTO `sys_user` VALUES (894, 'zitaoho64', '', '209364120', '侯子韬', 1, '2003-09-29', 'http://www.loui10.com/CollectiblesFineArt', 86, '4914665385167082', 'zitao1116@outlook.com', '1000001', '1001003', '2001-04-24 17:25:59', '2024-05-23 05:55:02', '2024-05-10 22:24:55', 'T', 182, 757);
INSERT INTO `sys_user` VALUES (895, 'anqizha1979', '', '14476787991', '张安琪', 0, '2005-02-02', 'http://image.kiwasaki.xyz/ArtsHandicraftsSewing', 18, '5149760003467453', 'anqizha1129@icloud.com', '1000001', '1001003', '2016-02-28 02:36:22', '2024-05-10 12:20:11', '2024-05-10 22:24:55', 'F', 765, 644);
INSERT INTO `sys_user` VALUES (896, 'yunxili67', '', '2866105100', '黎云熙', 0, '2011-08-15', 'http://video.richdo.com/Handcrafts', 40, '4440073137477425', 'yunxili315@outlook.com', '1000001', '1001003', '2019-11-17 06:13:45', '2024-05-28 03:33:38', '2024-05-10 22:24:55', 'F', 242, 946);
INSERT INTO `sys_user` VALUES (897, 'luzita', '', '15279485370', '陆子韬', 1, '2000-11-16', 'http://auth.michellep.biz/Appliances', 18, '3574975341456179', 'luzit@icloud.com', '1000001', '1001003', '2021-05-08 23:39:47', '2024-06-03 13:02:25', '2024-05-10 22:24:55', 'F', 540, 923);
INSERT INTO `sys_user` VALUES (898, 'taziy', '', '17229441834', '唐子异', 0, '2022-05-12', 'https://www.kazuma2.biz/AppsGames', 25, '3568217866071416', 'tziy@outlook.com', '1000001', '1001003', '2009-10-29 11:14:45', '2024-06-09 06:43:14', '2024-05-10 22:24:55', 'F', 796, 40);
INSERT INTO `sys_user` VALUES (899, 'canqi', '', '2840576135', '崔安琪', 0, '2022-02-03', 'https://www.xmo.jp/Books', 100, '3550811933815092', 'anqicui@mail.com', '1000001', '1001003', '2009-10-20 01:12:46', '2024-06-07 09:28:51', '2024-05-10 22:24:55', 'F', 497, 509);
INSERT INTO `sys_user` VALUES (900, 'lejieh', '', '104954085', '雷杰宏', 1, '2022-05-19', 'https://auth.dong4.org/SportsOutdoor', 32, '5209309124589568', 'leijiehong9@icloud.com', '1000001', '1001003', '2011-05-03 03:49:21', '2024-06-01 06:30:15', '2024-05-10 22:24:55', 'T', 4, 689);
INSERT INTO `sys_user` VALUES (901, 'yfang5', '', '285727845', '方宇宁', 1, '2007-05-22', 'https://www.sem06.com/IndustrialScientificSupplies', 29, '6272755611974145', 'fanyuning@outlook.com', '1000001', '1001003', '2014-12-11 05:26:45', '2024-05-23 11:32:08', '2024-05-10 22:24:55', 'F', 20, 213);
INSERT INTO `sys_user` VALUES (902, 'yuanqi57', '', '7552283857', '于安琪', 0, '2006-04-03', 'http://image.ryotamor9.us/CDsVinyl', 99, '3534015691618828', 'anqiyu@gmail.com', '1000001', '1001003', '2014-07-22 01:15:46', '2024-06-07 14:35:34', '2024-05-10 22:24:55', 'T', 75, 706);
INSERT INTO `sys_user` VALUES (903, 'yunxixie', '', '7607987366', '谢云熙', 1, '2002-12-18', 'http://drive.zz606.xyz/ClothingShoesandJewelry', 77, '344869263756945', 'xieyunxi1@outlook.com', '1000001', '1001003', '2021-09-21 12:23:25', '2024-05-24 17:38:40', '2024-05-10 22:24:55', 'T', 346, 981);
INSERT INTO `sys_user` VALUES (904, 'jiehong9', '', '19870447645', '余杰宏', 1, '2014-11-23', 'https://video.ayano5.net/HouseholdKitchenAppliances', 50, '6270047678682692', 'yujiehong@hotmail.com', '1000001', '1001003', '2002-05-20 22:44:16', '2024-05-31 12:01:31', '2024-05-10 22:24:55', 'T', 527, 477);
INSERT INTO `sys_user` VALUES (905, 'ziyg', '', '213342328', '顾子异', 1, '2008-01-26', 'https://auth.tsubasa314.xyz/CollectiblesFineArt', 82, '5134717905154545', 'ziyi3@mail.com', '1000001', '1001003', '2010-02-28 20:43:29', '2024-05-26 07:58:37', '2024-05-10 22:24:55', 'T', 630, 175);
INSERT INTO `sys_user` VALUES (906, 'maa6', '', '17688274123', '马安琪', 0, '2008-09-01', 'https://video.yutoue06.cn/CellPhonesAccessories', 63, '4182382194215574', 'anqima10@gmail.com', '1000001', '1001003', '2003-08-10 08:31:43', '2024-06-08 03:03:06', '2024-05-10 22:24:55', 'F', 693, 902);
INSERT INTO `sys_user` VALUES (907, 'xiuyliang1995', '', '16824973445', '梁秀英', 0, '2006-09-30', 'https://auth.xiaomingwang5.info/ToolsHomeDecoration', 47, '6295909487648704', 'lxi@yahoo.com', '1000001', '1001003', '2010-01-14 04:31:28', '2024-05-15 17:46:09', '2024-05-10 22:24:55', 'T', 670, 748);
INSERT INTO `sys_user` VALUES (908, 'yunxi9', '', '200903032', '徐云熙', 0, '2022-07-13', 'http://video.clarencecla69.net/BeautyPersonalCare', 87, '373245163037518', 'yunxi811@icloud.com', '1000001', '1001003', '2013-04-26 01:39:42', '2024-05-25 18:48:58', '2024-05-10 22:24:55', 'T', 258, 17);
INSERT INTO `sys_user` VALUES (909, 'xiongz', '', '2871794811', '熊震南', 1, '2022-02-02', 'https://auth.reynoldsj.jp/AppsGames', 75, '4198932546785021', 'xiozhennan@icloud.com', '1000001', '1001003', '2011-06-05 20:47:38', '2024-05-22 12:49:46', '2024-05-10 22:24:55', 'F', 148, 412);
INSERT INTO `sys_user` VALUES (910, 'xiaomingdeng', '', '14151293474', '邓晓明', 1, '2005-01-13', 'https://www.fungkm.us/CellPhonesAccessories', 57, '3563180744922249', 'xiaoming11@gmail.com', '1000001', '1001003', '2010-07-10 18:18:52', '2024-06-05 01:23:45', '2024-05-10 22:24:55', 'T', 514, 115);
INSERT INTO `sys_user` VALUES (911, 'zhennanren', '', '75590327108', '任震南', 0, '2014-11-04', 'http://drive.tin1225.org/CollectiblesFineArt', 19, '373276380468046', 'rezh508@gmail.com', '1000001', '1001003', '2001-07-10 05:24:00', '2024-05-25 18:29:07', '2024-05-10 22:24:55', 'T', 369, 281);
INSERT INTO `sys_user` VALUES (912, 'duxi', '', '2033186338', '段秀英', 0, '2022-12-07', 'https://auth.deborah316.info/ToysGames', 89, '5365672956842631', 'duanxiuying@hotmail.com', '1000001', '1001003', '2020-04-03 02:13:32', '2024-05-29 08:03:12', '2024-05-10 22:24:55', 'F', 485, 643);
INSERT INTO `sys_user` VALUES (913, 'yunxi71', '', '75549539659', '阎云熙', 1, '2003-09-03', 'http://video.kelwa78.net/BaggageTravelEquipment', 99, '3585498916707725', 'yunxiyan8@outlook.com', '1000001', '1001003', '2013-11-19 10:34:11', '2024-05-13 17:12:04', '2024-05-10 22:24:55', 'F', 23, 652);
INSERT INTO `sys_user` VALUES (914, 'calan', '', '18901652582', '曹岚', 0, '2011-09-25', 'https://video.bgonzalez.cn/Others', 80, '341651051000758', 'lcao7@icloud.com', '1000001', '1001003', '2021-02-13 01:37:23', '2024-05-18 09:01:32', '2024-05-10 22:24:55', 'F', 971, 347);
INSERT INTO `sys_user` VALUES (915, 'xcui', '', '17255618197', '崔秀英', 1, '2011-05-11', 'http://drive.evhe1.jp/Baby', 30, '4050042400969146', 'cxiu804@icloud.com', '1000001', '1001003', '2008-03-30 10:54:51', '2024-05-28 10:08:14', '2024-05-10 22:24:55', 'F', 30, 475);
INSERT INTO `sys_user` VALUES (916, 'yunxiliang', '', '13086178931', '梁云熙', 1, '2015-04-28', 'https://auth.elizabeth90.info/VideoGames', 98, '3571776407334372', 'liany@gmail.com', '1000001', '1001003', '2007-06-09 05:39:58', '2024-05-26 08:06:27', '2024-05-10 22:24:55', 'T', 279, 223);
INSERT INTO `sys_user` VALUES (917, 'dengjiehong', '', '2877184096', '邓杰宏', 0, '2006-08-29', 'https://www.hujennifer.cn/CellPhonesAccessories', 95, '349219179177072', 'djiehong@gmail.com', '1000001', '1001003', '2007-10-14 16:53:13', '2024-05-27 00:35:54', '2024-05-10 22:24:55', 'T', 328, 989);
INSERT INTO `sys_user` VALUES (918, 'xiuytang', '', '7690537339', '唐秀英', 1, '2012-08-03', 'http://auth.zhiyuan84.info/BeautyPersonalCare', 53, '4204153840598802', 'tangx1@gmail.com', '1000001', '1001003', '2016-10-28 09:01:20', '2024-05-11 01:51:24', '2024-05-10 22:24:55', 'F', 572, 906);
INSERT INTO `sys_user` VALUES (919, 'zhiyuzhang5', '', '103454199', '张致远', 1, '2013-10-24', 'https://image.ema.cn/BaggageTravelEquipment', 73, '5067932389842587', 'zhiyuanzhang@icloud.com', '1000001', '1001003', '2007-02-19 03:38:05', '2024-06-07 22:38:14', '2024-05-10 22:24:55', 'F', 494, 307);
INSERT INTO `sys_user` VALUES (920, 'xiuying1', '', '19374280462', '蒋秀英', 1, '2018-11-23', 'http://video.yaziyi.net/ComputersElectronics', 55, '3534416088094237', 'jiangxiuyi@yahoo.com', '1000001', '1001003', '2011-06-02 17:32:21', '2024-05-24 18:48:47', '2024-05-10 22:24:55', 'F', 924, 342);
INSERT INTO `sys_user` VALUES (921, 'lanfan323', '', '14313094796', '范岚', 1, '2024-01-21', 'http://www.murakamisakura.jp/ArtsHandicraftsSewing', 93, '5129458991428694', 'fanl@mail.com', '1000001', '1001003', '2015-09-21 01:07:05', '2024-05-15 18:46:16', '2024-05-10 22:24:55', 'T', 466, 860);
INSERT INTO `sys_user` VALUES (922, 'lanpe', '', '76931909513', '彭岚', 0, '2011-05-30', 'https://image.marjoriejames.us/BeautyPersonalCare', 83, '376150869575802', 'plan@gmail.com', '1000001', '1001003', '2004-02-21 18:47:22', '2024-05-25 13:48:58', '2024-05-10 22:24:55', 'F', 532, 374);
INSERT INTO `sys_user` VALUES (923, 'hexiaom1959', '', '2130008254', '贺晓明', 1, '2013-03-18', 'https://www.cheziy.xyz/ClothingShoesandJewelry', 99, '4400958159433298', 'xiaoming44@outlook.com', '1000001', '1001003', '2013-07-28 08:55:27', '2024-05-29 12:53:51', '2024-05-10 22:24:55', 'F', 201, 208);
INSERT INTO `sys_user` VALUES (924, 'zl16', '', '18801786241', '梁子韬', 1, '2021-05-09', 'https://www.dezit.co.jp/SportsOutdoor', 89, '4915653593043385', 'liang1@yahoo.com', '1000001', '1001003', '2020-07-14 13:19:11', '2024-05-28 22:40:38', '2024-05-10 22:24:55', 'F', 318, 376);
INSERT INTO `sys_user` VALUES (925, 'wanr4', '', '76010679760', '汪睿', 0, '2017-01-02', 'https://www.kfern.info/Books', 59, '5390040459027002', 'wangrui506@icloud.com', '1000001', '1001003', '2004-05-18 17:45:00', '2024-05-30 18:17:47', '2024-05-10 22:24:55', 'F', 709, 184);
INSERT INTO `sys_user` VALUES (926, 'lulu7', '', '19540817011', '吕璐', 1, '2020-08-03', 'https://drive.kamingkao.info/MusicalInstrument', 29, '3569286928905589', 'llu3@gmail.com', '1000001', '1001003', '2012-11-28 00:36:44', '2024-06-05 00:18:52', '2024-05-10 22:24:55', 'F', 666, 780);
INSERT INTO `sys_user` VALUES (927, 'lumao', '', '7695840655', '毛璐', 1, '2004-05-01', 'http://image.chunyung3.xyz/BeautyPersonalCare', 70, '4325290266875480', 'mao3@mail.com', '1000001', '1001003', '2003-10-06 21:01:18', '2024-06-08 12:16:48', '2024-05-10 22:24:55', 'T', 831, 578);
INSERT INTO `sys_user` VALUES (928, 'lan83', '', '17049356189', '戴岚', 1, '2017-06-14', 'https://video.fujitr.jp/Books', 50, '4342297538259301', 'dalan75@mail.com', '1000001', '1001003', '2008-03-21 23:36:54', '2024-05-24 19:03:12', '2024-05-10 22:24:55', 'T', 773, 555);
INSERT INTO `sys_user` VALUES (929, 'xiaozhu8', '', '15820896490', '朱晓明', 0, '2017-07-29', 'http://drive.kongky.info/ToysGames', 75, '6242531759546046', 'zhux@icloud.com', '1000001', '1001003', '2012-06-26 17:26:09', '2024-06-05 08:54:44', '2024-05-10 22:24:55', 'F', 939, 181);
INSERT INTO `sys_user` VALUES (930, 'jiangshih', '', '17356026739', '姜詩涵', 0, '2010-01-26', 'https://auth.chiba430.xyz/Beauty', 18, '4081030240567942', 'jsh@icloud.com', '1000001', '1001003', '2000-03-01 23:18:24', '2024-06-07 18:15:27', '2024-05-10 22:24:55', 'T', 892, 42);
INSERT INTO `sys_user` VALUES (931, 'taoz', '', '18569196555', '陶子韬', 1, '2004-11-07', 'http://image.haoxi7.xyz/CenturionGardenOutdoor', 46, '4230286920972022', 'tao2016@hotmail.com', '1000001', '1001003', '2015-04-28 23:47:30', '2024-06-10 00:59:33', '2024-05-10 22:24:55', 'F', 121, 128);
INSERT INTO `sys_user` VALUES (932, 'xiuying602', '', '13084798495', '江秀英', 0, '2017-10-14', 'http://www.jacqueline01.us/Beauty', 81, '6200739829902061', 'xiujiang820@mail.com', '1000001', '1001003', '2011-06-15 22:41:28', '2024-05-17 19:21:36', '2024-05-10 22:24:55', 'F', 969, 854);
INSERT INTO `sys_user` VALUES (933, 'jhe310', '', '19874874762', '何杰宏', 0, '2021-10-31', 'http://video.tzhen18.jp/ToolsHomeDecoration', 91, '4477310346296451', 'hej6@outlook.com', '1000001', '1001003', '2005-02-27 18:01:49', '2024-05-23 06:13:57', '2024-05-10 22:24:55', 'F', 48, 700);
INSERT INTO `sys_user` VALUES (934, 'jxiuying10', '', '2068021542', '贾秀英', 1, '2010-03-28', 'http://video.alberco67.cn/AutomotivePartsAccessories', 88, '3571628667553334', 'jx65@gmail.com', '1000001', '1001003', '2009-04-27 23:33:57', '2024-05-15 15:23:08', '2024-05-10 22:24:55', 'F', 801, 34);
INSERT INTO `sys_user` VALUES (935, 'zhiyuanxue3', '', '19074883381', '薛致远', 0, '2021-05-26', 'http://drive.xionyu5.xyz/ToysGames', 18, '4548029103400289', 'zhiyuan621@icloud.com', '1000001', '1001003', '2018-06-03 06:53:30', '2024-05-17 11:23:29', '2024-05-10 22:24:55', 'T', 967, 932);
INSERT INTO `sys_user` VALUES (936, 'zhiyuantan', '', '19752574295', '汤致远', 1, '2015-01-05', 'http://image.cziy104.cn/ClothingShoesandJewelry', 73, '5534107062441016', 'ztan42@outlook.com', '1000001', '1001003', '2004-05-29 15:54:41', '2024-06-03 22:23:11', '2024-05-10 22:24:55', 'F', 747, 488);
INSERT INTO `sys_user` VALUES (937, 'guoji', '', '15613154181', '郭杰宏', 0, '2010-02-21', 'http://drive.hyt.biz/BaggageTravelEquipment', 73, '6277235792350832', 'jiehong1104@outlook.com', '1000001', '1001003', '2023-01-16 20:50:27', '2024-05-17 05:00:10', '2024-05-10 22:24:55', 'F', 407, 394);
INSERT INTO `sys_user` VALUES (938, 'lanmeng62', '', '19091877913', '孟岚', 0, '2005-02-12', 'https://auth.masudakasum.org/CollectiblesFineArt', 44, '3538166589911910', 'menglan18@hotmail.com', '1000001', '1001003', '2000-12-11 16:30:06', '2024-06-08 20:35:20', '2024-05-10 22:24:55', 'F', 215, 917);
INSERT INTO `sys_user` VALUES (939, 'yunxilu', '', '76052619797', '卢云熙', 0, '2022-05-02', 'http://drive.kingd1960.biz/CDsVinyl', 85, '6236693401739763', 'yunlu5@gmail.com', '1000001', '1001003', '2017-12-15 05:55:24', '2024-06-04 02:25:45', '2024-05-10 22:24:55', 'F', 270, 348);
INSERT INTO `sys_user` VALUES (940, 'yanlan10', '', '7691172338', '杨岚', 1, '2005-02-20', 'http://image.sakura1114.net/ComputersElectronics', 23, '5437070857398817', 'lan2@yahoo.com', '1000001', '1001003', '2019-12-24 08:07:51', '2024-06-02 09:08:22', '2024-05-10 22:24:55', 'F', 49, 343);
INSERT INTO `sys_user` VALUES (941, 'denjialun7', '', '19270930442', '邓嘉伦', 1, '2022-04-03', 'http://auth.kenta624.us/ToolsHomeDecoration', 69, '6235049619924898', 'denjia@icloud.com', '1000001', '1001003', '2003-12-12 09:09:51', '2024-05-16 08:53:40', '2024-05-10 22:24:55', 'T', 462, 730);
INSERT INTO `sys_user` VALUES (942, 'ly8', '', '17326669860', '严岚', 1, '2012-01-08', 'https://www.ksa63.cn/HouseholdKitchenAppliances', 78, '4733276292428770', 'yalan1021@mail.com', '1000001', '1001003', '2019-05-14 04:17:59', '2024-05-31 02:55:22', '2024-05-10 22:24:55', 'F', 391, 307);
INSERT INTO `sys_user` VALUES (943, 'gxiaoming814', '', '100424885', '高晓明', 1, '2005-03-23', 'http://image.zhiyudu.jp/IndustrialScientificSupplies', 64, '378784102795633', 'xiaominggao41@outlook.com', '1000001', '1001003', '2010-03-16 21:42:35', '2024-05-12 14:43:32', '2024-05-10 22:24:55', 'F', 407, 757);
INSERT INTO `sys_user` VALUES (944, 'warui', '', '19112635892', '汪睿', 0, '2006-08-17', 'http://drive.yoshidak.org/FilmSupplies', 48, '4852271261942344', 'ruiwa@hotmail.com', '1000001', '1001003', '2023-10-28 12:07:05', '2024-05-25 02:30:48', '2024-05-10 22:24:55', 'T', 815, 599);
INSERT INTO `sys_user` VALUES (945, 'he1988', '', '15812113836', '贺嘉伦', 0, '2003-10-17', 'https://image.wschiang1946.jp/ClothingShoesandJewelry', 34, '3581410865068029', 'hjia726@gmail.com', '1000001', '1001003', '2015-03-09 08:37:00', '2024-06-03 23:44:28', '2024-05-10 22:24:55', 'F', 123, 768);
INSERT INTO `sys_user` VALUES (946, 'zcao07', '', '1092214845', '曹震南', 1, '2021-06-02', 'http://drive.yunxihe.org/IndustrialScientificSupplies', 37, '3529437844945287', 'zhennancao@gmail.com', '1000001', '1001003', '2015-03-14 22:59:41', '2024-05-17 13:19:03', '2024-05-10 22:24:55', 'T', 400, 417);
INSERT INTO `sys_user` VALUES (947, 'lu1018', '', '108251405', '崔璐', 1, '2023-06-25', 'http://image.daniel1218.org/Books', 60, '4970746761549373', 'culu@icloud.com', '1000001', '1001003', '2016-07-27 04:30:05', '2024-05-21 13:12:37', '2024-05-10 22:24:55', 'T', 17, 588);
INSERT INTO `sys_user` VALUES (948, 'huayuni', '', '13483863155', '黄宇宁', 1, '2011-12-14', 'https://auth.hirao.biz/ClothingShoesandJewelry', 39, '5460615495557990', 'hyuning@gmail.com', '1000001', '1001003', '2014-07-12 01:35:42', '2024-05-26 21:30:29', '2024-05-10 22:24:55', 'T', 886, 979);
INSERT INTO `sys_user` VALUES (949, 'tang4', '', '212640624', '唐詩涵', 0, '2006-04-28', 'https://www.taozhennan.cn/CDsVinyl', 87, '346511091842061', 'shihantang1125@gmail.com', '1000001', '1001003', '2001-11-24 14:56:47', '2024-05-27 23:56:33', '2024-05-10 22:24:55', 'T', 763, 461);
INSERT INTO `sys_user` VALUES (950, 'zhey95', '', '2114365462', '严震南', 0, '2007-03-12', 'https://www.lawingkuen.net/Baby', 61, '4465727274006905', 'yanz9@gmail.com', '1000001', '1001003', '2007-12-07 17:23:44', '2024-05-17 16:32:17', '2024-05-10 22:24:55', 'F', 729, 348);
INSERT INTO `sys_user` VALUES (951, 'yinzhi', '', '17759782130', '尹致远', 0, '2023-01-19', 'http://auth.fu8.us/CollectiblesFineArt', 46, '3575664016473458', 'zyin@yahoo.com', '1000001', '1001003', '2001-10-03 11:42:02', '2024-05-31 03:38:52', '2024-05-10 22:24:55', 'T', 92, 749);
INSERT INTO `sys_user` VALUES (952, 'changxiuy', '', '7696712408', '常秀英', 1, '2018-01-26', 'http://auth.melvin40.xyz/MusicalInstrument', 75, '5597779741276528', 'xiuyingcha831@mail.com', '1000001', '1001003', '2002-03-12 23:43:02', '2024-05-28 12:52:43', '2024-05-10 22:24:55', 'T', 233, 702);
INSERT INTO `sys_user` VALUES (953, 'mo1219', '', '280455206', '莫璐', 1, '2012-12-31', 'http://auth.fushing4.biz/Beauty', 70, '376283093844445', 'lumo@icloud.com', '1000001', '1001003', '2008-06-16 17:12:34', '2024-05-25 00:28:25', '2024-05-10 22:24:55', 'F', 888, 143);
INSERT INTO `sys_user` VALUES (954, 'shihan57', '', '2138868874', '武詩涵', 0, '2022-09-12', 'http://auth.douglk.co.jp/ToysGames', 40, '340000792302662', 'wus@gmail.com', '1000001', '1001003', '2013-08-21 06:01:12', '2024-05-27 02:59:22', '2024-05-10 22:24:55', 'T', 669, 457);
INSERT INTO `sys_user` VALUES (955, 'lila', '', '13436578151', '廖岚', 1, '2018-05-10', 'https://image.ruihu.cn/MusicalInstrument', 16, '4022718366577669', 'lila@icloud.com', '1000001', '1001003', '2002-03-18 13:56:20', '2024-06-08 18:01:57', '2024-05-10 22:24:55', 'F', 423, 207);
INSERT INTO `sys_user` VALUES (956, 'jialun216', '', '17894280231', '蒋嘉伦', 1, '2023-02-04', 'https://image.aoshisakurai.net/AutomotivePartsAccessories', 23, '4122708634189842', 'jjiang44@mail.com', '1000001', '1001003', '2022-10-27 11:19:38', '2024-05-29 01:18:23', '2024-05-10 22:24:55', 'T', 544, 955);
INSERT INTO `sys_user` VALUES (957, 'xijialun10', '', '76073101584', '夏嘉伦', 1, '2023-06-04', 'http://image.yotanakamori.net/ToysGames', 50, '5315441699171307', 'xjialun@icloud.com', '1000001', '1001003', '2015-11-11 23:47:22', '2024-05-10 19:17:19', '2024-05-10 22:24:55', 'T', 789, 156);
INSERT INTO `sys_user` VALUES (958, 'dz515', '', '7690297674', '杜震南', 1, '2012-12-06', 'https://image.hawkifrederick.net/ClothingShoesandJewelry', 36, '372651194087417', 'duzhennan@icloud.com', '1000001', '1001003', '2024-02-06 17:47:03', '2024-05-29 22:40:10', '2024-05-10 22:24:55', 'F', 758, 956);
INSERT INTO `sys_user` VALUES (959, 'qiulu3', '', '2109283557', '邱璐', 1, '2016-10-16', 'http://auth.hoyho.info/ClothingShoesandJewelry', 34, '348455044519988', 'luqiu8@icloud.com', '1000001', '1001003', '2001-08-09 01:52:19', '2024-05-17 01:43:12', '2024-05-10 22:24:55', 'T', 996, 715);
INSERT INTO `sys_user` VALUES (960, 'lyu68', '', '14704666794', '吕宇宁', 1, '2023-01-13', 'http://video.caxiao.net/AppsGames', 59, '5310478946144476', 'luyuning10@yahoo.com', '1000001', '1001003', '2018-07-05 10:13:46', '2024-05-18 03:02:41', '2024-05-10 22:24:55', 'F', 900, 270);
INSERT INTO `sys_user` VALUES (961, 'sunxiaoming68', '', '14442935512', '孙晓明', 0, '2006-08-22', 'https://auth.chmedi.info/SportsOutdoor', 60, '4155066592082636', 'xiaomingsun@icloud.com', '1000001', '1001003', '2004-08-15 08:06:15', '2024-06-09 11:45:20', '2024-05-10 22:24:55', 'T', 280, 226);
INSERT INTO `sys_user` VALUES (962, 'shihan1965', '', '285661436', '侯詩涵', 1, '2008-11-17', 'https://auth.emmaburns.co.jp/HealthBabyCare', 32, '6266293503466606', 'houshihan@yahoo.com', '1000001', '1001003', '2012-08-29 12:44:20', '2024-05-15 14:57:30', '2024-05-10 22:24:55', 'T', 245, 926);
INSERT INTO `sys_user` VALUES (963, 'lan515', '', '282204461', '范岚', 1, '2012-11-30', 'http://www.madiana.biz/BaggageTravelEquipment', 35, '3535937070536405', 'lfan1955@icloud.com', '1000001', '1001003', '2000-12-08 00:17:26', '2024-06-10 11:05:25', '2024-05-10 22:24:55', 'F', 89, 601);
INSERT INTO `sys_user` VALUES (964, 'yunxi2', '', '16657582206', '石云熙', 1, '2021-02-24', 'https://video.alexander2.us/BaggageTravelEquipment', 55, '6246227818062713', 'shy@outlook.com', '1000001', '1001003', '2012-12-01 11:32:37', '2024-05-15 16:19:57', '2024-05-10 22:24:55', 'F', 41, 831);
INSERT INTO `sys_user` VALUES (965, 'jiehomo', '', '76044023009', '莫杰宏', 1, '2019-11-15', 'https://video.sjian.co.jp/ToolsHomeDecoration', 94, '6233503038662747', 'jiehong86@outlook.com', '1000001', '1001003', '2023-03-24 18:51:56', '2024-06-09 04:54:11', '2024-05-10 22:24:55', 'F', 239, 716);
INSERT INTO `sys_user` VALUES (966, 'qjialu50', '', '19597333022', '钱嘉伦', 0, '2018-07-18', 'https://www.tammybell.cn/MusicalInstrument', 83, '4924082084428740', 'jialun7@hotmail.com', '1000001', '1001003', '2020-11-08 02:19:14', '2024-06-03 01:23:07', '2024-05-10 22:24:55', 'T', 35, 606);
INSERT INTO `sys_user` VALUES (967, 'ziya8', '', '14778866303', '严子韬', 1, '2019-08-13', 'https://www.qzhennan10.biz/AutomotivePartsAccessories', 57, '5537418547576053', 'zityan@gmail.com', '1000001', '1001003', '2017-03-16 02:28:56', '2024-05-11 13:51:13', '2024-05-10 22:24:55', 'F', 775, 922);
INSERT INTO `sys_user` VALUES (968, 'xiaomingw', '', '7609429847', '武晓明', 1, '2014-05-04', 'http://drive.carowilliams.jp/CellPhonesAccessories', 27, '6231874539621413', 'wuxiaoming@gmail.com', '1000001', '1001003', '2009-05-03 20:24:19', '2024-05-31 20:14:47', '2024-05-10 22:24:55', 'F', 559, 23);
INSERT INTO `sys_user` VALUES (969, 'xue48', '', '281223128', '薛晓明', 0, '2005-10-08', 'https://www.patteellen.xyz/Baby', 70, '4827414064028489', 'xxiaoming@mail.com', '1000001', '1001003', '2000-02-13 14:32:45', '2024-05-21 14:03:16', '2024-05-10 22:24:55', 'T', 0, 537);
INSERT INTO `sys_user` VALUES (970, 'lrui4', '', '17797631748', '吕睿', 1, '2016-11-21', 'https://drive.diaz63.co.jp/BeautyPersonalCare', 41, '3536585330534634', 'ruilu6@mail.com', '1000001', '1001003', '2005-02-20 16:57:15', '2024-06-08 06:30:27', '2024-05-10 22:24:55', 'F', 215, 873);
INSERT INTO `sys_user` VALUES (971, 'ziwe', '', '76050781934', '魏子异', 1, '2016-03-13', 'https://video.zh4.biz/CDsVinyl', 44, '349540606890973', 'weiziy1025@hotmail.com', '1000001', '1001003', '2011-03-07 20:05:30', '2024-06-04 05:24:56', '2024-05-10 22:24:55', 'T', 817, 960);
INSERT INTO `sys_user` VALUES (972, 'fanshihan', '', '17411222694', '范詩涵', 0, '2005-01-10', 'http://www.wingsze57.net/PetSupplies', 45, '4148649348333542', 'fshihan6@gmail.com', '1000001', '1001003', '2012-05-15 10:06:18', '2024-06-10 13:54:54', '2024-05-10 22:24:55', 'F', 388, 663);
INSERT INTO `sys_user` VALUES (973, 'liao7', '', '7690916695', '廖杰宏', 0, '2017-08-10', 'http://auth.choionkay.info/MusicalInstrument', 43, '346527165430096', 'liaojieho4@yahoo.com', '1000001', '1001003', '2018-11-12 17:11:22', '2024-05-18 05:58:02', '2024-05-10 22:24:55', 'F', 788, 808);
INSERT INTO `sys_user` VALUES (974, 'zzhu', '', '280488221', '朱致远', 0, '2015-08-19', 'http://drive.ayanosato8.biz/SportsOutdoor', 73, '347311490136697', 'zhu10@outlook.com', '1000001', '1001003', '2016-11-28 08:38:15', '2024-05-20 17:26:27', '2024-05-10 22:24:55', 'F', 12, 825);
INSERT INTO `sys_user` VALUES (975, 'mlu', '', '75574490031', '毛璐', 1, '2020-10-26', 'https://image.whitev.info/CollectiblesFineArt', 43, '3545654946589892', 'lu103@outlook.com', '1000001', '1001003', '2008-11-28 17:53:57', '2024-06-03 06:15:53', '2024-05-10 22:24:55', 'T', 118, 303);
INSERT INTO `sys_user` VALUES (976, 'hul', '', '18836944728', '胡璐', 0, '2013-07-19', 'http://auth.gladys208.cn/ComputersElectronics', 27, '5240237857717463', 'luh@mail.com', '1000001', '1001003', '2011-02-21 17:42:25', '2024-06-07 00:00:11', '2024-05-10 22:24:55', 'T', 934, 330);
INSERT INTO `sys_user` VALUES (977, 'qianzitao', '', '18825463640', '钱子韬', 1, '2022-12-29', 'https://video.miuram.org/CellPhonesAccessories', 98, '3576253369590214', 'qianz@hotmail.com', '1000001', '1001003', '2004-06-18 00:24:43', '2024-06-06 02:28:19', '2024-05-10 22:24:55', 'F', 730, 609);
INSERT INTO `sys_user` VALUES (978, 'zw1954', '', '104430523', '吴震南', 0, '2010-10-09', 'https://image.wrightroger.jp/ToolsHomeDecoration', 98, '344364322670857', 'wzhennan@mail.com', '1000001', '1001003', '2023-07-26 19:40:07', '2024-05-15 10:28:33', '2024-05-10 22:24:55', 'F', 548, 638);
INSERT INTO `sys_user` VALUES (979, 'jz228', '', '2855642915', '钟杰宏', 0, '2005-03-27', 'http://drive.xueru8.net/Appliances', 15, '5056037440706616', 'zhongj@gmail.com', '1000001', '1001003', '2012-01-15 10:34:02', '2024-05-13 14:55:48', '2024-05-10 22:24:55', 'F', 697, 596);
INSERT INTO `sys_user` VALUES (980, 'lushen', '', '204291430', '沈璐', 1, '2005-03-15', 'https://video.tinago2.net/Handcrafts', 60, '6217988173991110', 'lu1968@hotmail.com', '1000001', '1001003', '2002-06-20 11:45:29', '2024-06-04 03:23:53', '2024-05-10 22:24:55', 'T', 500, 548);
INSERT INTO `sys_user` VALUES (981, 'fengzhennan', '', '14135974184', '冯震南', 0, '2002-11-28', 'http://video.swshe.biz/AutomotivePartsAccessories', 66, '342412716553577', 'zhenfeng1@hotmail.com', '1000001', '1001003', '2014-03-05 18:20:01', '2024-05-16 12:05:18', '2024-05-10 22:24:55', 'T', 976, 132);
INSERT INTO `sys_user` VALUES (982, 'moanqi8', '', '17179563016', '莫安琪', 1, '2012-08-28', 'http://www.kimua.us/Books', 70, '4696735899228839', 'amo10@gmail.com', '1000001', '1001003', '2023-09-12 17:14:52', '2024-05-15 01:51:21', '2024-05-10 22:24:55', 'F', 132, 507);
INSERT INTO `sys_user` VALUES (983, 'zren1', '', '17803776372', '任子异', 0, '2002-08-06', 'http://image.hanyuning510.jp/CellPhonesAccessories', 97, '342030729120844', 'rz1991@gmail.com', '1000001', '1001003', '2006-05-06 11:10:28', '2024-05-27 22:28:08', '2024-05-10 22:24:55', 'T', 59, 221);
INSERT INTO `sys_user` VALUES (984, 'ruihan723', '', '2051719760', '韩睿', 0, '2016-10-16', 'http://drive.wingfat11.jp/BeautyPersonalCare', 37, '6247039071955338', 'ruhan@gmail.com', '1000001', '1001003', '2002-03-02 20:17:46', '2024-05-15 13:19:14', '2024-05-10 22:24:55', 'F', 925, 748);
INSERT INTO `sys_user` VALUES (985, 'yunxidu1', '', '18859404018', '杜云熙', 1, '2023-06-21', 'http://image.wu7.net/PetSupplies', 66, '372148196150897', 'duyunxi@yahoo.com', '1000001', '1001003', '2002-02-12 18:05:30', '2024-05-18 06:19:44', '2024-05-10 22:24:55', 'T', 583, 537);
INSERT INTO `sys_user` VALUES (986, 'lu1231', '', '7607192342', '梁璐', 1, '2005-09-29', 'https://image.wujiehong716.org/HealthBabyCare', 96, '4054467129320535', 'lilu8@icloud.com', '1000001', '1001003', '2010-07-09 18:41:27', '2024-05-21 21:35:51', '2024-05-10 22:24:55', 'T', 550, 636);
INSERT INTO `sys_user` VALUES (987, 'xiong1209', '', '18544777922', '熊安琪', 0, '2022-09-04', 'http://image.mok5.jp/ToolsHomeDecoration', 47, '5360243074634006', 'xiong10@mail.com', '1000001', '1001003', '2003-05-22 02:39:51', '2024-05-20 21:09:57', '2024-05-10 22:24:55', 'F', 678, 554);
INSERT INTO `sys_user` VALUES (988, 'anqixie96', '', '7600916546', '谢安琪', 0, '2012-10-23', 'http://www.djo1.org/CenturionGardenOutdoor', 32, '375936529677694', 'xianqi1968@icloud.com', '1000001', '1001003', '2023-03-20 20:26:32', '2024-05-12 07:13:34', '2024-05-10 22:24:55', 'F', 709, 130);
INSERT INTO `sys_user` VALUES (989, 'yuning4', '', '19739341895', '向宇宁', 0, '2020-09-23', 'http://video.mio49.us/BaggageTravelEquipment', 77, '371609723983002', 'xiang630@gmail.com', '1000001', '1001003', '2018-03-17 03:59:05', '2024-05-20 00:23:53', '2024-05-10 22:24:55', 'F', 940, 504);
INSERT INTO `sys_user` VALUES (990, 'aca', '', '107648375', '曹安琪', 1, '2004-10-15', 'https://video.yls618.info/ClothingShoesandJewelry', 52, '6211037039802677', 'anqcao@gmail.com', '1000001', '1001003', '2007-12-22 06:10:14', '2024-06-10 02:17:29', '2024-05-10 22:24:55', 'F', 645, 163);
INSERT INTO `sys_user` VALUES (991, 'lu2000', '', '217195655', '邹璐', 0, '2016-11-26', 'https://auth.lyshing.us/AutomotivePartsAccessories', 65, '3551180616028970', 'luzo@gmail.com', '1000001', '1001003', '2009-05-22 19:30:36', '2024-05-26 03:59:33', '2024-05-10 22:24:55', 'F', 708, 152);
INSERT INTO `sys_user` VALUES (992, 'chang3', '', '1068837463', '常嘉伦', 0, '2004-04-20', 'http://drive.lfang407.us/Books', 87, '3537733170201687', 'jc10@hotmail.com', '1000001', '1001003', '2020-12-11 23:07:43', '2024-05-10 13:06:32', '2024-05-10 22:24:55', 'F', 223, 71);
INSERT INTO `sys_user` VALUES (993, 'jihu1004', '', '15138154944', '胡嘉伦', 0, '2003-04-02', 'https://auth.zitaoda60.cn/HealthBabyCare', 84, '4322045884211713', 'jiahu1@icloud.com', '1000001', '1001003', '2007-04-14 03:00:47', '2024-05-15 10:55:42', '2024-05-10 22:24:55', 'F', 439, 930);
INSERT INTO `sys_user` VALUES (994, 'zheqia10', '', '13337905681', '钱震南', 0, '2023-07-22', 'http://video.yianq.org/ArtsHandicraftsSewing', 74, '5173424176490235', 'zqian110@gmail.com', '1000001', '1001003', '2017-09-06 19:08:14', '2024-05-16 15:07:30', '2024-05-10 22:24:55', 'T', 712, 576);
INSERT INTO `sys_user` VALUES (995, 'yz610', '', '13933754812', '严致远', 1, '2018-02-02', 'https://www.rono.info/CDsVinyl', 69, '6278474126547297', 'zhiyuanya@outlook.com', '1000001', '1001003', '2003-11-28 06:51:31', '2024-05-21 16:51:04', '2024-05-10 22:24:55', 'T', 522, 83);
INSERT INTO `sys_user` VALUES (996, 'jiehongzo4', '', '7604466867', '邹杰宏', 0, '2019-01-08', 'https://image.chicm817.net/HealthBabyCare', 41, '344861338930084', 'jiehong526@gmail.com', '1000001', '1001003', '2001-01-02 12:28:21', '2024-05-29 21:07:04', '2024-05-10 22:24:55', 'F', 185, 208);
INSERT INTO `sys_user` VALUES (997, 'weiyunxi', '', '1010658064', '韦云熙', 1, '2008-03-12', 'https://www.yuningx.net/ComputersElectronics', 57, '3586761181776512', 'yunxi3@gmail.com', '1000001', '1001003', '2001-10-13 23:47:50', '2024-05-13 06:32:20', '2024-05-10 22:24:55', 'T', 407, 853);
INSERT INTO `sys_user` VALUES (998, 'yhe3', '', '16520432040', '何宇宁', 0, '2010-01-09', 'https://video.benjaminpo625.com/Baby', 40, '6243331323157167', 'heyuning2@outlook.com', '1000001', '1001003', '2017-11-10 06:07:21', '2024-05-18 13:22:46', '2024-05-10 22:24:55', 'T', 710, 869);
INSERT INTO `sys_user` VALUES (999, 'wangshih9', '', '13181840045', '王詩涵', 0, '2008-08-27', 'https://video.cui728.jp/CenturionGardenOutdoor', 23, '370817628329038', 'washih718@gmail.com', '1000001', '1001003', '2021-07-15 21:30:18', '2024-05-26 18:43:57', '2024-05-10 22:24:55', 'F', 759, 431);
INSERT INTO `sys_user` VALUES (1000, 'jt721', '', '13967541721', '谭杰宏', 1, '2018-05-19', 'http://auth.dab.co.jp/ComputersElectronics', 69, '347470850298537', 'jtan4@mail.com', '1000001', '1001003', '2024-05-05 15:49:44', '2024-06-09 16:00:38', '2024-05-10 22:24:55', 'T', 534, 981);

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `dept_id` int NULL DEFAULT NULL COMMENT 'sys_dept_id',
  `user_id` int NULL DEFAULT NULL COMMENT 'sys_user_id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-部门关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
INSERT INTO `sys_user_dept` VALUES (6, 5, 2);
INSERT INTO `sys_user_dept` VALUES (7, 16, 149);
INSERT INTO `sys_user_dept` VALUES (8, 16, 862);
INSERT INTO `sys_user_dept` VALUES (9, 16, 757);
INSERT INTO `sys_user_dept` VALUES (10, 16, 672);
INSERT INTO `sys_user_dept` VALUES (11, 16, 485);
INSERT INTO `sys_user_dept` VALUES (12, 4, 691);
INSERT INTO `sys_user_dept` VALUES (13, 4, 992);
INSERT INTO `sys_user_dept` VALUES (14, 4, 14);
INSERT INTO `sys_user_dept` VALUES (15, 4, 586);
INSERT INTO `sys_user_dept` VALUES (16, 4, 407);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色id (sys_role_id)',
  `user_id` int NOT NULL COMMENT '用户id(sys_user_id)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户-角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3, 2);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_db_version
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师统计总览表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of teacher_statistics
-- ----------------------------
INSERT INTO `teacher_statistics` VALUES (1, '2022', '12', '2010-07', '1', 1000001, 466, 414, 201.98, 643, '2013-09-29 06:45:31', '2005-07-17 15:51:54', '2024-05-10 22:06:48', '2007-12-08 17:53:09', '');
INSERT INTO `teacher_statistics` VALUES (2, '2006', '10', '2009-11', '10', 1000002, 985, 716, 960.44, 751, '2008-06-16 06:32:13', '2009-06-23 15:22:50', '2024-05-10 22:06:53', '2020-01-16 19:12:48', '');
INSERT INTO `teacher_statistics` VALUES (3, '2001', '03', '2003-12', '14', 1000001, 511, 828, 634.67, 394, '2023-07-22 02:17:23', '2010-07-06 09:21:12', '2024-05-10 22:06:58', '2019-07-26 03:56:30', '');
INSERT INTO `teacher_statistics` VALUES (4, '2009', '12', '2002-10', '12', 1000001, 826, 582, 92.45, 789, '2018-01-31 09:32:51', '2023-01-09 16:06:21', '2024-05-10 22:07:02', '2002-11-15 07:50:19', '');
INSERT INTO `teacher_statistics` VALUES (5, '2023', '08', '2010-12', '11', 1000003, 952, 967, 46.67, 238, '2007-07-18 17:52:25', '2006-01-11 00:14:01', '2024-05-10 22:07:06', '2007-03-19 21:19:52', '');
INSERT INTO `teacher_statistics` VALUES (6, '2024', '10', '2007-10', '14', 1000001, 176, 892, 903.71, 257, '2009-09-07 11:52:29', '2009-06-21 17:58:22', '2024-05-10 22:07:10', '2012-08-11 23:42:25', '');
INSERT INTO `teacher_statistics` VALUES (7, '2013', '11', '2011-12', '13', 1000002, 919, 687, 328.96, 563, '2007-05-11 09:37:12', '2019-08-05 21:32:04', '2024-05-10 22:07:15', '2000-03-24 06:35:26', '');
INSERT INTO `teacher_statistics` VALUES (8, '2011', '12', '2006-10', '9', 1000003, 531, 290, 69.36, 849, '2013-09-06 14:28:38', '2002-08-17 12:52:52', '2024-05-10 22:07:19', '2001-05-29 16:26:40', '');
INSERT INTO `teacher_statistics` VALUES (9, '2012', '07', '2011-01', '2', 1000001, 266, 640, 579.83, 110, '2001-05-15 05:47:46', '2001-06-12 14:13:21', '2024-05-10 22:07:23', '2012-02-19 01:50:29', '');
INSERT INTO `teacher_statistics` VALUES (10, '2009', '11', '2004-10', '2', 1000001, 513, 5, 904.22, 695, '2000-06-22 01:38:19', '2019-12-09 06:55:27', '2024-05-10 22:07:39', '2006-01-29 01:00:09', '');
INSERT INTO `teacher_statistics` VALUES (11, '2003', '10', '2008-12', '12', 1000002, 299, 574, 983.94, 333, '2001-09-14 19:40:22', '2008-03-28 05:20:35', '2024-05-10 22:07:44', '2003-10-26 07:23:26', '');
INSERT INTO `teacher_statistics` VALUES (12, '2011', '12', '2003-06', '8', 1000002, 223, 604, 449.45, 501, '2018-08-07 09:29:13', '2003-09-02 19:24:23', '2024-05-10 22:07:49', '2010-11-11 04:14:00', '');
INSERT INTO `teacher_statistics` VALUES (13, '2003', '12', '2010-11', '2', 1000002, 833, 45, 624.98, 329, '2001-03-20 20:58:46', '2003-07-24 09:16:34', '2024-05-10 22:07:54', '2021-11-15 14:35:55', '');
INSERT INTO `teacher_statistics` VALUES (14, '2011', '12', '2006-01', '4', 1000003, 945, 879, 365.55, 533, '2003-02-02 22:13:38', '2020-03-26 11:54:46', '2024-05-10 22:07:58', '2023-04-14 01:22:50', '');
INSERT INTO `teacher_statistics` VALUES (15, '2002', '11', '2012-04', '2', 1000001, 883, 386, 610.66, 938, '2011-02-04 17:03:34', '2020-07-02 02:25:46', '2024-05-10 22:08:02', '2001-01-29 06:48:32', '');
INSERT INTO `teacher_statistics` VALUES (16, '2024', '11', '2005-10', '9', 1000001, 925, 561, 773.45, 312, '2001-08-19 06:41:23', '2002-11-26 04:22:26', '2024-05-10 22:08:06', '2019-11-12 07:43:03', '');
INSERT INTO `teacher_statistics` VALUES (17, '2013', '10', '2012-03', '14', 1000001, 220, 589, 916.78, 65, '2003-08-20 14:23:14', '2007-08-08 22:36:47', '2024-05-10 22:08:15', '2008-10-10 14:10:10', '');
INSERT INTO `teacher_statistics` VALUES (18, '2012', '11', '2004-06', '3', 1000002, 201, 619, 19.83, 45, '2013-03-14 12:30:50', '2002-02-11 17:41:37', '2024-05-10 22:08:18', '2010-11-02 14:31:15', '');
INSERT INTO `teacher_statistics` VALUES (19, '2013', '10', '2008-08', '10', 1000001, 574, 113, 675.62, 86, '2003-06-05 19:58:55', '2014-02-18 02:02:51', '2024-05-10 22:08:22', '2019-06-01 03:41:13', '');
INSERT INTO `teacher_statistics` VALUES (20, '2022', '10', '2014-01', '3', 611, 941, 338, 779.27, 1000001, '2022-07-29 00:42:30', '2024-04-12 13:44:06', '2024-05-10 22:08:26', '2002-02-02 18:22:06', '');

SET FOREIGN_KEY_CHECKS = 1;
