package com.capstone.sandwich.Repository;

import com.capstone.sandwich.Domain.Entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



public interface CarRepository extends JpaRepository<Car,Integer> {
    Optional<Car> findByCarNumber(String carNumber);

    @Query("SELECT o FROM Car o WHERE YEAR(o.createdDate) = :year AND MONTH(o.createdDate) = :month")
    List<Car> findByThisMonth(int year, int month);

    List<Car> findByCreatedDate(LocalDate createdDate);

    void deleteByCarNumber(String carNumber);

    void deleteByUserId(Long userId);

    List<Car> findByUserId(Long userId);
}
