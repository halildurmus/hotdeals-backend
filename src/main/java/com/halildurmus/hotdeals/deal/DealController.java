package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.security.SecurityService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
public class DealController {

  @Autowired
  private DealService service;

  @Autowired
  private EsDealService esDealService;

  @Autowired
  private SecurityService securityService;

  @GetMapping("/deals/elastic-search")
  public ResponseEntity<Object> searchDeals(
      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
      Pageable pageable) {
    if (ObjectUtils.isEmpty(keyword)) {
      return ResponseEntity.status(200).body(Collections.emptyList());
    }

    final List<SearchHit<EsDeal>> response = esDealService.queryDeals(keyword, pageable);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/deals")
  public ResponseEntity<Object> saveOrUpdateDeal(@RequestBody Deal deal) {
    final Deal response = service.saveOrUpdateDeal(deal);

    return ResponseEntity.status(200).body(response);
  }

  @DeleteMapping("/deals/{dealId}")
  public ResponseEntity<Object> removeDeal(@PathVariable String dealId) {
    service.removeDeal(dealId);

    return ResponseEntity.status(200).body(HttpStatus.OK);
  }

  @PostMapping("/deals/{dealId}/incrementViewsCounter")
  public ResponseEntity<Object> vote(@PathVariable String dealId)
      throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final Deal response = service.incrementViewsCounter(dealId);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/deals/vote")
  public ResponseEntity<Object> vote(@RequestBody Map<String, String> json)
      throws Exception {
    final String dealId = json.get("dealId");
    final String voteType = json.get("voteType");
    if (!voteType.equals("upVote") && !voteType.equals("downVote")) {
      throw new IllegalArgumentException("Invalid vote type! Valid vote types: {upVote, downVote}");
    }

    final ObjectId userId = new ObjectId(securityService.getUser().getId());
    if (ObjectUtils.isEmpty(dealId) || ObjectUtils.isEmpty(voteType)) {
      throw new IllegalArgumentException("dealId and voteType fields cannot be blank!");
    }

    final Deal response = service.vote(dealId, userId, voteType);

    return ResponseEntity.status(200).body(response);
  }

}
