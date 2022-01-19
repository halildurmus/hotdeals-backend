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
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.bson.types.ObjectId;
import org.springdoc.api.annotations.ParameterObject;
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

@Tag(name = "deals")
@RestController
@RequestMapping("/deals")
@Validated
public class DealController {

  private static final List<String> SUPPORTED_SORT_BYS = List.of("createdAt", "price");
  private static final List<String> ORDER_TYPES = List.of("asc", "desc");

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

  @GetMapping
  @IsSuper
  @Operation(summary = "Returns all deals", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Deal.class)))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public List<Deal> getDeals(@ParameterObject Pageable pageable) {
    return service.findAll(pageable).getContent();
  }

  @GetMapping("/count/byPostedBy")
  @Operation(summary = "Returns the number of deals posted by a user")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))),
      @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content)
  })
  public int getCountDealsByPostedBy(
      @Parameter(description = "String representation of the User ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @RequestParam String postedBy) {
    return service.countDealsByPostedBy(new ObjectId(postedBy));
  }

  @GetMapping("/count/byStoreId")
  @Operation(summary = "Returns the number of deals a store has")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))),
      @ApiResponse(responseCode = "400", description = "Invalid store ID", content = @Content)
  })
  public int getCountDealsByStoreId(
      @Parameter(description = "String representation of the Store ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @RequestParam String storeId) {
    return service.countDealsByStore(new ObjectId(storeId));
  }

  @GetMapping("/search/byCategory")
  @Operation(summary = "Finds deals by category")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
  })
  public List<DealGetDTO> getDealsByCategory(
      @Parameter(description = "The category path", example = "/computers")
      @RequestParam String category,
      @ParameterObject Pageable pageable) {
    final Page<Deal> deals = service.getDealsByCategory(category, pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/byStoreId")
  @Operation(summary = "Finds deals by store ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))),
      @ApiResponse(responseCode = "400", description = "Invalid store ID", content = @Content)
  })
  public List<DealGetDTO> getDealsByStoreId(
      @Parameter(description = "String representation of the Store ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @RequestParam String storeId,
      @ParameterObject Pageable pageable) {
    final Page<Deal> deals = service.getDealsByStoreId(new ObjectId(storeId), pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/latestActive")
  @Operation(summary = "Returns deals sorted by post date")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))))
  public List<DealGetDTO> getLatestActiveDeals(@ParameterObject Pageable pageable) {
    final Page<Deal> deals = service.getLatestActiveDeals(pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search/mostLikedActive")
  @Operation(summary = "Returns deals sorted by deal score")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))))
  public List<DealGetDTO> getMostLikedActiveDeals(@ParameterObject Pageable pageable) {
    final Page<Deal> deals = service.getMostLikedActiveDeals(pageable);

    return deals.getContent().stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }


  private List<PriceRange> parsePricesParam(List<String> prices) {
    final List<PriceRange> priceRanges = new ArrayList<>();
    try {
      for (String price : prices) {
        final String[] arr = price.split(":");
        final double from = Double.parseDouble(arr[0]);
        Double to = null;
        if (!arr[1].equals("*")) {
          to = Double.parseDouble(arr[1]);
        }
        if (to != null && (from < 0 || to < 0 || from > to)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Invalid PriceRange: " + from + ":" + to);
        }
        priceRanges.add(new PriceRange(from, to));
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid prices parameter!");
    }

    return priceRanges;
  }

  @GetMapping("/searches")
  @Operation(summary = "Returns search results for given query and filters")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JsonNode.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
  })
  public JsonNode searchDeals(
      @Parameter(description = "Search query", example = "iphone")
      @RequestParam(value = "query") String query,
      @Parameter(description = "Category paths", example = "[\"/computers\", \"/electronics\"]")
      @RequestParam(value = "categories", required = false) List<String> categories,
      @Parameter(description = "Price ranges", examples = {
          @ExampleObject(name = "20:50", description = "Lists deals between $20 and $50"),
          @ExampleObject(name = "1500:*", description = "Lists deals with a price of at least $1500")})
      @RequestParam(value = "prices", required = false) List<String> prices,
      @Parameter(description = "Store IDs", example = "[\"5fbe790ec6f0b32014074bb2\", \"5fbe790ec6f0b32014074bb3\"]")
      @RequestParam(value = "stores", required = false) List<String> stores,
      @Parameter(description = "Whether to hide expired deals")
      @RequestParam(value = "hideExpired", required = false, defaultValue = "false") Boolean hideExpired,
      @Parameter(description = "Sort results by", examples = {
          @ExampleObject(name = "createdAt", description = "Sorts results by deal post date"),
          @ExampleObject(name = "price", description = "Sorts results by deal price")})
      @RequestParam(value = "sortBy", required = false) String sortBy,
      @Parameter(description = "Order results by")
      @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
      @ParameterObject Pageable pageable) {
    List<PriceRange> priceRanges = null;
    if (prices != null) {
      priceRanges = parsePricesParam(prices);
    }

    if (sortBy != null && !SUPPORTED_SORT_BYS.contains(sortBy)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid sortBy! Supported sortBy values => " + SUPPORTED_SORT_BYS);
    }

    if (order != null && !ORDER_TYPES.contains(order)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid order! Supported order values => " + ORDER_TYPES);
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

    return esDealService.searchDeals(searchParams, pageable);
  }

  @GetMapping("/suggestions")
  @Operation(summary = "Returns search suggestions for a query")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SearchSuggestion.class)))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
  })
  public List<SearchSuggestion> getSuggestions(
      @Parameter(description = "Search query", example = "iph")
      @NotBlank @Size(min = 3, max = 100) @RequestParam String query) {
    final JsonNode suggestions = esDealService.getSuggestions(query);
    final List<SearchSuggestion> searchSuggestions = new ArrayList<>();
    suggestions.elements().forEachRemaining(element -> searchSuggestions.add(
        SearchSuggestion.builder()
            .id(element.get("_id").asText())
            .title(element.get("_source").get("title").asText())
            .build()));

    return searchSuggestions;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Finds a deal by ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public DealGetDTO getDeal(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.findById(id).orElseThrow(DealNotFoundException::new);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "The deal created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public DealGetDTO createDeal(@Valid @RequestBody DealPostDTO dealPostDTO) {
    final Deal deal = service.create(mapStructMapper.dealPostDTOToDeal(dealPostDTO));

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
  @Operation(summary = "Updates a deal's status", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The deal successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "You can only update your own deal", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public DealGetDTO patchDeal(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @RequestBody JsonPatch patch) {
    return mapStructMapper.dealToDealGetDTO(service.patch(id, patch));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Updates a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The deal successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "You can only update your own deal", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public DealGetDTO updateDeal(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody DealPostDTO dealPostDTO) {
    final Deal deal = convertToEntity(id, dealPostDTO);

    return mapStructMapper.dealToDealGetDTO(service.update(deal));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Deletes a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "The deal successfully deleted", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "You can only remove your own deal", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public void deleteDeal(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id) {
    service.delete(id);
  }

  @GetMapping("/{id}/comments")
  @Operation(summary = "Returns the comments posted to a deal")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentsDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
  })
  public CommentsDTO getComments(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @ParameterObject Pageable pageable) {
    final Page<Comment> comments = commentService.getCommentsByDealId(new ObjectId(id),
        pageable);
    final List<CommentGetDTO> commentGetDTOs = comments.getContent().stream()
        .map(mapStructMapper::commentToCommentGetDTO).collect(Collectors.toList());

    return CommentsDTO.builder().count(comments.getTotalElements())
        .comments(commentGetDTOs).build();
  }

  @GetMapping("/{id}/comment-count")
  @Operation(summary = "Returns the number of comments that a deal has")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content)
  })
  public int getCommentCount(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id) {
    return commentService.getCommentCountByDealId(new ObjectId(id));
  }

  @PostMapping("/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Adds a comment to a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public CommentGetDTO postComment(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody CommentPostDTO commentPostDTO) {
    service.findById(id).orElseThrow(DealNotFoundException::new);
    final Comment comment = mapStructMapper.commentPostDTOToComment(commentPostDTO);
    comment.setDealId(new ObjectId(id));

    return mapStructMapper.commentToCommentGetDTO(commentService.save(comment));
  }

  @PostMapping("/{id}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Reports a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public void createDealReport(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody DealReportPostDTO dealReportPostDTO) {
    final Deal deal = service.findById(id).orElseThrow(DealNotFoundException::new);
    final DealReport dealReport = mapStructMapper.dealReportPostDTOToDealReport(
        dealReportPostDTO);
    dealReport.setReportedDeal(deal);
    dealReportService.save(dealReport);
  }

  @PutMapping("/{id}/votes")
  @Operation(summary = "Casts vote to a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "304", description = "You've already upvoted/downvoted this deal before", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public DealGetDTO voteDeal(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody DealVote dealVote) {
    final DealVoteType voteType = dealVote.getVoteType();
    if (voteType.equals(DealVoteType.UNVOTE)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "To unvote the deal you need to make a DELETE request!");
    }
    final Deal deal = service.vote(id, voteType);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  @DeleteMapping("/{id}/votes")
  @Operation(summary = "Deletes the vote from a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealGetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public DealGetDTO deleteVote(
      @Parameter(description = "String representation of the Deal ID", example = "5fbe790ec6f0b32014074bb1")
      @ObjectIdConstraint @PathVariable String id) {
    final Deal deal = service.vote(id, DealVoteType.UNVOTE);

    return mapStructMapper.dealToDealGetDTO(deal);
  }

  private Deal convertToEntity(String id, DealPostDTO dealPostDTO) {
    // Fetch the deal from the db and set the missing properties from it
    final Deal originalDeal = service.findById(id).orElseThrow(DealNotFoundException::new);
    final Deal deal = mapStructMapper.dealPostDTOToDeal(dealPostDTO);
    deal.setId(id);
    deal.setPostedBy(originalDeal.getPostedBy());
    deal.setDealScore(originalDeal.getDealScore());
    deal.setUpvoters(originalDeal.getUpvoters());
    deal.setDownvoters(originalDeal.getDownvoters());
    deal.setStatus(originalDeal.getStatus());
    deal.setViews(originalDeal.getViews());
    deal.setCreatedAt(originalDeal.getCreatedAt());

    return deal;
  }

}
