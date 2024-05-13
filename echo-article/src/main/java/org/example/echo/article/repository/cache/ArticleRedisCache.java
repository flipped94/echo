package org.example.echo.article.repository.cache;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.common.vo.PageResult;
import org.example.echo.article.entity.Article;
import org.example.echo.article.entity.PublishedArticle;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component("articleRedisCache")
public class ArticleRedisCache implements ArticleCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<Article> getFirstPage(Long authorId) {
        final String s = stringRedisTemplate.opsForValue().get(authorId + ":articles");
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        final Gson gson = new Gson();
        final PageResult pageResult = gson.fromJson(s, PageResult.class);
        final List data = pageResult.getData();
        final List articleForPublishes = data.stream().map(o -> {
            final String json = gson.toJson(o);
            return gson.fromJson(json, Article.class);
        }).toList();
        pageResult.setData(articleForPublishes);
        return pageResult;
    }

    @Override
    public void setFirstPage(Long authorId, PageResult<Article> page) {
        page.getData().forEach(article -> article.setContent(article.getAbstraction()));
        final Gson gson = new Gson();
        final String json = gson.toJson(page);
        stringRedisTemplate.opsForValue().set(authorId + ":articles", json, Duration.ofMinutes(10));
    }


    @Override
    public void delFirstPage(Long authorId) {
        stringRedisTemplate.delete(authorId + ":articles");
    }

    @Override
    public Article get(Long id, Long authorId) {
        final String s = stringRedisTemplate.opsForValue().get("author:" + authorId + ":article:detail:" + id);
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        final Gson gson = new Gson();
        return gson.fromJson(s, Article.class);
    }

    @Override
    public void set(Article article, Long authorId) {
        final Gson gson = new Gson();
        final String s = gson.toJson(article);
        stringRedisTemplate.opsForValue().set("author:" + authorId + ":article:detail:" + article.get_id(), s, Duration.ofMinutes(1));
    }

    @Override
    public PublishedArticle getPub(Long id) {
        final String s = stringRedisTemplate.opsForValue().get("article:pub:detail:" + id);
        if (!StringUtils.isEmpty(s)) {
            final Gson gson = new Gson();
            return gson.fromJson(s, PublishedArticle.class);
        }
        return null;
    }

    @Override
    public void setPub(PublishedArticle article) {
        final Gson gson = new Gson();
        final String s = gson.toJson(article);
        stringRedisTemplate.opsForValue().set("article:pub:detail:" + article.get_id(), s, Duration.ofMinutes(10));
    }

    @Override
    public void delPub(Long id) {
        stringRedisTemplate.delete("article:pub:detail:" + id);
    }

}
