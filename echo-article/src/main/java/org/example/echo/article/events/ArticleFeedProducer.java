package org.example.echo.article.events;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.article.ArticleEvent;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleFeedProducer {


    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void articlePublishFeed(FeedEvent feedEvent) {
        for (int i = 0; i < 3; i++) {
            try {
                kafkaTemplate.send("article_feed_event", feedEvent);
                break;
            } catch (Exception e) {
                log.error("发送article_feed_event第{}失败: {}", i, e.getMessage());
            }

        }
    }


    public void syncArticleEvent(ArticleEvent articleEvent) {
        for (int i = 0; i < 3; i++) {
            try {
                kafkaTemplate.send("sync_article_event", articleEvent);
                break;
            } catch (Exception e) {
                log.error("发送sync_article_event第{}失败: {}", i, e.getMessage());
            }
        }
    }
}
