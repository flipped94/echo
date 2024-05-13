package org.example.echo.search.events;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataSet;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.article.ArticleEvent;
import org.example.echo.search.converter.ArticleConverter;
import org.example.echo.search.domain.Article;
import org.example.echo.search.service.SyncService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class ArticleConsumer {

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private SyncService syncService;

    @KafkaListener(topics = {"sync_article_event"}, groupId = "async_article_group")
    public void syncArticle(ArticleEvent event) {
        MutableDataSet options = new MutableDataSet();

        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        CountDownLatch latch = new CountDownLatch(2);

        executor.execute(() -> {
            try {
                Node abstractionNode = parser.parse(event.getAbstraction());
                String abstractionHtml = renderer.render(abstractionNode);
                final String abstractionText = Jsoup.clean(abstractionHtml, Safelist.simpleText());
                event.setAbstraction(abstractionText);
            } catch (Exception e) {
                log.error("markdown 摘要转纯文本失败 {}", e.getMessage());
            } finally {
                latch.countDown();
            }

        });
        executor.execute(() -> {
            try {
                Node contentNode = parser.parse(event.getContent());
                String contentHtml = renderer.render(contentNode);  // "<p>This is <em>Sparta</em></p>\n"
                final String contentText = Jsoup.clean(contentHtml, Safelist.simpleText());
                event.setContent(contentText);
            } catch (Exception e) {
                log.error("markdown内容转纯文本失败 {}", e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        final Article article = ArticleConverter.INSTANCE.convert(event);
        for (int i = 0; i < 3; i++) {
            try {
                syncService.inputArticle(article);
                break;
            } catch (Exception e) {
                log.error("article写入es失败 {}", e.getMessage(), e);
            }
        }
    }


}
