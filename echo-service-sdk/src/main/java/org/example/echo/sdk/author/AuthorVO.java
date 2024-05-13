package org.example.echo.sdk.author;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String email;
    private String avatar;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private Integer gender = 0;

    private String profile;

    private String career;

    private Long birthday = 0L;

    private String degree;
}
