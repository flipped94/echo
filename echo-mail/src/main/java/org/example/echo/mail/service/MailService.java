package org.example.echo.mail.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.util.StringUtil;
import org.example.common.vo.Result;
import org.example.echo.mail.dao.MailMapper;
import org.example.echo.mail.entity.Mail;
import org.example.echo.mail.feign.AuthorFeign;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.author.AuthorVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class MailService {

    @Resource(name = "customThreadPool")
    private Executor executor;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MailMapper mailMapper;

    @Resource
    private AuthorFeign authorFeign;

    @Value("${spring.mail.username}")
    private String from;

    public void sendCode(String scene) {
        final String code = StringUtil.numberCode(6);
        final String s = codeKey(scene);
        final Long userId = LoginUserContext.getUserId();
        executor.execute(() -> {
            stringRedisTemplate.opsForValue().set(s, code, Duration.ofMinutes(5));
            String to = "";
            try {
                final Result<AuthorVO> authorRes = authorFeign.getById(userId);
                to = authorRes.getData().getEmail();
            } catch (Exception e) {
                log.error("mail 服务查询author失败 {}", e.getMessage(), e);
            }
            String subject = "Echo 验证码";
            String content = codeMail(scene, code);
            if (StringUtils.isEmpty(to)) {
                saveMail(subject, content, "", userId);
                return;
            }
            final org.example.echo.sdk.mail.Mail mail = new org.example.echo.sdk.mail.Mail(subject, content, to);
            sendMail(mail);
        });
    }

    private void saveMail(String subject, String content, String to, Long toId) {
        final Mail mail = new Mail();
        final long now = System.currentTimeMillis();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContent(content);
        mail.setStatus(2);
        mail.setToId(toId);
        mail.setCreateTime(now);
        mail.setUpdateTime(now);
        mailMapper.save(mail);
    }

    private String codeKey(String scene) {
        return "code:" + LoginUserContext.getUserId() + ":" + scene;
    }

    private String codeMail(String scene, String code) {
        if (scene.equals("changepassword")) {
            return "修改密码验证码: " + code + " ,五分钟内有效";
        } else if (scene.equals("changemail")) {
            return "修改登录邮箱验证码: " + code + " ,五分钟内有效";
        }
        return "";
    }

    public void sendMail(org.example.echo.sdk.mail.Mail mail) {
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(mail.getSubject());
        simpleMailMessage.setText(mail.getContent());
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(mail.getTo());
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            saveMail(mail.getSubject(), mail.getContent(), mail.getTo(), -1L);
            log.error("发送验证码失败 {}, 稍后重试", e.getMessage(), e);
        }
    }
    
}
