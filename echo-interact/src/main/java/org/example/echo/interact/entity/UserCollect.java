package org.example.echo.interact.entity;

import lombok.Data;

@Data
public class UserCollect {
    private Long id;
    private Long userId;
    private String biz;
    private Long bizId;
    private Long cid;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
