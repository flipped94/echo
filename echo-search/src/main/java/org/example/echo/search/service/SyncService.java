package org.example.echo.search.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.search.domain.Article;
import org.example.echo.search.domain.Author;
import org.example.echo.search.repository.ArticleRepository;
import org.example.echo.search.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SyncService {


    @Resource
    private ArticleRepository articleRepository;

    @Resource
    private AuthorRepository authorRepository;

    public void inputArticle(Article article) throws IOException {
        articleRepository.inputArticle(article);
    }


    public void inputAuthor(Author author) throws IOException {
        authorRepository.inputAuthor(author);
    }
}
