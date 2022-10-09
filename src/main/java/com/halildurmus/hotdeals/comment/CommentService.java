package com.halildurmus.hotdeals.comment;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  Optional<Comment> findById(String id);

  Page<Comment> getCommentsByDealId(ObjectId dealId, Pageable pageable);

  int getCommentCountByDealId(ObjectId dealId);

  int getCommentCountByPostedById(ObjectId postedById);

  void deleteDealComments(String dealId);

  Comment save(Comment comment);
}
