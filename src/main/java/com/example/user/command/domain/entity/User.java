package com.example.user.command.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class User {
    private UUID user_id;
    private String user_phone;
    private String user_name;
    private String user_image;
    private String user_desc;
    private Date user_create_at;
    private Integer user_total_post;
    private Integer user_total_like;
    private Boolean user_disable;
}
