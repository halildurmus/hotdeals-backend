package com.halildurmus.hotdeals.deal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DealServiceImpl implements DealService {

  @Autowired
  private DealRepository repository;

  @Override
  public Deal vote(String dealId, String userId, String voteType)
      throws Exception {
    Deal deal = repository.findById(dealId).orElseThrow(() -> new Exception("Deal could not be found!"));
    List<ObjectId> upVoters = deal.getUpVoters();
    List<ObjectId> downVoters = deal.getDownVoters();

    if (voteType.equals("upVote")) {
      if (upVoters.contains(new ObjectId(userId))) {
        throw new Exception("You've already upvoted this deal before!");
      } else {
        downVoters.remove(new ObjectId(userId));
        upVoters.add(new ObjectId(userId));
        deal.setUpVoters(upVoters);
      }
    } else if (voteType.equals("downVote")) {
      if (downVoters.contains(new ObjectId(userId))) {
        throw new Exception("You've already downvoted this deal before!");
      } else {
        upVoters.remove(new ObjectId(userId));
        downVoters.add(new ObjectId(userId));
        deal.setDownVoters(downVoters);
      }
    } else {
      throw new Exception("Invalid vote type! Valid vote types: {upVote, downVote}");
    }

    repository.save(deal);

    return deal;
  }

}
