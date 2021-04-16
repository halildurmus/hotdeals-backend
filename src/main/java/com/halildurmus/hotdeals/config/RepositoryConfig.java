package com.halildurmus.hotdeals.config;

import com.halildurmus.hotdeals.category.Category;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.store.Store;
import com.halildurmus.hotdeals.user.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
class RepositoryConfig implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
      CorsRegistry cors) {
    config.exposeIdsFor(Category.class, Deal.class, Store.class, User.class);
  }

}