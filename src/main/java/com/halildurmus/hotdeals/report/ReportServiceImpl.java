package com.halildurmus.hotdeals.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

  @Autowired
  private ReportRepository repository;

}
