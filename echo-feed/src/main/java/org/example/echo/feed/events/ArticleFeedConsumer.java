package org.example.echo.feed.events;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.feed.service.FeedService;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleFeedConsumer {

    @Resource
    private FeedService feedService;

    @KafkaListener(topics = {"article_feed_event"}, groupId = "feed_group")
    public void createFeedEvent(FeedEvent event) {
        for (int i = 0; i < 3; i++) {
            try {
                Long count = feedService.CreateFeedEvent(event);
                if (count > 0) {
                    break;
                }
            } catch (Exception e) {
                log.error("feed写入失败 {}", e.getMessage());
            }
        }
    }

}
