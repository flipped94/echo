package org.example.echo.article.service;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.vo.PageResult;
import org.example.echo.article.converter.ArticleConverter;
import org.example.echo.article.domain.ArticleForPublish;
import org.example.echo.article.domain.ArticleForSave;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;
import org.example.echo.article.enums.ArticleStatusEnum;
import org.example.echo.article.events.ArticleFeedProducer;
import org.example.echo.article.repository.ArticleAuthorRepository;
import org.example.echo.article.repository.ArticleReaderRepository;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleEvent;
import org.example.echo.sdk.article.ArticleVO;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class ArticleService {

    @Resource
    private ArticleAuthorRepository articleAuthorRepository;

    @Resource
    private ArticleReaderRepository articleReaderRepository;

    @Resource(name = "customThreadPool")
    private Executor executor;

    private static final String ARTICLE_FEED_EVENT = "article_feed_event";

    @Resource
    private ArticleFeedProducer articleFeedProducer;


    public Long publish(ArticleForPublish articleForPublish) {
        articleForPublish.setStatus(ArticleStatusEnum.PUBLISHED.getCode());
        Article article = ArticleConverter.INSTANCE.toArticleEntity(articleForPublish);
        Long articleId = saveToAuthorDBSync(article);
        PublishedArticle publishedArticle = ArticleConverter.INSTANCE.toPublishedArticle(article);
        saveToReaderDBAsync(publishedArticle);
        publishArticleFeedAsync(publishedArticle);
        return articleId;
    }

    private void publishArticleFeedAsync(PublishedArticle publishedArticle) {
        executor.execute(() -> {
            final Gson gson = new Gson();
            Map<String, String> ext = new HashMap<>();
            ext.put("biz", "article");
            ext.put("bizId", String.valueOf(publishedArticle.get_id()));
            ext.put("cover", publishedArticle.getCover());
            ext.put("title", publishedArticle.getTitle());
            ext.put("abstraction", publishedArticle.getAbstraction());
            ext.put("tags", gson.toJson(publishedArticle.getTags()));
            ext.put("createTime", String.valueOf(publishedArticle.getCreateTime()));
            FeedEvent feedEvent = new FeedEvent(publishedArticle.getAuthorId(), ARTICLE_FEED_EVENT, publishedArticle.getCreateTime(), ext);
            articleFeedProducer.articlePublishFeed(feedEvent);
            sendEsEvent(publishedArticle);
        });
    }

    private void sendEsEvent(PublishedArticle publishedArticle) {
        articleFeedProducer.syncArticleEvent(
                new ArticleEvent(
                        publishedArticle.get_id(),
                        publishedArticle.getTitle(),
                        publishedArticle.getAbstraction(),
                        publishedArticle.getContent(),
                        publishedArticle.getStatus(),
                        publishedArticle.getTags().toArray(new String[0]),
                        publishedArticle.getAuthorId())
        );
    }


    private void saveToReaderDBAsync(PublishedArticle publishedArticle) {
        // 在线库，失败重试
        executor.execute(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    long modified = articleReaderRepository.save(publishedArticle);
                    if (modified > 0) {
                        break;
                    }
                } catch (Exception e) {
                    log.error("发布文章失败，文章ID：{}", publishedArticle, e);
                }
            }
        });
    }

    private Long saveToAuthorDBSync(Article article) {
        try {
            if (article.get_id() == null) {
                return articleAuthorRepository.create(article);
            } else {
                long modifiedCount = articleAuthorRepository.update(article);
                if (modifiedCount <= 0) {
                    throw new BusinessException(BizCodeEnum.ARTICLE_SAVE_FAIL);
                }
                return article.get_id();
            }
        } catch (Exception e) {
            throw new BusinessException(BizCodeEnum.ARTICLE_SAVE_FAIL);
        }
    }


    public Long save(ArticleForSave articleForSave) {
        articleForSave.setStatus(ArticleStatusEnum.DRAFT.getCode());
        Article article = ArticleConverter.INSTANCE.toArticleEntity(articleForSave);
        saveToAuthorDBSync(article);
        return article.get_id();
    }

    /**
     * 设置为仅自己可见
     *
     * @param id 文章id
     * @return 设置是否成功
     */
    public Boolean syncStatus(Long id, Integer status) {
        final long modifiedCount = articleAuthorRepository.syncStatus(id, LoginUserContext.getUserId(), status);
        if (modifiedCount <= 0) {
            throw new BusinessException(BizCodeEnum.ARTICLE_WITHDRAW_FAIL);
        }
        articleReaderRepository.syncStatus(id, LoginUserContext.getUserId(), status);

        return Boolean.TRUE;
    }

    public PageResult<ArticleVO> listByAuthor(ListReq req, Long userId) {
        return articleAuthorRepository.listByAuthor(req, userId);
    }


    public ArticleVO getByIdAndAuthorId(Long id, Long authorId) {
        return articleAuthorRepository.getByIdAndAuthorId(id, authorId);
    }

    public ArticleVO getPubById(Long id) {
        return articleReaderRepository.getById(id);
    }

    public PageResult<ArticleVO> readerPubList(ListReq req) {
        return articleReaderRepository.readerPubList(req);
    }

    public List<ArticleVO> listPub(long time, long days, long offset, long batchSize) {
        return articleReaderRepository.listPub(time, days, offset, batchSize);
    }
}
