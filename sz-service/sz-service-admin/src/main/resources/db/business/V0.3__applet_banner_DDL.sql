-- 小程序Banner表
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
  `del_flag` varchar(10) DEFAULT '0' COMMENT '删除标识',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_id` bigint DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`)
) COMMENT='小程序Banner表';