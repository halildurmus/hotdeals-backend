package com.halildurmus.hotdeals.deal;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.util.EnumUtil;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
  public ResponseEntity<Deal> createDeal(@Valid @RequestBody Deal deal) {
    final Deal createdDeal = service.saveDeal(deal);

    return ResponseEntity.status(201).body(createdDeal);
  }

  @PutMapping("/deals")
  public ResponseEntity<Deal> updateDeal(@Valid @RequestBody Deal deal) {
    final Deal updatedDeal = service.saveDeal(deal);

    return ResponseEntity.status(200).body(updatedDeal);
  }

  @DeleteMapping("/deals/{id}")
  public ResponseEntity<?> removeDeal(@ObjectIdConstraint @PathVariable String id) {
    service.removeDeal(id);

    return ResponseEntity.status(204).build();
  }

  @PutMapping("/deals/{id}/votes")
  public ResponseEntity<Deal> voteDeal(
      @ObjectIdConstraint @PathVariable String id,
      @Valid @NotNull @RequestBody Map<String, String> json) throws Exception {
    if (!json.containsKey("voteType")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "You need to include 'voteType' inside the request body!");
    } else if (!EnumUtil.isInEnum(json.get("voteType"), DealVoteType.class)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid voteType! Allowed voteTypes => " + Arrays.toString(DealVoteType.values()));
    } else if (json.get("voteType").equals("UNVOTE")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "To unvote the deal you need to make a DELETE request!");
    }

    final DealVoteType voteType = DealVoteType.valueOf(json.get("voteType"));
    final Deal deal = service.voteDeal(id, voteType);

    return ResponseEntity.ok().body(deal);
  }

  @DeleteMapping("/deals/{id}/votes")
  public ResponseEntity<Deal> removeVote(
      @ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.voteDeal(id, DealVoteType.UNVOTE);

    return ResponseEntity.ok().body(deal);
  }

}
