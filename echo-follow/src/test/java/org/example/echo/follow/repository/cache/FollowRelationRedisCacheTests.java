package org.example.echo.follow.repository.cache;

import jakarta.annotation.Resource;
import org.example.echo.sdk.follow.FollowStatics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FollowRelationRedisCacheTests {

    @Resource
    private FollowRelationCache followRelationCache;

    @Test
    public void testSetFollowStatics() {

        final FollowStatics followStatics = new FollowStatics();
        followStatics.setFollowees(100L);
        followStatics.setFollowers(101L);
        followRelationCache.setFollowStaticsInfo(1L, followStatics);
    }

    @Test
    public void getSetFollowStatics() {

        final FollowStatics followStatics = followRelationCache.followStaticsInfo(2L);
        assert followStatics != null;
    }
}
