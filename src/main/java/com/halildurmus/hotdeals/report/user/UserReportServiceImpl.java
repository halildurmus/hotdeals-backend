package com.halildurmus.hotdeals.report.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserReportServiceImpl implements UserReportService {

  @Autowired private UserReportRepository repository;

  @Override
  public UserReport save(UserReport userReport) {
    return repository.save(userReport);
  }
}
