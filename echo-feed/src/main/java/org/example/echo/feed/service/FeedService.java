package org.example.echo.feed.service;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.vo.Result;
import org.example.echo.feed.feign.AuthorFeign;
import org.example.echo.feed.feign.InteractFeign;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.example.echo.sdk.feed.FeedEvent;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedService {

    @Resource
    private Map<String, FeedHandler> handlerMap;

    @Resource
    private InteractFeign interactFeign;

    @Resource
    private AuthorFeign authorFeign;

    @Resource(name = "customThreadPool")
    private Executor executor;

    public Long createFeedEvent(FeedEvent feedEvent) {
        final FeedHandler handler = handlerMap.get(feedEvent.getType());
        if (handler == null) {
            log.error("没有对应 handler {}", feedEvent.getType());
            return 0L;
        }
        return handler.createFeedEvent(feedEvent);
    }


    public List<FeedEvent> getFeedEvents(Long uid, long timestamp, int limit) {
        if (uid == null) {
            return Collections.emptyList();
        }
        Lock lock = new ReentrantLock();
        CountDownLatch latch = new CountDownLatch(handlerMap.size());
        final List<FeedEvent> events = new ArrayList<>();
        handlerMap.values().forEach(feedHandler -> executor.execute(() -> {
            try {
                final List<FeedEvent> feedEventList = feedHandler.getFeedEventList(uid, timestamp, limit);
                lock.lock();
                events.addAll(feedEventList);
            } finally {
                lock.unlock();
                latch.countDown();
            }
        }));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        events.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        int count = Math.min(limit, events.size());
        if (count == 0) {
            return Collections.emptyList();
        }
        final List<FeedEvent> res = events.subList(0, count);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        executor.execute(() -> {
            setInteract(uid, res);
            countDownLatch.countDown();
        });
        executor.execute(() -> {
            setAuthor(res);
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private void setInteract(Long uid, List<FeedEvent> res) {
        final Map<String, List<FeedEvent>> types = res.stream().collect(Collectors.groupingBy(FeedEvent::getType));
        types.values().forEach(events1 -> {
            final String bizType = events1.get(0).getExt().get("biz");
            final List<Long> ids = events1.stream().map(event -> {
                final String bizId = event.getExt().get("bizId");
                return Long.valueOf(bizId);
            }).toList();
            try {
                final Result<List<Interact>> interacts = interactFeign.getByIds(new GetByIdsRequest(uid, bizType, ids));
                final Map<Long, Interact> interactMap = interacts.getData()
                        .stream().collect(Collectors.toMap(Interact::getBizId, v -> v, (k1, k2) -> k1));
                events1.forEach(event -> {
                    final Long bizId = Long.valueOf(event.getExt().get("bizId"));
                    final String bizType1 = event.getExt().get("biz");
                    final Interact interact = interactMap.getOrDefault(bizId, Interact.defaultX(bizType1, bizId));
                    final Gson gson = new Gson();
                    final String json = gson.toJson(interact);
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    final Map<String, String> o = gson.fromJson(json, type);
                    event.getExt().putAll(o);
                });
            } catch (Exception e) {
                log.error("feed获取interact失败 {}", e.getMessage());
            }
        });
    }

    private void setAuthor(List<FeedEvent> res) {
        try {
            final Set<Long> idSet = res.stream()
                    .map(FeedEvent::getUid)
                    .collect(Collectors.toSet());
            final Ids ids = new Ids(idSet);
            final Result<List<AuthorVO>> authorResult = authorFeign.getByIds(ids);
            final Map<Long, AuthorVO> authorMap = authorResult.getData().stream()
                    .collect(Collectors.toMap(AuthorVO::getId, v -> v, (k1, k2) -> k1));
            final Gson gson = new Gson();
            res.forEach(feedEvent -> {
                final AuthorVO authorVO = authorMap.get(feedEvent.getUid());
                final String author = gson.toJson(authorVO);
                feedEvent.getExt().put("author", author);
            });
        } catch (Exception e) {
            log.error("feed设置author失败 {}", e.getMessage());
        }
    }
}
