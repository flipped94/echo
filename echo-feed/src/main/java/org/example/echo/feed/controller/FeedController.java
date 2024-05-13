package org.example.echo.feed.controller;

import jakarta.annotation.Resource;
import org.example.common.vo.Result;
import org.example.echo.feed.doamin.FindFeedEventsRequest;
import org.example.echo.feed.service.FeedService;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.feed.FeedEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/feed")
@RestController
public class FeedController {

    @Resource
    private FeedService feedService;

    @GetMapping
    public Result<List<FeedEvent>> findFeedEvents(FindFeedEventsRequest request) {
        List<FeedEvent> res = feedService.GetFeedEvents(LoginUserContext.getUserId(), request.getTimestamp(), request.getLimit());
        return Result.success(res);
    }
}
