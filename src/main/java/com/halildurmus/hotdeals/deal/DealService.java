package com.halildurmus.hotdeals.deal;

import java.util.Optional;

public interface DealService {

  Optional<Deal> findById(String id);

  Deal saveOrUpdateDeal(Deal deal);

  void removeDeal(String id) throws Exception;

  void favorite(String id) throws Exception;

  void unfavorite(String id) throws Exception;

  Deal vote(String id, String voteType) throws Exception;

}
