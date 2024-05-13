package org.example.echo.follow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.example.echo"})
@SpringBootApplication
public class FollowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowApplication.class, args);
    }
}
