package com.hbust.controller;

import com.hbust.pojo.Dept;
import com.hbust.pojo.Result;
import com.hbust.service.DeptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {
//    private static final Logger log = LoggerFactory.getLogger(DeptController.class);

    @Autowired
    private DeptService deptService;
//    @RequestMapping(value = "/depts",method = RequestMethod.GET)
    @GetMapping
    public Result list(){
//        System.out.println("查询全部部门信息！");
        log.info("查询全部部门信息！");
         List<Dept> depts = deptService.findAll();
         return Result.success(depts);
    }
    @DeleteMapping
    public Result delete(Integer id){
        log.info("删除部门{}",id);
       deptService.deleteById(id);
       return Result.success();
    }
    @PostMapping
    public Result add(@RequestBody Dept dept){
        log.info("添加部门{}",dept);
        deptService.add(dept);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("查询部门{}",id);
       Dept dept = deptService.getInfo(id);
        return Result.success(dept);
    }
    @PutMapping
    public Result update(@RequestBody Dept dept){
        log.info("更新部门为{}",dept);
        deptService.update(dept);
        return Result.success();
    }
}
