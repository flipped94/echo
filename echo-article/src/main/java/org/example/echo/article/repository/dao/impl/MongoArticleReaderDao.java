package org.example.echo.article.repository.dao.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.example.common.util.MathUtil;
import org.example.common.vo.PageResult;
import org.example.echo.article.converter.ArticleConverter;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;
import org.example.echo.article.repository.dao.ArticleReaderDao;
import org.example.echo.mvcconfig.LoginUserContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component("mongoArticleReaderDao")
public class MongoArticleReaderDao implements ArticleReaderDao {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 发表文章
     *
     * @param publishedArticle 文章
     * @return 条数
     */
    @Override
    public long Upsert(PublishedArticle publishedArticle) {
        final Query query = new Query(Criteria.where("_id")
                .is(publishedArticle.get_id())
                .andOperator(Criteria.where("authorId").is(publishedArticle.getAuthorId())));
        final Update update = new Update()
                .set("title", publishedArticle.getTitle())
                .set("cover", publishedArticle.getCover())
                .set("abstraction", publishedArticle.getAbstraction())
                .set("authorId", publishedArticle.getAuthorId())
                .set("tags", publishedArticle.getTags())
                .set("content", publishedArticle.getContent())
                .set("status", publishedArticle.getStatus())
                .set("createTime", publishedArticle.getCreateTime())
                .set("updateTime", publishedArticle.getUpdateTime());
        final UpdateResult updateResult = mongoTemplate.upsert(query, update, PublishedArticle.class);
        if (null != updateResult.getUpsertedId()) {
            return updateResult.getUpsertedId().asInt64().getValue();
        }
        return updateResult.getModifiedCount();
    }

    /**
     * 同步状态
     *
     * @param id     文章id
     * @param userId 创作者id
     * @param status 状态
     * @return 修改条数
     */
    @Override
    public long syncStatus(Long id, Long userId, int status) {
        final Query query = new Query(Criteria.where("_id").is(id).andOperator(Criteria.where("authorId").is(LoginUserContext.getUserId())));
        final Update update = new Update().set("status", status).set("updateTime", System.currentTimeMillis());
        final UpdateResult updateResult = mongoTemplate.updateFirst(query, update, PublishedArticle.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public PublishedArticle getById(Long id) {
        final Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, PublishedArticle.class);
    }

    @Override
    public PageResult<PublishedArticle> readerPubList(ListReq req) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("published_articles");
        AggregateIterable<Document> result = collection.aggregate(buildPipeline(req));
        return buildResult(req, result);
    }

    @Override
    public List<PublishedArticle> listPub(long time, long days, long offset, long batchSize) {
        final List<Document> query = buildPipeline(time, days, offset, batchSize);
        MongoCollection<Document> collection = mongoTemplate.getCollection("published_articles");
        AggregateIterable<Document> datas = collection.aggregate((query));
        return buildResult(datas);
    }

    private static List<PublishedArticle> buildResult(AggregateIterable<Document> result) {
        List<PublishedArticle> res = new ArrayList<>();
        for (Document document : result) {
            final String json = document.toJson();
            final Gson gson = new Gson();
            final Article publishedArticle = gson.fromJson(json, Article.class);
            final PublishedArticle article = ArticleConverter.INSTANCE.toPublishedArticle(publishedArticle);
            res.add(article);
        }
        return res;
    }

    private static List<Document> buildPipeline(long time, long days, long offset, long batchSize) {
        return Arrays.asList(new Document("$match",
                        new Document("updateTime",
                                new Document("$gt", time - days))),
                new Document("$sort",
                        new Document("updateTime", -1L)),
                new Document("$skip", offset),
                new Document("$limit", batchSize));
    }

    private static List<Document> buildPipeline(ListReq req) {
        final long skip = (long) (req.getPage() - 1) * req.getPageSize();
        return Arrays.asList(new Document("$match", new Document()),
                new Document("$facet", new Document("total",
                        List.of(new Document("$count", "count")))
                        .append("paginated", Arrays.asList(new Document("$sort", new Document("updateTime", -1L)),
                                new Document("$skip", skip), new Document("$limit", req.getPageSize())))),
                new Document("$project", new Document("total", new Document("$arrayElemAt",
                        Arrays.asList("$total.count", 0L))).append("paginated", 1L))
        );
    }

    private static PageResult<PublishedArticle> buildResult(ListReq req, AggregateIterable<Document> result) {
        final Document document = result.first();
        final PageResult<PublishedArticle> articlePageResult = new PageResult<>();
        if (document != null) {
            final Integer total = document.get("total", Integer.class);
            final List<Document> paginated = (List<Document>) document.get("paginated");
            final List<PublishedArticle> data = paginated.stream().map(document1 -> {
                final String json = document1.toJson();
                final Gson gson = new Gson();
                final Article publishedArticle = gson.fromJson(json, Article.class);
                return ArticleConverter.INSTANCE.toPublishedArticle(publishedArticle);
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
}
