package com.hbust.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperateLog {
    private Integer id;              // 日志ID
    private Integer operateEmpId;    // 操作员工ID
    private String operateEmpName;   // 操作员工名称
    private LocalDateTime operateTime; // 操作时间
    private String className;        // 类名
    private String methodName;       // 方法名
    private String methodParams;     // 方法参数
    private String returnValue;      // 返回值
    private Long costTime;           // 耗时（毫秒）
}