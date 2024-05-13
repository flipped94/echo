package org.example.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成这个对象
 *
 * @param <T> 类型参数
 */
@Data
public class Result<T> implements Serializable {

    // 响应状态码
    private String code;

    // 响应消息
    private String message;

    // 响应中的数据
    private T data;

    // 构造器私有
    private Result() {
    }

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>("200", "success", data);
    }

    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message, null);
    }

}
