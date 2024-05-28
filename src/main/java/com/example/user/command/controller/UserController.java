package com.example.user.command.controller;

import com.example.user.command.dto.request.UserEditRequest;
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
    public void createUser(@RequestBody User user){
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "insert");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "insert");
        kafkaList.remove(userKafkaStatus);
    }

    @PutMapping("{id}")
    public void editUser(
            @PathVariable String id,
            @RequestBody User req
    ){
        User user = User.builder()
                .user_id(id)
                .user_image(req.getUser_image())
                .user_name(req.getUser_name())
                .user_desc(req.getUser_desc())
                .build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "insert");
        kafkaList.add(userKafkaStatus);
        userProducer.send(user, "update");
        kafkaList.remove(userKafkaStatus);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable String id) {
        User user = User.builder().user_id(id).build();
        KafkaStatus<User> userKafkaStatus = new KafkaStatus<>(user, "insert");
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
