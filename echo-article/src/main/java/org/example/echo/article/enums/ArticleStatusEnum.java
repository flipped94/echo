package org.example.echo.article.enums;

import lombok.Getter;

@Getter
public enum ArticleStatusEnum {

    UnKnown(0, "未知"),
    DRAFT(1, "草稿"),
    PUBLISHED(2, "已发布"),
    PRIVATE(3, "尽自己可见"),
    ;

    private final int code;
    private final String status;

    ArticleStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static ArticleStatusEnum getByCode(int code) {
        for (ArticleStatusEnum articleStatusEnum : values()) {
            if (articleStatusEnum.getCode() == code) {
                return articleStatusEnum;
            }
        }
        return UnKnown;
    }
}
