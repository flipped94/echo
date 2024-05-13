package org.example.echo.author.cache;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.echo.sdk.author.AuthorVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Component
public class AuthorCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String PREFIX = "author:%d";

    private static final Duration AUTHOR_DURATION = Duration.ofHours(new Random().nextInt(24));

    public void setAuthor(AuthorVO authorVO) {
        final String key = String.format(PREFIX, authorVO.getId());
        final Gson gson = new Gson();
        final String authorInfo = gson.toJson(authorVO);
        try {
            stringRedisTemplate.opsForValue().set(key, authorInfo, AUTHOR_DURATION);
        } catch (Exception e) {
            log.error("缓存author信息失败: {}", e.getMessage());
        }
    }

    public void delAuthor(Long authorId) {
        final String key = String.format(PREFIX, authorId);
        stringRedisTemplate.delete(key);
    }

    public AuthorVO getAuthor(Long authorId) {
        final String key = String.format(PREFIX, authorId);
        final String authorInfo = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(authorInfo)) {
            return null;
        }
        final Gson gson = new Gson();
        return gson.fromJson(authorInfo, AuthorVO.class);
    }
}
