package org.example.echo.author.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorForLogin {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

}
