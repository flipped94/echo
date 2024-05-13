package org.example.echo.author.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.common.vo.Token;
import org.example.echo.author.domain.AuthorForCreate;
import org.example.echo.author.domain.AuthorForLogin;
import org.example.echo.author.domain.AuthorForUpdateEmail;
import org.example.echo.author.domain.AuthorForUpdatePass;
import org.example.echo.author.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @LoginNotRequired
    @PostMapping("/register")
    public Result<Token> register(@RequestBody @Valid AuthorForCreate author, HttpServletRequest request) {
        Token token = authService.register(author);
        return Result.success(token);
    }

    /**
     * 登录接口
     */
    @LoginNotRequired
    @PostMapping(value = "/login")
    public Result<Token> login(@RequestBody @Valid AuthorForLogin author) {
        Token token = authService.login(author);
        return Result.success(token);
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/update-pass")
    public Result<Valid> updatePassword(@RequestBody @Valid AuthorForUpdatePass pass) {
        pass.check();
        authService.updatePassword(pass);
        return Result.success(null);
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/update-email")
    public Result<Valid> updateEmail(@RequestBody @Valid AuthorForUpdateEmail email) {
        authService.updateEmail(email);
        return Result.success(null);
    }

    /**
     * 刷新 token
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/refresh")
    public Result<Token> refresh(HttpServletRequest request) {
        final String refreshToken = request.getHeader("X-Echo-RefreshToken");
        Token token = authService.refresh(refreshToken);
        return Result.success(token);
    }


}
