package org.example.webook.job.reposiotry.localcahce;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import org.example.echo.sdk.article.ArticleVO;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class RankingLocalCache {

    Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();
    private static final String key = "article:ranking";
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public synchronized void set(List<ArticleVO> articleVOS) {
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            final List<ArticleVO> list = articleVOS.stream().map(articleVO -> {
                ArticleVO newArticle = new ArticleVO();
                newArticle.setId(articleVO.getId());
                newArticle.setTitle(articleVO.getTitle());
                newArticle.setLikeCount(articleVO.getLikeCount());
                return newArticle;
            }).toList();
            final Gson gson = new Gson();
            final String s = gson.toJson(list);
            cache.put(key, s);
        } finally {
            writeLock.unlock();
        }
    }

    public synchronized List<ArticleVO> get() {
        final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            final String s = cache.getIfPresent(key);
            if (s == null) {
                return null;
            }
            final Gson gson = new Gson();
            final TypeToken<List<ArticleVO>> typeToken = new TypeToken<>() {
            };
            return gson.fromJson(s, typeToken.getType());
        } finally {
            readLock.unlock();
        }
    }
}
