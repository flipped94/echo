package org.example.webook.job.ranking;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("like")
public class TimeAndLikeCntScoreFunc implements ScoreFunc {
    @Override
    public double score(Object... args) {
        long time = (long) args[0];
        long likeCnt = (long) args[1];
        long sec = (new Date().getTime() - time) / 1000;
        return (likeCnt - 1) / Math.pow(sec + 2.0, 1.5);
    }
}
