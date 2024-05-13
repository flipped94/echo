package org.example.echo.author.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorForUpdate {

    private String avatar;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private Integer gender = 0;

    private String profile;

    private String career;

    private Long birthday = 0L;

    private String degree;

}
