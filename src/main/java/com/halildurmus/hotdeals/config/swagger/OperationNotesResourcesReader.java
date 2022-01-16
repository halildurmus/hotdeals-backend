package com.halildurmus.hotdeals.config.swagger;

import com.halildurmus.hotdeals.security.role.IsSuper;
import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Slf4j
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1)
public class OperationNotesResourcesReader implements OperationBuilderPlugin {

  private final DescriptionResolver descriptions;

  @Autowired
  public OperationNotesResourcesReader(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(OperationContext context) {
    try {
      final StringBuilder sb = new StringBuilder();
      sb.append("<b>Access Privileges & Rules</b>: ");
      // Check authorization details
      final Optional<IsSuper> isSuperAnnotation = context.findAnnotation(IsSuper.class);
      final Optional<PreAuthorize> preAuthorizeAnnotation = context.findAnnotation(
          PreAuthorize.class);
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
      final Optional<ApiOperation> annotation = context.findAnnotation(ApiOperation.class);
      if (annotation.isPresent() && StringUtils.hasText(annotation.get().notes())) {
        sb.append("<br /><br />");
        sb.append(annotation.get().notes());
      }
      // Add the note text to the Swagger UI
      context.operationBuilder().notes(descriptions.resolve(sb.toString()));
    } catch (Exception e) {
      log.error("Error when creating swagger documentation for security roles: ", e);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

}
