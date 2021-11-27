package com.halildurmus.hotdeals.report.deal;

import com.halildurmus.hotdeals.deal.DealRepository;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DealReportServiceImpl implements DealReportService {

  @Autowired
  private DealReportRepository repository;

  @Autowired
  private DealRepository dealRepository;

  @Override
  public DealReport saveDealReport(DealReport dealReport) {
    dealRepository.findById(dealReport.getReportedDeal().toString()).orElseThrow(
        DealNotFoundException::new);

    return repository.save(dealReport);
  }
}
