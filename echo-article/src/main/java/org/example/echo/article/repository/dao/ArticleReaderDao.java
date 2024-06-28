package org.example.echo.article.repository.dao;

import org.example.common.vo.PageResult;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.PublishedArticle;

import java.util.List;

public interface ArticleReaderDao {

    long Upsert(PublishedArticle publishedArticle);

    long syncStatus(Long id, Long userId, int status);

    PublishedArticle getById(Long id);

    PageResult<PublishedArticle> readerPubList(ListReq req);

    List<PublishedArticle> listPub(long time,long days, long offset, long batchSize);
}
