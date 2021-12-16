package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.util.EnumUtil;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@RepositoryRestController
@Validated
public class DealController {

  private final List<String> supportedSortBys = List.of("createdAt", "price");
  private final List<String> supportedOrders = List.of("asc", "desc");

  @Autowired
  private DealService service;

  @Autowired
  private EsDealService esDealService;

  private List<PriceRange> parsePricesParam(List<String> prices) {
    final List<PriceRange> priceRanges = new ArrayList<>();
    try {
      for (String p : prices) {
        final String[] arr = p.split(":");
        final double from = Double.parseDouble(arr[0]);
        Double to = null;
        if (!arr[1].equals("*")) {
          to = Double.parseDouble(arr[1]);
        }
        if (to != null && (from < 0 || to < 0 || from > to)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prices!");
        }
        priceRanges.add(new PriceRange(from, to));
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prices!");
    }

    return priceRanges;
  }

  @GetMapping("/deals/searches")
  public ResponseEntity<?> searchDeals(
      @RequestParam(value = "query") String query,
      @RequestParam(value = "categories", required = false) List<String> categories,
      @RequestParam(value = "prices", required = false) List<String> prices,
      @RequestParam(value = "stores", required = false) List<String> stores,
      @RequestParam(value = "sortBy", required = false) String sortBy,
      @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
      Pageable pageable) {

    List<PriceRange> priceRanges = null;
    if (prices != null) {
      priceRanges = parsePricesParam(prices);
    }

    if (sortBy != null && !supportedSortBys.contains(sortBy)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid sortBy! Supported sortBy values => " + supportedSortBys);
    }

    if (order != null && !supportedOrders.contains(order)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid order! Supported order values => " + supportedOrders);
    }

    final DealSearchParams searchParams = DealSearchParams.builder()
        .query(query)
        .categories(categories)
        .prices(priceRanges)
        .stores(stores)
        .sortBy(sortBy)
        .order(order)
        .build();
    // If all search params except 'order' are null then return HTTP 400
    if (searchParams.equals(DealSearchParams.builder().order("asc").build())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "You have to provide at least one parameter!");
    }

    final JsonNode searchResults = esDealService.searchDeals(searchParams, pageable);

    return ResponseEntity.ok(searchResults);
  }

  @GetMapping("/deals/suggestions")
  public ResponseEntity<JsonNode> getSuggestions(@NotBlank @Size(min = 3, max = 100)
  @RequestParam(value = "query") String query) {
    final JsonNode searchHits = esDealService.getSuggestions(query);

    return ResponseEntity.ok(searchHits);
  }

  @GetMapping("/deals/{id}")
  public ResponseEntity<Object> getDeal(@ObjectIdConstraint @PathVariable String id) {
    final Optional<Deal> deal = service.findById(id);
    if (deal.isEmpty()) {
      return ResponseEntity.status(404).build();
    }

    return ResponseEntity.ok(deal);
  }

  @PostMapping("/deals")
  public ResponseEntity<Deal> createDeal(@Valid @RequestBody Deal deal) {
    final Deal createdDeal = service.saveDeal(deal);

    return ResponseEntity.status(201).body(createdDeal);
  }

  @PatchMapping(value = "/deals/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Object> patchDeal(@ObjectIdConstraint @PathVariable String id,
      @RequestBody JsonPatch patch) {
    final Deal patchedDeal = service.patchDeal(id, patch);

    return ResponseEntity.ok(patchedDeal);
  }

  @PutMapping("/deals")
  public ResponseEntity<Deal> updateDeal(@Valid @RequestBody Deal deal) {
    final Deal updatedDeal = service.updateDeal(deal);

    return ResponseEntity.status(200).body(updatedDeal);
  }

  @DeleteMapping("/deals/{id}")
  public ResponseEntity<?> removeDeal(@ObjectIdConstraint @PathVariable String id) {
    service.removeDeal(id);

    return ResponseEntity.status(204).build();
  }

  @PutMapping("/deals/{id}/votes")
  public ResponseEntity<Deal> voteDeal(@ObjectIdConstraint @PathVariable String id,
      @Valid @NotNull @RequestBody Map<String, String> json) {
    if (!json.containsKey("voteType")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "You need to include 'voteType' inside the request body!");
    } else if (!EnumUtil.isInEnum(json.get("voteType"), DealVoteType.class)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid voteType! Supported voteTypes => " + Arrays.toString(DealVoteType.values()));
    } else if (json.get("voteType").equals("UNVOTE")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "To unvote the deal you need to make a DELETE request!");
    }
    final DealVoteType voteType = DealVoteType.valueOf(json.get("voteType"));
    final Deal deal = service.voteDeal(id, voteType);

    return ResponseEntity.ok().body(deal);
  }

  @DeleteMapping("/deals/{id}/votes")
  public ResponseEntity<Deal> removeVote(@ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.voteDeal(id, DealVoteType.UNVOTE);

    return ResponseEntity.ok().body(deal);
  }

}
