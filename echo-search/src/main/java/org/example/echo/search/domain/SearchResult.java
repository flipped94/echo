package org.example.echo.search.domain;

import lombok.Data;
import org.example.echo.sdk.article.ArticleVO;

import java.util.List;

@Data
public class SearchResult<T> {

    private List<ArticleVO> articles;
}
