package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.deal.Deal;
import java.util.List;

public interface UserService {

  User create(User user);

  void addFcmToken(String fcmToken);

  void logout(String fcmToken);

  User block(String userId) throws Exception;

  User unblock(String userId) throws Exception;

  List<Deal> getFavorites();

  User favorite(String dealId) throws Exception;

  User unfavorite(String dealId) throws Exception;

}