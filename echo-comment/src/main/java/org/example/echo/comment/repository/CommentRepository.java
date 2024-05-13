package org.example.echo.comment.repository;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.example.common.vo.Result;
import org.example.echo.comment.converter.CommentConverter;
import org.example.echo.comment.domain.CommentForCreate;
import org.example.echo.comment.domain.CommentVO;
import org.example.echo.comment.domain.ReplyForCreate;
import org.example.echo.comment.domain.ReplyVO;
import org.example.echo.comment.entity.Comment;
import org.example.echo.comment.entity.CommentWithTotalReply;
import org.example.echo.comment.entity.Reply;
import org.example.echo.comment.events.CommentEventProducer;
import org.example.echo.comment.feign.AuthorFeign;
import org.example.echo.comment.feign.InteractFeign;
import org.example.echo.comment.helper.IdGenerator;
import org.example.echo.comment.repository.dao.CommentDao;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.example.echo.sdk.feed.FeedEvent;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class CommentRepository {

    private static final Logger log = LoggerFactory.getLogger(CommentRepository.class);
    @Resource
    private CommentDao commentDao;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private AuthorFeign authorFeign;

    @Resource
    private InteractFeign interactFeign;

    @Resource
    private CommentEventProducer commentEventProducer;

    @Resource(name = "customThreadPool")
    private Executor executor;

    public Long createComment(CommentForCreate commentForCreate) {
        final Comment entity = CommentConverter.INSTANCE.toEntity(commentForCreate);
        final Long commentId = commentDao.createComment(entity);
        final Long authorId = LoginUserContext.getUserId();
        executor.execute(() -> {
            try {
                final FeedEvent feedEvent = new FeedEvent();
                feedEvent.setUid(authorId);
                Map<String, String> ext = new HashMap<>();
                ext.put("biz", commentForCreate.getBiz());
                ext.put("bizId", String.valueOf(commentForCreate.getBizId()));
                feedEvent.setExt(ext);
                commentEventProducer.commentFeed(feedEvent);
            } catch (Exception e) {
                log.error("增加评论数失败 {}", e.getMessage(), e);
            }
        });
        return commentId;
    }

    public Long createReply(ReplyForCreate replyForCreate) {
        final Reply reply = CommentConverter.INSTANCE.toEntity(replyForCreate);
        reply.setId(idGenerator.nextId());
        return commentDao.createReply(reply);
    }

    public List<CommentVO> getMoreComments(String biz, Long bizId, Long minCommentId, int limit) {
        final List<CommentWithTotalReply> comments = commentDao.getMoreComment(biz, bizId, minCommentId, limit);
        List<CommentVO> result = CommentConverter.INSTANCE.toCommentVO(comments);
        setCommentMetaData(result);
        return result;
    }

    private void setCommentMetaData(List<CommentVO> commentVOS) {
        final Long authorId = LoginUserContext.getUserId();
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(() -> {
            setCommentAuthor(commentVOS);
            latch.countDown();
        });
        executor.execute(() -> {
            setCommentInteract(commentVOS, authorId);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("设置评论元数据失败 {}", e.getMessage(), e);
        }
    }

    private void setCommentAuthor(List<CommentVO> comments) {
        try {
            final Set<Long> authorIds = comments.stream().map(CommentVO::getAuthorId).collect(Collectors.toSet());
            comments.forEach(commentVO -> {
                final Set<Long> replyAuthorIds = commentVO.getReplies().stream()
                        .map(ReplyVO::getAuthorId).collect(Collectors.toSet());
                authorIds.addAll(replyAuthorIds);
                final Set<Long> replyToIds = commentVO.getReplies().stream()
                        .map(ReplyVO::getReplyToId).collect(Collectors.toSet());
                authorIds.addAll(replyToIds);
            });
            final Result<List<AuthorVO>> authorFeignByIds = authorFeign.getByIds(new Ids(authorIds));
            final List<AuthorVO> authorS = authorFeignByIds.getData();
            if (CollectionUtils.isNotEmpty(authorS)) {
                final Map<Long, AuthorVO> authorMap = authorFeignByIds.getData()
                        .stream()
                        .collect(Collectors.toMap(AuthorVO::getId, v -> v, (k1, k2) -> k2));
                comments.forEach(comment -> {
                    comment.setAuthor(authorMap.get(comment.getAuthorId()));
                    comment.getReplies().forEach(replyVO -> {
                        final AuthorVO authorVO = authorMap.get(replyVO.getAuthorId());
                        final AuthorVO replyToAuthor = authorMap.get(replyVO.getReplyToId());
                        replyVO.setAuthor(authorVO);
                        replyVO.setReplyTo(replyToAuthor);
                    });
                });
            }
        } catch (Exception e) {
            log.error("设置author失败 {}", e.getMessage(), e);
        }
    }

    private void setCommentInteract(List<CommentVO> comments, Long authorId) {
        try {
            final List<Long> ids = new ArrayList<>(comments.stream().map(CommentVO::getId).toList());
            comments.forEach(commentVO -> {
                final Set<Long> replyIds = commentVO.getReplies().stream().map(ReplyVO::getId).collect(Collectors.toSet());
                ids.addAll(replyIds);
            });
            final Result<List<Interact>> interacts = interactFeign.getByIds(new GetByIdsRequest(authorId, "comment", ids));
            final Map<Long, Interact> interactMap = interacts.getData().stream().collect(Collectors.toMap(Interact::getBizId, v -> v, (k1, k2) -> k1));
            comments.forEach(comment -> {
                final Interact interact = interactMap.getOrDefault(comment.getId(), Interact.defaultX("comment", comment.getId()));
                comment.setReadCount(interact.getReadCount());
                comment.setLikeCount(interact.getLikeCount());
                comment.setCollectCount(interact.getCollectCount());
                comment.setLiked(interact.getLiked());
                comment.setCollected(interact.getCollected());
                comment.setCommentCount(interact.getCommentCount());
                comment.getReplies().forEach(replyVO -> {
                    final Interact replyInteract = interactMap.getOrDefault(replyVO.getId(), Interact.defaultX("comment", replyVO.getId()));
                    replyVO.setReadCount(replyInteract.getReadCount());
                    replyVO.setLikeCount(replyInteract.getLikeCount());
                    replyVO.setCollectCount(replyInteract.getCollectCount());
                    replyVO.setLiked(replyInteract.getLiked());
                    replyVO.setCollected(replyInteract.getCollected());
                    replyVO.setCommentCount(replyInteract.getCommentCount());
                });
            });
        } catch (Exception e) {
            log.error("设置作点赞、收藏失败 {}", e.getMessage(), e);
        }
    }

    public List<ReplyVO> getMoreRely(String biz, Long bizId, Long rootCommentId, Long minReplyId) {
        final List<Reply> replies = commentDao.getMoreReply(biz, bizId, rootCommentId, minReplyId);
        List<ReplyVO> result = CommentConverter.INSTANCE.toReplyVO(replies);
        setReplyMetaData(result);
        return result;
    }

    private void setReplyMetaData(List<ReplyVO> replies) {
        final Long authorId = LoginUserContext.getUserId();
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(() -> {
            setReplyAuthor(replies);
            latch.countDown();
        });
        executor.execute(() -> {
            setReplyInteract(replies, authorId);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("设置评论元数据失败 {}", e.getMessage(), e);
        }
    }

    private void setReplyAuthor(List<ReplyVO> replies) {
        try {
            final Set<Long> authorIds = new HashSet<>();
            final Set<Long> auIds = replies.stream().map(ReplyVO::getAuthorId).collect(Collectors.toSet());
            final Set<Long> replyToIds = replies.stream().map(ReplyVO::getReplyToId).collect(Collectors.toSet());
            authorIds.addAll(auIds);
            authorIds.addAll(replyToIds);
            final Result<List<AuthorVO>> authors = authorFeign.getByIds(new Ids(authorIds));
            final Map<Long, AuthorVO> authorVOMap = authors.getData().stream().collect(Collectors.toMap(AuthorVO::getId, v -> v, (k1, k2) -> k1));
            replies.forEach(replyVO -> {
                final AuthorVO authorVO = authorVOMap.get(replyVO.getAuthorId());
                final AuthorVO replyTo = authorVOMap.get(replyVO.getReplyToId());
                replyVO.setAuthor(authorVO);
                replyVO.setReplyTo(replyTo);
            });
        } catch (Exception e) {
            log.error("批量查询作者信息失败, {}", e.getMessage(), e);
        }
    }

    private void setReplyInteract(List<ReplyVO> replyVOS, Long authorId) {
        try {
            final List<Long> ids = new ArrayList<>(replyVOS.stream().map(ReplyVO::getId).toList());
            final Result<List<Interact>> interacts = interactFeign.getByIds(new GetByIdsRequest(authorId, "comment", ids));
            final Map<Long, Interact> interactMap = interacts.getData().stream().collect(Collectors.toMap(Interact::getBizId, v -> v, (k1, k2) -> k1));
            replyVOS.forEach(reply -> {
                final Interact interact = interactMap.getOrDefault(reply.getId(), Interact.defaultX("comment", reply.getId()));
                reply.setReadCount(interact.getReadCount());
                reply.setLikeCount(interact.getLikeCount());
                reply.setCollectCount(interact.getCollectCount());
                reply.setLiked(interact.getLiked());
                reply.setCollected(interact.getCollected());
                reply.setCommentCount(interact.getCommentCount());
            });
        } catch (Exception e) {
            log.error("设置作点赞、收藏失败 {}", e.getMessage(), e);
        }

    }
}
