package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User create(User user);

  User update(JsonPatch patch)
      throws DuplicateKeyException, JsonPatchException, JsonProcessingException;

  void addFcmToken(String fcmToken);

  void removeFcmToken(String userUid, String fcmToken);

  void logout(String fcmToken);

  User block(String userId) throws Exception;

  User unblock(String userId) throws Exception;

  List<Deal> getDeals(Pageable pageable);

  List<Deal> getFavorites(Pageable pageable);

  User favorite(String dealId) throws Exception;

  User unfavorite(String dealId) throws Exception;

}