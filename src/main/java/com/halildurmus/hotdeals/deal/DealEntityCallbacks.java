package com.halildurmus.hotdeals.deal;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

@Order(1)
@Component
class DealEntityCallbacks implements BeforeConvertCallback<Deal>, BeforeSaveCallback<Deal> {

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