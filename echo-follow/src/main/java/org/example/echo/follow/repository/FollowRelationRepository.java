package org.example.echo.follow.repository;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.vo.PageResult;
import org.example.echo.follow.entity.FollowRelation;
import org.example.echo.follow.enums.FollowRelationEnum;
import org.example.echo.follow.repository.cache.FollowRelationCache;
import org.example.echo.follow.repository.dao.FollowRelationMapper;
import org.example.echo.sdk.follow.FollowStatics;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FollowRelationRepository {

    @Resource(name = "followRelationRedisCache")
    private FollowRelationCache followRelationCache;

    @Resource
    private FollowRelationMapper followRelationMapper;

    /**
     * 添加关注
     *
     * @param follower 关注者
     * @param followee 被关注者
     */
    public void addFollowRelation(Long follower, Long followee) {
        followRelationCache.follow(follower, followee);
        final FollowRelation followRelation = new FollowRelation();
        followRelation.setFollower(follower);
        followRelation.setFollowee(followee);
        followRelation.setStatus(FollowRelationEnum.ACTIVE.getCode());
        final long now = System.currentTimeMillis();
        followRelation.setCreateTime(now);
        followRelation.setUpdateTime(now);
        followRelationMapper.upsert(followRelation);
    }

    /**
     * 设置未关注
     *
     * @param follower
     * @param followee
     */
    public void inactiveFollowRelation(Long follower, Long followee) {
        followRelationCache.cancelFollow(follower, followee);
        followRelationMapper.updateStatus(follower, followee, FollowRelationEnum.INACTIVE.getCode());
    }

    public FollowRelation followRelationDetail(Long follower, Long followee) {
        return followRelationMapper.followRelationDetail(follower, followee);
    }

    /**
     * 获取粉丝列表
     *
     * @param follower 关注者
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<FollowRelation> getFollowee(Long follower, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FollowRelation> followRelationList = followRelationMapper.followeeList(follower);
        PageInfo<FollowRelation> followRelationPageInfo = new PageInfo<>(followRelationList);
        final PageResult<FollowRelation> result = new PageResult<>();
        result.setData(followRelationPageInfo.getList());
        result.setPages(followRelationPageInfo.getPages());
        result.setTotal(followRelationPageInfo.getTotal());
        result.setPageNum(followRelationPageInfo.getPageNum());
        result.setPageSize(followRelationPageInfo.getPageSize());
        return result;
    }

    public PageResult<FollowRelation> getFollower(Long followee, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FollowRelation> followRelationList = followRelationMapper.followerList(followee);
        PageInfo<FollowRelation> followRelationPageInfo = new PageInfo<>(followRelationList);
        final PageResult<FollowRelation> result = new PageResult<>();
        result.setData(followRelationPageInfo.getList());
        result.setPages(followRelationPageInfo.getPages());
        result.setTotal(followRelationPageInfo.getTotal());
        result.setPageNum(followRelationPageInfo.getPageNum());
        result.setPageSize(followRelationPageInfo.getPageSize());
        return result;
    }


    public FollowStatics getFollowStatics(Long uid) {
        FollowStatics followStatics = followRelationCache.followStaticsInfo(uid);
        if (followStatics == null) {
            Long followers = followRelationMapper.cntFollower(uid);
            Long followees = followRelationMapper.cntFollowee(uid);
            followStatics = new FollowStatics();
            followStatics.setFollowers(followers);
            followStatics.setFollowees(followees);
            followRelationCache.setFollowStaticsInfo(uid, followStatics);
        }
        return followStatics;
    }


}
