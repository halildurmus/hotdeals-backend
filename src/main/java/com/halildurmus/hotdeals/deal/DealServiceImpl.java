package com.halildurmus.hotdeals.deal;

import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Ne.valueOf;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.deal.DTO.DealPatchDTO;
import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealRepository;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Subtract;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ConcatArrays;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Size;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class DealServiceImpl implements DealService {

  private final ObjectMapper objectMapper = JsonMapper.builder()
      .findAndAddModules()
      .build();
  @Autowired
  private CommentService commentService;
  @Autowired
  private DealRepository repository;
  @Autowired
  private EsDealRepository esDealRepository;
  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private SecurityService securityService;

  @Override
  public Optional<Deal> findById(String id) {
    final Optional<Deal> deal = repository.findById(id);
    if (deal.isPresent()) {
      return Optional.of(incrementViewsCounter(id));
    }

    return deal;
  }

  @Override
  public Page<Deal> getDealsByCategory(String category, Pageable pageable) {
    return repository.findAllByCategoryStartsWithOrderByCreatedAtDesc(category, pageable);
  }

  @Override
  public Page<Deal> getDealsByStoreId(ObjectId storeId, Pageable pageable) {
    return repository.findAllByStoreOrderByCreatedAtDesc(storeId, pageable);
  }

  @Override
  public Page<Deal> getLatestActiveDeals(Pageable pageable) {
    return repository.findAllByStatusEqualsOrderByCreatedAtDesc(DealStatus.ACTIVE, pageable);
  }

  @Override
  public Page<Deal> getMostLikedActiveDeals(Pageable pageable) {
    return repository.findAllByStatusEqualsOrderByDealScoreDesc(DealStatus.ACTIVE, pageable);
  }

  @Transactional
  @Override
  public Deal save(Deal deal) {
    final Deal savedDeal = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));

    return savedDeal;
  }

  private DealPatchDTO applyPatchToDeal(JsonPatch patch)
      throws JsonPatchException, JsonProcessingException {
    final DealPatchDTO dealPatchDTO = new DealPatchDTO();
    // Converts the deal to a JsonNode
    final JsonNode target = objectMapper.convertValue(dealPatchDTO, JsonNode.class);
    // Applies the patch to the deal
    final JsonNode patched = patch.apply(target);

    // Converts the JsonNode to a DealPatchDTO instance
    return objectMapper.treeToValue(patched, DealPatchDTO.class);
  }

  @Override
  public Deal patchDeal(String id, JsonPatch patch) {
    final Deal deal = repository.findById(id).orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own deal!");
    }
    try {
      final DealPatchDTO patchedDeal = applyPatchToDeal(patch);
      deal.setStatus(patchedDeal.getStatus());
      repository.save(deal);
      esDealRepository.save(new EsDeal(deal));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    return deal;
  }

  @Transactional
  @Override
  public Deal updateDeal(Deal deal) {
    repository.findById(deal.getId()).orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own deal!");
    }
    final Deal savedDeal = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));

    return savedDeal;
  }

  @Transactional
  @Override
  public void removeDeal(String id) {
    final Deal deal = repository.findById(id).orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own deal!");
    }
    commentService.deleteDealComments(id);
    repository.deleteById(id);
    esDealRepository.deleteById(id);
  }

  private Deal incrementViewsCounter(String id) {
    final Query query = query(where("_id").is(id));
    final Update update = new Update().inc("views", 1);
    final FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

    return mongoTemplate.findAndModify(query, update, options, Deal.class);
  }

  @Override
  public Deal voteDeal(String id, DealVoteType voteType) {
    final User user = securityService.getUser();
    final ObjectId userId = new ObjectId(user.getId());
    final Deal deal = repository.findById(id)
        .orElseThrow(DealNotFoundException::new);

    if (voteType.equals(DealVoteType.UP) && deal.getUpvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already upvoted this deal before!");
    } else if (voteType.equals(DealVoteType.DOWN) && deal.getDownvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already downvoted this deal before!");
    }

    final Query query = query(where("_id").is(id));
    final AggregationUpdate update = AggregationUpdate.update();
    final FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

    if (voteType.equals(DealVoteType.UNVOTE)) {
      update.set("upvoters").toValue(filter("upvoters").as("id")
          .by(valueOf("id").notEqualToValue(userId)));
      update.set("downvoters").toValue(filter("downvoters").as("id")
          .by(valueOf("id").notEqualToValue(userId)));
    } else {
      final String fieldName1 = voteType.equals(DealVoteType.UP) ? "upvoters" : "downvoters";
      final String fieldName2 = voteType.equals(DealVoteType.UP) ? "downvoters" : "upvoters";
      update.set(fieldName1).toValue(ConcatArrays.arrayOf(fieldName1)
          .concat(ConcatArrays.arrayOf(new ArrayList<>(List.of(userId)))));
      update.set(fieldName2).toValue(filter(fieldName2).as("id")
          .by(valueOf("id").notEqualToValue(userId)));
    }

    update.set("dealScore").toValue(Subtract.valueOf(Size.lengthOfArray("upvoters"))
        .subtract(Size.lengthOfArray("downvoters")));

    return mongoTemplate.findAndModify(query, update, options, Deal.class);
  }

}
