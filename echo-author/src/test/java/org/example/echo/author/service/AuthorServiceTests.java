package org.example.echo.author.service;

import jakarta.annotation.Resource;
import org.example.echo.author.domain.AuthorForUpdate;
import org.example.echo.mvcconfig.LoginUser;
import org.example.echo.mvcconfig.LoginUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthorServiceTests {

    @Resource
    private AuthorService authorService;

    @BeforeEach
    public void init() {
        final LoginUser loginUser = new LoginUser();
        loginUser.setUserId(3L);
        LoginUserContext.set(loginUser);
    }

    @Test
    public void update() {
        final AuthorForUpdate authorForUpdate = new AuthorForUpdate();
        authorForUpdate.setNickname("Flipped");
        authorForUpdate.setAvatar("/default_header.png");
        authorService.update(authorForUpdate);
    }
}
