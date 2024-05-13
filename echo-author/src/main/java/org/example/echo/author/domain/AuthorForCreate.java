package org.example.echo.author.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.util.RSAUtil;

@Data
public class AuthorForCreate {

    @Email(message = "请输入正确的邮箱")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "请确认密码")
    private String repeatPassword;

    public void check() {
        try {
            password = RSAUtil.privateDecrypt(password);
            repeatPassword = RSAUtil.privateDecrypt(repeatPassword);
            if (!password.equals(repeatPassword)) {
                throw new BusinessException(BizCodeEnum.AUTH_PASSWORD_NOT_SAME);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
