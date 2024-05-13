package org.example.echo.sdk.author;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorEvent {

    private Long authorId;

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
