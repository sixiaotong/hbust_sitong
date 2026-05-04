package com.hbust.exception;

import com.hbust.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result handleException(Exception e){
        log.error("服务器发生异常:{}",e.getMessage());
        return Result.error("服务器发生异常,请联系管理员");
    }

    @ExceptionHandler
    public Result handleException(DuplicateKeyException e){
        log.error("手机号重复:{}",e.getMessage());
        String message = e.getMessage();
        int i= message.indexOf("Duplicate entry");
        String errmsg = message.substring(i);
        String [] arr = errmsg.split(" ");
        return Result.error(arr[2]+"已存在");
    }
}
