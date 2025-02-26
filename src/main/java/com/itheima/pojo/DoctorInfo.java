package com.itheima.pojo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor   // 全参构造函数
@Getter
@Setter

// 医生信息类，该类不对应数据库中的表，而是用于向前端展示医生信息
public class DoctorInfo {
    private String department;
    private String doctor;
    private String detail;
    private int state;      // 医生预约状态，即当前预约人数
    private boolean flag;    // 是否已经预约该医生
}