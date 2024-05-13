package org.example.echo.comment.feign;

import org.example.common.vo.Result;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.example.echo.sdk.interact.InteractReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "echo-interact", path = "/interact")
public interface InteractFeign {

    @GetMapping("/biz-ids")
    Result<List<Interact>> getByIds(@SpringQueryMap GetByIdsRequest request);

    @PostMapping("/incr-comment")
    Result<Void> incrComment(@RequestBody @Validated InteractReq interactReq);
}
