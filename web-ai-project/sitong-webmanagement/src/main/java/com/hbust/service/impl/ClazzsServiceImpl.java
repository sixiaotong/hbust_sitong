package com.hbust.service.impl;

import com.hbust.mapper.ClazzsMapper;
import com.hbust.pojo.Clazz;
import com.hbust.pojo.PageResult;
import com.hbust.service.ClazzsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClazzsServiceImpl implements ClazzsService {

    @Autowired
    private ClazzsMapper clazzsMapper;

    @Override
    public PageResult<Clazz> findAll(String name, String begin, String end,
                                     Integer page, Integer pageSize) {
        // 1. 计算偏移量
        int offset = (page - 1) * pageSize;

        // 2. 查询数据列表 - 调用 findAll 方法，传入5个参数
        List<Clazz> rows = clazzsMapper.findAll(name, begin, end, offset, pageSize);

        // 3. 查询总记录数 - 调用 countAll 方法
        Long total = clazzsMapper.countAll(name, begin, end);

        // 4. 日志记录
        log.info("查询结果：total={}, rows.size={}", total, rows != null ? rows.size() : 0);

        // 5. 返回分页结果
        return new PageResult<>(total, rows);
    }


    @Override
    public void deleteById(Integer id) {
        log.info("删除班级，id={}", id);

        // 检查班级是否存在
        Clazz clazz = clazzsMapper.selectById(id);
        if (clazz == null) {
            throw new RuntimeException("班级不存在，删除失败");
        }

        // 调用Mapper删除
        clazzsMapper.deleteById(id);

        log.info("班级删除成功，id={}", id);
    }

    @Override
    public void add(Clazz clazz) {
        log.info("添加班级：{}", clazz);

        // 1. 数据校验（修正：LocalDate 类型不需要 trim()）
        if (clazz.getName() == null || clazz.getName().trim().isEmpty()) {
            throw new RuntimeException("班级名称不能为空");
        }
        if (clazz.getRoom() == null || clazz.getRoom().trim().isEmpty()) {
            throw new RuntimeException("教室不能为空");
        }
        // 修正：LocalDate 类型直接判断是否为 null
        if (clazz.getBeginDate() == null) {
            throw new RuntimeException("开课时间不能为空");
        }
        if (clazz.getEndDate() == null) {
            throw new RuntimeException("结课时间不能为空");
        }

        // 2. 检查班级名称是否已存在
        Clazz existClazz = clazzsMapper.selectByName(clazz.getName());
        if (existClazz != null) {
            throw new RuntimeException("班级名称已存在，请使用其他名称");
        }

        // 3. 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        clazz.setCreateTime(now);
        clazz.setUpdateTime(now);

        // 4. 调用Mapper插入数据
        clazzsMapper.insert(clazz);

        log.info("班级添加成功，id={}", clazz.getId());
    }


}