package com.hbust.mapper;

import com.hbust.pojo.OperateLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LogMapper {
    
    // 插入日志
    @Insert("INSERT INTO operate_log (operate_emp_id, operate_emp_name, operate_time, " +
            "class_name, method_name, method_params, return_value, cost_time) " +
            "VALUES (#{operateEmpId}, #{operateEmpName}, #{operateTime}, " +
            "#{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperateLog operateLog);
    
    // 分页查询日志列表
    @Select("SELECT id, operate_emp_id, operate_emp_name, operate_time, " +
            "class_name, method_name, method_params, return_value, cost_time " +
            "FROM operate_log ORDER BY operate_time DESC LIMIT #{offset}, #{pageSize}")
    List<OperateLog> selectPage(@Param("offset") int offset, 
                                @Param("pageSize") int pageSize);
    
    // 查询总记录数
    @Select("SELECT COUNT(*) FROM operate_log")
    Long selectTotalCount();
}