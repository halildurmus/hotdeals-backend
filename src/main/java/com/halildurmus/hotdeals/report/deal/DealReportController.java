package com.halildurmus.hotdeals.report.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public class DealReportController {

  @Autowired
  private DealReportService service;

}
