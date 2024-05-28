package com.example.user.command.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class User {
    private String user_id;
    private String user_image;
    private String user_name;
    private String user_desc;
    private Integer user_total_post;
    private Integer user_total_like;
    private Boolean user_disable;
}
