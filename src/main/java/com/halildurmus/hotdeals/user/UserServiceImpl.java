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
import com.halildurmus.hotdeals.exception.DuplicateNicknameException;
import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.dto.UserPatchDTO;
import com.halildurmus.hotdeals.util.FakerUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private final DealRepository dealRepository;

  private final UserRepository repository;

  private final FakerUtil fakerUtil;

  private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

  @Autowired private SecurityService securityService;

  @Autowired
  public UserServiceImpl(
      DealRepository dealRepository, UserRepository userRepository, FakerUtil fakerUtil) {
    this.dealRepository = dealRepository;
    this.repository = userRepository;
    this.fakerUtil = fakerUtil;
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  @Override
  public Optional<User> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public Optional<User> findByUid(String uid) {
    return repository.findByUid(uid);
  }

  @Override
  public User create(User user) {
    boolean errorOccurred;
    // Try until the generated nickname is unique
    do {
      try {
        var nickname = fakerUtil.generateNickname();
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

  private UserPatchDTO applyPatchToUser(JsonPatch patch)
      throws JsonPatchException, JsonProcessingException {
    var userPatchDTO = new UserPatchDTO();
    // Convert the user to a JsonNode
    var target = objectMapper.convertValue(userPatchDTO, JsonNode.class);
    // Apply the patch to the user
    var patched = patch.apply(target);

    // Convert the JsonNode to a UserPatchDTO instance
    return objectMapper.treeToValue(patched, UserPatchDTO.class);
  }

  @Override
  public User patchUser(JsonPatch patch) {
    var user = securityService.getUser();
    try {
      var patchedUser = applyPatchToUser(patch);
      if (patchedUser.getAvatar().isPresent()) {
        user.setAvatar(patchedUser.getAvatar().get());
      } else if (patchedUser.getNickname().isPresent()) {
        user.setNickname(patchedUser.getNickname().get());
      }
      repository.save(user);
    } catch (DuplicateKeyException e) {
      throw new DuplicateNicknameException();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    return user;
  }

  @Override
  public void addFCMToken(FCMTokenParams fcmTokenParams) {
    var deviceId = fcmTokenParams.getDeviceId();
    var token = fcmTokenParams.getToken();
    var user = securityService.getUser();
    Map<String, String> fcmTokens = user.getFcmTokens();
    fcmTokens.put(deviceId, token);
    user.setFcmTokens(fcmTokens);
    repository.save(user);
  }

  @Override
  public void deleteFCMToken(String userUid, FCMTokenParams fcmTokenParams) {
    var user = repository.findByUid(userUid).orElse(null);
    if (user == null) {
      return;
    }
    var token = fcmTokenParams.getToken();
    Map<String, String> fcmTokens = user.getFcmTokens();
    var isTokenExists = fcmTokens.entrySet().removeIf(entry -> (token.equals(entry.getValue())));
    if (!isTokenExists) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "User does not have this token: " + token);
    }
    user.setFcmTokens(fcmTokens);
    repository.save(user);
  }

  @Override
  public List<Deal> getDeals(Pageable pageable) {
    var user = securityService.getUser();
    return dealRepository
        .findAllByPostedByOrderByCreatedAtDesc(new ObjectId(user.getId()), pageable)
        .getContent();
  }

  @Override
  public List<Deal> getFavorites(Pageable pageable) {
    var user = securityService.getUser();
    var favorites = user.getFavorites();
    return dealRepository.findAllByIdIn(favorites, pageable).getContent();
  }

  @Override
  public void favoriteDeal(String dealId) {
    dealRepository.findById(dealId).orElseThrow(DealNotFoundException::new);
    var user = securityService.getUser();
    var favorites = user.getFavorites();
    if (favorites.contains(dealId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already favorited this deal before!");
    }
    favorites.add(dealId);
    user.setFavorites(favorites);
    repository.save(user);
  }

  @Override
  public void unfavoriteDeal(String dealId) {
    dealRepository.findById(dealId).orElseThrow(DealNotFoundException::new);
    var user = securityService.getUser();
    var favorites = user.getFavorites();
    if (!favorites.contains(dealId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already unfavorited this deal before!");
    }
    favorites.remove(dealId);
    user.setFavorites(favorites);
    repository.save(user);
  }

  @Override
  public List<User> getBlockedUsers(Pageable pageable) {
    var user = securityService.getUser();
    var blockedUsers = user.getBlockedUsers();
    return repository.findAllByIdIn(blockedUsers, pageable).getContent();
  }

  @Override
  public void block(String id) {
    repository.findById(id).orElseThrow(UserNotFoundException::new);
    var user = securityService.getUser();
    var blockedUsers = user.getBlockedUsers();
    if (blockedUsers.contains(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already blocked this user before!");
    }
    blockedUsers.add(id);
    user.setBlockedUsers(blockedUsers);
    repository.save(user);
  }

  @Override
  public void unblock(String id) {
    repository.findById(id).orElseThrow(UserNotFoundException::new);
    var user = securityService.getUser();
    var blockedUsers = user.getBlockedUsers();
    if (!blockedUsers.contains(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_MODIFIED, "You've already unblocked this user before!");
    }
    blockedUsers.remove(id);
    user.setBlockedUsers(blockedUsers);
    repository.save(user);
  }
}
