package org.example.echo.author.event;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.author.AuthorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorProducer {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;


    public void syncAuthorEvent(AuthorEvent authorEvent) {
        for (int i = 0; i < 3; i++) {
            try {
                kafkaTemplate.send("sync_author_event", authorEvent);
                break;
            } catch (Exception e) {
                log.error("发送sync_article_event第{}失败: {}", i, e.getMessage());
            }
        }
    }
}
