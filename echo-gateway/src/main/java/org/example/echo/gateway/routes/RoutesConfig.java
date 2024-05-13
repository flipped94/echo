package org.example.echo.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RoutesConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(p -> p.path("/auth/**", "/author/**").uri("lb://echo-author"))
                .route(p -> p.path("/follow/**").uri("lb://echo-follow"))
                .route(p -> p.path("/articles/**").uri("lb://echo-article"))
                .route(p -> p.path("/interact/**").uri("lb://echo-interact"))
                .route(p -> p.path("/feed/**").uri("lb://echo-feed"))
                .route(p -> p.path("/oss/**").uri("lb://echo-oss"))
                .route(p -> p.path("/comments/**").uri("lb://echo-comment"))
                .route(p -> p.path("/search/**").uri("lb://echo-search"))
                .route(p -> p.path("/mail/**").uri("lb://echo-mail"))
                .build();
    }
}
