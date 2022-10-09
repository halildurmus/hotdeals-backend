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
import com.halildurmus.hotdeals.deal.dto.DealPatchDTO;
import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealRepository;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.security.SecurityService;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class DealServiceImpl implements DealService {

  private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

  @Autowired private CommentService commentService;

  @Autowired private DealRepository repository;

  @Autowired private EsDealRepository esDealRepository;

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private SecurityService securityService;

  @Override
  public Page<Deal> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public int countDealsByPostedBy(ObjectId postedBy) {
    return repository.countDealsByPostedBy(postedBy);
  }

  @Override
  public int countDealsByStore(ObjectId storeId) {
    return repository.countDealsByStore(storeId);
  }

  @Override
  public Optional<Deal> findById(String id) {
    var deal = repository.findById(id);
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
  public Deal create(Deal deal) {
    var savedDeal = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));
    return savedDeal;
  }

  private DealPatchDTO applyPatchToDeal(JsonPatch patch)
      throws JsonPatchException, JsonProcessingException {
    var dealPatchDTO = new DealPatchDTO();
    // Convert the deal to a JsonNode
    var target = objectMapper.convertValue(dealPatchDTO, JsonNode.class);
    // Apply the patch to the deal
    var patched = patch.apply(target);
    // Convert the JsonNode to a DealPatchDTO instance
    return objectMapper.treeToValue(patched, DealPatchDTO.class);
  }

  @Override
  public Deal patch(String id, JsonPatch patch) {
    var deal = repository.findById(id).orElseThrow(DealNotFoundException::new);
    var user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own deal!");
    }

    try {
      var patchedDeal = applyPatchToDeal(patch);
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
  public Deal update(Deal deal) {
    var user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own deal!");
    }
    var savedDeal = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));
    return savedDeal;
  }

  @Transactional
  @Override
  public void delete(String id) {
    var deal = repository.findById(id).orElseThrow(DealNotFoundException::new);
    var user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own deal!");
    }
    commentService.deleteDealComments(id);
    repository.deleteById(id);
    esDealRepository.deleteById(id);
  }

  private Deal incrementViewsCounter(String id) {
    var query = query(where("_id").is(id));
    var update = new Update().inc("views", 1);
    var options = FindAndModifyOptions.options().returnNew(true);
    return mongoTemplate.findAndModify(query, update, options, Deal.class);
  }

  @Override
  public Deal vote(String id, DealVoteType voteType) {
    var user = securityService.getUser();
    var userId = new ObjectId(user.getId());
    var deal = repository.findById(id).orElseThrow(DealNotFoundException::new);

    if (voteType.equals(DealVoteType.UP) && deal.getUpvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already upvoted this deal before!");
    } else if (voteType.equals(DealVoteType.DOWN) && deal.getDownvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already downvoted this deal before!");
    }

    var query = query(where("_id").is(id));
    var update = AggregationUpdate.update();
    var options = FindAndModifyOptions.options().returnNew(true);

    if (voteType.equals(DealVoteType.UNVOTE)) {
      // Remove the userId from the upvoters array if it exists
      update
          .set("upvoters")
          .toValue(filter("upvoters").as("id").by(valueOf("id").notEqualToValue(userId)));
      // Remove the userId from the downvoters array if it exists
      update
          .set("downvoters")
          .toValue(filter("downvoters").as("id").by(valueOf("id").notEqualToValue(userId)));
    } else {
      var fieldName1 = voteType.equals(DealVoteType.UP) ? "upvoters" : "downvoters";
      var fieldName2 = voteType.equals(DealVoteType.UP) ? "downvoters" : "upvoters";
      // If the voteType is DealVoteType.UP, add the userId to the upvoters array,
      // if the voteType is DealVoteType.DOWN then add the userId to the downvoters
      // array
      update
          .set(fieldName1)
          .toValue(
              ConcatArrays.arrayOf(fieldName1)
                  .concat(ConcatArrays.arrayOf(new ArrayList<>(List.of(userId)))));
      // If the voteType is DealVoteType.UP, remove the userId from the downvoters
      // array,
      // if the voteType is DealVoteType.DOWN then remove the userId from the
      // upvoters array
      update
          .set(fieldName2)
          .toValue(filter(fieldName2).as("id").by(valueOf("id").notEqualToValue(userId)));
    }
    // Subtract the size of the downvoters array from the size of the upvoters array
    // and assign the result to dealScore property
    update
        .set("dealScore")
        .toValue(
            Subtract.valueOf(Size.lengthOfArray("upvoters"))
                .subtract(Size.lengthOfArray("downvoters")));

    return mongoTemplate.findAndModify(query, update, options, Deal.class);
  }
}
