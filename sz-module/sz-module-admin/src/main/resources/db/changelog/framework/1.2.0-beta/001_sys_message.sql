--liquibase formatted sql

--changeset 升职哦（sz）:20250424_1750
CREATE TABLE IF NOT EXISTS `sys_message` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                               `message_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息类型',
                               `sender_id` bigint NOT NULL COMMENT '发送人ID',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
                               `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `create_id` bigint DEFAULT NULL COMMENT '创建人 ID',
                               `update_id` bigint DEFAULT NULL COMMENT '更新人 ID',
                               `menu_router` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单路由地址，冗余',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息管理';

CREATE TABLE IF NOT EXISTS `sys_message_user` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `message_id` bigint NOT NULL COMMENT '消息ID',
                                    `receiver_id` bigint NOT NULL COMMENT '接收人ID',
                                    `is_read` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否已读',
                                    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
                                    `del_flag` enum('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息接收用户表';


INSERT IGNORE INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`, `type`) VALUES (1008, '消息类型', 'message_type', 'F', 'T', 'F', '系统消息的类型（待办、通知等）', '2025-04-21T15:22:15', NULL, NULL, NULL, NULL, NULL, 'system');

INSERT IGNORE INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1008001, '1008', '待办', 'todo', 1, 'warning', '', 'F', 'T', 'F', '2025-04-21T15:22:45', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (1008002, '1008', '消息', 'msg', 2, 'success', '', 'F', 'T', 'F', '2025-04-21T15:23:01', NULL, NULL, NULL, NULL, NULL);

INSERT IGNORE INTO `sys_message` (`id`, `message_type_cd`, `sender_id`, `title`, `content`, `del_flag`, `create_time`, `update_time`, `create_id`, `update_id`, `menu_router`) VALUES (1, 'msg', 1, '欢迎使用sz-admin', '如果喜欢这个项目或支持作者，欢迎Star、Fork、Watch 一键三连 🚀！！', 'F', '2025-04-24 16:14:25', '2025-04-24 16:14:25', 1, 1, NULL);
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (1, 1, 1, 'F', NULL, 'F');
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (2, 1, 2, 'F', NULL, 'F');
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (3, 1, 3, 'F', NULL, 'F');
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (4, 1, 4, 'F', NULL, 'F');
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (5, 1, 5, 'F', NULL, 'F');
INSERT IGNORE INTO `sys_message_user` (`id`, `message_id`, `receiver_id`, `is_read`, `read_time`, `del_flag`) VALUES (6, 1, 6, 'F', NULL, 'F');