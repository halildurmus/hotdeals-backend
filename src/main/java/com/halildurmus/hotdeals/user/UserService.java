package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User create(User user);

  User patchUser(JsonPatch patch);

  List<Deal> getDeals(Pageable pageable);

  List<Deal> getFavorites(Pageable pageable);

  void favoriteDeal(String dealId);

  void unfavoriteDeal(String dealId);

  List<User> getBlockedUsers(Pageable pageable);

  void block(String userId);

  void unblock(String userId);

  void addFCMToken(FCMTokenParams fcmTokenParams);

  void removeFCMToken(String userUid, FCMTokenParams fcmTokenParams);

}