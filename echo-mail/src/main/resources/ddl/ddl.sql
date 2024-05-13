-- echo_mail.mail definition

CREATE TABLE `mail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `to` varchar(100) NOT NULL DEFAULT '' COMMENT '收件箱',
  `to_id` bigint NOT NULL DEFAULT '0' COMMENT '收件人id',
  `subject` varchar(100) DEFAULT '' COMMENT '邮件主题',
  `content` varchar(1024) NOT NULL DEFAULT '' COMMENT '邮件内容',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `mail_status_IDX` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮件';