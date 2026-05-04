package com.hbust.mapper;

import com.hbust.pojo.Emp;
import com.hbust.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper {
    //========================原始分页查询方法====================
//    @Select("select  count(*) from emp left join  dept on emp.dept_id = dept.id" )
//    public Long count();
//
//    @Select("select emp.*,dept.name from emp left join  dept on emp.dept_id = dept.id " +
//            "order by emp.update_time desc limit #{start},#{pageSize}")
//    public List<Emp> list(Integer start,Integer pageSize);

//    @Select("select emp.*,dept.name from emp left join  dept on emp.dept_id = dept.id " +
//            "order by emp.update_time desc ")
//    public List<Emp> list(String name, Integer gender,
//                          LocalDate begin, LocalDate end);

    public List<Emp> list(EmpQueryParam empQueryParam);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into emp(username, name, gender, phone, job, salary, image," +
            " entry_date, dept_id, create_time, update_time) " +
            "values(#{username},#{name},#{gender},#{phone},#{job}," +
            "#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);


    void deleteByIds(List<Integer> ids);


    Emp getById(Integer id);

    void updateById(Emp emp);

    @MapKey("pos")
    List<Map<String,Object>> countEmpJobData();
    @MapKey("name")
    List<Map<String, Object>> countEmpGenderData();
    @Select("select id,username,name from emp where username=#{username} and password=#{password}")
    Emp selectByUsernameAndPassword(Emp emp);
}
