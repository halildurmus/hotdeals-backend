package com.halildurmus.hotdeals.comment;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.config.FirebaseConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureJsonTesters
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerIntegrationTest {

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
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyComments.comment1).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.postedBy").value(not(empty())))
        .andExpect(jsonPath("$.message").value(DummyComments.comment1.getMessage()));
  }

  @Test
  @DisplayName("GET /comments (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(0)));
  }

  @Test
  @DisplayName("GET /comments (returns 1 comment)")
  public void shouldReturnOneCommentInArray() throws Exception {
    mongoTemplate.insert(DummyComments.comment1);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/comments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comments", hasSize(1)))
        .andExpect(jsonPath("$._embedded.comments[0].postedBy").value(not(empty())))
        .andExpect(jsonPath("$._embedded.comments[0].message").value(DummyComments.comment1.getMessage()));
  }
}
