package org.example.echo.search.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchParam {
    @NotBlank(message = "请输入关键字")
    private String q;
    private Integer page = 0;
    private Integer limit = 20;
    private String type = "article";
}
