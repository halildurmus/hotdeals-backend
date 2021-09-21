package com.halildurmus.hotdeals.report.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RepositoryRestController
public class DealReportController {

  @Autowired
  private DealReportService service;

  @GetMapping("/deal-reports")
  public ResponseEntity<Page<DealReport>> getDealReports(Pageable pageable) {
    final Page<DealReport> response = service.findAll(pageable);

    return ResponseEntity.ok(response);
  }

}
