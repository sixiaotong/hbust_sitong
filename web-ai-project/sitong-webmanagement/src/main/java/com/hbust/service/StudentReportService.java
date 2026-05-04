package com.hbust.service;

import java.util.Map;

public interface StudentReportService {
    
    // 统计班级人数分布
    Map<String, Object> getStudentCountData();
    
    // 统计学员学历分布
    Object getStudentDegreeData();
}