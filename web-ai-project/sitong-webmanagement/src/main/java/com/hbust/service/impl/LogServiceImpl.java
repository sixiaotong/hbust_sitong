package com.hbust.service.impl;

import com.hbust.mapper.LogMapper;
import com.hbust.pojo.OperateLog;
import com.hbust.pojo.PageResult;
import com.hbust.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LogServiceImpl implements LogService {
    
    @Autowired
    private LogMapper logMapper;
    
    @Override
    public PageResult<OperateLog> page(Integer page, Integer pageSize) {
        log.info("日志分页查询：page={}, pageSize={}", page, pageSize);
        
        // 1. 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 2. 查询当前页数据列表
        List<OperateLog> rows = logMapper.selectPage(offset, pageSize);
        
        // 3. 查询总记录数
        Long total = logMapper.selectTotalCount();
        
        // 4. 返回分页结果
        return new PageResult<>(total, rows);
    }
}