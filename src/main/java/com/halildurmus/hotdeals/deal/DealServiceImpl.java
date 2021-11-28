package com.halildurmus.hotdeals.deal;

import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Ne.valueOf;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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

  @Transactional
  @Override
  public Deal saveDeal(Deal deal) {
    final Deal savedDeal = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));

    return savedDeal;
  }

  @Transactional
  @Override
  public void removeDeal(String id) throws Exception {
    final Deal deal = repository.findById(id)
        .orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    if (!user.getId().equals(deal.getPostedBy().toString())) {
      // TODO(halildurmus): Return HTTP 403
      throw new Exception("You can only remove your own deal!");
    }

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
  public Deal voteDeal(String id, VoteType voteType) {
    final User user = securityService.getUser();
    final ObjectId userId = new ObjectId(user.getId());
    final Deal deal = repository.findById(id)
        .orElseThrow(DealNotFoundException::new);

    if (voteType.equals(VoteType.UP) && deal.getUpvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already upvoted this deal before!");
    } else if (voteType.equals(VoteType.DOWN) && deal.getDownvoters().contains(userId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already downvoted this deal before!");
    }

    final Query query = query(where("_id").is(id));
    final AggregationUpdate update = AggregationUpdate.update();
    final FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

    if (voteType.equals(VoteType.UNVOTE)) {
      update.set("upvoters").toValue(filter("upvoters").as("id")
          .by(valueOf("id").notEqualToValue(userId)));
      update.set("downvoters").toValue(filter("downvoters").as("id")
          .by(valueOf("id").notEqualToValue(userId)));
    } else {
      final String fieldName1 = voteType.equals(VoteType.UP) ? "upvoters" : "downvoters";
      final String fieldName2 = voteType.equals(VoteType.UP) ? "downvoters" : "upvoters";
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
