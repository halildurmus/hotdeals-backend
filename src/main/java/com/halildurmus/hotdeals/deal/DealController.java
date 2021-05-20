package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import io.netty.handler.codec.http.HttpScheme;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class DealController {

  @Autowired
  private DealService service;

  @Autowired
  private SecurityService securityService;

  @PostMapping("/deals/{dealId}/incrementViewsCounter")
  public ResponseEntity<Object> vote(@PathVariable String dealId)
      throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final Deal deal = service.incrementViewsCounter(dealId);
    if (deal == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(200).body(deal);
  }

  @PostMapping("/deals/vote")
  public ResponseEntity<Object> vote(@RequestBody Map<String, String> json)
      throws Exception {
    final String dealId = json.get("dealId");
    final String voteType = json.get("voteType");
    final ObjectId userId = new ObjectId(securityService.getUser().getId());

    if (ObjectUtils.isEmpty(dealId) || ObjectUtils.isEmpty(voteType)) {
      throw new IllegalArgumentException("dealId and voteType fields cannot be blank!");
    }

    Deal deal = service.vote(dealId, userId, voteType);
    if (deal == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(200).body(deal);
  }

}
