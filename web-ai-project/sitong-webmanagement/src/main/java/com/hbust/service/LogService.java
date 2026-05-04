package com.hbust.service;

import com.hbust.pojo.OperateLog;
import com.hbust.pojo.PageResult;

public interface LogService {
    
    // 日志分页查询
    PageResult<OperateLog> page(Integer page, Integer pageSize);
}