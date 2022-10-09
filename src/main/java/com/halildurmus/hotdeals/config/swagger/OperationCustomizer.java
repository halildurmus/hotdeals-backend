package com.halildurmus.hotdeals.config.swagger;

import com.halildurmus.hotdeals.security.role.IsSuper;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Component
public class OperationCustomizer implements org.springdoc.core.customizers.OperationCustomizer {

  @Override
  public io.swagger.v3.oas.models.Operation customize(
      io.swagger.v3.oas.models.Operation operation, HandlerMethod handlerMethod) {
    var sb = new StringBuilder();
    // Check if the operation requires specific authorization
    var isSuperAnnotation = Optional.ofNullable(handlerMethod.getMethodAnnotation(IsSuper.class));
    isSuperAnnotation.ifPresent(
        isSuper ->
            sb.append("<b>Access Privileges</b>: ")
                .append("Available for users with <em><b>ROLE_SUPER</b></em> authority.")
                .append("<br /><br />"));
    if (!ObjectUtils.isEmpty(operation.getDescription())) {
      sb.append(operation.getDescription());
    }
    if (sb.length() > 0) {
      // Add the description text to the Swagger UI
      operation.description(sb.toString());
    }

    return operation;
  }
}
