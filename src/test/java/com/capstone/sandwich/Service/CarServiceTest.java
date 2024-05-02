package com.capstone.sandwich.Service;

import com.capstone.sandwich.Domain.DTO.AiResponseDTO;
import com.capstone.sandwich.Domain.DTO.RequestDTO;
import com.capstone.sandwich.Domain.Entity.Car;
import com.capstone.sandwich.Domain.Exception.ApiException;
import com.capstone.sandwich.Repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RequiredArgsConstructor
@Transactional
class CarServiceTest {

    @Autowired
    private CarService carService;

    @Autowired
    private CarRepository carRepository;


    @Test
    void ValidationNotAllowed() throws IOException, ApiException {
        File file1 = new File("src/test/resources/testPhotos/roofSide.png");
        MultipartFile multipartFile1 = new MockMultipartFile(file1.getName(), file1.getName(), "image/png", Files.readAllBytes(file1.toPath()));
        List<MultipartFile> files = new ArrayList<>();
        files.add(multipartFile1);

        RequestDTO dto = new RequestDTO("A", files);

        assertThrows(ApiException.class, () -> carService.validateDTO(dto));
    }
    @Test
    void ValidationSuccess() throws IOException, ApiException {
        File file1 = new File("src/test/resources/testPhotos/roofSide.png");
        List<MultipartFile> files = new ArrayList<>();
        for(int i=0;i<8;i++) {
            MultipartFile multipartFile1 = new MockMultipartFile(file1.getName(), file1.getName(), "image/png", Files.readAllBytes(file1.toPath()));
            files.add(multipartFile1);
        }
        RequestDTO dto = new RequestDTO("A", files);

        assertThat(carService.validateDTO(dto)).isEqualTo(0);
    }


    @Test
    void ValidationNull(){
        List<MultipartFile> files = new ArrayList<>();
        RequestDTO dto = new RequestDTO("A", files);
        assertThrows(ApiException.class, () -> carService.validateDTO(dto));
    }

    @Test
    void ValidationUnsupportedMedia() throws ApiException, IOException {
        File file1 = new File("src/test/resources/testPhotos/roofSide.png");
        File file2 = new File("src/test/resources/testPhotos/unsupportedMediaType.txt");
        MultipartFile multipartFile1 = new MockMultipartFile(file1.getName(), file1.getName(), "image/png", Files.readAllBytes(file1.toPath()));
        MultipartFile multipartFile2 = new MockMultipartFile(file1.getName(), file2.getName(), "text/plain", Files.readAllBytes(file2.toPath()));

        List<MultipartFile> files = new ArrayList<>();
        files.add(multipartFile1);
        files.add(multipartFile2);
        RequestDTO dto = new RequestDTO("A", files);
        assertThrows(ApiException.class, () -> carService.validateDTO(dto));
    }

    @Test
    void saveToDB() throws IOException {
        File file1 = new File("src/test/resources/testPhotos/roofSide.png");
        File file2 = new File("src/test/resources/testPhotos/radiatorGrill.png");
        MultipartFile multipartFile1 = new MockMultipartFile(file1.getName(), file1.getName(), "image/png", Files.readAllBytes(file1.toPath()));
        MultipartFile multipartFile2 = new MockMultipartFile(file1.getName(), file2.getName(), "image/jpeg", Files.readAllBytes(file2.toPath()));
        List<MultipartFile> files = new ArrayList<>();
        files.add(multipartFile1);
        files.add(multipartFile2);

        String testCarNumber = "a";

        AiResponseDTO aiResponseDTO = new AiResponseDTO(testCarNumber,files,0,0,0,0,0);
        List<String> urls= new ArrayList<String>();
        urls.add("www.url1.storage");
        urls.add("www.url2.storage");
//        carService.insertDB(aiResponseDTO,urls);


        assertThat(carService.getCar(testCarNumber).getCarNumber()).isEqualTo(testCarNumber);

    }
    @Test
    void findCarThisMonth(){
        Car car1 = makeCar("c1");
        Car car2 = makeCar("c2");

        carRepository.save(car1);
        carRepository.save(car2);

        List<Car> cars = carService.findCarThisMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        Assertions.assertThat(cars.size()).isEqualTo(2);

    }

    private static Car makeCar(String carNumber) {
        Car car = Car.builder()
                .carNumber(carNumber)
                .gap(0)
                .scratch(2)
                .exterior(1)
                .installation(0)
                .totalDefects(3)
                .createdDate(LocalDate.now())
                .build();
        return car;
    }

}