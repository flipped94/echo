package org.example.echo.search.service;


import java.util.List;

public interface SearchHandler {
    List search(String[] keywords, Integer offset, Integer limit);
}
