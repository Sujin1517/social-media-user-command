package com.example.user.command.controller;

import com.example.user.command.domain.entity.User;
import com.example.user.command.domain.dto.KafkaStatus;
import com.example.user.command.kafka.producer.UserProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vi/users")
@RequiredArgsConstructor
public class UserController {
    private final UserProducer userProducer;
    private final List<KafkaStatus<User>> kafkaList = new ArrayList<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody User req){
        User user = User.builder()
                .user_id(UUID.randomUUID())
                .user_phone(req.getUser_phone())
                .user_name(req.getUser_name())
                .user_image(req.getUser_image())
                .user_desc(req.getUser_desc())
                .user_create_at(new Date())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "insert");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "insert");
        kafkaList.remove(userKafkaStatus);
    }

    @PutMapping("{id}")
    public void editUser(
            @PathVariable UUID id,
            @RequestBody User req
    ){
        User user = User.builder()
                .user_id(id)
                .user_image(req.getUser_image())
                .user_name(req.getUser_name())
                .user_desc(req.getUser_desc())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "update");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "update");
        kafkaList.remove(userKafkaStatus);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable UUID id) {
        User user = User.builder().user_id(id).build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "delete");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "delete");
        kafkaList.remove(userKafkaStatus);
    }


    @Scheduled(cron ="*/10 * * * * *")
    public void reSend() {
        List<KafkaStatus<User>> sucessList = new ArrayList<>();
        kafkaList.forEach(e -> {
            userProducer.send(e.data(), e.status());
            sucessList.add(e);
        });
        sucessList.forEach(kafkaList::remove);
    }
}
