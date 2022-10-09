package com.halildurmus.hotdeals.security.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "roles")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/roles")
public class RoleController {

  @Autowired private RoleService service;

  @PutMapping
  @IsSuper
  @Operation(summary = "Adds a role to a user in the Firebase")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "The role successfully added",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public void addRole(
      @Parameter(
              description = "String representation of the Firebase User ID",
              example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
          @RequestParam
          @NotBlank
          String uid,
      @Parameter(description = "User role") @RequestParam Role role) {
    service.add(uid, role);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  @Operation(summary = "Deletes a role from a user in the Firebase")
  @ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "The role successfully deleted",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public void deleteRole(
      @Parameter(
              description = "String representation of the Firebase User ID",
              example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
          @RequestParam
          @NotBlank
          String uid,
      @Parameter(description = "User role") @RequestParam Role role) {
    service.delete(uid, role);
  }
}
