package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealService;
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

  @GetMapping("/deals/elastic-search")
  public ResponseEntity<List<SearchHit<EsDeal>>> searchDeals(
      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
      Pageable pageable) {
    if (ObjectUtils.isEmpty(keyword)) {
      return ResponseEntity.status(200).body(Collections.emptyList());
    }

    final List<SearchHit<EsDeal>> response = esDealService.queryDeals(keyword, pageable);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/deals")
  public ResponseEntity<Deal> saveOrUpdateDeal(@RequestBody Deal deal) {
    final Deal response = service.saveOrUpdateDeal(deal);

    return ResponseEntity.status(201).body(response);
  }

  @DeleteMapping("/deals/{dealId}")
  public ResponseEntity<Object> removeDeal(@PathVariable String dealId) {
    try {
      service.removeDeal(dealId);

      return ResponseEntity.ok(HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(403).body(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/deals/vote")
  public ResponseEntity<Deal> vote(@RequestBody Map<String, String> json) throws Exception {
    final String dealId = json.get("dealId");
    final String voteType = json.get("voteType");
    if (ObjectUtils.isEmpty(dealId) || ObjectUtils.isEmpty(voteType)) {
      throw new IllegalArgumentException("dealId and voteType fields cannot be blank!");
    }

    if (!voteType.equals("upvote") && !voteType.equals("downvote")) {
      throw new IllegalArgumentException("Invalid vote type! Valid vote types: {upvote, downvote}");
    }

    final Deal response = service.vote(dealId, voteType);

    return ResponseEntity.ok(response);
  }

}
