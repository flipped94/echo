package org.example.common.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {

    UNKONWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女"),

    ;

    private final Integer code;
    private final String msg;

    GenderEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
