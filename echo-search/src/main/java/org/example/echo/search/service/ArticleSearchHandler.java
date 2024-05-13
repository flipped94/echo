package org.example.echo.search.service;

import jakarta.annotation.Resource;
import org.example.echo.search.repository.ArticleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("article")
public class ArticleSearchHandler implements SearchHandler {


    @Resource
    private ArticleRepository articleRepository;

    @Override
    public List search(String[] keywords, Integer page, Integer limit) {
        return articleRepository.searchArticle(keywords, page, limit);
    }
}
