package com.halildurmus.hotdeals.report.user;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class UserReportController {

  @Autowired
  private UserReportService service;

  @PostMapping("/user-reports")
  public ResponseEntity<UserReport> createUserReport(@Valid @RequestBody UserReport userReport) {
    return ResponseEntity.status(201).body(service.saveUserReport(userReport));
  }

}