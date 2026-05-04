package com.hbust.controller;

import com.hbust.pojo.Result;
import com.hbust.service.StudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/report")
public class StudentReportController {
    
    @Autowired
    private StudentReportService studentReportService;

    // 统计班级人数分布
    @GetMapping("/studentCountData")
    public Result getStudentCountData() {
        log.info("统计班级人数分布");
        Map<String, Object> countData = studentReportService.getStudentCountData();
        return Result.success(countData);
    }

    // 统计学员学历分布
    @GetMapping("/studentDegreeData")
    public Result getStudentDegreeData() {
        log.info("统计学员学历分布");
        Object degreeData = studentReportService.getStudentDegreeData();
        return Result.success(degreeData);
    }
}