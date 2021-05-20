package com.halildurmus.hotdeals.deal;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;

public interface DealService {

  Deal incrementViewsCounter(String dealId) throws Exception;

  Deal vote(String dealId, ObjectId userId, String voteType) throws Exception;

}
