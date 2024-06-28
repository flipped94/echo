package org.example.webook.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class XxlJobConfig {
    @Resource
    private XxlJobConfigProperties xxlJobConfigProperties;

    @Resource
    private InetUtils inetUtils;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobConfigProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobConfigProperties.getAppName());
        final String hostAddress = inetUtils.findFirstNonLoopbackAddress().getHostAddress();
        xxlJobSpringExecutor.setIp(StringUtils.isNotEmpty(xxlJobConfigProperties.getIp()) ? xxlJobConfigProperties.getIp() : hostAddress);
        xxlJobSpringExecutor.setPort(xxlJobConfigProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobConfigProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobConfigProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobConfigProperties.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }
}
