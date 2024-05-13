package org.example.echo.search.feign;

import org.example.common.vo.Result;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "echo-interact", path = "/interact")
public interface InteractFeign {

    @GetMapping("/biz-ids")
    Result<List<Interact>> getByIds(@SpringQueryMap GetByIdsRequest request);
}
