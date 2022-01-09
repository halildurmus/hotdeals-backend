package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.comment.DTO.CommentGetDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentsDTO;
import com.halildurmus.hotdeals.deal.DTO.DealGetDTO;
import com.halildurmus.hotdeals.deal.DTO.DealPostDTO;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.report.deal.DTO.DealReportPostDTO;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.deal.DealReportService;
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
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/deals")
@Validated
public class DealController {

  private final List<String> supportedSortBys = List.of("createdAt", "price");
  private final List<String> orderTypes = List.of("asc", "desc");

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private CommentService commentService;

  @Autowired
  private DealReportService dealReportService;

  @Autowired
  private DealService service;

  @Autowired
  private EsDealService esDealService;

  @GetMapping("/count/byPostedBy")
  public int getCountDealsByPostedBy(@ObjectIdConstraint @RequestParam String postedBy) {
    return service.countDealsByPostedBy(new ObjectId(postedBy));
  }

  @GetMapping("/count/byStore")
  public int getCountDealsByStore(@ObjectIdConstraint @RequestParam String storeId) {
    return service.countDealsByStore(new ObjectId(storeId));
  }

  @GetMapping("/search/byCategory")
  public List<DealGetDTO> getDealsByCategory(@RequestParam String category, Pageable pageable) {
    final Page<Deal> deals = service.getDealsByCategory(category, pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/byStoreId")
  public List<DealGetDTO> getDealsByStoreId(@ObjectIdConstraint @RequestParam String storeId,
      Pageable pageable) {
    final Page<Deal> deals = service.getDealsByStoreId(new ObjectId(storeId), pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/latestActive")
  public List<DealGetDTO> getLatestActiveDeals(Pageable pageable) {
    final Page<Deal> deals = service.getLatestActiveDeals(pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/mostLikedActive")
  public List<DealGetDTO> getMostLikedActiveDeals(Pageable pageable) {
    final Page<Deal> deals = service.getMostLikedActiveDeals(pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }


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

  @GetMapping("/searches")
  public JsonNode searchDeals(
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

    return esDealService.searchDeals(searchParams, pageable);
  }

  @GetMapping("/suggestions")
  public JsonNode getSuggestions(@NotBlank @Size(min = 3, max = 100)
  @RequestParam(value = "query") String query) {
    return esDealService.getSuggestions(query);
  }

  @GetMapping("/{id}")
  public DealGetDTO getDeal(@ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.findById(id).orElseThrow(DealNotFoundException::new);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DealGetDTO createDeal(@Valid @RequestBody DealPostDTO dealPostDTO) {
    final Deal deal = service.create(mapStructMapper.dealPostDTOToDeal(dealPostDTO));

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
  public DealGetDTO patchDeal(@ObjectIdConstraint @PathVariable String id,
      @RequestBody JsonPatch patch) {
    return mapStructMapper.dealToDealGetDTO(service.patch(id, patch));
  }

  @PutMapping("/{id}")
  public DealGetDTO updateDeal(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody DealPostDTO dealPostDTO) {
    final Deal deal = mapStructMapper.dealPostDTOToDeal(dealPostDTO);
    deal.setId(id);

    return mapStructMapper.dealToDealGetDTO(service.update(deal));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDeal(@ObjectIdConstraint @PathVariable String id) {
    service.delete(id);
  }

  @GetMapping("/{id}/comments")
  public CommentsDTO getComments(@ObjectIdConstraint @PathVariable String id, Pageable pageable) {
    final Page<Comment> comments = commentService.getCommentsByDealId(new ObjectId(id),
        pageable);
    final List<CommentGetDTO> commentGetDTOs = comments.getContent().stream()
        .map(mapStructMapper::commentToCommentGetDTO).collect(Collectors.toList());

    return CommentsDTO.builder().count(comments.getTotalElements())
        .comments(commentGetDTOs).build();
  }

  @GetMapping("/{id}/comment-count")
  public int getCommentCount(@ObjectIdConstraint @PathVariable String id) {
    return commentService.getCommentCountByDealId(new ObjectId(id));
  }


  @PostMapping("/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentGetDTO postComment(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody CommentPostDTO commentPostDTO) {
    service.findById(id).orElseThrow(DealNotFoundException::new);
    commentPostDTO.setDealId(new ObjectId(id));
    final Comment comment = commentService.save(
        mapStructMapper.commentPostDTOToComment(commentPostDTO));

    return mapStructMapper.commentToCommentGetDTO(comment);
  }

  @PostMapping("/{id}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  public void createDealReport(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody DealReportPostDTO dealReportPostDTO) {
    final Deal deal = service.findById(id).orElseThrow(DealNotFoundException::new);
    final DealReport dealReport = mapStructMapper.dealReportPostDTOToDealReport(
        dealReportPostDTO);
    dealReport.setReportedDeal(deal);
    dealReportService.save(dealReport);
  }

  @PutMapping("/{id}/votes")
  public DealGetDTO voteDeal(@ObjectIdConstraint @PathVariable String id,
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
    final Deal deal = service.vote(id, voteType);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @DeleteMapping("/{id}/votes")
  public DealGetDTO deleteVote(@ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.vote(id, DealVoteType.UNVOTE);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

}
