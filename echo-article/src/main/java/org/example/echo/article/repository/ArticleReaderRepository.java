package org.example.echo.article.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.vo.PageResult;
import org.example.common.vo.Result;
import org.example.echo.article.converter.ArticleConverter;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.PublishedArticle;
import org.example.echo.article.feign.AuthorFeign;
import org.example.echo.article.feign.InteractFeign;
import org.example.echo.article.repository.cache.ArticleCache;
import org.example.echo.article.repository.dao.ArticleReaderDao;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleVO;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ArticleReaderRepository {

    @Resource
    private ArticleReaderDao articleReaderDao;

    @Resource
    private ArticleCache articleCache;

    @Resource
    private AuthorFeign authorFeign;

    @Resource
    private InteractFeign interactFeign;

    @Resource(name = "customThreadPool")
    private Executor executor;

    public long save(PublishedArticle publishedArticle) {
        return articleReaderDao.Upsert(publishedArticle);
    }

    public long syncStatus(Long id, Long userId, int status) {
        return articleReaderDao.syncStatus(id, userId, status);
    }

    public ArticleVO getById(Long id) {
        PublishedArticle articleFromCache = articleCache.getPub(id);
        if (articleFromCache != null) {
            final ArticleVO articleVO = ArticleConverter.INSTANCE.toArticleVOIgnoreAbstraction(articleFromCache);
            setMetaData(List.of(articleVO));
            return articleVO;
        }
        final PublishedArticle articleFromDB = articleReaderDao.getById(id);
        if (articleFromDB != null) {
            ArticleVO art = ArticleConverter.INSTANCE.toArticleVOIgnoreAbstraction(articleFromDB);
            setMetaData(List.of(art));
            executor.execute(() -> {
                try {
                    articleCache.setPub(articleFromDB);
                } catch (Exception e) {
                    log.error("文章缓存失败 {}", e.getMessage(), e);
                }
            });
            return art;
        }
        throw new BusinessException(BizCodeEnum.ARTICLE_NOT_FOUND);
    }

    public PageResult<ArticleVO> readerPubList(ListReq req) {
        final PageResult<PublishedArticle> articlePageResult = articleReaderDao.readerPubList(req);
        return convert(articlePageResult);
    }

    private PageResult<ArticleVO> convert(PageResult<PublishedArticle> pageResult) {
        PageResult<ArticleVO> result = new PageResult<>();
        result.setPageNum(pageResult.getPageNum());
        result.setPageSize(pageResult.getPageSize());
        result.setPages(pageResult.getPages());
        result.setTotal(pageResult.getTotal());
        final List<ArticleVO> datas = pageResult.getData()
                .stream()
                .map(ArticleConverter.INSTANCE::toArticleVOIgnoreContent).toList();
        setMetaData(datas);
        result.setData(datas);
        return result;
    }

    /**
     * 设置作者和点赞、收藏
     *
     * @param datas
     */
    private void setMetaData(List<ArticleVO> datas) {
        CountDownLatch latch = new CountDownLatch(2);
        final Long authorId = LoginUserContext.getUserId();
        executor.execute(() -> {
            setAuthor(datas);
            latch.countDown();
        });
        executor.execute(() -> {
            setInteract(datas, authorId);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("设置作者和点赞、收藏失败 {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    private void setInteract(List<ArticleVO> datas, Long authorId) {
        try {
            final List<Long> ids = datas.stream().map(ArticleVO::getId).toList();
            final Result<List<Interact>> interacts = interactFeign.getByIds(new GetByIdsRequest(authorId, "article", ids));
            final Map<Long, Interact> interactMap = interacts.getData().stream().collect(Collectors.toMap(Interact::getBizId, v -> v, (k1, k2) -> k1));
            datas.forEach(articleVO -> {
                final Interact interact = interactMap.getOrDefault(articleVO.getId(), Interact.defaultX("article", articleVO.getId()));
                articleVO.setReadCount(interact.getReadCount());
                articleVO.setLikeCount(interact.getLikeCount());
                articleVO.setCollectCount(interact.getCollectCount());
                articleVO.setLiked(interact.getLiked());
                articleVO.setCollected(interact.getCollected());
                articleVO.setCommentCount(interact.getCommentCount());
            });
        } catch (Exception e) {
            log.error("设置作点赞、收藏失败 {}", e.getMessage(), e);
        }

    }

    private void setAuthor(List<ArticleVO> articles) {
        try {
            final Set<Long> authorIds = articles.stream().map(ArticleVO::getAuthorId).collect(Collectors.toSet());
            final Result<List<AuthorVO>> authorFeignByIds = authorFeign.getByIds(new Ids(authorIds));
            final List<AuthorVO> authorS = authorFeignByIds.getData();
            if (CollectionUtils.isNotEmpty(authorS)) {
                final Map<Long, AuthorVO> authorMap = authorFeignByIds.getData()
                        .stream()
                        .collect(Collectors.toMap(AuthorVO::getId, v -> v, (k1, k2) -> k2));
                articles.forEach(articleVO -> articleVO.setAuthor(authorMap.get(articleVO.getAuthorId())));
            }
        } catch (Exception e) {
            log.error("设置author失败 {}", e.getMessage(), e);
        }
    }
}
