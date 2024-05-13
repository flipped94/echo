package org.example.echo.follow.service;

import jakarta.annotation.Resource;
import org.example.common.vo.PageResult;
import org.example.echo.follow.convertter.FollowRelationConverter;
import org.example.echo.sdk.follow.FollowRelation;
import org.example.echo.follow.repository.FollowRelationRepository;
import org.example.echo.sdk.follow.FollowStatics;
import org.springframework.stereotype.Service;

@Service
public class FollowRelationService {

    @Resource
    private FollowRelationRepository followRelationRepository;


    /**
     * 关注
     *
     * @param follower 关注者
     * @param followee 被关注者
     */
    public void follow(Long follower, Long followee) {
        followRelationRepository.addFollowRelation(follower, followee);
    }

    /**
     * 取消关注
     *
     * @param follower 关注者
     * @param followee 被关注者
     */
    public void cancelFollow(Long follower, Long followee) {
        followRelationRepository.inactiveFollowRelation(follower, followee);
    }

    public FollowRelation followInfo(Long follower, Long followee) {
        org.example.echo.follow.entity.FollowRelation followRelation = followRelationRepository.followRelationDetail(follower, followee);
        return FollowRelationConverter.INSTANCE.toDomain(followRelation);
    }

    /**
     * 获取关注列表
     *
     * @param follower 关注者
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<FollowRelation> getFollowee(Long follower, int pageNum, int pageSize) {
        final PageResult<org.example.echo.follow.entity.FollowRelation> followees =
                followRelationRepository.getFollowee(follower, pageNum, pageSize);
        return FollowRelationConverter.INSTANCE.toDomain(followees);
    }

    /**
     * 获取粉丝列表
     *
     * @param followee
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<FollowRelation> getFollower(Long followee, int pageNum, int pageSize) {
        final PageResult<org.example.echo.follow.entity.FollowRelation> followers =
                followRelationRepository.getFollower(followee, pageNum, pageSize);
        return FollowRelationConverter.INSTANCE.toDomain(followers);
    }

    public FollowStatics getFollowStatics(Long uid) {
        return followRelationRepository.getFollowStatics(uid);
    }

}
