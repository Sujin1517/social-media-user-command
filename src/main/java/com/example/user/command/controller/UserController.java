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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserProducer userProducer;
    private final List<KafkaStatus<User>> kafkaList = new ArrayList<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody User req){
        User user = User.builder()
                .id(UUID.randomUUID())
                .password(req.getPassword())
                .phone(req.getPhone())
                .name(req.getName())
                .image(req.getImage())
                .desc(req.getDesc())
                .createdAt(new Date())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "insertUser");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "insertUser");
        kafkaList.remove(userKafkaStatus);
    }

    @PutMapping("{id}")
    public void editUser(
            @PathVariable UUID id,
            @RequestBody User req
    ){
        User user = User.builder()
                .id(id)
                .image(req.getImage())
                .name(req.getName())
                .desc(req.getDesc())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "updateUser");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "updateUser");
        kafkaList.remove(userKafkaStatus);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable UUID id) {
        User user = User.builder().id(id).build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "deleteUser");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "deleteUser");
        kafkaList.remove(userKafkaStatus);
    }


    @PostMapping("{userId}/followers/{followerId}")
    public void addFollower(
            @PathVariable UUID userId,
            @PathVariable UUID followerId
    ) {

    }
    @DeleteMapping("{userId}/followers/{followerId}")
    public void deleteFollower(
            @PathVariable UUID userId,
            @PathVariable UUID followerId
    ) {

    }


    @PostMapping("{userId}/block/users/{blockedUserId}")
    public void addBlockUser(
            @PathVariable UUID userId,
            @PathVariable UUID blockedUserId
    ) {

    }
    @DeleteMapping("{userId}/block/users/{blockedUserId}")
    public void deleteBlockUser(
            @PathVariable UUID userId,
            @PathVariable UUID blockedUserId
    ) {

    }


    @PostMapping("{userId}/block/keyword/{keyword}")
    public void addBlockKeyword(
            @PathVariable UUID userId,
            @PathVariable String keyword
    ) {

    }
    @DeleteMapping("{userId}/block/keyword/{keyword}")
    public void deleteBlockKeyword(
            @PathVariable UUID userId,
            @PathVariable String keyword
    ) {

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
