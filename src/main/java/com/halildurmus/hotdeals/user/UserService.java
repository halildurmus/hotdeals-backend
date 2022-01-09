package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface UserService {

  Optional<User> findByEmail(String email);

  Optional<User> findById(String id);

  Optional<User> findByUid(String uid);

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

  void deleteFCMToken(String userUid, FCMTokenParams fcmTokenParams);

}