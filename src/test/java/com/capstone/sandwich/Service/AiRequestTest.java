package com.capstone.sandwich.Service;


import com.capstone.sandwich.Domain.DTO.AiResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class AiRequestTest {

//    private static String ENDPOINT = "http://ec2-3-137-69-122.us-east-2.compute.amazonaws.com:5000/v2/object-detection/best";
    private static String ENDPOINT = "http://172.20.10.7:5000/v2/object-detection/best";

    private static ResponseEntity<Map> getApiResponse() throws IOException {
        MultiValueMap<String, Object> requestBody = makeRequestBody();

        HttpHeaders headers = makeRequestHeader();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);
        return response;
    }

    private static HttpHeaders makeRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }
    private static File multipartFileToFile(MultipartFile mfile) throws IOException {
        File file = new File(mfile.getOriginalFilename());
        mfile.transferTo(file);
        return file;
    }

    public class RequestBodyClass{
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        public RequestBodyClass(MultiValueMap<String, Object> requestBody) {
            this.requestBody = requestBody;
        }
    }
    private static MultiValueMap<String, Object> makeRequestBody() throws IOException {


        // 이미지 파일을 바이트 배열로 읽어와서 request body에 추가
        Path imagePath1 = Paths.get("src/test/resources/testPhotos/IMG1.jpg");  // 이미지 파일 경로를 실제 파일 경로로 변경
        Path imagePath2 = Paths.get("src/test/resources/testPhotos/IMG2.jpg");  // 이미지 파일 경로를 실제 파일 경로로 변경

        File file1 = new File("src/test/resources/testPhotos/roofSide.png");
        MultipartFile multipartFile1 = new MockMultipartFile(file1.getName(), file1.getName(), "image/png", Files.readAllBytes(file1.toPath()));



        // 이미지 파일을 MultiValueMap에 추가
//        requestBody.add("images", new FileSystemResource(multipartFileToFile(multipartFile1)));
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("images", new FileSystemResource(multipartFileToFile(multipartFile1)));
        requestBody.add("images", new FileSystemResource(new File(imagePath2.toString())));

        System.out.println("requestBody = " + requestBody.get("images"));
        return requestBody;
    }
    @Test
    void requestToAi() throws IOException {
        ResponseEntity<Map> apiResponse = getApiResponse();

        //apiResponse.getBody() 내에 데이터 있음
        //결과값은 results
        //이미지들은 images로 오며, 아래 함수를 통해 decoding 해야됨

        ObjectMapper objectMapper = new ObjectMapper();
        AiResponseDTO aiResponseDTO = objectMapper.convertValue(apiResponse.getBody(), AiResponseDTO.class);



    }


}
