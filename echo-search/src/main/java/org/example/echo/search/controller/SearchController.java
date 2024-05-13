package org.example.echo.search.controller;

import jakarta.annotation.Resource;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.echo.search.domain.SearchParam;
import org.example.echo.search.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/search")
@RestController
public class SearchController {

    @Resource
    private SearchService searchService;

    @LoginNotRequired
    @GetMapping("")
    public Result<List> search(SearchParam searchParam) {
        List result = searchService.search(searchParam);
        return Result.success(result);
    }
}
