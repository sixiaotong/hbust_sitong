package com.hbust.controller;

import com.hbust.pojo.Emp;
import com.hbust.pojo.LoginInfo;
import com.hbust.pojo.Result;
import com.hbust.service.EmpService;
import com.hbust.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private EmpService empService;
    
    @PostMapping
    public Result login(@RequestBody Emp emp) {
        log.info("用户登录：{}", emp.getUsername());
        
        // 验证用户名密码，返回 LoginInfo
        LoginInfo loginInfo = empService.login(emp);

        if (loginInfo != null) {
            // 生成JWT Token，存储用户信息
            Map<String, Object> claims = new HashMap<>();
            claims.put("empId", loginInfo.getId());      // 使用 getId()
            claims.put("empName", loginInfo.getName());  // 使用 getName()
            claims.put("username", loginInfo.getUsername());

            String token = JwtUtils.generateToken(claims);

            // 设置token到LoginInfo中
            loginInfo.setToken(token);
            
            return Result.success(loginInfo);
        }
        
        return Result.error("用户名或密码错误");
    }
}