package com.halildurmus.hotdeals.comment;

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
class CommentEntityCallbacks implements BeforeSaveCallback<Comment> {

  @Autowired
  private SecurityService securityService;

  @Override
  public Comment onBeforeSave(Comment comment, Document document, String collection) {
    if (collection.equals("comments") && ObjectUtils.isEmpty(comment.getPostedBy())) {
      final User user = securityService.getUser();
      final ObjectId userId = new ObjectId(user.getId());
      comment.setPostedBy(userId);
      document.put("postedBy", userId);
    }

    return comment;
  }

}