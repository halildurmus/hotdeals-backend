package com.halildurmus.hotdeals.security.role;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/roles")
@IsSuper
public class RoleController {

  @Autowired
  private RoleService service;

  private boolean isInvalidRole(String value) {
    return Arrays.stream(Role.class.getEnumConstants()).noneMatch(e -> e.name().equals(value));
  }

  @PutMapping
  public ResponseEntity<?> addRole(@RequestParam String uid, @RequestParam String role) {
    if (isInvalidRole(role)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid role! Allowed roles => " + Arrays.toString(Role.values()));
    }

    service.addRole(uid, role);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping
  public ResponseEntity<?> removeRole(@RequestParam String uid, @RequestParam String role) {
    if (isInvalidRole(role)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid role! Allowed roles => " + Arrays.toString(Role.values()));
    }

    service.removeRole(uid, role);

    return ResponseEntity.status(204).build();
  }

}