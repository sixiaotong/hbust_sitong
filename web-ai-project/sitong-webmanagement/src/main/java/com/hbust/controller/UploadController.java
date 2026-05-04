package com.hbust.controller;

import com.hbust.pojo.Result;
import com.hbust.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {
//    @PostMapping("/upload")
//    public Result upload(String name, Integer age, MultipartFile file) throws Exception {
//        log.info("上传文件,参数:{},{},{}",name,age,file);
//        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String newFileName = UUID.randomUUID().toString() + extension;
//        file.transferTo(new File("D:/App/test_learn/" +newFileName));
//        return Result.success();
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    @PostMapping("/upload")
        public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传:{}",file.getOriginalFilename());
        String url= aliyunOSSOperator.upload(file.getBytes(),file.getOriginalFilename());
        log.info("上传成功,返回url:{}",url);
        return Result.success(url);

        }





}
