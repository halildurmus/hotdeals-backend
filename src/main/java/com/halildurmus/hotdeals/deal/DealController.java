package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
@Validated
public class DealController {

  @Autowired
  private DealService service;

  @Autowired
  private EsDealService esDealService;

  @GetMapping("/deals/{id}")
  public ResponseEntity<Object> getDeal(@ObjectIdConstraint @PathVariable String id) {
    final Optional<Deal> deal = service.findById(id);
    if (deal.isEmpty()) {
      return ResponseEntity.status(404).build();
    }

    return ResponseEntity.ok(deal);
  }

  @GetMapping("/deals/elastic-search")
  public ResponseEntity<List<SearchHit<EsDeal>>> searchDeals(
      @NotBlank @RequestParam(value = "keyword", defaultValue = "") String keyword,
      Pageable pageable) {
    final List<SearchHit<EsDeal>> searchHits = esDealService.queryDeals(keyword, pageable);

    return ResponseEntity.ok(searchHits);
  }

  @PostMapping("/deals")
  public ResponseEntity<Deal> saveOrUpdateDeal(@Valid @RequestBody Deal deal) {
    final Deal createdDeal = service.saveOrUpdateDeal(deal);

    return ResponseEntity.status(201).body(createdDeal);
  }

  @DeleteMapping("/deals/{id}")
  public ResponseEntity<Object> removeDeal(@ObjectIdConstraint @PathVariable String id) {
    try {
      service.removeDeal(id);

      return ResponseEntity.status(204).build();
    } catch (Exception e) {
      return ResponseEntity.status(403).build();
    }
  }

  @PutMapping("/deals/{id}/favorite")
  public ResponseEntity<?> favorite(@ObjectIdConstraint @PathVariable String id) throws Exception {
    service.favorite(id);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/deals/{id}/unfavorite")
  public ResponseEntity<?> unfavorite(@ObjectIdConstraint @PathVariable String id)
      throws Exception {
    service.unfavorite(id);

    return ResponseEntity.status(204).build();
  }

  @PostMapping("/deals/{id}/upvote")
  public ResponseEntity<Deal> upvote(@ObjectIdConstraint @PathVariable String id) throws Exception {
    final Deal deal = service.upvote(id);

    return ResponseEntity.ok().body(deal);
  }

  @PostMapping("/deals/{id}/downvote")
  public ResponseEntity<Deal> downvote(@ObjectIdConstraint @PathVariable String id)
      throws Exception {
    final Deal deal = service.downvote(id);

    return ResponseEntity.ok().body(deal);
  }

}
