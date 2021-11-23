package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.security.SecurityService;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class UserController {

  @Autowired
  private UserService service;

  @Autowired
  private SecurityService securityService;

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    final User response = service.create(user);

    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/users/me")
  public ResponseEntity<Object> getAuthenticatedUser() {
    User response = securityService.getUser();
    if (response == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/users/me", consumes = "application/json-patch+json")
  public ResponseEntity<Object> updateUser(@RequestBody JsonPatch patch) {
    try {
      final User patchedUser = service.update(patch);

      return ResponseEntity.ok(patchedUser);
    } catch (DuplicateKeyException | JsonPatchException | JsonProcessingException e) {
      if (e instanceof DuplicateKeyException) {
        return ResponseEntity.status(400).body(e.getLocalizedMessage());
      }

      return ResponseEntity.status(500).body(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/users/add-fcm-token")
  public ResponseEntity<Object> addFcmToken(@RequestBody Map<String, String> json) {
    final String fcmToken = json.get("fcmToken");
    service.addFcmToken(fcmToken);

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PostMapping("/users/logout")
  public ResponseEntity<Object> logout(@RequestBody Map<String, String> json) {
    final String fcmToken = json.get("fcmToken");
    service.logout(fcmToken);

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PostMapping("/users/block/{userId}")
  public ResponseEntity<User> blockUser(@PathVariable String userId) throws Exception {
    final User response = service.block(userId);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/users/unblock/{userId}")
  public ResponseEntity<User> unblockUser(@PathVariable String userId) throws Exception {
    final User response = service.unblock(userId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/users/deals")
  public ResponseEntity<List<Deal>> getDeals(Pageable pageable) {
    final List<Deal> response = service.getDeals(pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/users/favorites")
  public ResponseEntity<List<Deal>> getFavorites(Pageable pageable) {
    final List<Deal> response = service.getFavorites(pageable);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/users/favorite/{dealId}")
  public ResponseEntity<User> favorite(@PathVariable String dealId) throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final User response = service.favorite(dealId);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/users/unfavorite/{dealId}")
  public ResponseEntity<User> unfavorite(@PathVariable String dealId) throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final User response = service.unfavorite(dealId);

    return ResponseEntity.ok(response);
  }

}