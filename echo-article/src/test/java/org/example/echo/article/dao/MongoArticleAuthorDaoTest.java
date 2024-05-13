package org.example.echo.article.dao;

import jakarta.annotation.Resource;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.repository.dao.impl.MongoArticleAuthorDao;
import org.example.echo.mvcconfig.LoginUser;
import org.example.echo.mvcconfig.LoginUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MongoArticleAuthorDaoTest {

    @Resource
    private MongoArticleAuthorDao mongoArticleAuthorDao;

    @BeforeEach
    public void init() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        LoginUserContext.set(loginUser);
    }

    @Test
    public void testList() {
        ListReq req = new ListReq();
        final Object list = mongoArticleAuthorDao.listByAuthor(req, LoginUserContext.getUserId());
    }
}
