package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.DealRepository;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.util.FakerUtil;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private final DealRepository dealRepository;
  private final UserRepository repository;
  private final FakerUtil fakerUtil;
  private final ObjectMapper objectMapper = JsonMapper.builder()
      .findAndAddModules()
      .build();
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
    boolean errorOccurred;
    do {
      try {
        final String nickname = fakerUtil.generateNickname();
        user.setNickname(nickname);
        repository.insert(user);
        errorOccurred = false;
      } catch (Exception e) {
        log.error(e.getMessage());

        if (e instanceof DuplicateKeyException && e.getMessage().contains("nickname")) {
          errorOccurred = true;
        } else {
          throw e;
        }
      }
    } while (errorOccurred);

    return user;
  }

  private UserPatchDTO patchUser(JsonPatch patch)
      throws JsonPatchException, JsonProcessingException {
    final UserPatchDTO userPatchDTO = new UserPatchDTO();
    // Converts the user to a JsonNode
    final JsonNode target = objectMapper.convertValue(userPatchDTO, JsonNode.class);
    // Applies the patch to the user
    final JsonNode patched = patch.apply(target);

    // Converts the JsonNode to a UserPatchDTO instance
    return objectMapper.treeToValue(patched, UserPatchDTO.class);
  }

  @Override
  public User update(JsonPatch patch) throws JsonPatchException, JsonProcessingException {
    final User user = securityService.getUser();
    final UserPatchDTO patchedUser = patchUser(patch);
    if (patchedUser.getAvatar().isPresent()) {
      user.setAvatar(patchedUser.getAvatar().get());
    } else if (patchedUser.getNickname().isPresent()) {
      user.setNickname(patchedUser.getNickname().get());
    }
    repository.save(user);

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
    } else {
      log.warn("addFcmToken() -> " + user + " already has this fcmToken: " + fcmToken);
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
    } else {
      log.warn("removeFcmToken() -> " + user + " does not have this fcmToken: " + fcmToken);
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
    } else {
      log.warn("logout() -> " + user + " does not have this fcmToken: " + fcmToken);
    }
  }

  @Override
  public List<Deal> getFavorites(Pageable pageable) {
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();

    return dealRepository.findAllByIdIn(favorites.keySet(), pageable).getContent();
  }

  @Override
  public void favoriteDeal(String dealId) throws Exception {
    dealRepository.findById(dealId).orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();
    if (favorites.containsKey(dealId)) {
      // TODO(halildurmus): Return HTTP 304 NOT MODIFIED
      throw new Exception("You've already favorited this deal before!");
    }

    favorites.put(dealId, true);
    user.setFavorites(favorites);
    repository.save(user);
  }

  @Override
  public void unfavoriteDeal(String dealId) throws Exception {
    dealRepository.findById(dealId).orElseThrow(DealNotFoundException::new);
    final User user = securityService.getUser();
    final Map<String, Boolean> favorites = user.getFavorites();
    if (!favorites.containsKey(dealId)) {
      // TODO(halildurmus): Return HTTP 304 NOT MODIFIED
      throw new Exception("You've already unfavorited this deal before!");
    }

    favorites.remove(dealId);
    user.setFavorites(favorites);
    repository.save(user);
  }

  @Override
  public void block(String id) throws Exception {
    repository.findById(id).orElseThrow(UserNotFoundException::new);
    final User user = securityService.getUser();
    final Map<String, Boolean> blockedUsers = user.getBlockedUsers();
    if (blockedUsers.containsKey(id)) {
      // TODO(halildurmus): Return HTTP 304 NOT MODIFIED
      throw new Exception("You've already blocked this user before!");
    }

    blockedUsers.put(id, true);
    user.setBlockedUsers(blockedUsers);
    repository.save(user);
  }

  @Override
  public void unblock(String id) throws Exception {
    repository.findById(id).orElseThrow(UserNotFoundException::new);
    final User user = securityService.getUser();
    final Map<String, Boolean> blockedUsers = user.getBlockedUsers();
    if (!blockedUsers.containsKey(id)) {
      // TODO(halildurmus): Return HTTP 304 NOT MODIFIED
      throw new Exception("You've already unblocked this user before!");
    }

    blockedUsers.remove(id);
    user.setBlockedUsers(blockedUsers);
    repository.save(user);
  }

  @Override
  public List<Deal> getDeals(Pageable pageable) {
    final User user = securityService.getUser();

    return dealRepository.findAllByPostedByOrderByCreatedAtDesc(new ObjectId(user.getId()),
        pageable).getContent();
  }

}
