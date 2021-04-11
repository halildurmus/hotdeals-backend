package com.halildurmus.hotdeals.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

  @Bean
  public ValidatingMongoEventListener validatingMongoEventListener(LocalValidatorFactoryBean factory) {
    return new ValidatingMongoEventListener(factory);
  }
}