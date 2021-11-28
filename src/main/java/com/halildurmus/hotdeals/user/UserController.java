package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class UserController {

  @Autowired
  private UserService service;

  @Autowired
  private SecurityService securityService;

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    final User createdUser = service.create(user);

    return ResponseEntity.status(201).body(createdUser);
  }

  @GetMapping("/users/me")
  public ResponseEntity<Object> getAuthenticatedUser() {
    final User user = securityService.getUser();
    if (user == null) {
      return ResponseEntity.status(401).build();
    }

    return ResponseEntity.ok(user);
  }

  @PatchMapping(value = "/users/me", consumes = "application/json-patch+json")
  public ResponseEntity<Object> updateUser(@RequestBody JsonPatch patch) throws Exception {
    final User patchedUser = service.update(patch);

    return ResponseEntity.ok(patchedUser);
  }

  @GetMapping("/users/me/deals")
  public ResponseEntity<List<Deal>> getDeals(Pageable pageable) {
    final List<Deal> deals = service.getDeals(pageable);

    return ResponseEntity.ok(deals);
  }

  @GetMapping("/users/me/favorites")
  public ResponseEntity<List<Deal>> getFavorites(Pageable pageable) {
    final List<Deal> favorites = service.getFavorites(pageable);

    return ResponseEntity.ok(favorites);
  }

  @PutMapping("/users/me/favorites/{dealId}")
  public ResponseEntity<?> favoriteDeal(@ObjectIdConstraint @PathVariable String dealId)
      throws Exception {
    service.favoriteDeal(dealId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/favorites/{dealId}")
  public ResponseEntity<?> unfavoriteDeal(@ObjectIdConstraint @PathVariable String dealId)
      throws Exception {
    service.unfavoriteDeal(dealId);

    return ResponseEntity.status(204).build();
  }

  @GetMapping("/users/me/blocks")
  public ResponseEntity<List<User>> getBlockedUsers(Pageable pageable) {
    final List<User> blockedUsers = service.getBlockedUsers(pageable);

    return ResponseEntity.ok(blockedUsers);
  }

  @PutMapping("/users/me/blocks/{id}")
  public ResponseEntity<?> blockUser(@ObjectIdConstraint @PathVariable String id) throws Exception {
    service.block(id);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/blocks/{id}")
  public ResponseEntity<?> unblockUser(@ObjectIdConstraint @PathVariable String id)
      throws Exception {
    service.unblock(id);

    return ResponseEntity.status(204).build();
  }

  @PutMapping("/users/me/fcm-tokens")
  public ResponseEntity<?> addFcmToken(@Valid @NotNull @RequestBody Map<String, String> json)
      throws Exception {
    if (!json.containsKey("fcmToken")) {
      throw new Exception("You need to include 'fcmToken' inside the request body!");
    }

    final String fcmToken = json.get("fcmToken");
    service.addFcmToken(fcmToken);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/fcm-tokens")
  public ResponseEntity<?> removeFcmToken(@Valid @NotNull @RequestBody Map<String, String> json)
      throws Exception {
    if (!json.containsKey("fcmToken")) {
      throw new Exception("You need to include 'fcmToken' inside the request body!");
    }

    final User user = securityService.getUser();
    final String fcmToken = json.get("fcmToken");
    service.removeFcmToken(user.getUid(), fcmToken);

    return ResponseEntity.status(204).build();
  }

}