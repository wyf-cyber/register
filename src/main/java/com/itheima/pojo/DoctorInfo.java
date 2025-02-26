package com.itheima.pojo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoctorInfo {
    private String department;
    private String doctor;
    private String detail;
    private int state;

    // 构造函数
    public DoctorInfo(String department, String doctor, String detail, int state) {
        this.department = department;
        this.doctor = doctor;
        this.detail = detail;
        this.state = state;
    }
}