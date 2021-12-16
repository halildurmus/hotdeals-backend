package com.halildurmus.hotdeals.deal;

import com.github.fge.jsonpatch.JsonPatch;
import java.util.Optional;

public interface DealService {

  Optional<Deal> findById(String id);

  Deal saveDeal(Deal deal);

  Deal patchDeal(String id, JsonPatch patch);

  Deal updateDeal(Deal deal);

  void removeDeal(String id);

  Deal voteDeal(String id, DealVoteType voteType);

}
