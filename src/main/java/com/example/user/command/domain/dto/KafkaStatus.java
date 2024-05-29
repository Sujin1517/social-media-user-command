package com.example.user.command.domain.dto;

public record KafkaStatus<T>(
        T data,
        String status
) {
}
