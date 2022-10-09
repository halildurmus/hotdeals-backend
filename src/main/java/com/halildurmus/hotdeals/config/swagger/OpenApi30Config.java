package com.halildurmus.hotdeals.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi30Config {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  private static final Tag CATEGORIES_TAG = new Tag().name("categories");

  private static final Tag DEALS_TAG = new Tag().name("deals");

  private static final Tag NOTIFICATIONS_TAG = new Tag().name("notifications");

  private static final Tag ROLES_TAG = new Tag().name("roles");

  private static final Tag STORES_TAG = new Tag().name("stores");

  private static final Tag USERS_TAG = new Tag().name("users");

  private static final List<Tag> TAGS =
      List.of(CATEGORIES_TAG, DEALS_TAG, NOTIFICATIONS_TAG, ROLES_TAG, STORES_TAG, USERS_TAG);

  @Autowired private SwaggerProperties swaggerProperties;

  @Bean
  public OpenAPI hotdealsAPI() {
    return new OpenAPI().info(getApiInfo()).components(getComponents()).tags(TAGS);
  }

  private Info getApiInfo() {
    var contact =
        new Contact()
            .name(swaggerProperties.getContact().getName())
            .email(swaggerProperties.getContact().getEmail())
            .url(swaggerProperties.getContact().getUrl());

    return new Info()
        .contact(contact)
        .title(swaggerProperties.getTitle())
        .description(swaggerProperties.getDescription())
        .version(swaggerProperties.getVersion());
  }

  private SecurityScheme getBearerSecurityScheme() {
    return new SecurityScheme()
        .name(SECURITY_SCHEME_NAME)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description("Do not include the <b>Bearer</b> prefix when entering your API key");
  }

  private Components getComponents() {
    return new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, getBearerSecurityScheme());
  }
}
