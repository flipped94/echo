package org.example.echo.search.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Author {
    private Long id;
    private String email;

    private String avatar;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private Integer gender = 0;

    private String profile;

    private String career;

    private Long birthday;

    private String degree;
    private Integer status;
}
