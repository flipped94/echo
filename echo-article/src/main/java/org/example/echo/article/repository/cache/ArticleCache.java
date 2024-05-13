package org.example.echo.article.repository.cache;

import org.example.common.vo.PageResult;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;

public interface ArticleCache {

    PageResult<Article> getFirstPage(Long authorId);

    void setFirstPage(Long authorId, PageResult<Article> page);

    void delFirstPage(Long authorId);

    Article get(Long id, Long authorId);

    void set(Article article, Long authorId);

    PublishedArticle getPub(Long id);

    void setPub(PublishedArticle articleForPublish);

    void delPub(Long id);
}
