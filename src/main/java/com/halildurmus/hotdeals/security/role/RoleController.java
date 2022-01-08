package com.halildurmus.hotdeals.security.role;

import com.halildurmus.hotdeals.util.EnumUtil;
import java.util.Arrays;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/roles")
@IsSuper
public class RoleController {

  @Autowired
  private RoleService service;

  @PutMapping
  public void addRole(@RequestParam @NotBlank String uid, @RequestParam String role) {
    if (!EnumUtil.isInEnum(role, Role.class)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid role! Supported roles => " + Arrays.toString(Role.values()));
    }
    service.add(uid, role);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRole(@RequestParam @NotBlank String uid, @RequestParam String role) {
    if (!EnumUtil.isInEnum(role, Role.class)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid role! Supported roles => " + Arrays.toString(Role.values()));
    }
    service.delete(uid, role);
  }

}