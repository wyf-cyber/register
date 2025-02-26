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
    private int time;   // 预约名次 or 预约时间
}