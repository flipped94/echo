package org.example.echo.sdk.article;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.example.echo.sdk.author.AuthorVO;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String abstraction;
    private List<String> tags;
    private AuthorVO author;
    private Integer status;
    private Long createTime;
    private Long updateTime;

    private Long readCount;
    private Long likeCount;
    private Long collectCount;
    private Boolean liked;
    private Boolean collected;
    private Long commentCount;
}
