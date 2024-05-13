package org.example.echo.interact.entity;

import lombok.Data;

@Data
public class UserLike {
    private Long id;
    private Long userId;
    private String biz;
    private Long bizId;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
