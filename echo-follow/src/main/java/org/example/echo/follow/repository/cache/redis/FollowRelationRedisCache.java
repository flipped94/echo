package org.example.echo.follow.repository.cache.redis;

import jakarta.annotation.Resource;
import org.example.echo.follow.repository.cache.FollowRelationCache;
import org.example.echo.sdk.follow.FollowStatics;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("followRelationRedisCache")
public class FollowRelationRedisCache implements FollowRelationCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 被多少人关注
    private static final String fieldFollowerCnt = "follower_cnt";
    // 关注了多少人
    private static final String fieldFolloweeCnt = "followee_cnt";

    public void follow(Long follower, Long followee) {
        updateStaticsInfo(follower, followee, 1);
    }

    public void cancelFollow(Long follower, Long followee) {
        updateStaticsInfo(follower, followee, -1);
    }

    @Override
    public void setFollowStaticsInfo(Long uid, FollowStatics followStatics) {
        Map<String, String> map = new HashMap<>();
        map.put(fieldFollowerCnt, String.valueOf(followStatics.getFollowers()));
        map.put(fieldFolloweeCnt, String.valueOf(followStatics.getFollowees()));
        stringRedisTemplate.opsForHash().putAll(staticsKey(uid), map);
    }

    private void updateStaticsInfo(Long follower, Long followee, int i) {
        stringRedisTemplate.opsForHash().increment(staticsKey(follower), fieldFolloweeCnt, i);
        stringRedisTemplate.opsForHash().increment(staticsKey(followee), fieldFollowerCnt, i);
    }

    public FollowStatics followStaticsInfo(Long uid) {
        final Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(staticsKey(uid));
        if (entries.isEmpty()) {
            return null;
        }
        FollowStatics followStatics = new FollowStatics();
        followStatics.setFollowers(Long.valueOf((String) entries.get(fieldFollowerCnt)));
        followStatics.setFollowees(Long.valueOf((String) entries.get(fieldFolloweeCnt)));
        return followStatics;
    }

    private String staticsKey(Long uid) {
        return String.format("follow:statics:%d", uid);
    }

}
