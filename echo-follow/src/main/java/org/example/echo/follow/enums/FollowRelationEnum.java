package org.example.echo.follow.enums;

import lombok.Getter;

@Getter
public enum FollowRelationEnum {

    UNKNOWN(0, "未知"),
    ACTIVE(1, "关注"),
    INACTIVE(2, "未关注"),

    ;

    private final Integer code;
    private final String status;

    FollowRelationEnum(Integer i, String status) {
        this.code = i;
        this.status = status;
    }
}
