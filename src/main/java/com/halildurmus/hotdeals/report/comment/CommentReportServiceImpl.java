package com.halildurmus.hotdeals.report.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentReportServiceImpl implements CommentReportService {

  @Autowired
  private CommentReportRepository repository;

  @Override
  public CommentReport save(CommentReport commentReport) {
    return repository.save(commentReport);
  }

}
