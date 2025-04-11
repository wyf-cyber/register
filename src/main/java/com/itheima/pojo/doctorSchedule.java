package com.itheima.pojo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor   // 全参构造函数
@Getter
@Setter

// 医生排班信息，对应数据库中的doctors表
public class doctorSchedule {
    private String department;
    private String doctor;
    private String detail;
    private int state;
    private String work_date;
}
