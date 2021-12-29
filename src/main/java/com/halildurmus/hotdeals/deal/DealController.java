package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.comment.DTO.CommentGetDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentsDTO;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.util.EnumUtil;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  private final List<String> orderTypes = List.of("asc", "desc");

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private CommentService commentService;

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
  public ResponseEntity<JsonNode> searchDeals(
      @RequestParam(value = "query") String query,
      @RequestParam(value = "categories", required = false) List<String> categories,
      @RequestParam(value = "prices", required = false) List<String> prices,
      @RequestParam(value = "stores", required = false) List<String> stores,
      @RequestParam(value = "hideExpired", required = false, defaultValue = "false") Boolean hideExpired,
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

    if (order != null && !orderTypes.contains(order)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid order! Supported order values => " + orderTypes);
    }

    final DealSearchParams searchParams = DealSearchParams.builder()
        .query(query)
        .categories(categories)
        .prices(priceRanges)
        .stores(stores)
        .hideExpired(hideExpired)
        .sortBy(sortBy)
        .order(order)
        .build();
    // If all search params except 'order' are null then return HTTP 400
    if (searchParams.equals(DealSearchParams.builder().hideExpired(false).order("asc").build())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "You have to provide at least one parameter!");
    }

    return ResponseEntity.ok(esDealService.searchDeals(searchParams, pageable));
  }

  @GetMapping("/deals/suggestions")
  public ResponseEntity<JsonNode> getSuggestions(@NotBlank @Size(min = 3, max = 100)
  @RequestParam(value = "query") String query) {
    return ResponseEntity.ok(esDealService.getSuggestions(query));
  }

  @GetMapping("/deals/{id}")
  public ResponseEntity<Deal> getDeal(@ObjectIdConstraint @PathVariable String id) {
    return ResponseEntity.of(service.findById(id));
  }

  @PostMapping("/deals")
  public ResponseEntity<Deal> createDeal(@Valid @RequestBody Deal deal) {
    return ResponseEntity.status(201).body(service.saveDeal(deal));
  }

  @PatchMapping(value = "/deals/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Deal> patchDeal(@ObjectIdConstraint @PathVariable String id,
      @RequestBody JsonPatch patch) {
    return ResponseEntity.ok(service.patchDeal(id, patch));
  }

  @PutMapping("/deals")
  public ResponseEntity<Deal> updateDeal(@Valid @RequestBody Deal deal) {
    return ResponseEntity.status(200).body(service.updateDeal(deal));
  }

  @DeleteMapping("/deals/{id}")
  public ResponseEntity<Void> removeDeal(@ObjectIdConstraint @PathVariable String id) {
    service.removeDeal(id);

    return ResponseEntity.status(204).build();
  }

  @GetMapping("/deals/{id}/comments")
  public ResponseEntity<CommentsDTO> getComments(
      @ObjectIdConstraint @PathVariable String id, Pageable pageable) {
    final Page<Comment> comments = commentService.getCommentsByDealId(new ObjectId(id),
        pageable);
    final List<CommentGetDTO> commentGetDTOS = comments.getContent().stream()
        .map(comment -> mapStructMapper.commentToCommentGetDto(comment)).collect(
            Collectors.toList());
    final CommentsDTO commentsDTO = CommentsDTO.builder()
        .count(comments.getTotalElements())
        .comments(commentGetDTOS)
        .build();

    return ResponseEntity.ok(commentsDTO);
  }

  @GetMapping("/deals/{id}/comment-count")
  public ResponseEntity<Integer> getCommentCount(@ObjectIdConstraint @PathVariable String id) {
    return ResponseEntity.ok(commentService.getCommentCountByDealId(new ObjectId(id)));
  }


  @PostMapping("/deals/{id}/comments")
  public ResponseEntity<CommentGetDTO> postComment(
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody CommentPostDTO commentPostDTO) {
    service.findById(id).orElseThrow(DealNotFoundException::new);
    commentPostDTO.setDealId(new ObjectId(id));
    final Comment comment = commentService.save(
        mapStructMapper.commentPostDtoToComment(commentPostDTO));
    final CommentGetDTO commentGetDTO = mapStructMapper.commentToCommentGetDto(comment);

    return ResponseEntity.status(201).body(commentGetDTO);
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

    return ResponseEntity.ok().body(service.voteDeal(id, voteType));
  }

  @DeleteMapping("/deals/{id}/votes")
  public ResponseEntity<Deal> deleteVote(@ObjectIdConstraint @PathVariable String id) {
    return ResponseEntity.ok().body(service.voteDeal(id, DealVoteType.UNVOTE));
  }

}
