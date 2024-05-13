package org.example.echo.article.repository;

import jakarta.annotation.Resource;
import org.example.common.vo.PageResult;
import org.example.echo.article.converter.ArticleConverter;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.entity.Article;
import org.example.echo.article.repository.cache.ArticleCache;
import org.example.echo.article.repository.dao.ArticleAuthorDao;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleAuthorRepository {

    @Resource(name = "mongoArticleAuthorDao")
    private ArticleAuthorDao articleAuthorDao;

    @Resource(name = "articleRedisCache")
    private ArticleCache articleCache;

    public Long create(Article article) {
        final Long l = articleAuthorDao.create(article);
        articleCache.delFirstPage(LoginUserContext.getUserId());
        return l;
    }

    public Long update(Article article) {
        final Long modifiedCount = articleAuthorDao.updateByIdAndAuthor(article);
        if (modifiedCount > 0) {
            articleCache.delFirstPage(LoginUserContext.getUserId());
        }
        return modifiedCount;
    }

    public PageResult<ArticleVO> listByAuthor(ListReq req, Long authorId) {
        if (req.getPage() == 1 && req.getPageSize() == 100) {
            // 查缓存
            final PageResult<Article> articlePageResult = articleCache.getFirstPage(authorId);
            return convert(articlePageResult);
        }

        final PageResult<Article> articlePageResult = articleAuthorDao.listByAuthor(req, authorId);
        final PageResult<ArticleVO> result = convert(articlePageResult);
        if (req.getPage() == 1 && req.getPageSize() == 100 && articlePageResult.getPages() > 0) {
            // 缓存第一页
            articleCache.setFirstPage(authorId, articlePageResult);
            // 预加载第一条
            final int threshold = 1024 * 1024;
            final Article firstArticle = articlePageResult.getData().get(0);
            if (firstArticle.getContent().length() <= threshold) {
                articleCache.set(firstArticle, authorId);
            }
        }
        return result;
    }

    private PageResult<ArticleVO> convert(PageResult<Article> pageResult) {
        PageResult<ArticleVO> result = new PageResult<>();
        result.setPageNum(pageResult.getPageNum());
        result.setPageSize(pageResult.getPageSize());
        result.setPages(pageResult.getPages());
        result.setTotal(pageResult.getTotal());
        final List<ArticleVO> datas = pageResult.getData()
                .stream()
                .map(ArticleConverter.INSTANCE::toArticleVOIgnoreContent).toList();
        result.setData(datas);
        return result;
    }

    public ArticleVO getByIdAndAuthorId(Long id, Long authorId) {
        final Article article = articleCache.get(id, authorId);
        if (article != null) {
            return ArticleConverter.INSTANCE.toArticleVO(article);
        }
        Article art = articleAuthorDao.getById(id);
        return ArticleConverter.INSTANCE.toArticleVO(art);
    }

    public long syncStatus(Long id, Long authorId, Integer status) {
        articleCache.delPub(id);
        long modifiedCount = articleAuthorDao.syncStatus(id, authorId, status);
        if (modifiedCount > 0) {
            articleCache.delFirstPage(authorId);
        }
        return modifiedCount;
    }
}
