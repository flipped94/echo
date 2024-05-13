package org.example.echo.article.repository.dao;

import org.example.common.vo.PageResult;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.PublishedArticle;

public interface ArticleReaderDao {

    long Upsert(PublishedArticle publishedArticle);

    long syncStatus(Long id, Long userId, int status);

    PublishedArticle getById(Long id);

    PageResult<PublishedArticle> readerPubList(ListReq req);
}
