package com.glmall.member.interceptor;

import TO.MemberTo;
import com.glmall.utils.AuthConsts;
import com.glmall.utils.BeanMapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class MemberInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", requestURI);
        if (match) return true;

        Map attribute = (Map) request.getSession().getAttribute(AuthConsts.LOGIN_USER);
        if (attribute==null){
            request.getSession().setAttribute("msg", "Please Login");
            response.sendRedirect("http://auth.glmall.com/login.html");
            return false;
        }
        else {
            MemberTo loginUser = BeanMapUtils.mapToBean(attribute, MemberTo.class);
            threadLocal.set(loginUser);
            return true;
        }
    }
}
