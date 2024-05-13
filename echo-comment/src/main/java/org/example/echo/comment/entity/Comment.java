package org.example.echo.comment.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "comments")
public class Comment {

    @Id
    private Long _id;

    // 发表评论的人
    private Long authorId;

    // 被评价的东西
    private String biz;
    private Long bizId;

    private String content;

    private List<Reply> replies;

    private Long createTime;
    private Long updateTime;
}
