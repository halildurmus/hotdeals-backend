package com.halildurmus.hotdeals.config.swagger;

import com.halildurmus.hotdeals.security.role.IsSuper;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Component
public class OperationCustomizer implements org.springdoc.core.customizers.OperationCustomizer {

  @Override
  public io.swagger.v3.oas.models.Operation customize(io.swagger.v3.oas.models.Operation operation,
      HandlerMethod handlerMethod) {
    final StringBuilder sb = new StringBuilder();
    sb.append("<b>Access Privileges & Rules</b>: ");
    // Check authorization details
    final Optional<IsSuper> isSuperAnnotation = Optional.ofNullable(
        handlerMethod.getMethodAnnotation(IsSuper.class));
    final Optional<PreAuthorize> preAuthorizeAnnotation = Optional.ofNullable(
        handlerMethod.getMethodAnnotation(
            PreAuthorize.class));
    if (isSuperAnnotation.isPresent()) {
      final String preAuthorizeValue = isSuperAnnotation.get().annotationType()
          .getAnnotation(PreAuthorize.class).value();
      sb.append("<em>")
          .append(preAuthorizeValue)
          .append("</em>");
    } else if (preAuthorizeAnnotation.isPresent()) {
      sb.append("<em>").append(preAuthorizeAnnotation.get().value()).append("</em>");
    } else {
      sb.append("<em>NOT_FOUND</em>");
    }
    // Check notes
    if (!ObjectUtils.isEmpty(operation.getDescription())) {
      sb.append("<br /><br />");
      sb.append(operation.getDescription());
    }
    // Add the note text to the Swagger UI
    operation.description(sb.toString());

    return operation;
  }

}
