package com.hbust.service.impl;

import com.hbust.mapper.StudentReportMapper;
import com.hbust.service.StudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class StudentReportServiceImpl implements StudentReportService {
    
    @Autowired
    private StudentReportMapper studentReportMapper;

    @Override
    public Map<String, Object> getStudentCountData() {
        log.info("统计班级人数分布");
        
        // 1. 查询各班人数
        List<Map<String, Object>> clazzCountList = studentReportMapper.selectStudentCountByClazz();
        
        // 2. 提取班级名称列表和人数列表
        List<String> clazzList = new ArrayList<>();
        List<Integer> dataList = new ArrayList<>();
        
        for (Map<String, Object> map : clazzCountList) {
            String clazzName = (String) map.get("clazzName");
            Long studentCountLong = (Long) map.get("studentCount");
            Integer studentCount = studentCountLong != null ? studentCountLong.intValue() : 0;
            
            clazzList.add(clazzName);
            dataList.add(studentCount);
        }
        
        // 3. 封装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("clazzList", clazzList);
        result.put("dataList", dataList);
        
        log.info("班级人数统计结果：共{}个班级，总人数={}", clazzList.size(), 
                 dataList.stream().mapToInt(Integer::intValue).sum());
        
        return result;
    }

    @Override
    public Object getStudentDegreeData() {
        log.info("统计学员学历分布");
        
        // 1. 查询学历统计数据
        List<Map<String, Object>> degreeList = studentReportMapper.selectStudentDegreeCount();
        
        // 2. 处理数据，移除未知学历
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> map : degreeList) {
            String name = (String) map.get("name");
            Long valueLong = (Long) map.get("value");
            Integer value = valueLong != null ? valueLong.intValue() : 0;
            
            // 跳过未知学历
            if (!"未知".equals(name)) {
                Map<String, Object> degreeItem = new HashMap<>();
                degreeItem.put("name", name);
                degreeItem.put("value", value);
                result.add(degreeItem);
            }
        }
        
        log.info("学历统计结果：{}", result);
        
        return result;
    }
}