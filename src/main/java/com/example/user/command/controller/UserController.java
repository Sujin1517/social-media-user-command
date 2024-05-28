package com.example.user.command.controller;

import com.example.user.command.entity.User;
import com.example.user.command.kafka.dto.KafkaStatus;
import com.example.user.command.kafka.producer.UserProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/vi/users")
@RequiredArgsConstructor
public class UserController {
    private final UserProducer userProducer;
    private final List<KafkaStatus<User>> kafkaList = new ArrayList<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(){

    }

    @PutMapping("{id}")
    public void editUser(){

    }

    @DeleteMapping("{id}")
    public void deleteUser() {

    }


    @Scheduled(cron ="*/10 * * * * *")
    public void reSend() {

    }
}
