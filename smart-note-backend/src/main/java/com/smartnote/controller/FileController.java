package com.smartnote.controller;

import com.aliyun.oss.OSS;
import com.smartnote.common.Result;
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
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(
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
        String objectKey = "notes/" + datePath + "/" + UUID.randomUUID() + ext;

        try {
            ossClient.putObject(bucketName, objectKey, file.getInputStream());
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }

        String url = "https://" + bucketName + "." + endpoint.replace("http://", "").replace("https://", "") + "/" + objectKey;

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }
}
