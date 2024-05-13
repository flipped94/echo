package org.example.echo.follow.controller;

import jakarta.annotation.Resource;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.PageResult;
import org.example.common.vo.Result;
import org.example.echo.follow.service.FollowRelationService;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.follow.FollowRelation;
import org.example.echo.sdk.follow.FollowStatics;
import org.example.echo.sdk.follow.GetFolloweeRequest;
import org.example.echo.sdk.follow.GetFollowerRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/follow")
@RestController
public class FollowRelationController {

    @Resource
    private FollowRelationService followRelationService;

    /**
     * 关注
     *
     * @param followee 被关注者id
     * @return
     */
    @GetMapping("/{followee}")
    public Result<Void> follow(@PathVariable Long followee) {
        followRelationService.follow(LoginUserContext.getUserId(), followee);
        return Result.success(null);
    }

    /**
     * 取消关注
     *
     * @param followee 被关注者id
     * @return
     */
    @GetMapping("/cancel/{followee}")
    public Result<Void> cancelFollow(@PathVariable Long followee) {
        followRelationService.cancelFollow(LoginUserContext.getUserId(), followee);
        return Result.success(null);
    }

    /**
     * 关注信息
     *
     * @param follower 关注的人
     * @param followee 被关注的人
     * @return
     */
    @GetMapping("/follow-info")
    public Result<FollowRelation> followInfo(@RequestParam("follower") Long follower, @RequestParam("followee") Long followee) {
        FollowRelation relation = followRelationService.followInfo(follower, followee);
        return Result.success(relation);
    }

    /**
     * 获取关注列表
     *
     * @param follower 关注者
     * @param request
     * @return
     */
    @LoginNotRequired
    @GetMapping("/followees/{follower}")
    public Result<PageResult<FollowRelation>> getFollowee(@PathVariable("follower") Long follower, GetFolloweeRequest request) {
        PageResult<FollowRelation> res =
                followRelationService.getFollowee(follower, request.getPage(), request.getPageSize());
        return Result.success(res);
    }

    /**
     * 获取粉丝列表
     *
     * @param followee 被关注者
     * @param request
     * @return
     */
    @LoginNotRequired
    @GetMapping("/followers/{followee}")
    public Result<PageResult<FollowRelation>> getFollower(@PathVariable(value = "followee") Long followee, GetFollowerRequest request) {
        PageResult<FollowRelation> res =
                followRelationService.getFollower(followee, request.getPage(), request.getPageSize());
        return Result.success(res);
    }

    /**
     * 获取关注和被关注数
     *
     * @param uid 用户id
     * @return 关注和被关注信息
     */
    @LoginNotRequired
    @GetMapping("/follow-statics/{uid}")
    public Result<FollowStatics> getFollowStatics(@PathVariable(value = "uid") Long uid) {
        FollowStatics res = followRelationService.getFollowStatics(uid);
        return Result.success(res);
    }
}
