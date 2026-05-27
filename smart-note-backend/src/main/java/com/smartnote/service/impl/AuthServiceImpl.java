package com.smartnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartnote.common.JwtUtils;
import com.smartnote.dto.LoginRequest;
import com.smartnote.dto.LoginResponse;
import com.smartnote.dto.RegisterRequest;
import com.smartnote.entity.User;
import com.smartnote.mapper.UserMapper;
import com.smartnote.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (user == null) {
            throw new RuntimeException("邮箱未注册");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getAvatar());
    }

    @Override
    public void register(RegisterRequest request) {
        User existByEmail = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (existByEmail != null) {
            throw new RuntimeException("该邮箱已注册");
        }
        User existByName = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (existByName != null) {
            throw new RuntimeException("该用户名已被使用");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBio("ta还没想好签名~");
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setNoteCount(0);
        userMapper.insert(user);
    }
}
