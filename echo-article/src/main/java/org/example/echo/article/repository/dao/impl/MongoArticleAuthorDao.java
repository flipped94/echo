package org.example.echo.article.repository.dao.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.example.common.util.MathUtil;
import org.example.common.vo.PageResult;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;
import org.example.echo.article.helper.IdGenerator;
import org.example.echo.article.repository.dao.ArticleAuthorDao;
import org.example.echo.mvcconfig.LoginUserContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component("mongoArticleAuthorDao")
public class MongoArticleAuthorDao implements ArticleAuthorDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private IdGenerator idGenerator;

    @Override
    public Long create(Article article) {
        long now = System.currentTimeMillis();
        article.set_id(idGenerator.nextId());
        article.setCreateTime(now);
        article.setUpdateTime(now);
        article.setAuthorId(LoginUserContext.getUserId());
        mongoTemplate.insert(article);
        return article.get_id();
    }

    @Override
    public Long updateByIdAndAuthor(Article article) {
        final Query query = new Query(Criteria.where("_id").is(article.get_id())
                .andOperator(Criteria.where("authorId").is(LoginUserContext.getUserId()))
        );
        final Update update = new Update()
                .set("cover", article.getCover())
                .set("title", article.getTitle())
                .set("content", article.getContent())
                .set("abstraction", article.getAbstraction())
                .set("tags", article.getTags())
                .set("status", article.getStatus())
                .set("updateTime", System.currentTimeMillis());
        final UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Article.class);
        return updateResult.getModifiedCount();
    }

    public PageResult<Article> listByAuthor(ListReq req, Long userId) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("articles");
        AggregateIterable<Document> result = collection.aggregate(buildPipeline(req, userId));
        return buildResult(req, result);
    }

    @Override
    public Article getById(Long id) {
        final Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Article.class);
    }

    @Override
    public long syncStatus(Long id, Long authorId, Integer status) {
        final Query query = new Query(Criteria.where("_id").is(id)
                .andOperator(Criteria.where("authorId").is(LoginUserContext.getUserId()))
        );
        final Update update = new Update()
                .set("status", status)
                .set("updateTime", System.currentTimeMillis());
        final UpdateResult updateResult = mongoTemplate.updateFirst(query, update, PublishedArticle.class);
        return updateResult.getModifiedCount();
    }

    private static PageResult<Article> buildResult(ListReq req, AggregateIterable<Document> result) {
        final Document document = result.first();
        final PageResult<Article> articlePageResult = new PageResult<>();
        if (document != null) {
            final Integer total = document.get("total", Integer.class);
            final List<Document> paginated = (List<Document>) document.get("paginated");
            final List<Article> data = paginated.stream().map(document1 -> {
                final String json = document1.toJson();
                final Gson gson = new Gson();
                return gson.fromJson(json, Article.class);
            }).toList();
            articlePageResult.setPageNum(req.getPage());
            articlePageResult.setPageSize(req.getPageSize());
            articlePageResult.setTotal(total == null ? 0 : total);
            articlePageResult.setPages(total == null ? 0 : MathUtil.ceil(total, req.getPageSize()));
            articlePageResult.setData(data);
        } else {
            articlePageResult.setPageNum(req.getPage());
            articlePageResult.setPageSize(req.getPageSize());
            articlePageResult.setTotal(0);
            articlePageResult.setPages(0);
            articlePageResult.setData(Collections.emptyList());
        }

        return articlePageResult;
    }

    private static List<Document> buildPipeline(ListReq req, Long userId) {
        final long skip = (req.getPage() - 1) * req.getPageSize();
        return Arrays.asList(new Document("$match",
                        new Document("authorId", userId)),
                new Document("$facet",
                        new Document("total", Arrays.asList(new Document("$count", "count")))
                                .append("paginated", Arrays.asList(new Document("$sort",
                                                new Document("updateTime", -1L)),
                                        new Document("$skip", skip),
                                        new Document("$limit", req.getPageSize())))),
                new Document("$project",
                        new Document("total",
                                new Document("$arrayElemAt", Arrays.asList("$total.count", 0L)))
                                .append("paginated", 1L)));
    }
}
