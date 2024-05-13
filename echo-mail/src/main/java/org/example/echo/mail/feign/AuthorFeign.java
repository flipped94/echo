package org.example.echo.mail.feign;

import org.example.common.vo.Result;
import org.example.echo.sdk.author.AuthorVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "echo-author", path = "/author")
public interface AuthorFeign {

    @GetMapping("/{id}")
    Result<AuthorVO> getById(@PathVariable("id") Long id);
}
