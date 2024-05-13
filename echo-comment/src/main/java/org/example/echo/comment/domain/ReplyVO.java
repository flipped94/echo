package org.example.echo.comment.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.example.echo.sdk.author.AuthorVO;

@Data
public class ReplyVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    private Long authorId;

    private AuthorVO author;

    private Long replyToId;

    private AuthorVO replyTo;

    // 评论或回复
    private String type;

    private String content;

    private Long createTime;
    private Long updateTime;

    private Long readCount;
    private Long likeCount;
    private Long collectCount;
    private Long commentCount;
    private Boolean liked;
    private Boolean collected;
}
