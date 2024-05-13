package org.example.echo.author.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.util.JwtUtil;
import org.example.common.util.RSAUtil;
import org.example.common.vo.Token;
import org.example.echo.author.cache.AuthorCache;
import org.example.echo.author.converter.AuthorConverter;
import org.example.echo.author.domain.AuthorForCreate;
import org.example.echo.author.domain.AuthorForLogin;
import org.example.echo.author.domain.AuthorForUpdateEmail;
import org.example.echo.author.domain.AuthorForUpdatePass;
import org.example.echo.author.entity.Author;
import org.example.echo.author.event.AuthorProducer;
import org.example.echo.author.mapper.AuthorMapper;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.author.AuthorEvent;
import org.example.echo.sdk.author.AuthorVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class AuthService {


    @Resource
    private AuthorCache authorCache;

    @Resource
    private AuthorMapper authorMapper;

    @Resource
    private AuthorProducer authorProducer;

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Duration ACCESS_TOKEN_EXPIRE = Duration.ofHours(3);
    private static final Duration REFRESH_TOKEN_EXPIRE = Duration.ofDays(30);

    @Async
    public void cacheUserInfo(Author author) {
        final AuthorVO authorVO = AuthorConverter.INSTANCE.toAuthor(author);
        authorCache.setAuthor(authorVO);
    }

    public Token login(AuthorForLogin authorForLogin) {
        final Author author = authorMapper.getByEmail(authorForLogin.getEmail());
        if (author == null) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_PASSWORD_INCORRECT);
        }
        try {
            final String decrypt = RSAUtil.privateDecrypt(authorForLogin.getPassword());
            final String password = Md5Crypt.apr1Crypt(decrypt, author.getSalt());
            if (author.getPassword().equals(password)) {
                final Token token = generateToken(author.getId());
                cacheUserInfo(author);
                return token;
            } else {
                throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_PASSWORD_INCORRECT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Token refresh(String refreshToken) {
        final Long userId = JwtUtil.parse(refreshToken);
        if (userId == null) {
            throw new BusinessException(BizCodeEnum.AUTH_LOGIN_EXPIRATION);
        }
        final String accessToken = JwtUtil.generateToken(userId, ACCESS_TOKEN_EXPIRE);
        return new Token(accessToken, refreshToken);
    }

    public Token register(AuthorForCreate authorForCreate) {
        authorForCreate.check();
        Author author = authorMapper.getByEmail(authorForCreate.getEmail());
        if (author != null) {
            throw new BusinessException(BizCodeEnum.AUTH_EMAIL_EXISTS);
        }
        author = Author.forCreate(authorForCreate.getEmail(), authorForCreate.getPassword());
        authorMapper.save(author);
        Author finalAuthor = author;
        executor.execute(() -> cacheUserInfo(finalAuthor));
        executor.execute(() -> sendAuthorEvent(finalAuthor));
        return generateToken(author.getId());
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

    private Token generateToken(Long authorId) {
        final String accessToken = JwtUtil.generateToken(authorId, ACCESS_TOKEN_EXPIRE);
        final String refreshToken = JwtUtil.generateToken(authorId, REFRESH_TOKEN_EXPIRE);
        return new Token(accessToken, refreshToken);
    }

    public void updatePassword(AuthorForUpdatePass updatePass) {
        codeCheck("changepassword", updatePass.getCode());
        final Author author = authorMapper.getById(LoginUserContext.getUserId());
        if (author == null) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_PASSWORD_UPDATE_FAIL);
        }
        String newPassword = Md5Crypt.apr1Crypt(updatePass.getPassword(), author.getSalt());
        Integer i = authorMapper.updatePassword(author.getId(), newPassword);
        if (i <= 0) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_PASSWORD_UPDATE_FAIL);
        }
    }

    public void updateEmail(AuthorForUpdateEmail updateEmail) {
        codeCheck("changemail", updateEmail.getCode());
        Author author = authorMapper.getById(LoginUserContext.getUserId());
        if (author == null) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_EMAIL_UPDATE_FAIL);
        }
        author = authorMapper.getByEmail(updateEmail.getEmail());
        if (null != author) {
            throw new BusinessException(BizCodeEnum.AUTH_EMAIL_EXISTS);
        }

    }

    private void codeCheck(String scene, String code) {
        final String key = codeKey(scene);
        final String codeRedis = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(code) || !code.equals(codeRedis)) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCOUNT_CODE_INCORRECT);
        }
    }

    private String codeKey(String scene) {
        return "code:" + LoginUserContext.getUserId() + ":" + scene;
    }
}
