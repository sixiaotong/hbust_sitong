package com.smartnote.service;

import com.smartnote.dto.LoginRequest;
import com.smartnote.dto.LoginResponse;
import com.smartnote.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
