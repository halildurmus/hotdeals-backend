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
import com.halildurmus.hotdeals.user.DTO.UserBasicDTO;
import com.halildurmus.hotdeals.user.DTO.UserExtendedDTO;
import com.halildurmus.hotdeals.user.DTO.UserPostDTO;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserBasicDTO createUser(@Valid @RequestBody UserPostDTO userPostDTO) {
    final User user = service.create(mapStructMapper.userPostDTOToUser(userPostDTO));

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/search/findByEmail")
  public UserExtendedDTO getUserByEmail(@RequestParam @Email String email) {
    final User user = service.findByEmail(email).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/search/findByUid")
  public UserExtendedDTO getUserByUid(@RequestParam String uid) {
    final User user = service.findByUid(uid).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @GetMapping("/me")
  public User getAuthenticatedUser() {
    return securityService.getUser();
  }

  @PatchMapping(value = "/me", consumes = "application/json-patch+json")
  public UserExtendedDTO patchUser(@RequestBody JsonPatch patch) {
    return mapStructMapper.userToUserExtendedDTO(service.patchUser(patch));
  }

  @GetMapping("/me/blocks")
  public List<UserExtendedDTO> getBlockedUsers(Pageable pageable) {
    final List<User> blockedUsers = service.getBlockedUsers(pageable);

    return blockedUsers.stream().map(mapStructMapper::userToUserExtendedDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/blocks/{id}")
  public void blockUser(@ObjectIdConstraint @PathVariable String id) {
    service.block(id);
  }

  @DeleteMapping("/me/blocks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unblockUser(@ObjectIdConstraint @PathVariable String id) {
    service.unblock(id);
  }

  @GetMapping("/me/deals")
  public List<DealGetDTO> getDeals(Pageable pageable) {
    return service.getDeals(pageable).stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/me/favorites")
  public List<DealGetDTO> getFavorites(Pageable pageable) {
    return service.getFavorites(pageable).stream().map(mapStructMapper::dealToDealGetDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/me/favorites/{dealId}")
  public void favoriteDeal(@ObjectIdConstraint @PathVariable String dealId) {
    service.favoriteDeal(dealId);
  }

  @DeleteMapping("/me/favorites/{dealId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unfavoriteDeal(@ObjectIdConstraint @PathVariable String dealId) {
    service.unfavoriteDeal(dealId);
  }

  @PutMapping("/me/fcm-tokens")
  public void addFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    if (ObjectUtils.isEmpty(fcmTokenParams.getDeviceId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "deviceId parameter cannot be empty!");
    }
    service.addFCMToken(fcmTokenParams);
  }

  @DeleteMapping("/me/fcm-tokens")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFCMToken(@Valid @RequestBody FCMTokenParams fcmTokenParams) {
    final User user = securityService.getUser();
    service.deleteFCMToken(user.getUid(), fcmTokenParams);
  }

  @GetMapping("/{id}")
  public UserBasicDTO getUser(@ObjectIdConstraint @PathVariable String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserBasicDTO(user);
  }

  @GetMapping("/{id}/comment-count")
  public int getUsersCommentCount(@ObjectIdConstraint @PathVariable String id) {
    return commentService.getCommentCountByPostedById(new ObjectId(id));
  }

  @GetMapping("/{id}/extended")
  public UserExtendedDTO getUserExtended(@ObjectIdConstraint @PathVariable String id) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);

    return mapStructMapper.userToUserExtendedDTO(user);
  }

  @PostMapping("/{id}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  public void createDealReport(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody UserReportPostDTO userReportPostDTO) {
    final User user = service.findById(id).orElseThrow(UserNotFoundException::new);
    final UserReport userReport = mapStructMapper.userReportPostDTOToUserReport(userReportPostDTO);
    userReport.setReportedUser(user);
    userReportService.save(userReport);
  }

}