package com.capstone.sandwich.Controller;

import com.capstone.sandwich.Domain.DTO.*;
import com.capstone.sandwich.Domain.Exception.ApiException;
import com.capstone.sandwich.Service.CarService;
import com.capstone.sandwich.aws.s3.service.S3Service;
import com.capstone.sandwich.rabbitmq.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final CarService carService;
    private final S3Service s3Service;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQService rabbitMQService;

    @PostMapping("/inspection")
    public String requestInspection(@ModelAttribute RequestDTO requestDTO, @CookieValue(value = "userId") Long userId) throws ApiException, IOException {
        log.info("request car = {}, image cnt = {}", requestDTO.getCarNumber(), requestDTO.getImageList().size());

        //validation
        carService.validateDTO(requestDTO);

        //request to Ai - input requestDTO
        //response from Ai - output AiResponseDTO
        AiResponseDTO aiResponseDTO = carService.requestToAi(requestDTO);

        //insert Storage - input AiResponseDTO.getPhotos() output url List
        List<MultipartFile> imageList = aiResponseDTO.getImageList();
        List<String> imageUrlList = new ArrayList<>();
        for (MultipartFile image : imageList) {
            String imageUrl = s3Service.upload(image);
            imageUrlList.add(imageUrl);
        }
        log.info("imageURLList = {}", imageUrlList);

        //insert DB - input AiResponseDTO
        carService.insertDB(aiResponseDTO, imageUrlList, userId);

        //redirect date
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "redirect:http://localhost:8000/api/report/" + date;
    }

    @GetMapping("/users/car-info/{userId}")
    public String getCarInfo(Model model, @PathVariable("userId") Long userId) {
        List<CarDto> carDtoList = carService.getCarInfoFromUserId(userId);

        if (carDtoList != null) {
            model.addAttribute("carInfoList", carDtoList);
        }

        return "info";
    }

    @DeleteMapping("/delete/{carNumber}")
    public String deleteReport(@PathVariable("carNumber") String carNumber, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Cookie[] cookies = request.getCookies();
        Long userId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    try {
                        userId = Long.valueOf(cookie.getValue());
                    } catch (NumberFormatException e) {
                        log.error("Invalid user id in cookie", e);
                    }
                    break;
                }
            }
        }

        if (userId == null) {
            log.info("user id가 쿠키에 없음");
            return "redirect:http://localhost:8000/main";
        }

        return "redirect:http://localhost:8000/report/{date}";
    }

//    @GetMapping("/report/{date}")
//    public ResponseEntity<?> readReportFromDate(@PathVariable("date") String date){
//        //date: ex) 2023-11-11
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate localDate = LocalDate.parse(date, formatter);
//
//        List<ReportDto> reportList = carService.getReportDtoFromDate(localDate);
//
//        if(reportList.isEmpty()){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 날짜에 진행된 검수가 없습니다.");
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(reportList);
//    }

}
