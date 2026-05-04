package com.hbust.aspect;

import com.alibaba.fastjson.JSON;
import com.hbust.mapper.LogMapper;
import com.hbust.pojo.OperateLog;
import com.hbust.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {
    
    @Autowired
    private LogMapper logMapper;
    
    @Pointcut("execution(* com.hbust.controller.*.*(..)) && " +
              "!execution(* com.hbust.controller.LoginController.*(..))")
    public void logPointcut() {}
    
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 在主线程中获取请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        
        Object result = joinPoint.proceed();
        
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;
        
        // 将请求上下文传递给异步线程
        saveLogAsync(joinPoint, result, costTime, requestAttributes);
        
        return result;
    }
    
    private void saveLogAsync(ProceedingJoinPoint joinPoint, Object result, 
                              long costTime, RequestAttributes requestAttributes) {
        // 使用异步线程，并传递请求上下文
        new Thread(() -> {
            try {
                // 将请求上下文绑定到当前线程
                RequestContextHolder.setRequestAttributes(requestAttributes);
                saveLog(joinPoint, result, costTime);
            } finally {
                // 清理上下文
                RequestContextHolder.resetRequestAttributes();
            }
        }).start();
    }
    
    private void saveLog(ProceedingJoinPoint joinPoint, Object result, long costTime) {
        try {
            OperateLog operateLog = new OperateLog();
            operateLog.setOperateTime(LocalDateTime.now());
            operateLog.setCostTime(costTime);
            operateLog.setClassName(joinPoint.getTarget().getClass().getName());
            operateLog.setMethodName(joinPoint.getSignature().getName());
            
            Object[] args = joinPoint.getArgs();
            String methodParams = getMethodParams(args);
            operateLog.setMethodParams(methodParams);
            
            String returnValue = result != null ? JSON.toJSONString(result) : "";
            if (returnValue.length() > 2000) {
                returnValue = returnValue.substring(0, 2000) + "...";
            }
            operateLog.setReturnValue(returnValue);
            
            // 获取操作人信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("token");
                
                log.info("获取到的token: {}", token);
                
                if (token != null && !token.isEmpty()) {
                    try {
                        Claims claims = JwtUtils.parseToken(token);
                        log.info("claims中的所有key: {}", claims.keySet());
                        
                        Integer empId = null;
                        if (claims.get("empId") != null) {
                            empId = claims.get("empId", Integer.class);
                        } else if (claims.get("id") != null) {
                            empId = claims.get("id", Integer.class);
                        }
                        
                        String empName = null;
                        if (claims.get("empName") != null) {
                            empName = claims.get("empName", String.class);
                        } else if (claims.get("name") != null) {
                            empName = claims.get("name", String.class);
                        }
                        
                        log.info("解析出的用户信息：empId={}, empName={}", empId, empName);
                        
                        if (empId != null) {
                            operateLog.setOperateEmpId(empId);
                        }
                        if (empName != null) {
                            operateLog.setOperateEmpName(empName);
                        } else {
                            operateLog.setOperateEmpName("未知");
                        }
                    } catch (Exception e) {
                        log.error("解析Token失败: {}", e.getMessage());
                        operateLog.setOperateEmpName("解析失败");
                    }
                } else {
                    operateLog.setOperateEmpName("无Token");
                }
            } else {
                log.warn("无法获取ServletRequestAttributes");
                operateLog.setOperateEmpName("无Request");
            }
            
            logMapper.insert(operateLog);
            log.info("操作日志已记录：{} - {}，耗时：{}ms，操作人：{}", 
                     operateLog.getClassName(), operateLog.getMethodName(), 
                     costTime, operateLog.getOperateEmpName());
            
        } catch (Exception e) {
            log.error("记录操作日志失败：{}", e.getMessage(), e);
        }
    }
    
    private String getMethodParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        
        Object[] filteredArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .filter(arg -> !(arg instanceof HttpSession))
                .toArray();
        
        if (filteredArgs.length == 0) {
            return "";
        }
        
        String params = JSON.toJSONString(filteredArgs);
        if (params.length() > 2000) {
            params = params.substring(0, 2000) + "...";
        }
        return params;
    }
}