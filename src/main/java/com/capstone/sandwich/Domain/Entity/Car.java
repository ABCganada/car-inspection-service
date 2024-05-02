package com.capstone.sandwich.Domain.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String carNumber;

    /*TODO
    불량 유형 체크
     */

    private Integer scratch; //스크래치 개수
    private Integer installation; // 장착 불량 개수
    private Integer exterior; //외관 손상 개수
    private Integer gap; // 단차 손상 개수
    private Integer totalDefects;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CarImages> carImages;

    private Long userId;

    private LocalDate createdDate;

    public void setCarImages(List<CarImages> carImages) {
        this.carImages = carImages;
    }

    @PreRemove
    private void preRemove() {
        for (CarImages image : carImages) {
            image.setCar(null);
        }
    }
}
