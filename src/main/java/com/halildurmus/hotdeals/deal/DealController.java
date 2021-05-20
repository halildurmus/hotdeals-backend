package com.halildurmus.hotdeals.deal;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class DealController {

  @Autowired
  private DealService service;

  @PostMapping("/deals/vote")
  public Deal vote(@RequestBody Map<String, String> json)
      throws Exception {
    final String dealId = json.get("dealId");
    final String userId = json.get("userId");
    final String voteType = json.get("voteType");

    if (ObjectUtils.isEmpty(dealId) || ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(voteType)) {
      throw new Exception("dealId, userId and voteType fields cannot be blank!");
    }

    return service.vote(dealId, userId, voteType);
  }

}
