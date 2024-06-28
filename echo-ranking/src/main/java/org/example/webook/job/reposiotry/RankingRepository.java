package org.example.webook.job.reposiotry;

import jakarta.annotation.Resource;
import org.example.echo.sdk.article.ArticleVO;
import org.example.webook.job.reposiotry.localcahce.RankingLocalCache;
import org.example.webook.job.reposiotry.rediscache.RankingRedisCache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RankingRepository {

    @Resource
    private RankingLocalCache rankingLocalCache;

    @Resource
    private RankingRedisCache rankingRedisCache;

    public void replaceTopN(List<ArticleVO> arts) {
        rankingLocalCache.set(arts);
        rankingRedisCache.set(arts);
    }

    public List<ArticleVO> getTopN() {
        List<ArticleVO> articleVOS = rankingLocalCache.get();
        if (null != articleVOS) {
            return articleVOS;
        }
        articleVOS = rankingRedisCache.get();
        if (null != articleVOS) {
            rankingLocalCache.set(articleVOS);
        }
        return articleVOS;
    }
}
