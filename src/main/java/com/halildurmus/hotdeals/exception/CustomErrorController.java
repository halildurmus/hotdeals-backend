package com.halildurmus.hotdeals.exception;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/error")
public class CustomErrorController extends AbstractErrorController {

  public CustomErrorController(final ErrorAttributes errorAttributes) {
    super(errorAttributes, Collections.emptyList());
  }

  @RequestMapping
  public ResponseEntity<Object> error(HttpServletRequest request) {
    var status = this.getStatus(request);
    Map<String, Object> body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
    return ResponseEntity.status(status).body(body);
  }
}
