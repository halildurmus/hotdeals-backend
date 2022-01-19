package com.halildurmus.hotdeals.config.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("swagger.contact")
@Data
class SwaggerContact {

  private String email;
  private String name;
  private String url;

}
