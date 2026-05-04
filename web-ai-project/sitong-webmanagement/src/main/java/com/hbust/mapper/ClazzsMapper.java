package com.hbust.mapper;

import com.hbust.pojo.Clazz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClazzsMapper {

    // 条件分页查询 - 保留方法名 findAll
    List<Clazz> findAll(@Param("name") String name,
                        @Param("begin") String begin,
                        @Param("end") String end,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    // 统计总数
    Long countAll(@Param("name") String name,
                  @Param("begin") String begin,
                  @Param("end") String end);

    // 根据ID查询班级
    Clazz selectById(@Param("id") Integer id);

    // 根据ID删除班级
    void deleteById(@Param("id") Integer id);

    // 根据名称查询班级（添加这个方法）
    Clazz selectByName(@Param("name") String name);

    void insert(Clazz clazz);
}
