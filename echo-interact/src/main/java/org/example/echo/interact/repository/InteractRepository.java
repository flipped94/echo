package org.example.echo.interact.repository;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.example.echo.interact.converter.InteractConverter;
import org.example.echo.interact.entity.UserCollect;
import org.example.echo.interact.entity.UserLike;
import org.example.echo.interact.repository.cache.InteractCache;
import org.example.echo.interact.repository.dao.InteractMapper;
import org.example.echo.interact.repository.dao.UserCollectMapper;
import org.example.echo.interact.repository.dao.UserLikeMapper;
import org.example.echo.sdk.interact.Interact;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Component
public class InteractRepository {

    @Resource
    private InteractMapper interactMapper;

    @Resource
    private UserLikeMapper userLikeMapper;

    @Resource
    private UserCollectMapper userCollectMapper;

    @Resource
    private InteractCache interactCache;

    public void incrReadCount(String biz, Long bizId) {
        interactMapper.incrReadCount(biz, bizId, 1L);
        interactCache.incrReadCountIfPresent(biz, bizId, 1L);
    }

    public void incrCommentCount(String biz, Long bizId) {
        interactMapper.incrCommentCount(biz, bizId, 1L);
        interactCache.incrCommentCountIfPresent(biz, bizId, 1L);
    }

    @Transactional
    public void incrLikeCount(String biz, Long bizId, Long userId) {
        userLikeMapper.upsert(biz, bizId, userId, 1);
        interactMapper.incrLikeCount(biz, bizId, 1L);
        interactCache.incrLikeCountIfPresent(biz, bizId, 1L);
    }

    @Transactional
    public void decrLikeCount(String biz, Long bizId, Long userId) {
        userLikeMapper.upsert(biz, bizId, userId, 0);
        interactMapper.incrLikeCount(biz, bizId, -1L);
        interactCache.incrLikeCountIfPresent(biz, bizId, -1L);
    }

    @Transactional
    public void incrCollectCount(String biz, Long bizId, Long userId, Long cid) {
        userCollectMapper.upsert(biz, bizId, userId, cid, 1);
        interactMapper.incrCollectCount(biz, bizId, 1L);
        interactCache.incrCollectIfPresent(biz, bizId, 1L);
    }

    @Transactional
    public void decrCollectCount(String biz, Long bizId, Long userId) {
        userCollectMapper.upsert(biz, bizId, userId, 0L, 0);
        interactMapper.incrCollectCount(biz, bizId, -1L);
        interactCache.incrCollectIfPresent(biz, bizId, -1L);
    }


    public Interact get(String biz, Long bizId) {
        Interact interact = interactCache.get(biz, bizId);
        if (interact == null) {
            org.example.echo.interact.entity.Interact i = interactMapper.get(biz, bizId);
            if (null != i) {
                interact = InteractConverter.INSTANCE.toDomain(i);
                interactCache.set(interact);
            } else {
                interact = Interact.defaultX(biz, bizId);
            }
        }
        return interact;
    }

    public List<Interact> getByBizAndIds(String biz, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<org.example.echo.interact.entity.Interact> interacts = interactMapper.getByBizAndIds(biz, ids);
        return InteractConverter.INSTANCE.toDomain(interacts);
    }

    public boolean liked(String biz, Long bizId, Long userId) {
        if (userId == null) {
            return false;
        }
        UserLike userLike = userLikeMapper.GetLikeInfo(biz, bizId, userId, 1);
        return null != userLike;
    }

    public boolean collected(String biz, Long bizId, Long userId) {
        if (userId == null) {
            return false;
        }
        UserCollect userCollect = userCollectMapper.getCollectInfo(biz, bizId, userId, 1);
        return null != userCollect;
    }

    @Async
    public void rank(Long id) {
        interactCache.articleReRanking(id);
    }


    public List<Long> getTopK() {
//       List<Long> ids = interactCache.getToK();
        return null;
    }

}
