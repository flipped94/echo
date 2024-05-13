package org.example.echo.search.converter;

import org.example.echo.sdk.article.ArticleEvent;
import org.example.echo.sdk.article.ArticleVO;
import org.example.echo.search.domain.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ArticleConverter {

    ArticleConverter INSTANCE = Mappers.getMapper(ArticleConverter.class);

    Article convert(ArticleEvent event);

    org.example.echo.search.entity.Article convert(Article article);

    @Mapping(source = "content", target = "content", ignore = true)
    ArticleVO toArticleVOIgnoreContent(org.example.echo.search.entity.Article article);

    List<ArticleVO> toArticleVOIgnoreContent(List<org.example.echo.search.entity.Article> article);
}
