package com.halildurmus.hotdeals.security.role;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "roles")
@RestController
@RequestMapping("/roles")
public class RoleController {

  @Autowired
  private RoleService service;

  @PutMapping
  @IsSuper
  @ApiOperation(value = "Adds a role to a user in the Firebase", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  public void addRole(
      @ApiParam("String representation of the Firebase User ID. e.g. 'ndj2KkbGwIUbfIUH2BT6700AQ832'")
      @RequestParam @NotBlank String uid, @ApiParam("User role") @RequestParam Role role) {
    service.add(uid, role);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  @ApiOperation(value = "Deletes a role from a user in the Firebase", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  public void deleteRole(
      @ApiParam("String representation of the Firebase User ID. e.g. 'ndj2KkbGwIUbfIUH2BT6700AQ832'")
      @RequestParam @NotBlank String uid, @ApiParam("User role") @RequestParam Role role) {
    service.delete(uid, role);
  }

}