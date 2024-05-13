package org.example.echo.feed.service;

import org.example.echo.sdk.feed.FeedEvent;

import java.util.List;

public interface FeedHandler {

    Long createFeedEvent(FeedEvent event);

    List<FeedEvent> getFeedEventList(Long uid, long timestamp, int limit);
}
