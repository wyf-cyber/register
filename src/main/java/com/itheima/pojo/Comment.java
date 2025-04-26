package com.itheima.pojo;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int id;
    private String doctor;
    private String content;
    private int rating;
}
