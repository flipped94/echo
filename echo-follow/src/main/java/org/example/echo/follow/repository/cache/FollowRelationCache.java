package org.example.echo.follow.repository.cache;


import org.example.echo.sdk.follow.FollowStatics;

public interface FollowRelationCache {

    void follow(Long follower, Long followee);

    void cancelFollow(Long follower, Long followee);

    void setFollowStaticsInfo(Long uid, FollowStatics followStatics);

    FollowStatics followStaticsInfo(Long uid);
}
