package org.example.echo.follow.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.follow.entity.FollowRelation;

import java.util.List;

@Mapper
public interface FollowRelationMapper {

    Integer upsert(FollowRelation followRelation);

    Integer updateStatus(
            @Param("follower") Long follower,
            @Param("followee") Long followee,
            @Param("status") Integer status);

    FollowRelation followRelationDetail(@Param("follower") Long follower,
                                        @Param("followee") Long followee);

    /**
     * 获取关注列表
     *
     * @param follower 关注者
     * @return
     */
    List<FollowRelation> followeeList(Long follower);

    /**
     * 获取粉丝列表
     *
     * @param followee 被关注者
     * @return
     */
    List<FollowRelation> followerList(Long followee);

    Long cntFollower(Long uid);

    Long cntFollowee(Long uid);
}
