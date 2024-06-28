package org.example.webook.job.openfeign;

import org.example.common.vo.Result;
import org.example.echo.sdk.article.ArticleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "echo-article", path = "/articles")
public interface ArticleFeign {

    @GetMapping("/listpub")
    Result<List<ArticleVO>> listpub(@RequestParam("time") long time,
                                    @RequestParam("days") long days,
                                    @RequestParam("offset") long offset,
                                    @RequestParam("batchSize") long batchSize);
}
