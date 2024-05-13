package org.example.echo.follow.entity;

import lombok.Data;

@Data
public class FollowRelation {
    private Long id;
    private Long follower;
    private Long followee;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
