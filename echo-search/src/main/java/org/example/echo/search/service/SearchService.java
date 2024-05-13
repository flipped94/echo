package org.example.echo.search.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.search.domain.SearchParam;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class SearchService {

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private Map<String, SearchHandler> handlerMap;

    public List<Object> search(SearchParam searchParam) {
        final SearchHandler searchHandler = handlerMap.get(searchParam.getType());
        if (searchHandler == null) {
            return Collections.emptyList();
        }
        final String[] keywords = searchParam.getQ().split(",");
        final List search = searchHandler.search(keywords, searchParam.getPage(), searchParam.getLimit());
        return search;
    }
}
