package com.halildurmus.hotdeals.exception;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

class AppErrorAttributes extends DefaultErrorAttributes {

  private final String currentApiVersion;

  public AppErrorAttributes(final String currentApiVersion) {
    this.currentApiVersion = currentApiVersion;
  }

  @Override
  public Map<String, Object> getErrorAttributes(final WebRequest webRequest,
      final ErrorAttributeOptions options) {
    final Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest,
        ErrorAttributeOptions.defaults().including(Include.MESSAGE));
    final AppError superHeroAppError = AppError.fromDefaultAttributeMap(currentApiVersion,
        defaultErrorAttributes);

    return superHeroAppError.toAttributeMap();
  }

}