package com.halildurmus.hotdeals.comment;

import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import javax.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
@Validated
public class CommentController {

  @Autowired
  private CommentService service;

  @GetMapping("/comments/search/findByDealId")
  public ResponseEntity<List<CommentDTO>> searchDeals(
      @ObjectIdConstraint @RequestParam(value = "dealId", defaultValue = "") String dealId,
      Pageable pageable) {

    final List<CommentDTO> response = service.getCommentsByDealId(new ObjectId(dealId), pageable);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/comments")
  public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
    final Comment savedComment = service.saveComment(comment);

    return ResponseEntity.status(201).body(savedComment);
  }

}
