package org.example.echo.search.repository.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.search.entity.Article;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ArticleEsDao {

    @Resource
    private ElasticsearchClient elasticsearchClient;


    public void inputArticle(Article article) throws IOException {
        final IndexResponse response = elasticsearchClient.index(req ->
                req.index("article_index")
                        .id(String.valueOf(article.getId())).
                        document(article));

    }

    public List<Article> search(String[] keywords, Integer page, Integer limit) {
        String queryString = String.join(" ", keywords);
        final BoolQuery.Builder builder = new BoolQuery.Builder();
        builder.must(new MatchQuery.Builder().field("status").query(2).build()._toQuery());
        builder.should(new MatchQuery.Builder().field("title").query(queryString).build()._toQuery());
        builder.should(new MatchQuery.Builder().field("content").query(queryString).build()._toQuery());
        builder.should(new MatchQuery.Builder().field("abstraction").query(queryString).build()._toQuery());
        builder.should(new MatchQuery.Builder().field("tags").query(queryString).build()._toQuery());
        builder.minimumShouldMatch("1");

        try {
            final SearchResponse<Article> searchResponse = elasticsearchClient.search(s -> s
                            .index("article_index")
                            .query(builder.build()._toQuery())
                            .from((page - 1) * limit)
                            .size(limit),
                    Article.class
            );
            return searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());

        } catch (IOException e) {
            log.error("搜索失败 {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
