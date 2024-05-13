-- echo_follow.follow_relation definition

CREATE TABLE `follow_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `follower` bigint NOT NULL DEFAULT '0' COMMENT '关注者',
  `followee` bigint NOT NULL DEFAULT '0' COMMENT '被关注者',
  `status` int NOT NULL DEFAULT '0' COMMENT '是否关注',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `follower_followee` (`follower`,`followee`),
  KEY `followee` (`followee`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关系表';


-- echo_follow.follow_statics definition

CREATE TABLE `follow_statics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` bigint NOT NULL DEFAULT '0' COMMENT '用户id',
  `followers` bigint NOT NULL DEFAULT '0' COMMENT '粉丝数',
  `followees` bigint NOT NULL DEFAULT '0' COMMENT '关注数',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `follow_statics_unique` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关注统计表';