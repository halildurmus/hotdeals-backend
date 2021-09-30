package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User create(User user);

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