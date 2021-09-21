package com.halildurmus.hotdeals.report.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RepositoryRestController
public class UserReportController {

  @Autowired
  private UserReportService service;

  @GetMapping("/user-reports")
  public ResponseEntity<Page<UserReport>> getUserReports(Pageable pageable) {
    final Page<UserReport> response = service.findAll(pageable);

    return ResponseEntity.ok(response);
  }

}