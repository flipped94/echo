package org.example.echo.article.domain;

import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class ArticleForPublish {

    private Long id;

    private String cover;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer status;

    private List<String> tags;

    private String abstraction;

    private Long createTime;

    private Long updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAbstraction() {

        return abstraction;
    }

    public void setAbstraction(String abstraction) {
        if (StringUtils.isEmpty(abstraction)) {
            this.abstraction = content.length() > 128 ? content.substring(0, 128) : content;
            return;
        }
        this.abstraction = abstraction;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

}
