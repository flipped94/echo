package org.example.webook.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobConfigProperties {


    private String adminAddresses;

    private String appName;

    private String ip;

    private int port;

    private String accessToken;

    private String logPath;

    private int logRetentionDays;
}
