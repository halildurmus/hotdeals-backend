package com.halildurmus.hotdeals.deal;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class DealController {

  @Autowired
  private DealService service;

  @PostMapping("/deals/vote")
  public ResponseEntity<Deal> vote(@RequestBody Map<String, String> json)
      throws Exception {
    final String dealId = json.get("dealId");
    final String userId = json.get("userId");
    final String voteType = json.get("voteType");

    if (dealId == null || userId == null || voteType == null) {
      throw new Exception("dealId, userId and voteType fields cannot be blank!");
    }

    Deal response = service.vote(dealId, userId, voteType);

    return ResponseEntity.status(200).body(response);
  }

}
