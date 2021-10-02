package com.halildurmus.hotdeals.comment;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
public class CommentController {

  @Autowired
  private CommentService service;

  @GetMapping("/comments/search/findByDealId")
  public ResponseEntity<List<CommentDTO>> searchDeals(
      @RequestParam(value = "dealId", defaultValue = "") String dealId,
      Pageable pageable) {
    if (ObjectUtils.isEmpty(dealId) || (!ObjectId.isValid(dealId))) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final List<CommentDTO> response = service.getCommentsByDealId(new ObjectId(dealId), pageable);

    return ResponseEntity.ok(response);
  }

}
