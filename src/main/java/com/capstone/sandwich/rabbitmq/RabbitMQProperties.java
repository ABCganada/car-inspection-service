package com.capstone.sandwich.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "spring.rabbitmq")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class RabbitMQProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
