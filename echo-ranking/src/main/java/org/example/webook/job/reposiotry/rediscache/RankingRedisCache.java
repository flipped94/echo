package org.example.webook.job.reposiotry.rediscache;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import jakarta.annotation.Resource;
import org.example.echo.sdk.article.ArticleVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RankingRedisCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final String key = "article:ranking";

    public void set(List<ArticleVO> articleVOS) {
        final List<ArticleVO> list = articleVOS.stream().map(articleVO -> {
            ArticleVO newArticle = new ArticleVO();
            newArticle.setId(articleVO.getId());
            newArticle.setTitle(articleVO.getTitle());
            newArticle.setLikeCount(articleVO.getLikeCount());
            return newArticle;
        }).toList();
        final Gson gson = new Gson();
        final String s = gson.toJson(list);
        stringRedisTemplate.opsForValue().set(key, s);
    }

    public List<ArticleVO> get() {
        final String s = stringRedisTemplate.opsForValue().get(key);
        final Gson gson = new Gson();
        final TypeToken<List<ArticleVO>> typeToken = new TypeToken<>() {
        };
        return gson.fromJson(s, typeToken.getType());
    }
}
