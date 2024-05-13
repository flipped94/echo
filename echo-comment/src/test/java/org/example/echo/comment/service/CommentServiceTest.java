package org.example.echo.comment.service;

import jakarta.annotation.Resource;
import org.example.echo.comment.domain.CommentForCreate;
import org.example.echo.comment.domain.CommentVO;
import org.example.echo.comment.domain.ReplyForCreate;
import org.example.echo.mvcconfig.LoginUser;
import org.example.echo.mvcconfig.LoginUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class CommentServiceTest {

    @Resource
    private CommentService commentService;

    @BeforeEach
    public void init() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(3L);
        LoginUserContext.set(loginUser);
    }

    @Test
    public void testCreateComment() {
        for (int i = 0; i < 20; i++) {
            CommentForCreate comment = new CommentForCreate();
            comment.setBiz("article");
            comment.setBizId(1247568948707201024L);
            comment.setContent(UUID.randomUUID().toString());
            final Long commentId = commentService.createComment(comment);
            assert commentId != null;
        }
    }

    @Test
    public void testMoreComment() {
        final List<CommentVO> comments = commentService.getMoreComments("article", 1247568948707201024L, Long.MAX_VALUE, 10);
    }

    @Test
    public void testCreateReply() {
        for (int i = 0; i < 20; i++) {
            final String type = i % 2 == 0 ? "comment" : "reply";
            ReplyForCreate reply = new ReplyForCreate("article", 1247568948707201024L, 3L, 1248226579000070144L, i + "", type);
            final Long replyId = commentService.createReply(reply);
            assert replyId != null;
        }
    }

    @Test
    public void testLt() {
        assert (1341310466580418560L) < (1241316213104054272L);
    }
}
