package org.example.echo.comment.dao;

import jakarta.annotation.Resource;
import org.example.echo.comment.repository.dao.CommentDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentDaoTest {

    @Resource
    private CommentDao commentDao;

    @Test
    public void testGetMoreComment() {
        commentDao.getMoreComment("article", 1L, Long.MAX_VALUE, 5);
    }

    @Test
    public void testGetMoreReply() {
        commentDao.getMoreReply("article", 1L, 1241391629890228224L, 1241391858437853184L);
    }
}
