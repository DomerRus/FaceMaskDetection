package ru.ityce4ka.yolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class YoloServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YoloServiceApplication.class, args);
    }
}
