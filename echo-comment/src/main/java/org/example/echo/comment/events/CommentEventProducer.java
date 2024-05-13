package org.example.echo.comment.events;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventProducer {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void commentFeed(FeedEvent feedEvent) {
        for (int i = 0; i < 3; i++) {
            try {
                kafkaTemplate.send("comment_feed_event", feedEvent);
                break;
            } catch (Exception e) {
                log.error("发送comment_feed_event第{}失败: {}", i, e.getMessage());
            }

        }
    }
}
