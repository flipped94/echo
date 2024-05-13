package org.example.echo.search.service;

import jakarta.annotation.Resource;
import org.example.echo.search.repository.ArticleRepository;
import org.example.echo.search.repository.AuthorRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("author")
public class AuthorSearchHandler implements SearchHandler {


    @Resource
    private AuthorRepository authorRepository;

    @Override
    public List search(String[] keywords, Integer page, Integer limit) {
        return authorRepository.searchAuthor(keywords, page, limit);
    }
}
