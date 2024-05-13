package org.example.echo.mvcconfig.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.common.util.JwtUtil;
import org.example.echo.mvcconfig.LoginUser;
import org.example.echo.mvcconfig.LoginUserContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            final String accessToken = request.getHeader("X-Echo-AccessToken");
            if (StringUtils.isNotBlank(accessToken)) {
                final Long userId = JwtUtil.parse(accessToken);
                final LoginUser loginUser = new LoginUser();
                loginUser.setUserId(userId);
                LoginUserContext.set(loginUser);
            }
        } catch (Exception e) {
            log.error("解密Token失败 {}", e.getMessage());
        }
        if (handler instanceof HandlerMethod method && method.hasMethodAnnotation(LoginNotRequired.class)) {
            return true;
        }
        if (LoginUserContext.getLoginUser() == null) {
            throw new BusinessException(BizCodeEnum.AUTH_NOT_LOGIN);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserContext.clear();
    }
}
