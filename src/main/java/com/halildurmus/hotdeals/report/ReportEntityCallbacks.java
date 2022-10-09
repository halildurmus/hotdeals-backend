package com.halildurmus.hotdeals.report;

import com.halildurmus.hotdeals.security.SecurityService;
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

  @Autowired private SecurityService securityService;

  @Override
  public Report onBeforeSave(Report report, Document document, String collection) {
    if (collection.equals("reports") && ObjectUtils.isEmpty(report.getReportedBy())) {
      var user = securityService.getUser();
      report.setReportedBy(user);
      // Since we're using @DocumentReference on the reportedBy property, we need to
      // add the user's ObjectId instead of the User object to the document
      document.put("reportedBy", new ObjectId(user.getId()));
    }
    return report;
  }
}
