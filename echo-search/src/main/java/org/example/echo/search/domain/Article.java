package org.example.echo.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Article {
    private Long id;
    private String title;
    private String abstraction;
    private String content;
    private Integer status;
    private String[] tags;
    private Long authorId;
}
