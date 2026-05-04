package com.hbust.interceptor;

import com.hbust.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if(requestURI.contains("/login")){
            log.info("登录操作，放行！");
            return true;
        }
        String token = request.getHeader("token");
        if(token == null || token.isEmpty()){
            log.info("用户未登录，请先登录！");
            response.setStatus(response.SC_UNAUTHORIZED);
            return false;
        }

        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("令牌非法，响应401");
            response.setStatus(response.SC_UNAUTHORIZED);
            return false;
        }

        log.info("令牌合法，放行！");
        return true;
    }
}
