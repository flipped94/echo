package org.example.echo.mvcconfig;

import lombok.Data;

@Data
public class LoginUserContext {
    private static final ThreadLocal<LoginUser> CONTEXT_HOLDER = new ThreadLocal<>();

    private LoginUserContext() {
    }

    public static void set(LoginUser loginUser) {
        CONTEXT_HOLDER.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return CONTEXT_HOLDER.get();
    }

    public static Long getUserId() {
        if(CONTEXT_HOLDER.get()==null){
            return null;
        }
        return CONTEXT_HOLDER.get().getUserId();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
