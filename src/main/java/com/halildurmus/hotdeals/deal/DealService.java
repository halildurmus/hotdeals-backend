package com.halildurmus.hotdeals.deal;

public interface DealService {

  Deal vote(String dealId, String userId, String voteType) throws Exception;

}
