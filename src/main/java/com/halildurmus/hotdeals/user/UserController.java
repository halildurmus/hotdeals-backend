package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.exception.ExceptionResponse;
import com.halildurmus.hotdeals.security.SecurityService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
    } catch (Exception e) {
      final ExceptionResponse response = ExceptionResponse.builder()
          .dateTime(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(e.getMessage()).build();

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

  @PostMapping("/users/{userId}/block")
  public ResponseEntity<User> blockUser(@PathVariable String userId) throws Exception {
    final User response = service.block(userId);

    return ResponseEntity.status(201).body(response);
  }

  @PostMapping("/users/{userId}/unblock")
  public ResponseEntity<User> unblockUser(@PathVariable String userId) throws Exception {
    final User response = service.unblock(userId);

    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/users/me/deals")
  public ResponseEntity<List<Deal>> getDeals(Pageable pageable) {
    final List<Deal> response = service.getDeals(pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/users/me/favorites")
  public ResponseEntity<List<Deal>> getFavorites(Pageable pageable) {
    final List<Deal> response = service.getFavorites(pageable);

    return ResponseEntity.ok(response);
  }

}