package org.example.echo.feed.service;

import jakarta.annotation.Resource;
import org.example.common.vo.PageResult;
import org.example.common.vo.Result;
import org.example.echo.feed.feign.FollowFeign;
import org.example.echo.feed.repository.FeedEventRepo;
import org.example.echo.sdk.feed.FeedEvent;
import org.example.echo.sdk.follow.FollowRelation;
import org.example.echo.sdk.follow.FollowStatics;
import org.example.echo.sdk.follow.GetFolloweeRequest;
import org.example.echo.sdk.follow.GetFollowerRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component(value = "article_feed_event")
public class ArticleEventHandler implements FeedHandler {

    private static final int THRESHOLD = 32;

    public static final String ARTICLE_FEED_EVENT = "article_feed_event";

    @Resource
    private FollowFeign followFeign;

    @Resource
    private FeedEventRepo feedEventRepo;

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Override
    public Long createFeedEvent(FeedEvent feedEvent) {
        final Long followee = feedEvent.getUid();
        final Result<FollowStatics> followStatic = followFeign.getFollowStatic(followee);
        // 粉丝数超过阈值，然后读扩散，不然写扩散
        if (followStatic.getData().getFollowers() == 0) {
            return 0L;
        } else if (followStatic.getData().getFollowers() > THRESHOLD) {
            return feedEventRepo.createPullEvent(feedEvent);
        } else {
            final Result<PageResult<FollowRelation>> follower =
                    followFeign.getFollower(followee, new GetFollowerRequest(1, 32));
            final List<FeedEvent> events = follower.getData()
                    .getData().stream()
                    .map(followRelation -> new FeedEvent(followRelation.getFollower(), ARTICLE_FEED_EVENT, feedEvent.getCreateTime(), feedEvent.getExt()))
                    .toList();
            return feedEventRepo.createPushEvents(events);
        }
    }

    @Override
    public List<FeedEvent> getFeedEventList(Long uid, long timestamp, int limit) {
        Lock lock = new ReentrantLock();
        CountDownLatch latch = new CountDownLatch(2);
        List<FeedEvent> res = new ArrayList<>(limit * 2);
        // Push Event
        executor.execute(() -> {
            List<FeedEvent> events = feedEventRepo.findPushEventsWithType(ARTICLE_FEED_EVENT, uid, timestamp, limit);
            latch.countDown();
            lock.lock();
            res.addAll(events);
            lock.unlock();
        });
        // Pull Event
        executor.execute(() -> {
            try {
                final Result<PageResult<FollowRelation>> followee = followFeign.getFollowee(uid, new GetFolloweeRequest(1, 200));
                final List<Long> followeeIds = followee.getData()
                        .getData()
                        .stream()
                        .map(FollowRelation::getFollowee).toList();
                List<FeedEvent> events = feedEventRepo.findPullEventsWithType(ARTICLE_FEED_EVENT, followeeIds, timestamp, limit);
                lock.lock();
                res.addAll(events);
                lock.unlock();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
