package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.security.SecurityService;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<Object> createUser(@RequestBody User user) {
    final User response = service.create(user);

    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/users/me")
  public ResponseEntity<Object> getAuthenticatedUser() {
    User response = securityService.getUser();

    if (response == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/add-fcm-token")
  public ResponseEntity<Object> addFcmToken(@RequestBody Map<String, String> json) {
    final String fcmToken = json.get("fcmToken");
    service.addFcmToken(fcmToken);

    return ResponseEntity.status(200).body(HttpStatus.OK);
  }

  @PostMapping("/users/logout")
  public ResponseEntity<Object> logout(@RequestBody Map<String, String> json) {
    final String fcmToken = json.get("fcmToken");
    service.logout(fcmToken);

    return ResponseEntity.status(200).body(HttpStatus.OK);
  }

  @PostMapping("/users/block/{userId}")
  public ResponseEntity<Object> blockUser(@PathVariable String userId) throws Exception {
    final User response = service.block(userId);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/unblock/{userId}")
  public ResponseEntity<Object> unblockUser(@PathVariable String userId) throws Exception {
    final User response = service.unblock(userId);

    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/users/favorites")
  public ResponseEntity<List<Deal>> getFavorites() {
    final List<Deal> response = service.getFavorites();

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/favorite/{dealId}")
  public ResponseEntity<Object> favorite(@PathVariable String dealId)
      throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final User response = service.favorite(dealId);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/unfavorite/{dealId}")
  public ResponseEntity<Object> unfavorite(@PathVariable String dealId)
      throws Exception {
    if (!ObjectId.isValid(dealId)) {
      throw new IllegalArgumentException("Invalid dealId!");
    }

    final User response = service.unfavorite(dealId);

    return ResponseEntity.status(200).body(response);
  }

}