package org.example.echo.search.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.common.vo.Result;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleVO;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.example.echo.search.converter.ArticleConverter;
import org.example.echo.search.domain.Article;
import org.example.echo.search.domain.Author;
import org.example.echo.search.feign.AuthorFeign;
import org.example.echo.search.feign.InteractFeign;
import org.example.echo.search.repository.dao.ArticleEsDao;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArticleRepository {

    @Resource
    private ArticleEsDao articleEsDao;

    @Resource
    private AuthorFeign authorFeign;

    @Resource
    private InteractFeign interactFeign;

    @Resource(name = "customThreadPool")
    private Executor executor;

    public void inputArticle(Article article) throws IOException {
        final org.example.echo.search.entity.Article a = ArticleConverter.INSTANCE.convert(article);
        articleEsDao.inputArticle(a);
    }

    public List<ArticleVO> searchArticle(String[] keywords, Integer page, Integer limit) {
        List<org.example.echo.search.entity.Article> articles = articleEsDao.search(keywords, page, limit);
        final List<ArticleVO> articleVOList = ArticleConverter.INSTANCE.toArticleVOIgnoreContent(articles);
        setMetaData(articleVOList);
        return articleVOList;
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
