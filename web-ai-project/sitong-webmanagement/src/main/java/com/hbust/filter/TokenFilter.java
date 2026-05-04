package com.hbust.filter;

import com.hbust.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
//@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        if(requestURI.contains("/login")){
            log.info("登录操作，放行！");
            filterChain.doFilter(request,response);
            return;
        }
        String token = request.getHeader("token");
        if(token == null || token.isEmpty()){
            log.info("用户未登录，请先登录！");
            response.setStatus(response.SC_UNAUTHORIZED);
            return;
        }

        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("令牌非法，响应401");
            response.setStatus(response.SC_UNAUTHORIZED);
        }

        log.info("令牌合法，放行！");
        filterChain.doFilter(request,response);
    }
}
