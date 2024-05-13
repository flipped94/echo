package org.example.echo.sdk.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticleEvent {
    private Long id;
    private String title;
    private String abstraction;
    private String content;
    private Integer status;
    private String[] tags;
    private Long authorId;
}
