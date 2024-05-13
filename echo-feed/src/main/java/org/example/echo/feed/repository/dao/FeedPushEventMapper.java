package org.example.echo.feed.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.feed.entity.FeedPushEvent;

import java.util.List;

@Mapper
public interface FeedPushEventMapper {

    Long insertBatch(@Param("list") List<FeedPushEvent> list);

    List<FeedPushEvent> getPushEventsWithType(
            @Param("type") String type,
            @Param("uid") Long uid,
            @Param("timestamp") long timestamp,
            @Param("limit") int limit);
}
