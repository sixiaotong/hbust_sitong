package com.hbust.controller;

import com.hbust.pojo.PageResult;
import com.hbust.pojo.Result;
import com.hbust.pojo.OperateLog;
import com.hbust.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {
    
    @Autowired
    private LogService logService;
    
    // 日志信息分页查询
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("日志分页查询：page={}, pageSize={}", page, pageSize);
        
        PageResult<OperateLog> pageResult = logService.page(page, pageSize);
        
        return Result.success(pageResult);
    }
}