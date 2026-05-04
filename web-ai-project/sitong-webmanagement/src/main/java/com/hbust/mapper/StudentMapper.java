package com.hbust.mapper;

import com.hbust.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    
    // 条件分页查询学员列表
    List<Student> findByCondition(@Param("name") String name,
                                  @Param("clazzId") Integer clazzId,
                                  @Param("gender") Integer gender,
                                  @Param("degree") Integer degree,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);
    
    // 统计满足条件的学员总数
    Long countByCondition(@Param("name") String name,
                          @Param("clazzId") Integer clazzId,
                          @Param("gender") Integer gender,
                          @Param("degree") Integer degree);


    int deleteByIds(@Param("ids") List<Integer> ids);

    // 统计指定ID列表的学员数量（用于校验）
    int countByIds(@Param("ids") List<Integer> ids);

    // 根据学号查询学员
    Student selectByNo(@Param("no") String no);

    // 根据身份证号查询学员
    Student selectByIdCard(@Param("idCard") String idCard);

    // 插入学员
    void insert(Student student);
}