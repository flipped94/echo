package org.example.echo.feed.feign;

import org.example.common.vo.PageResult;
import org.example.common.vo.Result;
import org.example.echo.sdk.follow.FollowRelation;
import org.example.echo.sdk.follow.FollowStatics;
import org.example.echo.sdk.follow.GetFolloweeRequest;
import org.example.echo.sdk.follow.GetFollowerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "echo-follow", path = "/follow")
public interface FollowFeign {

    @GetMapping("/follow-statics/{uid}")
    Result<FollowStatics> getFollowStatic(@PathVariable(value = "uid") Long uid);

    /**
     * 获取粉丝列表
     *
     * @param followee 被关注者
     * @param request
     * @return
     */
    @GetMapping("/followers/{followee}")
    Result<PageResult<FollowRelation>> getFollower(@PathVariable(value = "followee") Long followee, @SpringQueryMap GetFollowerRequest request);

    /**
     * 获取关注列表
     *
     * @param follower 关注者
     * @param request
     * @return
     */
    @GetMapping("/followees/{follower}")
    Result<PageResult<FollowRelation>> getFollowee(@PathVariable(value = "follower") Long follower, @SpringQueryMap GetFolloweeRequest request);
}