package com.capstone.sandwich.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Bean
    public Queue queue() {
        return new Queue("car-info-deletion-queue", true);
    }

    @Bean
    public Queue getCarDtoListQueue() {
        return new Queue("car-dto-generation-queue", true);
    }

    @Bean
    public Queue carTestDataCreationQueue() {
        return new Queue("car-test-data-creation-queue", true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }

    // 차량 정보 삭제 큐 바인딩
    @Bean
    public Binding bindingCarDeleteQueue(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("car.delete");
    }

    //차량 정보 조회 큐 바인딩
    @Bean
    public Binding bindingCarDtoListQueue(Queue getCarDtoListQueue, DirectExchange exchange) {
        return BindingBuilder.bind(getCarDtoListQueue).to(exchange).with("car.info.list");
    }

    @Bean
    public Binding bindingCarTestDataCreationQueue(Queue carTestDataCreationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(carTestDataCreationQueue).to(exchange).with("car.test.data.creation");
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQProperties.getHost());
        connectionFactory.setPort(rabbitMQProperties.getPort());
        connectionFactory.setUsername(rabbitMQProperties.getUsername());
        connectionFactory.setPassword(rabbitMQProperties.getPassword());
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
