package org.example.echo.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "es")
public class EsConfigurationProperties {
    private String serverUrl;
    private String apiKey;
}
