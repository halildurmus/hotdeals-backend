package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.DealRepository;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.util.FakerUtil;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private final DealRepository dealRepository;
  private final UserRepository repository;
  private final FakerUtil fakerUtil;
  @Autowired
  private SecurityService securityService;

  @Autowired
  public UserServiceImpl(DealRepository dealRepository,
      UserRepository userRepository, FakerUtil fakerUtil) {
    this.dealRepository = dealRepository;
    this.repository = userRepository;
    this.fakerUtil = fakerUtil;
  }

  @Override
  public User create(User user) {
    boolean error;

    do {
      try {
        final String nickname = fakerUtil.generateNickname();
        user.setNickname(nickname);
        repository.insert(user);
        error = false;
      } catch (Exception e) {
        log.error(e.getMessage());

        if (e instanceof DuplicateKeyException && e.getMessage().contains("nickname")) {
          error = true;
        } else {
          throw e;
        }
      }
    } while (error);

    return user;
  }

  @Override
  public void addFcmToken(String fcmToken) {
    final User user = securityService.getUser();
    final List<String> fcmTokens = user.getFcmTokens();

    if (!fcmTokens.contains(fcmToken)) {
      fcmTokens.add(fcmToken);
      user.setFcmTokens(fcmTokens);
      repository.save(user);
    }
  }

  @Override
  public void removeFcmToken(String userUid, String fcmToken) {
    final User user = repository.findByUid(userUid).orElse(null);
    if (user == null) {
      return;
    }

    final List<String> fcmTokens = user.getFcmTokens();
    if (fcmTokens.contains(fcmToken)) {
      fcmTokens.remove(fcmToken);
      user.setFcmTokens(fcmTokens);
      repository.save(user);
    }
  }

  @Override
  public void logout(String fcmToken) {
    final User user = securityService.getUser();
    final List<String> fcmTokens = user.getFcmTokens();

    if (fcmTokens.contains(fcmToken)) {
      fcmTokens.remove(fcmToken);
      user.setFcmTokens(fcmTokens);
      repository.save(user);
    }
  }

  @Override
  public User block(String userId) throws Exception {
    final User user = securityService.getUser();
    final List<String> blockedUsers = user.getBlockedUsers();

    if (blockedUsers.contains(userId)) {
      throw new Exception("You've already blocked this user before!");
    } else {
      blockedUsers.add(userId);
      user.setBlockedUsers(blockedUsers);
      repository.save(user);
    }

    return user;
  }

  @Override
  public User unblock(String userId) throws Exception {
    final User user = securityService.getUser();
    final List<String> blockedUsers = user.getBlockedUsers();

    if (blockedUsers.contains(userId)) {
      blockedUsers.remove(userId);
      user.setBlockedUsers(blockedUsers);
      repository.save(user);
    } else {
      throw new Exception("You've already unblocked this user before!");
    }

    return user;
  }

  @Override
  public List<Deal> getFavorites() {
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();

    return (List<Deal>) dealRepository.findAllById(favorites.keySet());
  }

  @Override
  public User favorite(String dealId) throws Exception {
    dealRepository.findById(dealId).orElseThrow(() -> new Exception("Deal could not be found!"));
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();

    if (favorites.containsKey(dealId)) {
      throw new Exception("You've already favorited this deal before!");
    } else {
      favorites.put(dealId, true);
      user.setFavorites(favorites);
      repository.save(user);
    }

    return user;
  }

  @Override
  public User unfavorite(String dealId) throws Exception {
    dealRepository.findById(dealId).orElseThrow(() -> new Exception("Deal could not be found!"));
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();

    if (favorites.containsKey(dealId)) {
      favorites.remove(dealId);
      user.setFavorites(favorites);
      repository.save(user);
    } else {
      throw new Exception("You've already unfavorited this deal before!");
    }

    return user;
  }

}
