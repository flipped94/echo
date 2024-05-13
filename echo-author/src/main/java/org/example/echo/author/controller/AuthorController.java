package org.example.echo.author.controller;

import jakarta.annotation.Resource;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.echo.author.domain.AuthorForUpdate;
import org.example.echo.author.service.AuthorService;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/author")
public class AuthorController {

    @Resource
    private AuthorService authorService;

    @GetMapping("/profile")
    public Result<AuthorVO> profile() {
        AuthorVO author = authorService.getById(LoginUserContext.getUserId());
        return Result.success(author);
    }

    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated AuthorForUpdate authorForUpdate) {
        authorService.update(authorForUpdate);
        return Result.success(null);
    }

    @LoginNotRequired
    @GetMapping("/{id}")
    public Result<AuthorVO> getById(@PathVariable Long id) {
        AuthorVO author = authorService.getById(id);
        return Result.success(author);
    }

    @GetMapping("/ids")
    @LoginNotRequired
    Result<List<AuthorVO>> getByIds(Ids ids) {
        final Set<Long> idSet = ids.getIds();
        List<AuthorVO> authors = authorService.getByIds(idSet);
        return Result.success(authors);
    }
}
