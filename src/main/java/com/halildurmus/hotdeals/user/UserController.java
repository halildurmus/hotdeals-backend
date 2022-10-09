package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.deal.dto.DealGetDTO;
import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.report.user.UserReportService;
import com.halildurmus.hotdeals.report.user.dto.UserReportPostDTO;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.user.dto.UserBasicDTO;
import com.halildurmus.hotdeals.user.dto.UserExtendedDTO;
import com.halildurmus.hotdeals.user.dto.UserPostDTO;
import com.halildurmus.hotdeals.util.IsObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "users")
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

  @Autowired private CommentService commentService;

  @Autowired private MapStructMapper mapStructMapper;

  @Autowired private SecurityService securityService;

  @Autowired private UserService service;

  @Autowired private UserReportService userReportService;

  @GetMapping
  @IsSuper
  @Operation(summary = "Returns all users", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = User.class)))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public List<User> getUsers(@ParameterObject Pageable pageable) {
    return service.findAll(pageable).getContent();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a user")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "The user created successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserBasicDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
  })
  public UserBasicDTO createUser(@Valid @RequestBody UserPostDTO userPostDTO) {
    final User user = service.create(mapStructMapper.userPostDTOToUser(userPostDTO));

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/search/findByEmail")
  @IsSuper
  @Operation(summary = "Finds user by email", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserExtendedDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public UserExtendedDTO getUserByEmail(
      @Parameter(description = "User's email address") @RequestParam @Email String email) {
    final User user = service.findByEmail(email).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/search/findByUid")
  @Operation(summary = "Finds user by uid", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserExtendedDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid uid", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public UserExtendedDTO getUserByUid(
      @Parameter(
              description = "String representation of the Firebase User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @RequestParam
          String uid) {
    final User user = service.findByUid(uid).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/me")
  @Operation(
      summary = "Returns the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public User getAuthenticatedUser() {
    return securityService.getUser();
  }

  @PatchMapping(value = "/me", consumes = "application/json-patch+json")
  @Operation(
      summary = "Updates the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserExtendedDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public UserExtendedDTO patchUser(@RequestBody JsonPatch patch) {
    return mapStructMapper.userToUserExtendedDTO(service.patchUser(patch));
  }

  @GetMapping("/me/blocks")
  @Operation(
      summary = "Returns the users blocked by the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = UserExtendedDTO.class)))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public List<UserExtendedDTO> getBlockedUsers(@ParameterObject Pageable pageable) {
    final List<User> blockedUsers = service.getBlockedUsers(pageable);

    return blockedUsers.stream()
        .map(mapStructMapper::userToUserExtendedDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/blocks/{id}")
  @Operation(summary = "Blocks a user", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content),
    @ApiResponse(
        responseCode = "304",
        description = "You've already blocked this user before",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public void blockUser(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    service.block(id);
  }

  @DeleteMapping("/me/blocks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Unblocks a user", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
    @ApiResponse(
        responseCode = "304",
        description = "You've already unblocked this user before",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public void unblockUser(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    service.unblock(id);
  }

  @GetMapping("/me/deals")
  @Operation(
      summary = "Returns the deals posted by the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public List<DealGetDTO> getDeals(@ParameterObject Pageable pageable) {
    return service.getDeals(pageable).stream()
        .map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/me/favorites")
  @Operation(
      summary = "Returns the deals favorited by the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = DealGetDTO.class)))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public List<DealGetDTO> getFavorites(@ParameterObject Pageable pageable) {
    return service.getFavorites(pageable).stream()
        .map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/favorites/{dealId}")
  @Operation(summary = "Favorites a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content),
    @ApiResponse(
        responseCode = "304",
        description = "You've already favorited this deal before",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public void favoriteDeal(
      @Parameter(
              description = "String representation of the Deal ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String dealId) {
    service.favoriteDeal(dealId);
  }

  @DeleteMapping("/me/favorites/{dealId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Unfavorites a deal", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
    @ApiResponse(
        responseCode = "304",
        description = "You've already unfavorited this deal before",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid deal ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "Deal not found", content = @Content)
  })
  public void unfavoriteDeal(
      @Parameter(
              description = "String representation of the Deal ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String dealId) {
    service.unfavoriteDeal(dealId);
  }

  @PutMapping("/me/fcm-tokens")
  @Operation(
      summary = "Adds a FCM token to the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public void addFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    if (ObjectUtils.isEmpty(fcmTokenParams.getDeviceId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "deviceId parameter cannot be empty!");
    }
    service.addFCMToken(fcmTokenParams);
  }

  @DeleteMapping("/me/fcm-tokens")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Deletes a FCM token from the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public void deleteFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    final User user = securityService.getUser();
    service.deleteFCMToken(user.getUid(), fcmTokenParams);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Finds user by ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserBasicDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public UserBasicDTO getUser(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/{id}/comment-count")
  @Operation(summary = "Returns the number of comments posted by a user")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content)
  })
  public int getUsersCommentCount(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    return commentService.getCommentCountByPostedById(new ObjectId(id));
  }

  @GetMapping("/{id}/extended")
  @Operation(summary = "Finds user by ID (Displays additional information about the user)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserExtendedDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public UserExtendedDTO getUserExtended(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @PostMapping("/{id}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Reports a user", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public void createUserReport(
      @Parameter(
              description = "String representation of the User ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id,
      @Valid @RequestBody UserReportPostDTO userReportPostDTO) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);
    final UserReport userReport = mapStructMapper.userReportPostDTOToUserReport(userReportPostDTO);
    userReport.setReportedUser(user);
    userReportService.save(userReport);
  }
}
