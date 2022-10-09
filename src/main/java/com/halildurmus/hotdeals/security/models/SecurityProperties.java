package com.halildurmus.hotdeals.security.models;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

  private List<String> allowedOrigins;

  private List<String> allowedMethods;

  private List<String> superAdmins;
}
