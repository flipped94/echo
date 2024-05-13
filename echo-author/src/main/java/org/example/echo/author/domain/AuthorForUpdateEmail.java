package org.example.echo.author.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorForUpdateEmail {

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "邮箱不能为空")
    private String email;

}
