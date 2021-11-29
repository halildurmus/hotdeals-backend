package com.halildurmus.hotdeals.deal;

import java.util.Optional;

public interface DealService {

  Optional<Deal> findById(String id);

  Deal saveDeal(Deal deal);

  void removeDeal(String id);

  Deal voteDeal(String id, DealVoteType voteType);

}
