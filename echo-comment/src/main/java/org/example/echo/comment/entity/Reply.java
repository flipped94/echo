package org.example.echo.comment.entity;

import lombok.Data;

@Data
public class Reply {

    private Long id;

    private String biz;

    private Long bizId;

    // 发表评论的人
    private Long authorId;

    // 回复对象
    private Long replyToId;

    // 评论或回复
    private String type;

    // 内容
    private String content;

    private Long createTime;

    private Long updateTime;

    private Long rootId;
}
