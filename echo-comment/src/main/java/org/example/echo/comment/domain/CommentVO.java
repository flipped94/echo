package org.example.echo.comment.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.example.echo.sdk.author.AuthorVO;

import java.util.List;

@Data
public class CommentVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 评论者
    private AuthorVO author;

    private Long authorId;
    // 评论对象
    private String biz;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    // 评论内容
    private String content;

    private List<ReplyVO> replies;

    private Long totalReplies;

    private Long createTime;
    private Long updateTime;

    private Long readCount;
    private Long likeCount;
    private Long collectCount;
    private Long commentCount;
    private Boolean liked;
    private Boolean collected;
}
