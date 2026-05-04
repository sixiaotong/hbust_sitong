package com.hbust;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testGnerateJwt() {
        Map<String, Object> dataMap= new HashMap<>() ;
        dataMap.put("id", 1);
        dataMap.put("username", "admin");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "c2l0b25n")// 设置密钥
                .addClaims(dataMap)   // 添加自定义数据
                .setExpiration(new Date(System.currentTimeMillis() + 30*1000))     // 设置过期时间
                .compact() ;
        System.out.println(jwt);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTc3NzM2MDA0MX0.iP0Uy15tYQaFI9giARlEdjWhMR1Mn3-BiezFUntkOGA";
        Claims claims = Jwts.parser()
                .setSigningKey("c2l0b25n") // 设置密钥
                .parseClaimsJws(token)// 解析JWT
                .getBody();// 获取数据
        System.out.println(claims);
            }



}
