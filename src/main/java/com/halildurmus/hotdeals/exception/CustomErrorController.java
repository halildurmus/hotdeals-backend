package com.halildurmus.hotdeals.exception;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class CustomErrorController extends AbstractErrorController {

  public CustomErrorController(final ErrorAttributes errorAttributes) {
    super(errorAttributes, Collections.emptyList());
  }

  @RequestMapping
  public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
    Map<String, Object> body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
    HttpStatus status = this.getStatus(request);

    return new ResponseEntity<>(body, status);
  }

}