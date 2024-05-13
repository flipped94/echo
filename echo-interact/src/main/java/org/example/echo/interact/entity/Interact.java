package org.example.echo.interact.entity;

import lombok.Data;

/**
 * 业务计数
 */
@Data
public class Interact {
    private Long id;
    private String biz;
    private Long bizId;
    private Long readCount;
    private Long likeCount;
    private Long collectCount;
    private Long commentCount;
    private Long createTime;
    private Long updateTime;
}
