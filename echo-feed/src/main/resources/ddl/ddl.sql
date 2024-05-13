-- echo_feed.feed_pull_event definition

CREATE TABLE `feed_pull_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL DEFAULT '0',
  `type` varchar(20) NOT NULL DEFAULT '' COMMENT '类型',
  `content` varchar(1024) NOT NULL DEFAULT '' COMMENT '内容json',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `feed_pull_event_uid_IDX` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='拉模型';


-- echo_feed.feed_push_event definition

CREATE TABLE `feed_push_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL DEFAULT '0',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '类型',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '内容json',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `feed_pull_event_uid_IDX` (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推模型，收件箱';