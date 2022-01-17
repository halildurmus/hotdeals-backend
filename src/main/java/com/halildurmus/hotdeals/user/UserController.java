package com.halildurmus.hotdeals.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.deal.DTO.DealGetDTO;
import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.report.user.DTO.UserReportPostDTO;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.report.user.UserReportService;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.user.DTO.UserBasicDTO;
import com.halildurmus.hotdeals.user.DTO.UserExtendedDTO;
import com.halildurmus.hotdeals.user.DTO.UserPostDTO;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

@Api(tags = "users")
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

  @Autowired
  private CommentService commentService;

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserService service;

  @Autowired
  private UserReportService userReportService;

  @GetMapping
  @IsSuper
  @ApiOperation(value = "Returns all users", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  public Page<User> getUsers(Pageable pageable) {
    return service.findAll(pageable);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation("Creates a user")
  @ApiResponses(@ApiResponse(code = 400, message = "Bad Request"))
  public UserBasicDTO createUser(@Valid @RequestBody UserPostDTO userPostDTO) {
    final User user = service.create(mapStructMapper.userPostDTOToUser(userPostDTO));

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/search/findByEmail")
  @IsSuper
  @ApiOperation(value = "Finds user by email", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid email format"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public UserExtendedDTO getUserByEmail(
      @ApiParam("User's email address") @RequestParam @Email String email) {
    final User user = service.findByEmail(email).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/search/findByUid")
  @ApiOperation(value = "Finds user by uid", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid uid"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public UserExtendedDTO getUserByUid(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @RequestParam String uid) {
    final User user = service.findByUid(uid).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/me")
  @ApiOperation(value = "Returns the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
  public User getAuthenticatedUser() {
    return securityService.getUser();
  }

  @PatchMapping(value = "/me", consumes = "application/json-patch+json")
  @ApiOperation(value = "Updates the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized")
  })
  public UserExtendedDTO patchUser(@RequestBody JsonPatch patch) {
    return mapStructMapper.userToUserExtendedDTO(service.patchUser(patch));
  }

  @GetMapping("/me/blocks")
  @ApiOperation(value = "Returns the users blocked by the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
  public List<UserExtendedDTO> getBlockedUsers(Pageable pageable) {
    final List<User> blockedUsers = service.getBlockedUsers(pageable);

    return blockedUsers.stream().map(mapStructMapper::userToUserExtendedDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/blocks/{id}")
  @ApiOperation(value = "Blocks a user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 304, message = "You've already blocked this user before"),
      @ApiResponse(code = 400, message = "Invalid user ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public void blockUser(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    service.block(id);
  }

  @DeleteMapping("/me/blocks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Unblocks a user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 304, message = "You've already unblocked this user before"),
      @ApiResponse(code = 400, message = "Invalid user ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public void unblockUser(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    service.unblock(id);
  }

  @GetMapping("/me/deals")
  @ApiOperation(value = "Returns the deals posted by the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
  public List<DealGetDTO> getDeals(Pageable pageable) {
    return service.getDeals(pageable).stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/me/favorites")
  @ApiOperation(value = "Returns the deals favorited by the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
  public List<DealGetDTO> getFavorites(Pageable pageable) {
    return service.getFavorites(pageable).stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/favorites/{dealId}")
  @ApiOperation(value = "Favorites a deal", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 304, message = "You've already favorited this deal before"),
      @ApiResponse(code = 400, message = "Invalid deal ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Deal not found")
  })
  public void favoriteDeal(
      @ApiParam("String representation of the Deal ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String dealId) {
    service.favoriteDeal(dealId);
  }

  @DeleteMapping("/me/favorites/{dealId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Unfavorites a deal", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 304, message = "You've already unfavorited this deal before"),
      @ApiResponse(code = 400, message = "Invalid deal ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Deal not found")
  })
  public void unfavoriteDeal(
      @ApiParam("String representation of the Deal ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String dealId) {
    service.unfavoriteDeal(dealId);
  }

  @PutMapping("/me/fcm-tokens")
  @ApiOperation(value = "Adds a FCM token to the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized")
  })
  public void addFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    if (ObjectUtils.isEmpty(fcmTokenParams.getDeviceId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "deviceId parameter cannot be empty!");
    }
    service.addFCMToken(fcmTokenParams);
  }

  @DeleteMapping("/me/fcm-tokens")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Deletes a FCM token from the authenticated user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized")
  })
  public void deleteFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    final User user = securityService.getUser();
    service.deleteFCMToken(user.getUid(), fcmTokenParams);
  }

  @GetMapping("/{id}")
  @ApiOperation("Finds user by ID")
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid user ID"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public UserBasicDTO getUser(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/{id}/comment-count")
  @ApiOperation("Returns the number of comments posted by a user")
  @ApiResponses(@ApiResponse(code = 400, message = "Invalid user ID"))
  public int getUsersCommentCount(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    return commentService.getCommentCountByPostedById(new ObjectId(id));
  }

  @GetMapping("/{id}/extended")
  @ApiOperation("Finds user by ID (Displays additional information about the user)")
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid user ID"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public UserExtendedDTO getUserExtended(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @PostMapping("/{id}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Reports a user", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid user ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "User not found")
  })
  public void createUserReport(
      @ApiParam("String representation of the User ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody UserReportPostDTO userReportPostDTO) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);
    final UserReport userReport = mapStructMapper.userReportPostDTOToUserReport(userReportPostDTO);
    userReport.setReportedUser(user);
    userReportService.save(userReport);
  }

}