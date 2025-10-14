-- 小程序文章表
DROP TABLE IF EXISTS `applet_article`;
CREATE TABLE `applet_article` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` varchar(50) DEFAULT NULL COMMENT '文章类型',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `avatar` varchar(500) DEFAULT NULL COMMENT '文章头图',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `time` varchar(50) DEFAULT NULL COMMENT '发布时间',
  `label` varchar(100) DEFAULT NULL COMMENT '标签',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `status` varchar(10) DEFAULT NULL COMMENT '状态（1-发布）',
  `content_type` varchar(20) DEFAULT NULL COMMENT '内容类型（link-链接, html-富文本）',
  `content` text COMMENT '内容（链接地址或富文本内容）',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶（0-否，1-是）',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `sort` int DEFAULT '0' COMMENT '排序',
  `del_flag` enum('T','F') DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
  `publish_id` bigint DEFAULT NULL COMMENT '发布人ID',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小程序文章表';

-- 小程序Banner表
DROP TABLE IF EXISTS `applet_banner`;
CREATE TABLE `applet_banner` (
 `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
 `link` varchar(500) DEFAULT NULL COMMENT '链接',
 `type` varchar(50) DEFAULT NULL COMMENT '类型',
 `names` varchar(255) DEFAULT NULL COMMENT '名称',
 `picture` varchar(500) DEFAULT NULL COMMENT '图片地址',
 `status` varchar(10) DEFAULT NULL COMMENT '状态（1-启用）',
 `sort` int DEFAULT '0' COMMENT '排序',
 `content_type` varchar(20) DEFAULT NULL COMMENT '内容类型（link-链接）',
 `content` text COMMENT '内容（链接地址）',
 `del_flag` enum('T','F') DEFAULT 'F' COMMENT '删除标识',
 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
 `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小程序Banner表';

-- 校友通行证申请表
DROP TABLE IF EXISTS `applet_alumni_pass_application`;
CREATE TABLE `applet_alumni_pass_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（关联用户表的外键）',
  `name` VARCHAR(50) NOT NULL COMMENT '申请人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '电话号码',
  `year` INT NULL COMMENT '毕业年份',
  `class_no` VARCHAR(20) NULL COMMENT '班级编号',
  `reason` VARCHAR(100) NOT NULL DEFAULT '其他' COMMENT '返校原因',
  `other_reason` TEXT NULL COMMENT '其他原因详情',
  `expected_time` DATE NOT NULL COMMENT '预计返校时间',
  `application_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态（0-待审核，1-已批准，2-已拒绝）',
  `use_status` TINYINT NOT NULL DEFAULT 3 COMMENT '使用状态（0-已归还，1-待归还，2-未归还，3-待处理）',
  `approver_id` BIGINT NULL COMMENT '审批人ID',
  `approve_time` DATETIME NULL COMMENT '审批时间',
  `approve_remark` TEXT NULL COMMENT '审批备注',
  `qr_code` VARCHAR(100) NULL COMMENT '二维码标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expected_time` (`expected_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='校友通行证申请表';
-- 学校师生表
DROP TABLE IF EXISTS `school_user`;
CREATE TABLE `school_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` varchar(255) NOT NULL COMMENT '姓名',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `identity` tinyint DEFAULT NULL COMMENT '身份（1-校友，2-教师）',
  `id_card` VARCHAR(50) NOT NULL COMMENT '身份证号',
  `student_id` VARCHAR(50) COMMENT '学号',
  `year` INT COMMENT '毕业年份',
  `class_no` VARCHAR(20) COMMENT '班级编号',
  `teacher_id` VARCHAR(50) COMMENT '教师编号',
  `del_flag` enum('T','F') DEFAULT 'F' COMMENT '删除标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态：0-待审核，1-审核通过，2-审核拒绝',
  `id_card_front` VARCHAR(255) COMMENT '身份证正面',
  `id_card_back` VARCHAR(255) COMMENT '身份证反面',
  `student_card` VARCHAR(255) COMMENT '学生证',
  `other_proof` VARCHAR(255) COMMENT '其他证明材料',
  `reviewer_id` bigint COMMENT '审核人ID',
  `review_time` datetime COMMENT '审核时间',
  `review_remark` VARCHAR(255) COMMENT '审核备注',
  `applicant_mini_user_id` bigint COMMENT '申请人微信用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  UNIQUE KEY `uk_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学校师生表';