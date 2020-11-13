package com.glmall.cart.interceptor;

import com.glmall.cart.to.UserOrTempUserTo;
import com.glmall.utils.AuthConsts;
import com.glmall.utils.CartConsts;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserOrTempUserTo> threadLocal=new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserOrTempUserTo userOrTempUserTo = new UserOrTempUserTo();
        Map<String, String> loginUser = (Map) session.getAttribute(AuthConsts.LOGIN_USER);
        if (loginUser != null) {
            userOrTempUserTo.setUserId(loginUser.get("id"));
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(CartConsts.TEMP_USER_KEY)) {
                    userOrTempUserTo.setTempUserKey(cookie.getValue());
                }
            }
        }

        if (StringUtils.isBlank(userOrTempUserTo.getTempUserKey())) {
            userOrTempUserTo.setTempUserKey(UUID.randomUUID().toString());
            userOrTempUserTo.setTemp(true);
        }
        threadLocal.set(userOrTempUserTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserOrTempUserTo userOrTempUserTo = threadLocal.get();

        if (userOrTempUserTo.getTemp()) {
            Cookie cookie = new Cookie(CartConsts.TEMP_USER_KEY, userOrTempUserTo.getTempUserKey());
            cookie.setMaxAge(CartConsts.TEMP_USER_KEY_EXP);
            cookie.setDomain("glmall.com");
            response.addCookie(cookie);
        }
    }
}
