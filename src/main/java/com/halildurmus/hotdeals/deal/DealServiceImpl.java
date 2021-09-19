package com.halildurmus.hotdeals.deal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.halildurmus.hotdeals.deal.es.EsDeal;
import com.halildurmus.hotdeals.deal.es.EsDealRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DealServiceImpl implements DealService {

  @Autowired
  private DealRepository repository;

  @Autowired
  private EsDealRepository esDealRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Transactional
  @Override
  public Deal saveOrUpdateDeal(Deal deal) {
    Deal returnValue = repository.save(deal);
    esDealRepository.save(new EsDeal(deal));

    return returnValue;
  }

  @Transactional
  @Override
  public void removeDeal(String id) {
    repository.deleteById(id);
    esDealRepository.deleteById(id);
  }

  @Override
  public Deal incrementViewsCounter(String dealId) throws Exception {
    Query query = query(where("_id").is(dealId));
    Update update = new Update().inc("views", 1);
    FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

    Deal deal = mongoTemplate.findAndModify(query, update, options, Deal.class);
    if (deal == null) {
      throw new Exception("Deal could not be found!");
    }

    return deal;
  }

  @Override
  public Deal vote(String dealId, ObjectId userId, String voteType) throws Exception {
    final Deal deal = repository.findById(dealId)
        .orElseThrow(() -> new Exception("Deal could not be found!"));
    final List<ObjectId> upvoters = deal.getUpvoters();
    final List<ObjectId> downvoters = deal.getDownvoters();

    if (voteType.equals("upvote")) {
      if (upvoters.contains(userId)) {
        throw new Exception("You've already upvoted this deal before!");
      }

      downvoters.remove(userId);
      upvoters.add(userId);
    } else {
      if (downvoters.contains(userId)) {
        throw new Exception("You've already downvoted this deal before!");
      }

      upvoters.remove(userId);
      downvoters.add(userId);
    }

    final int upVoteCount = upvoters.size();
    final int downVoteCount = downvoters.size();
    deal.setDealScore(upVoteCount - downVoteCount);
    repository.save(deal);

    return deal;
  }

}
