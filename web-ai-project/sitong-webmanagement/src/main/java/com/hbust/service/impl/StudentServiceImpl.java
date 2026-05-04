package com.hbust.service.impl;

import com.hbust.mapper.ClazzsMapper;
import com.hbust.mapper.StudentMapper;
import com.hbust.pojo.Clazz;
import com.hbust.pojo.PageResult;
import com.hbust.pojo.Student;
import com.hbust.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClazzsMapper clazzsMapper;
    
    @Override
    public PageResult<Student> findByCondition(String name, Integer clazzId, Integer gender, 
                                                Integer degree, Integer page, Integer pageSize) {
        // 1. 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 2. 查询数据列表
        List<Student> rows = studentMapper.findByCondition(name, clazzId, gender, degree, offset, pageSize);
        
        // 3. 查询总记录数
        Long total = studentMapper.countByCondition(name, clazzId, gender, degree);
        
        // 4. 日志记录
        log.info("查询结果：total={}, rows.size={}", total, rows != null ? rows.size() : 0);
        
        // 5. 返回分页结果
        return new PageResult<>(total, rows);
    }


    @Override
    public void deleteByIds(String ids) {
        log.info("批量删除学员，ids={}", ids);

        // 1. 参数校验
        if (!StringUtils.hasText(ids)) {
            throw new RuntimeException("要删除的学员ID不能为空");
        }

        // 2. 将字符串 ids 转换为 List<Integer>
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(String::trim)           // 去除空格
                .filter(s -> !s.isEmpty())   // 过滤空字符串
                .map(Integer::parseInt)      // 转换为 Integer
                .collect(Collectors.toList());

        if (idList.isEmpty()) {
            throw new RuntimeException("要删除的学员ID不能为空");
        }

        // 3. 检查学员是否存在（可选）
        int existCount = studentMapper.countByIds(idList);
        if (existCount != idList.size()) {
            throw new RuntimeException("部分学员不存在，请刷新后重试");
        }

        // 4. 调用Mapper批量删除
        int deleteCount = studentMapper.deleteByIds(idList);

        log.info("批量删除成功，共删除{}条记录", deleteCount);
    }


    @Override
    public void add(Student student) {
        log.info("添加学员：{}", student);

        // 1. 数据校验
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new RuntimeException("学员姓名不能为空");
        }
        if (student.getNo() == null || student.getNo().trim().isEmpty()) {
            throw new RuntimeException("学员学号不能为空");
        }
        if (student.getPhone() == null || student.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (student.getIdCard() == null || student.getIdCard().trim().isEmpty()) {
            throw new RuntimeException("身份证号不能为空");
        }
        if (student.getGender() == null) {
            throw new RuntimeException("性别不能为空");
        }
        if (student.getClazzId() == null) {
            throw new RuntimeException("所属班级不能为空");
        }

        // 2. 检查学号是否已存在
        Student existStudent = studentMapper.selectByNo(student.getNo());
        if (existStudent != null) {
            throw new RuntimeException("学号已存在，请使用其他学号");
        }

        // 3. 检查身份证号是否已存在
        existStudent = studentMapper.selectByIdCard(student.getIdCard());
        if (existStudent != null) {
            throw new RuntimeException("身份证号已存在，请使用其他身份证号");
        }

        // 4. 检查班级是否存在
        Clazz clazz = clazzsMapper.selectById(student.getClazzId());
        if (clazz == null) {
            throw new RuntimeException("所属班级不存在，请选择正确的班级");
        }

        // 5. 设置默认值
        if (student.getIsCollege() == null) {
            student.setIsCollege(0); // 默认非院校
        }
        if (student.getViolationCount() == null) {
            student.setViolationCount((short) 0); // 默认违纪次数0
        }
        if (student.getViolationScore() == null) {
            student.setViolationScore((short) 0); // 默认违纪扣分0
        }

        // 6. 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        student.setCreateTime(now);
        student.setUpdateTime(now);

        // 7. 调用Mapper插入数据
        studentMapper.insert(student);

        log.info("学员添加成功，id={}", student.getId());
    }


}