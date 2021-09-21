package com.halildurmus.hotdeals.report.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserReportService {

  Page<UserReport> findAll(Pageable pageable);

}
