package com.hbust.service;

import com.hbust.pojo.PageResult;
import com.hbust.pojo.Student;

public interface StudentService {
    
    // 条件分页查询学员
    PageResult<Student> findByCondition(String name, Integer clazzId, Integer gender, 
                                         Integer degree, Integer page, Integer pageSize);

    void deleteByIds(String ids);

    void add(Student student);
}