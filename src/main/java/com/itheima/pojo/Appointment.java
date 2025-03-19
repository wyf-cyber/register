package com.itheima.pojo;
import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private String department;
    private String doctor;
    private String username;
    private String day; // 预约日期
    private int time;   // 数据库存储的是预约时间，个人预约查询时转换为预约名次，历史预约查询时转换为预约的完成状态（0表示没有完成问诊，1表示已完成）
}