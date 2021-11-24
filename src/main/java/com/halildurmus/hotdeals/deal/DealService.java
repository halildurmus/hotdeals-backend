package com.halildurmus.hotdeals.deal;

public interface DealService {

  Deal saveOrUpdateDeal(Deal deal);

  void removeDeal(String id) throws Exception;

  Deal incrementViewsCounter(String dealId) throws Exception;

  Deal vote(String dealId, String voteType) throws Exception;

}
