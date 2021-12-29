package com.halildurmus.hotdeals.report.deal;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class DealReportController {

  @Autowired
  private DealReportService service;

  @PostMapping("/deal-reports")
  public ResponseEntity<DealReport> createDealReport(@Valid @RequestBody DealReport dealReport) {
    return ResponseEntity.status(201).body(service.saveDealReport(dealReport));
  }

}
