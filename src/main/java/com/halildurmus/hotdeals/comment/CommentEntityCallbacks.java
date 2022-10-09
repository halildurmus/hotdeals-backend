package com.halildurmus.hotdeals.comment;

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
class CommentEntityCallbacks implements BeforeSaveCallback<Comment> {

  @Autowired private SecurityService securityService;

  @Override
  public Comment onBeforeSave(Comment comment, Document document, String collection) {
    if (collection.equals("comments") && ObjectUtils.isEmpty(comment.getPostedBy())) {
      var user = securityService.getUser();
      comment.setPostedBy(user);
      // Since we're using @DocumentReference on the postedBy property, we need to
      // add the user's ObjectId instead of the User object to the document
      document.put("postedBy", new ObjectId(user.getId()));
    }
    return comment;
  }
}
