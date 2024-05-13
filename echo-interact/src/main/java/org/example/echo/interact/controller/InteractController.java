package org.example.echo.interact.controller;

import jakarta.annotation.Resource;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.echo.interact.service.InteractService;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.example.echo.sdk.interact.InteractReq;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interact")
public class InteractController {

    @Resource
    private InteractService interactService;

    @LoginNotRequired
    @PostMapping("/incr-read")
    public Result<Void> incrRead(@RequestBody @Validated InteractReq interactReq) {
        interactService.incrReadCount(interactReq);
        return Result.success(null);
    }

    @PostMapping("/incr-comment")
    public Result<Void> incrComment(@RequestBody @Validated InteractReq interactReq) {
        interactService.incrCommentCount(interactReq);
        return Result.success(null);
    }


    @PostMapping("/like")
    public Result<Void> like(@RequestBody @Validated InteractReq interactReq) {
        interactService.like(interactReq);
        return Result.success(null);
    }

    @PostMapping("/cancel-like")
    public Result<Void> cancelLike(@RequestBody @Validated InteractReq interactReq) {
        interactService.cancelLike(interactReq);
        return Result.success(null);
    }

    @PostMapping(value = {"/collect/{cid}", "/collect"})
    public Result<Void> collect(@RequestBody @Validated InteractReq interactReq,
                                @PathVariable(required = false) Long cid) {
        interactService.collect(interactReq, 0L);
        return Result.success(null);
    }

    @PostMapping("/cancel-collect")
    public Result<Void> cancelCollect(@RequestBody @Validated InteractReq interactReq) {
        interactService.cancelCollect(interactReq);
        return Result.success(null);
    }

    @LoginNotRequired
    @GetMapping("/get")
    public Result<Interact> get(InteractReq interactReq) {
        Interact res = interactService.get(interactReq);
        return Result.success(res);
    }

    @LoginNotRequired
    @GetMapping("/biz-ids")
    public Result<List<Interact>> getByBizAndIds(GetByIdsRequest request) {
        List<Interact> res = interactService.getByBizAndIds(request);
        return Result.success(res);
    }

    @GetMapping("/topk")
    public Result<List<Long>> getTopK() {
        List<Long> ids = interactService.getTopK();
        return Result.success(ids);
    }
}
