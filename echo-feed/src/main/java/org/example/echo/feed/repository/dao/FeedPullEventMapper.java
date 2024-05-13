package org.example.echo.feed.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.feed.entity.FeedPullEvent;

import java.util.List;

@Mapper
public interface FeedPullEventMapper {

    Long insert(FeedPullEvent feedPullEvent);

    List<FeedPullEvent> getPullEventsWithType(
            @Param("type") String type,
            @Param("followeeIds") List<Long> followeeIds,
            @Param("timestamp") long timestamp,
            @Param("limit") int limit);
}
