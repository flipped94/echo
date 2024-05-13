package org.example.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.enums.BizCodeEnum;

/**
 * 业务异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private String code;
    private String msg;

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMsg());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMsg();
    }
}
