package com.example.user.command.kafka.dto;

public record KafkaStatus<T>(
        T data,
        String status
) {
}
