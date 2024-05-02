package com.capstone.sandwich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@ConfigurationPropertiesScan
@EnableDiscoveryClient
@SpringBootApplication
public class SandwichApplication {

	public static void main(String[] args) {
		SpringApplication.run(SandwichApplication.class, args);
	}

}
