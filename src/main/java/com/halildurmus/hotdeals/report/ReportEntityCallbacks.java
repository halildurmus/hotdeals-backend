package com.halildurmus.hotdeals.report;

import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Order(1)
@Component
class ReportEntityCallbacks implements BeforeSaveCallback<Report> {

  @Autowired
  private SecurityService securityService;

  @Override
  public Report onBeforeSave(Report report, Document document, String collection) {
    if (collection.equals("reports") && ObjectUtils.isEmpty(report.getReportedBy())) {
      final User user = securityService.getUser();
      final ObjectId userId = new ObjectId(user.getId());
      report.setReportedBy(userId);
      document.put("postedBy", userId);
    }

    return report;
  }

}