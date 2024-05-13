package org.example.echo.mail.entity;

import lombok.Data;

@Data
public class Mail {
    private Long id;
    private String to;
    private Long toId;
    private String subject;
    private String content;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
