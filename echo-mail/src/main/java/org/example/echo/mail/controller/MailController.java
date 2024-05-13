package org.example.echo.mail.controller;

import jakarta.annotation.Resource;
import org.example.common.vo.Result;
import org.example.echo.mail.service.MailService;
import org.example.echo.sdk.mail.Mail;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {

    @Resource
    private MailService mailService;

    @PostMapping("/code/{scene}")
    public Result<Void> sendCode(@PathVariable("scene") String scene) {
        mailService.sendCode(scene);
        return Result.success(null);
    }

    @PostMapping("/common")
    public Result<Void> sendCode(@RequestBody Mail mail) {
        mailService.sendMail(mail);
        return Result.success(null);
    }

}
