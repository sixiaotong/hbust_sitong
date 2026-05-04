package com.hbust.controller;

import com.hbust.pojo.Emp;
import com.hbust.pojo.EmpQueryParam;
import com.hbust.pojo.PageResult;
import com.hbust.pojo.Result;
import com.hbust.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/emps")
@RestController
@Slf4j
public class EmpController {
    @Autowired
    private EmpService empService;

//    @GetMapping
//    public Result Page(@RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam (defaultValue = "10") Integer pageSize,
//                       String name, Integer gender,
//                       @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
//                       @DateTimeFormat (pattern = "yyyy-MM-dd")  LocalDate end){
//        log.info("分页查询,参数:{},{},{},{},{},{}",page,pageSize,name,gender,begin,end);
//        PageResult<Emp> pageResult  = empService.page(page,pageSize,name,gender,begin,end);
//        return Result.success(pageResult);



    @GetMapping
    public Result Page(EmpQueryParam empQueryParam){
        log.info("分页查询,参数:{}", empQueryParam);
        PageResult<Emp> pageResult  = empService.page(empQueryParam);
        return Result.success(pageResult);

    }
    @PostMapping
    public Result save(@RequestBody Emp emp){
        log.info("新增员工,参数:{}", emp);
        empService.save(emp);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List< Integer> ids){
        log.info("删除员工,参数:{}",ids);
        empService.delete(ids);
        return Result.success();
    }

    @GetMapping ("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("查询员工,id:{}",id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }
    @PutMapping
    public Result update(@RequestBody Emp emp){
        log.info("更新员工:{}", emp);
        empService.update(emp);
        return Result.success();
    }




}
