-- echo_interact.interact definition

CREATE TABLE `interact` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz` varchar(100) NOT NULL DEFAULT '' COMMENT '业务',
  `biz_id` bigint NOT NULL DEFAULT '0' COMMENT '业务id',
  `read_count` bigint NOT NULL DEFAULT '0' COMMENT '阅读计数',
  `like_count` bigint NOT NULL DEFAULT '0' COMMENT '点赞计数',
  `collect_count` varchar(100) DEFAULT '0' COMMENT '收藏计数',
  `comment_count` bigint NOT NULL DEFAULT '0' COMMENT '评论数',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `interact_unique` (`biz_id`,`biz`)
) ENGINE=InnoDB AUTO_INCREMENT=628 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='计数';


-- echo_interact.user_collect definition

CREATE TABLE `user_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户id',
  `biz` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '业务',
  `biz_id` bigint NOT NULL DEFAULT '0' COMMENT '业务主键',
  `cid` bigint NOT NULL DEFAULT '0' COMMENT '分类id',
  `status` bit(1) NOT NULL DEFAULT b'1' COMMENT '状态',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_interact_unique` (`user_id`,`biz`,`biz_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- echo_interact.user_like definition

CREATE TABLE `user_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户id',
  `biz` varchar(100) NOT NULL DEFAULT '' COMMENT '业务',
  `biz_id` bigint NOT NULL DEFAULT '0' COMMENT '业务主键',
  `status` bit(1) NOT NULL DEFAULT b'1' COMMENT '状态',
  `create_time` bigint DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_interact_unique` (`user_id`,`biz`,`biz_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;