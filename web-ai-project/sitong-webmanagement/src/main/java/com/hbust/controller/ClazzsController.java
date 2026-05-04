package com.hbust.controller;

import com.hbust.pojo.Clazz;
import com.hbust.pojo.PageResult;
import com.hbust.pojo.Result;
import com.hbust.service.ClazzsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzsController {
    @Autowired
    private ClazzsService clazzsService;
    @GetMapping
    public Result list(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String begin,
        @RequestParam(required = false) String end,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "5") Integer pageSize
    ) {
            log.info("条件分页查询班级：name={}, begin={}, end={}, page={}, pageSize={}",
                    name, begin, end, page, pageSize);
            // 调用Service的分页查询方法
            PageResult<Clazz> pageResult = clazzsService.findAll(name, begin, end, page, pageSize);
            return Result.success(pageResult);
        }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除班级信息，id={}", id);

        // 调用Service的删除方法
        clazzsService.deleteById(id);

        return Result.success("删除成功");
    }


    @PostMapping
    public Result add(@RequestBody Clazz clazz) {
        log.info("添加班级信息：{}", clazz);

        // 调用Service的添加方法
        clazzsService.add(clazz);

        return Result.success("添加成功");
    }
    }

