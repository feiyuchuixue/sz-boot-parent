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

 Date: 11/05/2024 14:39:03
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
INSERT INTO `sys_dept` VALUES (4, '研发团队', 1, 4, 100, 'T', 'F', 'F', '', 1, 1, '2024-05-10 21:40:29', '2024-05-11 14:30:38');
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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$EZ9YQ.qlyrpTLs5FaIBS/uFdXQKrVXvWSwwUT7H3AjHx2aMiH3LVm', '19988887777', '系统管理员', 1, '2022-01-01', 'http://59.110.220.106:9001/sz-admin-test/user/20240420/b2f4c7d4-cf47-4a26-a714-4974005b789d.jpg', 1, '', '', '1000001', '1001003', '2024-02-02 13:36:04', '2023-08-18 11:15:10', '2024-05-10 22:24:53', 'F', NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$Km4pU/DdW/.LXRYgR446S.HCdcjIHkp7uFisXtCVoaXyXfveBHjlO', NULL, '测试用户', 1, '2024-01-01', NULL, 0, NULL, NULL, '1000001', '1001003', NULL, '2024-05-09 21:50:02', '2024-05-10 22:30:54', 'F', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `dept_id` int NULL DEFAULT NULL COMMENT 'sys_dept_id',
                                  `user_id` int NULL DEFAULT NULL COMMENT 'sys_user_id',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-部门关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
INSERT INTO `sys_user_dept` VALUES (1, 4, 2);

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
