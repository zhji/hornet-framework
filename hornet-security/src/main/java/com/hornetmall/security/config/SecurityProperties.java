package com.hornetmall.security.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Data
@Configuration
@ConfigurationProperties(prefix = "hornet.security")
public class SecurityProperties {

    private Jwt jwt=new Jwt();

    private List<String> permitAll=new ArrayList<>();
    private List<String> authenticated=new ArrayList<>();


    @Data
    @Accessors(chain = true)
    public static final class Jwt{
        private String signerKey= Base64.getEncoder().encodeToString("hornet-mall//".getBytes(StandardCharsets.UTF_8));
        private Long expiredInSeconds=1800L;
    }
}
