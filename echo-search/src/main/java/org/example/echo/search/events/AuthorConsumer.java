package org.example.echo.search.events;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.author.AuthorEvent;
import org.example.echo.search.converter.AuthorConverter;
import org.example.echo.search.domain.Author;
import org.example.echo.search.service.SyncService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Slf4j
@Component
public class AuthorConsumer {

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private SyncService syncService;

    @KafkaListener(topics = {"sync_author_event"}, groupId = "async_author_group")
    public void syncAuthor(AuthorEvent authorEvent) {
        Author author = AuthorConverter.INSTANCE.toAuthor(authorEvent);
        for (int i = 0; i < 3; i++) {
            try {
                syncService.inputAuthor(author);
                break;
            } catch (Exception e) {
                log.error("author写入es失败 {}", e.getMessage(), e);
            }
        }
    }


}
