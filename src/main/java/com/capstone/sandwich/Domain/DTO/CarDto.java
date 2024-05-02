package com.capstone.sandwich.Domain.DTO;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private String carNumber;
    private Integer scratch; //스크래치 개수
    private Integer installation; // 장착 불량 개수
    private Integer exterior; //외관 손상 개수
    private Integer gap; // 단차 손상 개수
    private Integer totalDefects;
}
