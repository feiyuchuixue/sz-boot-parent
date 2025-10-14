-- 学校用户绑定小程序用户
DROP TABLE IF EXISTS `school_user_binding`;
CREATE TABLE `school_user_binding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `school_user_id` bigint NOT NULL COMMENT '学校用户ID',
  `mini_user_id` bigint NOT NULL COMMENT '小程序用户ID',
  `bind_type` tinyint NOT NULL DEFAULT '2' COMMENT '绑定类型：1-主绑定（认证），2-辅助绑定（共享）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '绑定状态：0-待审核，1-审核通过，2-审核拒绝',
  `del_flag` enum('T','F') DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_school_user_id` (`school_user_id`),
  KEY `idx_mini_user_id` (`mini_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学校用户绑定小程序用户';