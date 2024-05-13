package org.example.echo.interact.repository.cache;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.sdk.interact.Interact;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InteractCache {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final RedisScript<Long> countScript = RedisScript.of(new ClassPathResource("lua/incr_cnt.lua"), Long.class);
    private final RedisScript<Long> redisScript = RedisScript.of(new ClassPathResource("lua/article_topk.lua"), Long.class);

    private String fieldReadCnt = "readCnt";
    private String fieldCommentCnt = "commentCnt";
    private String fieldCollectCnt = "collectCnt";
    private String fieldLikeCnt = "likeCnt";

    public void incrReadCountIfPresent(String biz, Long bizId, Long delta) {
        try {
            stringRedisTemplate.execute(countScript, List.of(key(biz, bizId)), fieldReadCnt, String.valueOf(delta));
        } catch (Exception e) {
            log.error("阅读缓存更新失败：{},{}", biz, bizId, e);
        }
    }

    public void incrCommentCountIfPresent(String biz, Long bizId, Long delta) {
        try {
            stringRedisTemplate.execute(countScript, List.of(key(biz, bizId)), fieldCommentCnt, String.valueOf(delta));
        } catch (Exception e) {
            log.error("阅读缓存更新失败：{},{}", biz, bizId, e);
        }
    }

    public void incrLikeCountIfPresent(String biz, Long bizId, Long delta) {
        try {
            final Long execute = stringRedisTemplate.execute(countScript, List.of(key(biz, bizId)), fieldLikeCnt, String.valueOf(delta));
        } catch (Exception e) {
            log.error("点赞缓存更新失败：{},{}", biz, bizId, e);
        }
    }

    public void incrCollectIfPresent(String biz, Long bizId, Long delta) {
        try {
            stringRedisTemplate.execute(countScript, List.of(key(biz, bizId)), fieldCollectCnt, String.valueOf(delta));
        } catch (Exception e) {
            log.error("收藏缓存更新失败：{},{}", biz, bizId, e);
        }
    }

    public Interact get(String biz, Long bizId) {
        final Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key(biz, bizId));
        if (!entries.isEmpty()) {
            Interact interact = new Interact();
            interact.setBiz(biz);
            interact.setBizId(bizId);
            interact.setReadCount(Long.valueOf((String) entries.get(fieldReadCnt)));
            interact.setLikeCount(Long.valueOf((String) entries.get(fieldLikeCnt)));
            interact.setCollectCount(Long.valueOf((String) entries.get(fieldCollectCnt)));
            interact.setCollectCount(Long.valueOf((String) entries.get(fieldCommentCnt)));
            return interact;
        }
        return null;
    }

    public void set(Interact interact) {
        final String key = key(interact.getBiz(), interact.getBizId());
        Map<String, String> map = new HashMap<>();
        map.put(fieldReadCnt, interact.getReadCount() + "");
        map.put(fieldCommentCnt, interact.getCommentCount() + "");
        map.put(fieldCollectCnt, interact.getCollectCount() + "");
        map.put(fieldLikeCnt, interact.getLikeCount() + "");
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    private String key(String biz, Long bizId) {
        return "interactive:" + biz + ":" + bizId;
    }

    public void articleReRanking(Long id) {
        try {
            stringRedisTemplate.execute(redisScript, List.of("ranking:article"), String.valueOf(id));
        } catch (Exception e) {
            log.error("修改ranking失败：{}", id, e);
        }
    }

}
