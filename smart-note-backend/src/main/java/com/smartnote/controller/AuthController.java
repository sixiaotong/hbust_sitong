package com.smartnote.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartnote.common.Result;
import com.smartnote.dto.LoginRequest;
import com.smartnote.dto.LoginResponse;
import com.smartnote.dto.RegisterRequest;
import com.smartnote.entity.User;
import com.smartnote.mapper.UserMapper;
import com.smartnote.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return Result.success(resp);
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        Map<String, Object> info = new HashMap<>();
        info.put("userId", user.getId());
        info.put("username", user.getUsername());
        info.put("email", user.getEmail());
        info.put("avatar", user.getAvatar());
        return Result.success(info);
    }
}
