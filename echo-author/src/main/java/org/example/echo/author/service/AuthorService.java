package org.example.echo.author.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.echo.author.cache.AuthorCache;
import org.example.echo.author.converter.AuthorConverter;
import org.example.echo.author.domain.AuthorForUpdate;
import org.example.echo.author.entity.Author;
import org.example.echo.author.event.AuthorProducer;
import org.example.echo.author.mapper.AuthorMapper;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.author.AuthorEvent;
import org.example.echo.sdk.author.AuthorVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class AuthorService {

    @Resource
    private AuthorMapper authorMapper;

    @Resource
    private AuthorCache authorCache;

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private AuthorProducer authorProducer;

    public AuthorVO getById(Long id) {
        AuthorVO authorVO = authorCache.getAuthor(id);
        if (null == authorVO) {
            Author author = authorMapper.getById(id);
            authorVO = AuthorConverter.INSTANCE.toAuthor(author);
            authorCache.setAuthor(authorVO);
        }
        return authorVO;
    }

    public List<AuthorVO> getByIds(Set<Long> idSet) {
//        final Map<Long, AuthorVO> authorMap = idSet.stream().map(authorId -> {
//            try {
//                final String s = stringRedisTemplate.opsForValue().get(String.format(WECHAT, authorId));
//                return new Gson().fromJson(s, org.example.echo.sdk.domain.Author.class);
//            } catch (Exception e) {
//                log.error("获取用户信息缓存失败 {}", e.getMessage());
//            }
//            return null;
//        }).filter(Objects::nonNull).collect(Collectors.toMap(org.example.echo.sdk.domain.Author::getId, v -> v, (k1, k2) -> k2));
//        idSet.removeAll(authorMap.keySet());
//        if (!idSet.isEmpty()) {
//            List<Author> authorList = authorMapper.getByIds(idSet);
//            final List<org.example.echo.sdk.domain.Author> authors = AuthorConverter.INSTANCE.toAuthor(authorList);
//            authors.forEach(author -> authorMap.put(author.getId(), author));
//            // 可以异步
//            authors.forEach(author -> {
//                try {
//                    final String info = new Gson().toJson(author);
//                    stringRedisTemplate.opsForValue().set(String.format(WECHAT, author.getId()), info);
//                } catch (Exception e) {
//                    log.error("设置用户信息缓存失败 {}", e.getMessage());
//                }
//            });
//        }

        final List<Author> authors = authorMapper.getByIds(idSet);
        return AuthorConverter.INSTANCE.toAuthor(authors);
    }

    public void update(AuthorForUpdate authorForUpdate) {
        Author author = AuthorConverter.INSTANCE.toAuthor(authorForUpdate);
        author.setId(LoginUserContext.getUserId());
        author.setUpdateTime(System.currentTimeMillis());
        Integer i = authorMapper.updateBaseInfo(author);
        if (i > 0) {
            executor.execute(() -> sendAuthorEvent(author));
            executor.execute(() -> authorCache.delAuthor(author.getId()));
        }
    }

    public void sendAuthorEvent(Author author) {
        AuthorEvent authorEvent = new AuthorEvent();
        authorEvent.setAuthorId(author.getId());
        authorEvent.setBirthday(author.getBirthday());
        authorEvent.setAvatar(author.getAvatar());
        authorEvent.setEmail(author.getEmail());
        authorEvent.setDegree(author.getDegree());
        authorEvent.setCareer(author.getCareer());
        authorEvent.setGender(author.getGender());
        authorEvent.setStatus(author.getStatus());
        authorEvent.setNickname(author.getNickname());
        authorEvent.setProfile(author.getProfile());
        authorProducer.syncAuthorEvent(authorEvent);
    }
}
