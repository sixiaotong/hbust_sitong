package com.hbust.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentReportMapper {
    
    // 统计各班人数（班级ID、班级名称、学生数量）
    @Select("SELECT c.id, c.name as clazzName, COUNT(s.id) as studentCount " +
            "FROM clazz c " +
            "LEFT JOIN student s ON c.id = s.clazz_id " +
            "GROUP BY c.id, c.name " +
            "ORDER BY c.id")
    List<Map<String, Object>> selectStudentCountByClazz();
    
    // 统计学员学历分布
    @Select("SELECT " +
            "CASE degree " +
            "WHEN 1 THEN '初中' " +
            "WHEN 2 THEN '高中' " +
            "WHEN 3 THEN '大专' " +
            "WHEN 4 THEN '本科' " +
            "WHEN 5 THEN '硕士' " +
            "WHEN 6 THEN '博士' " +
            "ELSE '未知' END as name, " +
            "COUNT(*) as value " +
            "FROM student " +
            "WHERE degree IS NOT NULL " +
            "GROUP BY degree " +
            "ORDER BY degree")
    List<Map<String, Object>> selectStudentDegreeCount();
}