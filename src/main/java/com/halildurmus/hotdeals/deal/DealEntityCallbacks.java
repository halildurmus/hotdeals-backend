package com.halildurmus.hotdeals.deal;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Order(1)
@Component
class DealEntityCallbacks implements BeforeSaveCallback<Deal> {

  @Override
  public Deal onBeforeSave(Deal deal, Document document, String collection) {
    if (collection.equals("deals")) {
      // TODO: Replace this with the current authenticated user's id when user authentication
      //  is implemented.
      ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");
      deal.setPostedBy(objectId);
      document.put("postedBy", objectId);
    }

    return deal;
  }

}