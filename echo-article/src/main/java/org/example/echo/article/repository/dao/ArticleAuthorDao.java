package org.example.echo.article.repository.dao;

import org.example.common.vo.PageResult;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.Article;

public interface ArticleAuthorDao {

    Long create(Article article);

    Long updateByIdAndAuthor(Article Article);

    PageResult<Article> listByAuthor(ListReq req, Long userId);

    Article getById(Long id);

    long syncStatus(Long id, Long authorId, Integer status);
}
