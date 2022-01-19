package com.halildurmus.hotdeals.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.BaseControllerUnitTest;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.exception.UserNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import com.halildurmus.hotdeals.report.user.DTO.UserReportPostDTO;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import com.halildurmus.hotdeals.report.user.UserReportService;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.DTO.UserPostDTO;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.NestedServletException;

@Import({UserController.class, MapStructMapperImpl.class})
public class UserControllerTest extends BaseControllerUnitTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired
  private JacksonTester<UserPostDTO> json;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private CommentService commentService;

  @MockBean
  private SecurityService securityService;

  @MockBean
  private UserService service;

  @MockBean
  private UserReportService userReportService;

  @Test
  @DisplayName("GET /users (returns empty array)")
  public void getUsersReturnsEmptyArray() throws Exception {
    when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
    final RequestBuilder request = get("/users");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users (returns 1 user)")
  public void getUsersReturnsOneUser() throws Exception {
    final User user = DummyUsers.user1;
    final Page<User> pagedUsers = new PageImpl<>(List.of(user));
    when(service.findAll(any(Pageable.class))).thenReturn(pagedUsers);
    final RequestBuilder request = get("/users");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(10)))
        .andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].uid").value(user.getUid()))
        .andExpect(jsonPath("$[0].email").value(user.getEmail()))
        .andExpect(jsonPath("$[0].avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$[0].nickname").value(user.getNickname()))
        .andExpect(jsonPath("$[0].favorites").value(equalTo(asParsedJson(user.getFavorites()))))
        .andExpect(
            jsonPath("$[0].blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$[0].fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$[0].createdAt").value(user.getCreatedAt().toString()))
        .andExpect(jsonPath("$[0].updatedAt").value(user.getUpdatedAt().toString()));
  }

  @Test
  @DisplayName("POST /users")
  public void createsUser() throws Exception {
    final UserPostDTO userPostDTO = mapStructMapper.userToUserPostDTO(
        DummyUsers.user1);
    final User user = DummyUsers.user1;
    when(service.create(any(User.class))).thenReturn(user);
    final RequestBuilder request = post("/users")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(userPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("POST /users (empty body)")
  public void postUserValidationFails() throws Exception {
    final RequestBuilder request = post("/users")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'userPostDTO' on field 'avatar'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'userPostDTO' on field 'email'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'userPostDTO' on field 'uid'")));
  }

  @Test
  @DisplayName("GET /users/search/findByEmail?email={email}")
  public void findsUserByEmail() throws Exception {
    final User user = DummyUsers.user1;
    when(service.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    final RequestBuilder request = get("/users/search/findByEmail?email=" + user.getEmail());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(7)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$.fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/search/findByEmail?email={email} (user not found)")
  public void findByEmailThrowsUserNotFoundException() {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/users/search/findByEmail?email=" + user.getEmail());

    assertThrows(UserNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("GET /users/search/findByUid?uid={uid}")
  public void findsUserByUid() throws Exception {
    final User user = DummyUsers.user1;
    when(service.findByUid(user.getUid())).thenReturn(Optional.of(user));
    final RequestBuilder request = get("/users/search/findByUid?uid=" + user.getUid());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(7)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$.fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/search/findByUid?uid={uid} (user not found)")
  public void findByUidThrowsUserNotFoundException() {
    final User user = DummyUsers.user1;
    when(service.findByUid(user.getUid())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/users/search/findByUid?uid=" + user.getUid());

    assertThrows(UserNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("GET /users/me")
  public void returnsAuthenticatedUser() throws Exception {
    final User user = DummyUsers.user1;
    when(securityService.getUser()).thenReturn(user);
    final RequestBuilder request = get("/users/me");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(10)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.favorites").value(equalTo(asParsedJson(user.getFavorites()))))
        .andExpect(jsonPath("$.blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$.fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()))
        .andExpect(jsonPath("$.updatedAt").value(user.getUpdatedAt().toString()));

  }

  @Test
  @DisplayName("PATCH /users/me (empty body)")
  public void patchAuthenticatedUserValidationFails() throws Exception {
    final RequestBuilder request = patch("/users/me")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType("application/json-patch+json");

    mvc.perform(request)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof HttpMessageNotReadableException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains(
                    "Cannot deserialize value of type `java.util.ArrayList<com.github.fge.jsonpatch.JsonPatchOperation>`")));
  }

  @Test
  @DisplayName("PATCH /users/me (update avatar)")
  public void patchesAuthenticatedUsersAvatar() throws Exception {
    final User user = DummyUsers.patchedUser1;
    when(service.patchUser(any(JsonPatch.class))).thenReturn(user);
    final String jsonPatch =
        "[{\"op\": \"replace\", \"path\": \"/avatar\", \"value\": \"" + user.getAvatar()
            + "\"}]";
    final RequestBuilder request = patch("/users/me")
        .accept(MediaType.APPLICATION_JSON)
        .content(jsonPatch)
        .contentType("application/json-patch+json");

    mvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(7)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$.fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/me/blocks (returns empty array)")
  public void getBlockedUsersReturnsEmptyArray() throws Exception {
    when(service.getBlockedUsers(any(Pageable.class))).thenReturn(List.of());
    final RequestBuilder request = get("/users/me/blocks");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users/me/blocks (returns 1 user)")
  public void getBlockedUsersReturnsOneUser() throws Exception {
    final User user = DummyUsers.user1;
    when(service.getBlockedUsers(any(Pageable.class))).thenReturn(List.of(user));
    final RequestBuilder request = get("/users/me/blocks");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(7)))
        .andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].uid").value(user.getUid()))
        .andExpect(jsonPath("$[0].avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$[0].nickname").value(user.getNickname()))
        .andExpect(
            jsonPath("$[0].blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$[0].fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$[0].createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("PUT /users/me/blocks/{id}")
  public void addsGivenUserToBlockedUsers() throws Exception {
    final String id = DummyUsers.user1.getId();
    final RequestBuilder request = put("/users/me/blocks/" + id);
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /users/me/blocks/{id} (invalid id)")
  public void putBlockedUserThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = put("/users/me/blocks/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("DELETE /users/me/blocks/{id}")
  public void deletesGivenUserFromBlockedUsers() throws Exception {
    final String id = DummyUsers.user1.getId();
    final RequestBuilder request = delete("/users/me/blocks/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /users/me/blocks/{id} (invalid id)")
  public void deleteBlockedUserThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/users/me/blocks/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("GET /users/me/deals (returns empty array)")
  public void getDealsReturnsEmptyArray() throws Exception {
    when(service.getDeals(any(Pageable.class))).thenReturn(List.of());

    final RequestBuilder request = get("/users/me/deals");
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users/me/deals (returns 1 deal)")
  public void getDealsReturnsOneDeal() throws Exception {
    final Deal deal = DummyDeals.deal1;
    when(service.getDeals(any(Pageable.class))).thenReturn(List.of(deal));
    final RequestBuilder request = get("/users/me/deals");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(17)))
        .andExpect(jsonPath("$[0].id").value(deal.getId()))
        .andExpect(jsonPath("$[0].postedBy").value(deal.getPostedBy().toString()))
        .andExpect(jsonPath("$[0].title").value(deal.getTitle()))
        .andExpect(jsonPath("$[0].description").value(deal.getDescription()))
        .andExpect(jsonPath("$[0].originalPrice").value(deal.getOriginalPrice()))
        .andExpect(jsonPath("$[0].price").value(deal.getPrice()))
        .andExpect(jsonPath("$[0].store").value(deal.getStore().toString()))
        .andExpect(jsonPath("$[0].category").value(deal.getCategory()))
        .andExpect(jsonPath("$[0].coverPhoto").value(deal.getCoverPhoto()))
        .andExpect(jsonPath("$[0].photos", hasSize(deal.getPhotos().size())))
        .andExpect(jsonPath("$[0].dealUrl").value(deal.getDealUrl()))
        .andExpect(jsonPath("$[0].dealScore").value(deal.getDealScore()))
        .andExpect(jsonPath("$[0].upvoters", hasSize(deal.getUpvoters().size())))
        .andExpect(jsonPath("$[0].downvoters", hasSize(deal.getDownvoters().size())))
        .andExpect(jsonPath("$[0].views").value(deal.getViews()))
        .andExpect(jsonPath("$[0].status").value(deal.getStatus().toString()))
        .andExpect(jsonPath("$[0].createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/me/favorites (returns empty array)")
  public void getFavoritesReturnsEmptyArray() throws Exception {
    when(service.getFavorites(any(Pageable.class))).thenReturn(List.of());
    final RequestBuilder request = get("/users/me/favorites");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users/me/favorites (returns 1 deal)")
  public void getFavoritesReturnsOneDeal() throws Exception {
    final Deal deal = DummyDeals.deal1;
    when(service.getFavorites(any(Pageable.class))).thenReturn(List.of(deal));
    final RequestBuilder request = get("/users/me/favorites");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(17)))
        .andExpect(jsonPath("$[0].id").value(deal.getId()))
        .andExpect(jsonPath("$[0].postedBy").value(deal.getPostedBy().toString()))
        .andExpect(jsonPath("$[0].title").value(deal.getTitle()))
        .andExpect(jsonPath("$[0].description").value(deal.getDescription()))
        .andExpect(jsonPath("$[0].originalPrice").value(deal.getOriginalPrice()))
        .andExpect(jsonPath("$[0].price").value(deal.getPrice()))
        .andExpect(jsonPath("$[0].store").value(deal.getStore().toString()))
        .andExpect(jsonPath("$[0].category").value(deal.getCategory()))
        .andExpect(jsonPath("$[0].coverPhoto").value(deal.getCoverPhoto()))
        .andExpect(jsonPath("$[0].photos", hasSize(deal.getPhotos().size())))
        .andExpect(jsonPath("$[0].dealUrl").value(deal.getDealUrl()))
        .andExpect(jsonPath("$[0].dealScore").value(deal.getDealScore()))
        .andExpect(jsonPath("$[0].upvoters", hasSize(deal.getUpvoters().size())))
        .andExpect(jsonPath("$[0].downvoters", hasSize(deal.getDownvoters().size())))
        .andExpect(jsonPath("$[0].views").value(deal.getViews()))
        .andExpect(jsonPath("$[0].status").value(deal.getStatus().toString()))
        .andExpect(jsonPath("$[0].createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("PUT /users/me/favorites/{id}")
  public void addsGivenDealToFavorites() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = put("/users/me/favorites/" + id);
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /users/me/favorites/{id} (invalid id)")
  public void putFavoriteDealThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = put("/users/me/favorites/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("DELETE /users/me/favorites/{id}")
  public void deletesGivenDealFromFavorites() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = delete("/users/me/favorites/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /users/me/favorites/{id} (invalid id)")
  public void deleteFavoriteThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/users/me/favorites/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("PUT /users/me/fcm-tokens")
  public void savesFCMToken() throws Exception {
    final FCMTokenParams fcmTokenParams = FCMTokenParams.builder().deviceId("425ha4123a")
        .token("423623gasdfsa").build();
    final RequestBuilder request = put("/users/me/fcm-tokens/")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(fcmTokenParams))
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /users/me/fcm-tokens (empty body)")
  public void putFCMTokenValidationFails() throws Exception {
    final RequestBuilder request = put("/users/me/fcm-tokens/")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'FCMTokenParams' on field 'token'")));
  }

  @Test
  @DisplayName("PUT /users/me/fcm-tokens (missing deviceId)")
  public void putFCMTokenValidationFailsDueToMissingDeviceId() throws Exception {
    final FCMTokenParams fcmTokenParams = FCMTokenParams.builder().token("423623gasdfsa").build();
    final RequestBuilder request = put("/users/me/fcm-tokens/")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(fcmTokenParams))
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(equalTo("deviceId parameter cannot be empty!")));
  }

  @Test
  @DisplayName("DELETE /users/me/fcm-tokens")
  public void deletesFCMToken() throws Exception {
    final User user = DummyUsers.user1;
    when(securityService.getUser()).thenReturn(user);
    final FCMTokenParams fcmTokenParams = FCMTokenParams.builder().token("423623gasdfsa").build();
    final RequestBuilder request = delete("/users/me/fcm-tokens/")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(fcmTokenParams))
        .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /users/me/fcm-tokens (empty body)")
  public void deleteFCMTokenValidationFails() throws Exception {
    final RequestBuilder request = delete("/users/me/fcm-tokens/")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'FCMTokenParams' on field 'token'")));
  }

  @Test
  @DisplayName("GET /users/{id}")
  public void returnsGivenUser() throws Exception {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.of(user));
    final RequestBuilder request = get("/users/" + user.getId());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/{id} (user not found)")
  public void getUserThrowsUserNotFoundException() {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/users/" + user.getId());

    assertThrows(UserNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("GET /users/{id}/comment-count")
  public void returnsGivenUsersCommentCount() throws Exception {
    final User user = DummyUsers.user1;
    when(commentService.getCommentCountByPostedById(new ObjectId(user.getId())))
        .thenReturn(5);
    final RequestBuilder request = get("/users/" + user.getId() + "/comment-count");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(5));
  }

  @Test
  @DisplayName("GET /users/{id}/extended")
  public void returnsGivenUserExtended() throws Exception {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.of(user));
    final RequestBuilder request = get("/users/" + user.getId() + "/extended");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(7)))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").value(user.getNickname()))
        .andExpect(jsonPath("$.blockedUsers").value(equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(jsonPath("$.fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /users/{id}/extended (user not found)")
  public void getUserExtendedThrowsUserNotFoundException() {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/users/" + user.getId() + "/extended");

    assertThrows(UserNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("POST /users/{id}/reports")
  public void reportsUser() throws Exception {
    final User user = DummyUsers.user1;
    when(service.findById(user.getId())).thenReturn(Optional.of(user));
    final UserReportPostDTO userReportPostDTO = UserReportPostDTO.builder()
        .reasons(EnumSet.of(UserReportReason.HARASSING))
        .message("report message").build();
    final RequestBuilder request = post("/users/" + user.getId() + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userReportPostDTO))
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /users/{id}/reports (empty body)")
  public void postReportUserValidationFails() throws Exception {
    final User user = DummyUsers.user1;
    final RequestBuilder request = post("/users/" + user.getId() + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'userReportPostDTO' on field 'reasons'")));
  }

}
