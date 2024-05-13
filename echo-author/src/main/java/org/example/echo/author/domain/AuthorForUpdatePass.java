package org.example.echo.author.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.util.RSAUtil;

@Data
public class AuthorForUpdatePass {

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "密码不能为空")
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
