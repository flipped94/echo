package org.example.echo.mvcconfig.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.vo.Result;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
@Primary
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("[业务异常: {}, {}]", e.getCode(), e.getMsg());
        return Result.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> handleException(Exception e) {
        log.error("[非业务异常 {}]", e.getMessage());
        return Result.fail(BizCodeEnum.OPS_REPEAT.getCode(), "系统繁忙");
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result<String> handleArgumentException(BindException e) {
        final String message = e.getMessage();
        final ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return Result.fail("400", objectError.getDefaultMessage());
    }
}
