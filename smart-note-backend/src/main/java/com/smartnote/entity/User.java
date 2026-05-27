package com.smartnote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String bio;
    private Integer gender;
    private LocalDate birthday;
    private Integer followerCount;
    private Integer followingCount;
    private Integer noteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
