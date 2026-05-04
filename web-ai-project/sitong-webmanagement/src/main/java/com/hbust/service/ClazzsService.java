package com.hbust.service;

import com.hbust.pojo.Clazz;
import com.hbust.pojo.PageResult;

public interface ClazzsService {
    // 条件分页查询
    PageResult<Clazz> findAll(String name, String begin, String end,
                              Integer page, Integer pageSize);

    void deleteById(Integer id);

    void add(Clazz clazz);
}
