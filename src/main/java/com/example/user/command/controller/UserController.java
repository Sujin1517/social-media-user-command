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
                .phone(req.getPhone())
                .name(req.getName())
                .image(req.getImage())
                .desc(req.getDesc())
                .createdAt(new Date())
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
                .id(id)
                .image(req.getImage())
                .name(req.getName())
                .desc(req.getDesc())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "update");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "update");
        kafkaList.remove(userKafkaStatus);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable UUID id) {
        User user = User.builder().id(id).build();
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
