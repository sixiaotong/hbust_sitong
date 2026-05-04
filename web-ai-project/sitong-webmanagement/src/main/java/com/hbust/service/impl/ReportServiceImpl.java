package com.hbust.service.impl;

import com.hbust.mapper.EmpMapper;
import com.hbust.pojo.JobOption;
import com.hbust.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;


@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private EmpMapper empMapper;
    @Override
    public JobOption getEmpJobData() {
        List<Map<String,Object>> list=empMapper.countEmpJobData();
        List<Object> jobList= list.stream().map(m->m.get("pos")).toList();
        List<Object> dataList= list.stream().map(m->m.get("num")).toList();
        return new JobOption(jobList,dataList);

    }

    @Override
    public List<Map<String, Object>> getEmpGenderData() {
       return empMapper.countEmpGenderData();
    }
}
