package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.rest.webmvc.RepositoryRestController;
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

  @GetMapping("/deals/{id}")
  public ResponseEntity<Object> getDeal(@PathVariable String id) {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    final Optional<Deal> response = service.findById(id);
    if (response.isEmpty()) {
      return ResponseEntity.status(404).build();
    }

    return ResponseEntity.ok(response);
  }

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

  @DeleteMapping("/deals/{id}")
  public ResponseEntity<Object> removeDeal(@PathVariable String id) {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    try {
      service.removeDeal(id);

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(403).build();
    }
  }

  @PostMapping("/deals/{id}/favorite")
  public ResponseEntity<?> favorite(@PathVariable String id) throws Exception {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    service.favorite(id);

    return ResponseEntity.status(201).build();
  }

  @PostMapping("/deals/{id}/unfavorite")
  public ResponseEntity<?> unfavorite(@PathVariable String id) throws Exception {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    service.unfavorite(id);

    return ResponseEntity.status(201).build();
  }

  @PostMapping("/deals/{id}/upvote")
  public ResponseEntity<Deal> upvote(@PathVariable String id) throws Exception {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    final Deal response = service.upvote(id);

    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/deals/{id}/downvote")
  public ResponseEntity<Deal> downvote(@PathVariable String id) throws Exception {
    if (!ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Invalid deal id!");
    }

    final Deal response = service.downvote(id);

    return ResponseEntity.ok().body(response);
  }

}
