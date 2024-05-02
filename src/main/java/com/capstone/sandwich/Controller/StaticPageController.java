package com.capstone.sandwich.Controller;

import com.capstone.sandwich.Domain.DTO.BackResponseDTO;
import com.capstone.sandwich.Domain.DTO.ReportDto;
import com.capstone.sandwich.Domain.Entity.Car;
import com.capstone.sandwich.Service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class StaticPageController {

    private final CarService carService;

    @GetMapping("/api/inspection")
    public String inspectionPage() {
        return "inspection";
    }

    @GetMapping("/api/report/{date}")
    public String reportPage(@PathVariable("date") String date, Model model) {
        // 날짜를 파싱하여 LocalDate 객체로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        List<ReportDto> reportList = carService.getReportDtoFromDate(localDate);

        model.addAttribute("date", date);
        model.addAttribute("reportList", reportList);

        return "report";
    }

    @GetMapping("/api/result/{carNumber}")
    public String resultPage(@PathVariable("carNumber") String carNumber, Model model) {

        Car car = carService.getCar(carNumber);
        BackResponseDTO carInfo = carService.convertToBackResponseDto(car, car.getId());
        model.addAttribute("carInfo", carInfo);

        return "result";
    }
}
