package com.hbust.controller;

import com.hbust.pojo.PageResult;
import com.hbust.pojo.Result;
import com.hbust.pojo.Student;
import com.hbust.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping
    public Result list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer clazzId,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer degree,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        log.info("条件分页查询学员：name={}, clazzId={}, gender={}, degree={}, page={}, pageSize={}", 
                 name, clazzId, gender, degree, page, pageSize);
        
        PageResult<Student> pageResult = studentService.findByCondition(name, clazzId, gender, degree, page, pageSize);
        
        return Result.success(pageResult);
    }


    // 批量删除学员信息
    @DeleteMapping("/{ids}")
    public Result deleteBatch(@PathVariable String ids) {
        log.info("批量删除学员信息，ids={}", ids);

        // 调用Service的批量删除方法
        studentService.deleteByIds(ids);

        return Result.success("批量删除成功");
    }


    // 添加学员信息
    @PostMapping
    public Result add(@RequestBody Student student) {
        log.info("添加学员信息：{}", student);

        // 调用Service的添加方法
        studentService.add(student);

        return Result.success("添加成功");
    }
}