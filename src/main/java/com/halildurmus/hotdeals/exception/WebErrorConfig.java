package com.halildurmus.hotdeals.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebErrorConfig {

  @Value("${api-version}")
  private String currentApiVersion;

  @Bean
  public ErrorAttributes errorAttributes() {
    return new AppErrorAttributes(currentApiVersion);
  }

}