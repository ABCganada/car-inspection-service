package com.capstone.sandwich.rabbitmq;

import com.capstone.sandwich.Domain.DTO.CarDto;
import com.capstone.sandwich.Domain.Entity.Car;
import com.capstone.sandwich.Repository.CarRepository;
import com.capstone.sandwich.Service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQService {


    private final CarService carService;
    private final CarRepository carRepository;
    private final RabbitTemplate rabbitTemplate;

    //inspection service message queue logic
    public void sendMessageToUserService(Long userId, List<CarDto> carDtoList) {
        log.info("message send: {}", carDtoList.toString());

        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("carDtoList", carDtoList);

        log.info(message.toString());

        rabbitTemplate.convertAndSend("user-service-exchange", "user.car.info", message);
    }

    @Transactional
    @RabbitListener(queues = "car-info-deletion-queue")
    public void receiveMessageFromUserService(Long userId) {
        log.info("received message: user id is {}", userId);

        try {
            carRepository.deleteByUserId(userId);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "car-dto-generation-queue")
    public void responseCarDtoList(Long userId) {
        log.info("receive message, response car dto list");

        List<CarDto> carDtoList = carRepository.findByUserId(userId)
                .stream().map(car -> convertToCar(car))
                .collect(Collectors.toList());

        rabbitTemplate.convertAndSend("user-service-exchange", "user.car.info", carDtoList);
    }

    @RabbitListener(queues = "car-test-data-creation")
    public void receiveCarDataCreationRequest(Long userId) {
        log.info("테스트 차량 데이터 생성 요청");

        Car car = Car.builder()
                .carNumber("testCar")
                .exterior(0)
                .scratch(0)
                .installation(0)
                .gap(0)
                .totalDefects(0)
                .createdDate(LocalDate.now())
                .userId(userId)
                .build();

        carRepository.save(car);
    }

    private CarDto convertToCar(Car car) {
        CarDto carDto = CarDto.builder()
                .carNumber(car.getCarNumber())
                .scratch(car.getScratch())
                .exterior(car.getExterior())
                .installation(car.getInstallation())
                .gap(car.getGap())
                .totalDefects(car.getTotalDefects())
                .build();

        return carDto;
    }
}
