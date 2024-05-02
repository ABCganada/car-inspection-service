package com.capstone.sandwich.Domain.DTO;

import com.capstone.sandwich.Domain.Entity.CustomMultipartFile;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDTO {
    private String carNumber;
    private List<MultipartFile> imageList;
    private List<String> encodedImages;

    private Integer scratch; //스크래치 개수
    private Integer installation; // 장착 불량 개수
    private Integer exterior; //외관 손상 개수
    private Integer gap; // 단차 손상 개수

    private Integer totalDefects;

    public AiResponseDTO(String carNumber, List<MultipartFile> imageList, Integer scratch, Integer installation, Integer exterior, Integer gap, Integer totalDefects) {
        this.carNumber = carNumber;
        this.imageList = imageList;
        this.scratch = scratch;
        this.installation = installation;
        this.exterior = exterior;
        this.gap = gap;
        this.totalDefects = totalDefects;
    }

    public void setTotalDefects() {
        this.totalDefects = scratch + installation + exterior + gap;
    }

    public void setImageList(){//TODO 이미지 이름 정하는거 어케할지

        List<String> base64Images = encodedImages;
        List<MultipartFile> decodedImages = new ArrayList<>();
        // Base64 디코딩
        for (int i=0;i<base64Images.size();i++) {
            String base64Image = base64Images.get(i);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            MultipartFile image =
                    new CustomMultipartFile(imageBytes, carNumber+i, carNumber+i+".png", "png", imageBytes.length);
            decodedImages.add(image);
        }
        this.imageList = decodedImages;
    }
}
