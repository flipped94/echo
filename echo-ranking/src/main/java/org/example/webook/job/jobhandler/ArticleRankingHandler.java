package org.example.webook.job.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.vo.Result;
import org.example.echo.sdk.article.ArticleVO;
import org.example.webook.job.openfeign.ArticleFeign;
import org.example.webook.job.ranking.ScoreFunc;
import org.example.webook.job.reposiotry.RankingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ArticleRankingHandler {

    @Resource
    private Map<String, ScoreFunc> scoreFuncMap;

    @Resource
    private ArticleFeign articleFeign;

    @Resource
    private RankingRepository rankingRepository;

    private static final long FIFTEEN_DAYS = 7 * 24 * 60 * 60 * 1000;

    @XxlJob("articleranking")
    public void articleRanking() throws Exception {
        final String jobParam = XxlJobHelper.getJobParam();
        final String[] params = jobParam.split(",");
        int n = Integer.parseInt(params[0]);
        int batchSize = Integer.parseInt(params[1]);
        String scoreFunc = params[2];
        if (n <= 0) {
            throw new RuntimeException("个数不能小于0");
        }
        List<ArticleVO> res = topN(n, batchSize, scoreFunc);
        rankingRepository.replaceTopN(res);
    }

    private List<ArticleVO> topN(int n, int batchSize, String scoreFunc) {

        final long now = new Date().getTime();
        long offset = 0;
        final ScoreFunc func = scoreFuncMap.get(scoreFunc);
        final PriorityQueue<Score> topN = new PriorityQueue<>(n, (o1, o2) -> {
            if (o1.score > o2.score) {
                return 1;
            } else if (o1.score == o2.score) {
                return 0;
            }
            return -1;
        });

        while (true) {
            try {
                final Result<List<ArticleVO>> result = articleFeign.listpub(now, FIFTEEN_DAYS, offset, batchSize);
                final List<ArticleVO> arts = result.getData();
                arts.forEach(a -> {
                    final double score = func.score(now, a.getLikeCount());
                    if (topN.size() == n && n > 0) {
                        final Score val = topN.poll();
                        if (val.score < score) {
                            topN.add(new Score(a, score));
                        } else {
                            topN.add(val);
                        }
                    } else {
                        topN.add(new Score(a, score));
                    }
                });

                // 处理完毕
                if (arts.size() < batchSize) {
                    break;
                }
                offset = offset + arts.size();
            } catch (Exception e) {
                break;
            }
        }
        return topN.stream().map(Score::getArticle).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Score {
        private ArticleVO article;
        private double score;
    }
}
