package com.halildurmus.hotdeals.deal;

import java.util.Optional;

public interface DealService {

  Optional<Deal> findById(String id);

  Deal saveDeal(Deal deal);

  void removeDeal(String id) throws Exception;

  Deal upvote(String id) throws Exception;

  Deal downvote(String id) throws Exception;

}
