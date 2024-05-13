package org.example.echo.article.service;

import jakarta.annotation.Resource;
import org.example.common.vo.PageResult;
import org.example.echo.article.domain.ArticleForPublish;
import org.example.echo.article.domain.ArticleForSave;
import org.example.echo.article.domain.ListReq;
import org.example.echo.mvcconfig.LoginUser;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleServiceTest {

    @Resource
    private ArticleService articleService;

    @BeforeEach
    public void beforeEach() {
        final LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        LoginUserContext.set(loginUser);
    }

    @Test
    public void testPublichArticle() {
        ArticleForPublish articleForPublish = new ArticleForPublish();
        articleForPublish.setTitle("f33");
        articleForPublish.setContent("abc");
        articleForPublish.setStatus(1);
        final Long articleId = articleService.publish(articleForPublish);
        assert articleId != null;
    }

    @Test
    public void testUpdateArticle() {
        ArticleForSave articleForSave = new ArticleForSave();
        articleForSave.setId(1240161472114331648L);
        articleForSave.setTitle("Hello");
        articleForSave.setContent("MongoDB");
        articleForSave.setStatus(1);
        long id = articleService.save(articleForSave);
        assert id > 0;
    }

    @Test
    public void testUpdateOtherAuthorArticle() {
        ArticleForSave articleForSave = new ArticleForSave();
        final LoginUser loginUser = new LoginUser();
        loginUser.setUserId(666L);
        LoginUserContext.set(loginUser);
        articleForSave.setId(1240161472114331648L);
        articleForSave.setTitle("Hello");
        articleForSave.setContent("MongoDB");
        articleForSave.setStatus(1);
        articleService.save(articleForSave);
    }

    @Test
    void testListByAuthor() {
        final ListReq req = new ListReq();
        final PageResult<ArticleVO> articleVOPageResult = articleService.listByAuthor(req, LoginUserContext.getUserId());
    }

    @Test
    void testGetIdAndAuthorId() {
        final ArticleVO byIdAndAuthorId = articleService.getByIdAndAuthorId(1240161472114331648L, LoginUserContext.getUserId());
        assert byIdAndAuthorId != null;
    }

    @Test
    void testGetPubDetail() {
        final ArticleVO pub = articleService.getPubById(1240161472114331648L);
        assert pub != null;
    }
}
