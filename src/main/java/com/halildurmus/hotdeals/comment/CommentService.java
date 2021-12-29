package com.halildurmus.hotdeals.comment;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  int countCommentsByDealId(ObjectId dealId);

  int countCommentsByPostedById(ObjectId postedById);

  void deleteDealComments(String dealId);

  Page<Comment> getCommentsByDealId(ObjectId dealId, Pageable pageable);

  Comment saveComment(Comment comment);

}
