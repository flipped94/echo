package org.example.echo.comment.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplyForCreate {

    private String biz;

    private Long bizId;

    private Long replyToId;

    private Long rootId;

    private String content;

    // 评论或回复
    private String type;
}
