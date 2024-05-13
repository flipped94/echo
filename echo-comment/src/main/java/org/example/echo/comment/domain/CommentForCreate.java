package org.example.echo.comment.domain;

import lombok.Data;

@Data
public class CommentForCreate {

    // 评论对象
    private String Biz;

    private Long bizId;

    // 评论内容
    private String Content;

    private String type;
}
