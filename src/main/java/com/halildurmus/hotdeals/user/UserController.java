package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.security.SecurityService;
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
    User response = service.create(user);

    if (response == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/users/me")
  public ResponseEntity<Object> getAuthenticatedUser() {
    User response = securityService.getUser();

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/block/{userId}")
  public ResponseEntity<Object> blockUser(@PathVariable String userId) throws Exception {
    User response = service.block(userId);

    if (response == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/users/unblock/{userId}")
  public ResponseEntity<Object> unblockUser(@PathVariable String userId) throws Exception {
    User response = service.unblock(userId);

    if (response == null) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.status(200).body(response);
  }

}