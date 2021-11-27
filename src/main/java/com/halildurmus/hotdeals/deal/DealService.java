package com.halildurmus.hotdeals.deal;

import java.util.Optional;

public interface DealService {

  Optional<Deal> findById(String id);

  Deal saveDeal(Deal deal);

  void removeDeal(String id) throws Exception;

  Deal voteDeal(String id, VoteType voteType) throws Exception;

}
