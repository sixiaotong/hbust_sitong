package com.smartnote.controller;

import com.aliyun.oss.OSS;
import com.smartnote.common.Result;
import com.smartnote.dto.NoteVO;
import com.smartnote.dto.PageResult;
import com.smartnote.dto.UserProfileVO;
import com.smartnote.service.NoteService;
import com.smartnote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @GetMapping("/{id}/profile")
    public Result<UserProfileVO> profile(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            UserProfileVO vo = userService.getUserProfile(id, currentUserId);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/follow")
    public Result<String> follow(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            userService.follow(userId, id);
            return Result.success("关注成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/unfollow")
    public Result<String> unfollow(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            userService.unfollow(userId, id);
            return Result.success("已取消关注");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/notes")
    public Result<PageResult<NoteVO>> userNotes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<NoteVO> result = noteService.getUserNotes(id, page, size);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/favorites")
    public Result<PageResult<NoteVO>> userFavorites(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<NoteVO> result = noteService.getUserFavorites(id, page, size);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        String avatar = body.get("avatar");
        String bio = body.get("bio");
        if (avatar != null) {
            userService.updateAvatar(userId, avatar);
        }
        if (bio != null) {
            userService.updateBio(userId, bio);
        }
        return Result.success("更新成功");
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam("file") MultipartFile file) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectKey = "avatars/" + datePath + "/" + UUID.randomUUID() + ext;

        try {
            ossClient.putObject(bucketName, objectKey, file.getInputStream());
        } catch (IOException e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }

        String url = "https://" + bucketName + "." + endpoint.replace("http://", "").replace("https://", "") + "/" + objectKey;
        userService.updateAvatar(userId, url);

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }

    @GetMapping("/{id}/followers")
    public Result<PageResult<UserProfileVO>> followers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            PageResult<UserProfileVO> result = userService.listFollowers(id, currentUserId, page, size);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/following")
    public Result<PageResult<UserProfileVO>> following(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            PageResult<UserProfileVO> result = userService.listFollowing(id, currentUserId, page, size);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
