package org.example.echo.article.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "published_articles")
public class PublishedArticle {
    @Id
    private Long _id;
    private String cover;
    private String title;
    private String content;
    private String abstraction;
    private Long authorId;
    private List<String> tags;
    private int status;
    private long createTime;
    private long updateTime;
}

