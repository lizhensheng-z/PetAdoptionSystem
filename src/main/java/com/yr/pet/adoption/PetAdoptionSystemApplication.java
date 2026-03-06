package com.yr.pet.adoption;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yr.pet"})
@MapperScan(basePackages = {"com.yr.pet.adoption.mapper", "com.yr.pet.ai.mapper"})
public class PetAdoptionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetAdoptionSystemApplication.class, args);
    }

}
