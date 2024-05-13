package org.example.echo.article.feign;

import org.example.common.vo.Result;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "echo-author", path = "/author")
public interface AuthorFeign {

    @GetMapping("/ids")
    Result<List<AuthorVO>> getByIds(@SpringQueryMap Ids ids);
}
