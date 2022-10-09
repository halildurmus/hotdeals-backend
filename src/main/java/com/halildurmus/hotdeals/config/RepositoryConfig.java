package com.halildurmus.hotdeals.config;

import com.halildurmus.hotdeals.category.Category;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.report.comment.CommentReport;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.store.Store;
import com.halildurmus.hotdeals.user.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
class RepositoryConfig implements RepositoryRestConfigurer {

  private final Class<?>[] exposedClasses = {
    Category.class,
    Comment.class,
    CommentReport.class,
    Deal.class,
    DealReport.class,
    Store.class,
    User.class,
    UserReport.class
  };

  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
    config.exposeIdsFor(exposedClasses);
  }
}
