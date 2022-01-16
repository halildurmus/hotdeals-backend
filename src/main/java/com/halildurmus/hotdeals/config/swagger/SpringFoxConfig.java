package com.halildurmus.hotdeals.config.swagger;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  private static final String BASE_PACKAGE = "com.halildurmus.hotdeals";
  private static final List<SecurityScheme> SECURITY_SCHEMES = List.of(
      new ApiKey("Bearer", "Authorization", "header"));
  private static final Tag CATEGORIES_TAG =
      new Tag("categories", "TODO: description");
  private static final Tag DEALS_TAG = new Tag("deals", "TODO: description");
  private static final Tag NOTIFICATIONS_TAG =
      new Tag("notifications", "TODO: description");
  private static final Tag ROLES_TAG =
      new Tag("roles", "TODO: description");
  private static final Tag STORES_TAG = new Tag("stores", "TODO: description");
  private static final Tag USERS_TAG = new Tag("users", "TODO: description");

  @Autowired
  private SwaggerProperties swaggerProperties;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .apiInfo(getApiInfo())
        .securitySchemes(SECURITY_SCHEMES)
        .tags(CATEGORIES_TAG, DEALS_TAG, NOTIFICATIONS_TAG, ROLES_TAG, STORES_TAG, USERS_TAG)
        .useDefaultResponseMessages(false)
        .select()
        .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
        .paths(PathSelectors.regex("(?!/error).+"))
        .build();
  }

  private ApiInfo getApiInfo() {
    final Contact contact = new Contact(swaggerProperties.getContact().getName(),
        swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail());

    return new ApiInfoBuilder().title(swaggerProperties.getTitle())
        .description(swaggerProperties.getDescription())
        .version(swaggerProperties.getVersion())
        .contact(contact).build();
  }

}

