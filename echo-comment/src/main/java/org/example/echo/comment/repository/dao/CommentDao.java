package org.example.echo.comment.repository.dao;

import com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.echo.comment.entity.Comment;
import org.example.echo.comment.entity.CommentWithTotalReply;
import org.example.echo.comment.entity.Reply;
import org.example.echo.comment.helper.IdGenerator;
import org.example.echo.mvcconfig.LoginUserContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class CommentDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private IdGenerator idGenerator;

    public Long createComment(Comment entity) {
        entity.set_id(idGenerator.nextId());
        entity.setAuthorId(LoginUserContext.getUserId());
        final long now = System.currentTimeMillis();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setReplies(Collections.emptyList());
        mongoTemplate.insert(entity);
        return entity.get_id();
    }

    public Long createReply(Reply entity) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("comments");
        final Bson bizFilter = Filters.eq("biz", entity.getBiz());
        final Bson bizIdFilter = Filters.eq("bizId", entity.getBizId());
        final Bson rootIdFilter = Filters.eq("_id", entity.getRootId());
        final Bson filter = Filters.and(bizFilter, bizIdFilter, rootIdFilter);
        final long now = System.currentTimeMillis();
        Document reply = new Document("id", entity.getId())
                .append("authorId", LoginUserContext.getUserId())
                .append("replyToId", entity.getReplyToId())
                .append("biz", entity.getBiz())
                .append("bizId", entity.getBizId())
                .append("content", entity.getContent())
                .append("type", entity.getType())
                .append("createTime", now)
                .append("updateTime", now);
        final Bson update = Updates.push("replies", reply);
        final UpdateResult updateResult = collection.updateOne(filter, update);
        final long modifiedCount = updateResult.getModifiedCount();
        return modifiedCount;
    }

    public List<CommentWithTotalReply> getMoreComment(String biz, Long bizId, Long minCommentId, int limit) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("comments");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("biz", biz)
                                .append("bizId", bizId)
                                .append("_id",
                                        new Document("$lt", minCommentId))),
                new Document("$addFields",
                        new Document("totalReplies",
                                new Document("$size", "$replies"))),
                new Document("$sort",
                        new Document("createTime", -1L)),
                new Document("$limit", limit),
                new Document("$project",
                        new Document("_id", 1L)
                                .append("authorId", 1L)
                                .append("biz", 1L)
                                .append("bizId", 1L)
                                .append("content", 1L)
                                .append("createTime", 1L)
                                .append("updateTime", 1L)
                                .append("totalReplies", 1L)
                                .append("replies",
                                        new Document("$sortArray",
                                                new Document("input", "$replies")
                                                        .append("sortBy",
                                                                new Document("createTime", -1L))))),
                new Document("$project",
                        new Document("_id", 1L)
                                .append("authorId", 1L)
                                .append("biz", 1L)
                                .append("bizId", 1L)
                                .append("content", 1L)
                                .append("createTime", 1L)
                                .append("updateTime", 1L)
                                .append("totalReplies", 1L)
                                .append("replies",
                                        new Document("$slice", Arrays.asList("$replies", limit))))));


        List<CommentWithTotalReply> comments = new ArrayList<>(limit);
        final Gson gson = new Gson();
        result.forEach(document -> {
            final String s = document.toJson();
            final CommentWithTotalReply comment = gson.fromJson(s, CommentWithTotalReply.class);
            comments.add(comment);
        });
        return comments;
    }

    public List<Reply> getMoreReply(String biz, Long bizId, Long rootCommentId, Long minReplyId) {

        MongoCollection<Document> collection = mongoTemplate.getCollection("comments");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("biz", biz)
                                .append("bizId", bizId)
                                .append("_id",
                                        new Document("$eq", rootCommentId))),
                new Document("$project",
                        new Document("_id", 0L)
                                .append("replies",
                                        new Document("$filter",
                                                new Document("input", "$replies")
                                                        .append("as", "item")
                                                        .append("cond",
                                                                new Document("$lt", Arrays.asList("$$item.id", minReplyId)))))),
                new Document("$project",
                        new Document("replies",
                                new Document("$sortArray",
                                        new Document("input", "$replies")
                                                .append("sortBy",
                                                        new Document("createTime", -1L)))))));
        List<Reply> replies = new ArrayList<>();
        final Gson gson = new Gson();
        result.forEach(document -> {
            final String s = document.toJson();
            final Reply reply = gson.fromJson(s, Reply.class);
            replies.add(reply);
        });

        return replies;
    }
}
