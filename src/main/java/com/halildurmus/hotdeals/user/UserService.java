package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User create(User user);

  User update(JsonPatch patch) throws JsonPatchException, JsonProcessingException;

  List<Deal> getFavorites(Pageable pageable);

  void favoriteDeal(String dealId) throws Exception;

  void unfavoriteDeal(String dealId) throws Exception;

  void addFcmToken(String fcmToken);

  void removeFcmToken(String userUid, String fcmToken);

  void logout(String fcmToken);

  void block(String userId) throws Exception;

  void unblock(String userId) throws Exception;

  List<Deal> getDeals(Pageable pageable);


}