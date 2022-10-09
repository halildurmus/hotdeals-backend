package com.halildurmus.hotdeals.comment;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

  @Autowired private CommentRepository repository;

  @Override
  public Optional<Comment> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public Page<Comment> getCommentsByDealId(ObjectId dealId, Pageable pageable) {
    return repository.findByDealIdOrderByCreatedAt(dealId, pageable);
  }

  @Override
  public int getCommentCountByDealId(ObjectId dealId) {
    return repository.countCommentsByDealId(dealId);
  }

  @Override
  public int getCommentCountByPostedById(ObjectId postedById) {
    return repository.countCommentsByPostedById(postedById);
  }

  @Override
  public void deleteDealComments(String dealId) {
    var comments =
        repository.findByDealIdOrderByCreatedAt(new ObjectId(dealId), Pageable.unpaged());
    var commentIds =
        comments.getContent().stream().map(Comment::getId).collect(Collectors.toList());
    repository.deleteAllByIdIn(commentIds);
  }

  @Override
  public Comment save(Comment comment) {
    return repository.save(comment);
  }
}
