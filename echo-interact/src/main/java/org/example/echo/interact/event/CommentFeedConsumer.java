package org.example.echo.interact.event;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.interact.service.InteractService;
import org.example.echo.sdk.feed.FeedEvent;
import org.example.echo.sdk.interact.InteractReq;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentFeedConsumer {

    @Resource
    private InteractService interactService;

    @KafkaListener(topics = {"comment_feed_event"}, groupId = "feed_group")
    public void createFeedEvent(FeedEvent event) {
        for (int i = 0; i < 3; i++) {
            try {
                final String biz = event.getExt().get("biz");
                final Long bizId = Long.valueOf(event.getExt().get("bizId"));
                interactService.incrCommentCount(new InteractReq(biz, bizId, event.getUid()));
                break;
            } catch (Exception e) {
                log.error("feed写入失败 {}", e.getMessage());
            }
        }
    }

}
