package com.halildurmus.hotdeals.deal;

import com.github.fge.jsonpatch.JsonPatch;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealService {

  Page<Deal> findAll(Pageable pageable);

  int countDealsByPostedBy(ObjectId postedBy);

  int countDealsByStore(ObjectId storeId);

  Optional<Deal> findById(String id);

  Page<Deal> getDealsByCategory(String category, Pageable pageable);

  Page<Deal> getDealsByStoreId(ObjectId storeId, Pageable pageable);

  Page<Deal> getLatestActiveDeals(Pageable pageable);

  Page<Deal> getMostLikedActiveDeals(Pageable pageable);

  Deal create(Deal deal);

  Deal patch(String id, JsonPatch patch);

  Deal update(Deal deal);

  void delete(String id);

  Deal vote(String id, DealVoteType voteType);

}
