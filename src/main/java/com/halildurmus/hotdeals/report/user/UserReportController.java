package com.halildurmus.hotdeals.report.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public class UserReportController {

  @Autowired
  private UserReportService service;

}