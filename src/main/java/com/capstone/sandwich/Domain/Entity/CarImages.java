package com.capstone.sandwich.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "car_images")
public class CarImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    public void setCar(Car car) {
        this.car = car;
    }
}
