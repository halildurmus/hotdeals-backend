package com.halildurmus.hotdeals.report.user;

import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserReportServiceImpl implements UserReportService {

  @Autowired
  private UserReportRepository repository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserReport saveUserReport(UserReport userReport) {
    userRepository.findById(userReport.getReportedUser().toString()).orElseThrow(
        UserNotFoundException::new);

    return repository.save(userReport);
  }
}
