package com.halildurmus.hotdeals.deal;

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
class DealEntityCallbacks implements BeforeSaveCallback<Deal> {

  @Autowired private SecurityService securityService;

  @Override
  public Deal onBeforeSave(Deal deal, Document document, String collection) {
    if (collection.equals("deals") && ObjectUtils.isEmpty(deal.getPostedBy())) {
      var user = securityService.getUser();
      var userId = new ObjectId(user.getId());
      deal.setPostedBy(userId);
      document.put("postedBy", userId);
    }
    return deal;
  }
}
