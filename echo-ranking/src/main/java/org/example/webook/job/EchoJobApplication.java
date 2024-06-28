package org.example.webook.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"org.example.webook.job.openfeign"})
@SpringBootApplication
public class EchoJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoJobApplication.class, args);
    }
}
