package com.halildurmus.hotdeals.deal;

import com.github.fge.jsonpatch.JsonPatch;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealService {

  Optional<Deal> findById(String id);

  Page<Deal> getDealsByCategory(String category, Pageable pageable);

  Page<Deal> getDealsByStoreId(ObjectId storeId, Pageable pageable);

  Page<Deal> getLatestActiveDeals(Pageable pageable);

  Page<Deal> getMostLikedActiveDeals(Pageable pageable);

  Deal save(Deal deal);

  Deal patchDeal(String id, JsonPatch patch);

  Deal updateDeal(Deal deal);

  void removeDeal(String id);

  Deal voteDeal(String id, DealVoteType voteType);

}
