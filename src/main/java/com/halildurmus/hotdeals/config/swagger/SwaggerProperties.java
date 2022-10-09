package com.halildurmus.hotdeals.config.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("swagger")
@Data
class SwaggerProperties {

  private SwaggerContact contact;

  private String description;

  private String title;

  private String version;
}
