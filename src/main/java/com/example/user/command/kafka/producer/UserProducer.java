package com.example.user.command.kafka.producer;

import com.example.user.command.entity.User;
import com.example.user.command.kafka.dto.KafkaStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, KafkaStatus<User>> kafkaTemplate;
    @Value("${kafka.topic.name}") private final String topic;

    @Bean
    private NewTopic newTopic() {
        return new NewTopic(topic, 1, (short) 1);
    }

    public void send(User user, String status) {
        KafkaStatus<User> kafkaStatus = new KafkaStatus<>(user, status);
        kafkaTemplate.send(topic, kafkaStatus);
    }
}
