package com.halildurmus.hotdeals.report.deal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealReportService {

  Page<DealReport> findAll(Pageable pageable);


}
