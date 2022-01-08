package com.halildurmus.hotdeals.comment;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CommentIntegrationTest extends BaseIntegrationTest {

  static User fakeUser = User.builder().id("607345b0eeeee1452898128b").build();

  @MockBean
  SecurityService securityService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<Comment> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("comments");
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /comments")
  public void shouldCreateCommentThenReturnComment() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    final Deal deal = mongoTemplate.insert(DummyDeals.deal1);
    final Comment comment = DummyComments.comment1;
    comment.setDealId(new ObjectId(deal.getId()));

    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(comment).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.dealId").value(DummyComments.comment1.getDealId().toString()))
        .andExpect(jsonPath("$.postedBy").value(fakeUser.getId()))
        .andExpect(jsonPath("$.message").value(DummyComments.comment1.getMessage()));
  }

  @Test
  @DisplayName("GET /comments (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(0)));
  }

  @Test
  @DisplayName("GET /comments (returns 1 comment)")
  public void shouldReturnOneCommentInArray() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    mongoTemplate.insert(DummyComments.comment1);

    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(1)))
        .andExpect(
            jsonPath("$._embedded.comments[0].dealId").value(
                DummyComments.comment1.getDealId().toString()))
        .andExpect(jsonPath("$._embedded.comments[0].postedBy").value(fakeUser.getId()))
        .andExpect(
            jsonPath("$._embedded.comments[0].message").value(DummyComments.comment1.getMessage()));
  }
}
