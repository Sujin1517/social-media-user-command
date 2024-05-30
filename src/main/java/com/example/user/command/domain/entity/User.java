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
    private UUID id;
    private String password;
    private String phone;
    private String name;
    private String image;
    private String desc;
    private Date createdAt;
    private Integer totalPost;
    private Integer totalLike;
    private Integer totalFollower;
    private Boolean isDisable;
}
