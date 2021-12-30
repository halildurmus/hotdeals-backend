package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@RepositoryRestController
@Validated
public class UserController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private CommentService commentService;

  @Autowired
  private UserService service;

  @Autowired
  private SecurityService securityService;

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    return ResponseEntity.status(201).body(service.create(user));
  }

  @GetMapping("/users/{id}/comments-count")
  public ResponseEntity<Integer> getUsersCommentCount(@ObjectIdConstraint @PathVariable String id) {
    return ResponseEntity.ok(commentService.getCommentCountByPostedById(new ObjectId(id)));
  }

  @GetMapping("/users/me")
  public ResponseEntity<User> getAuthenticatedUser() {
    return ResponseEntity.ok(securityService.getUser());
  }

  @PatchMapping(value = "/users/me", consumes = "application/json-patch+json")
  public ResponseEntity<User> patchUser(@RequestBody JsonPatch patch) {
    return ResponseEntity.ok(service.patchUser(patch));
  }

  @GetMapping("/users/me/deals")
  public ResponseEntity<List<Deal>> getDeals(Pageable pageable) {
    return ResponseEntity.ok(service.getDeals(pageable));
  }

  @GetMapping("/users/me/favorites")
  public ResponseEntity<List<Deal>> getFavorites(Pageable pageable) {
    return ResponseEntity.ok(service.getFavorites(pageable));
  }

  @PutMapping("/users/me/favorites/{dealId}")
  public ResponseEntity<Void> favoriteDeal(@ObjectIdConstraint @PathVariable String dealId) {
    service.favoriteDeal(dealId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/favorites/{dealId}")
  public ResponseEntity<Void> unfavoriteDeal(@ObjectIdConstraint @PathVariable String dealId) {
    service.unfavoriteDeal(dealId);

    return ResponseEntity.status(204).build();
  }

  @GetMapping("/users/me/blocks")
  public ResponseEntity<List<User>> getBlockedUsers(Pageable pageable) {
    return ResponseEntity.ok(service.getBlockedUsers(pageable));
  }

  @PutMapping("/users/me/blocks/{id}")
  public ResponseEntity<Void> blockUser(@ObjectIdConstraint @PathVariable String id) {
    service.block(id);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/blocks/{id}")
  public ResponseEntity<Void> unblockUser(@ObjectIdConstraint @PathVariable String id) {
    service.unblock(id);

    return ResponseEntity.status(204).build();
  }

  @PutMapping("/users/me/fcm-tokens")
  public ResponseEntity<Void> addFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    if (ObjectUtils.isEmpty(fcmTokenParams.getDeviceId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "deviceId parameter cannot be empty!");
    }
    service.addFCMToken(fcmTokenParams);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/me/fcm-tokens")
  public ResponseEntity<Void> deleteFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    final User user = securityService.getUser();
    service.deleteFCMToken(user.getUid(), fcmTokenParams);

    return ResponseEntity.status(204).build();
  }

}