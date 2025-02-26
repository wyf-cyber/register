package com.itheima.pojo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterInfo {
    private String department;
    private String doctor;
    private String detail;
    private int state;
}