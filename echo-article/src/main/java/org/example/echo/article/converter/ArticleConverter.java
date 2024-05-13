package org.example.echo.article.converter;

import org.example.echo.article.domain.ArticleForPublish;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;
import org.example.echo.sdk.article.ArticleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleConverter {

    ArticleConverter INSTANCE = Mappers.getMapper(ArticleConverter.class);

    @Mapping(source = "id", target = "_id")
    Article toArticleEntity(ArticleForPublish articleForPublish);

    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVO(Article article);

    @Mapping(source = "content", target = "content", ignore = true)
    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVOIgnoreContent(Article article);

    @Mapping(source = "abstraction", target = "abstraction", ignore = true)
    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVOIgnoreAbstraction(Article article);

    @Mapping(source = "abstraction", target = "abstraction", ignore = true)
    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVOIgnoreAbstraction(PublishedArticle article);

    @Mapping(source = "content", target = "content", ignore = true)
    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVOIgnoreContent(PublishedArticle article);

    @Mapping(source = "_id", target = "id")
    ArticleVO toArticleVO(PublishedArticle article);

    PublishedArticle toPublishedArticle(Article article);
}
