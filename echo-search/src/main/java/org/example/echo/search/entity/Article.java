package org.example.echo.search.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Article {
    private String id;
    private String title;
    private String abstraction;
    private String content;
    private Integer status;
    private String[] tags;
    private Long authorId;
}
