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
}