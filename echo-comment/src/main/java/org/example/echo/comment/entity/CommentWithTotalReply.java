package org.example.echo.comment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentWithTotalReply extends Comment {
    private Long totalReplies;
}
