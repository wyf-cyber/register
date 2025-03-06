package com.itheima.pojo;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    // Getters and Setters
    private Long id;
    private String username;
    private String password;
    private String email;
}