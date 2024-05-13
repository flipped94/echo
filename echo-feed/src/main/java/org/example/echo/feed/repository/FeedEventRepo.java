package org.example.echo.feed.repository;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.example.echo.feed.converter.FeedEventConverter;
import org.example.echo.feed.entity.FeedPullEvent;
import org.example.echo.feed.entity.FeedPushEvent;
import org.example.echo.feed.repository.dao.FeedPullEventMapper;
import org.example.echo.feed.repository.dao.FeedPushEventMapper;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FeedEventRepo {

    @Resource
    private FeedPullEventMapper pullEventMapper;

    @Resource
    private FeedPushEventMapper pushEventMapper;

    public Long createPushEvents(List<FeedEvent> events) {
        final List<FeedPushEvent> pushEvents = events.stream()
                .map(FeedPushEvent::feedPushEvent)
                .toList();
        return pushEventMapper.insertBatch(pushEvents);
    }

    public Long createPullEvent(FeedEvent feedEvent) {
        return pullEventMapper.insert(FeedPullEvent.feedPullEvent(feedEvent));
    }

    public List<FeedEvent> findPushEventsWithType(String type, Long uid, long timestamp, int limit) {
        List<FeedPushEvent> events = pushEventMapper.getPushEventsWithType(type, uid, timestamp, limit);

        return FeedEventConverter.INSTANCE.pushEventsConvert(events);
    }

    public List<FeedEvent> findPullEventsWithType(String type, List<Long> followeeIds, long timestamp, int limit) {
        if (CollectionUtils.isEmpty(followeeIds)) {
            return Collections.emptyList();
        }
        List<FeedPullEvent> events = pullEventMapper.getPullEventsWithType(type, followeeIds, timestamp, limit);
        return FeedEventConverter.INSTANCE.pullEventsConvert(events);
    }
}
