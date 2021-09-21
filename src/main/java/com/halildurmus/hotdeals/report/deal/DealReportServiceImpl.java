package com.halildurmus.hotdeals.report.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DealReportServiceImpl implements DealReportService {

  @Autowired
  private DealReportRepository repository;

  @Override
  public Page<DealReport> findAll(Pageable pageable) {
    return repository.findAllByReportedDealNotNull(pageable);
  }

}
