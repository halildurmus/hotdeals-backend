package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Order(1)
@Component
class DealEntityCallbacks implements BeforeConvertCallback<Deal>, BeforeSaveCallback<Deal> {

  @Autowired
  private SecurityService securityService;

  @Override
  public Deal onBeforeSave(Deal deal, Document document, String collection) {
    if (collection.equals("deals")) {
      final User user = securityService.getUser();
      final ObjectId userId = new ObjectId(user.getId());
      deal.setPostedBy(userId);
      document.put("postedBy", userId);
    }

    return deal;
  }

  @Override
  public Deal onBeforeConvert(Deal deal, String collection) {
    if (collection.equals("deals")) {
      final int upVoteCount = deal.getUpVoters().size();
      final int downVoteCount = deal.getDownVoters().size();
      deal.setDealScore(upVoteCount - downVoteCount);
    }

    return deal;
  }

}