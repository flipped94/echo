package org.example.common.enums;

import lombok.Getter;

/**
 * 错误码和错误信息
 */
@Getter
public enum BizCodeEnum {
    // 通用
    OPS_REPEAT("110001", "请求太频繁"),

    // 登录认证相关
    AUTH_QRCODE_EXPIRATION("120001", "二维码已过期"),
    AUTH_ACCESS_TOKEN_EXPIRATION("120002", "accessToken过期"),
    AUTH_LOGIN_EXPIRATION("120003", "登录过期"),
    AUTH_NOT_LOGIN("120004", "未登录"),
    AUTH_PASSWORD_NOT_SAME("120005", "两次密码不一致"),
    AUTH_EMAIL_EXISTS("120006", "邮箱已注册，请登录"),
    AUTH_ACCOUNT_PASSWORD_INCORRECT("120007", "账号或密码错误"),
    AUTH_ACCOUNT_PASSWORD_UPDATE_FAIL("120008", "修改密码失败"),
    AUTH_ACCOUNT_EMAIL_UPDATE_FAIL("120009", "更换邮箱失败"),
    AUTH_ACCOUNT_CODE_INCORRECT("1200010", "验证码错误"),

    // article 相关
    ARTICLE_SAVE_FAIL("130001", "文章保存失败"),
    ARTICLE_PUBLISH_FAIL("130002", "文章发布失败"),
    ARTICLE_WITHDRAW_FAIL("130003", "文章撤回失败"),
    ARTICLE_NOT_FOUND("130004", "文章不存在"),


    // OSS 相关
    OSS_BUCKET_CREATE_FAIL("140001", "创建失败"),
    OSS_BUCKET_EXISTS("140002", "Bucket已存在"),
    OSS_UPLOAD_FAIL("140003", "上传失败"),
    OSS_DELETE_FAIL("140004", "删除失败"),

    ;

    private final String code;

    private final String msg;

    BizCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
