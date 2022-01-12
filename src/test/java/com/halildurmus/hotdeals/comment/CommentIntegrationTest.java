package com.halildurmus.hotdeals.comment;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class CommentIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("comments");
    mongoTemplate.dropCollection("users");
  }

  @Test
  @DisplayName("GET /comments (returns empty)")
  public void getCommentsReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(0)));
  }

  @Test
  @DisplayName("GET /comments (returns 1 comment)")
  public void getCommentsReturnsOneComment() throws Exception {
    final User user = mongoTemplate.insert(DummyUsers.user1);
    final Comment comment = DummyComments.comment1;
    comment.setPostedBy(user);
    mongoTemplate.insert(comment);
    final RequestBuilder requestBuilder = get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(1)))
        .andExpect(jsonPath("$._embedded.comments[0].*", hasSize(7)))
        .andExpect(jsonPath("$._embedded.comments[0].postedBy.id").value(user.getId()))
        .andExpect(jsonPath("$._embedded.comments[0].dealId").value(comment.getDealId().toString()))
        .andExpect(jsonPath("$._embedded.comments[0].message").value(comment.getMessage()))
        .andExpect(jsonPath("$._embedded.comments[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$._embedded.comments[0].updatedAt").isNotEmpty());
  }

}
