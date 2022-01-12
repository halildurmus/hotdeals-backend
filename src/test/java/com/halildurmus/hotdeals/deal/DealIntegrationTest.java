package com.halildurmus.hotdeals.deal;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.dummy.DummyDealReports;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class DealIntegrationTest extends BaseIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @MockBean
  private SecurityService securityService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<Deal> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("comments");
    mongoTemplate.dropCollection("deals");
    mongoTemplate.dropCollection("reports");
    mongoTemplate.dropCollection("users");
  }

//  @Test
//  @DisplayName("POST /deals")
//  public void createsDeal() throws Exception {
//    final User user = mongoTemplate.insert(DummyUsers.user1);
//    when(securityService.getUser()).thenReturn(user);
//    final Deal deal = DummyDeals.deal1;
//    final RequestBuilder requestBuilder = post("/deals")
//        .accept(MediaType.APPLICATION_JSON)
//        .content(json.write(deal).getJson())
//        .contentType(MediaType.APPLICATION_JSON);
//
//    mvc.perform(requestBuilder)
//        .andExpect(status().isCreated())
//        .andExpect(content().contentType("application/json"))
//        .andExpect(jsonPath("$.*", hasSize(17)))
//        .andExpect(jsonPath("$.id").value(deal.getId()))
//        .andExpect(jsonPath("$.postedBy").value(user.getId()))
//        .andExpect(jsonPath("$.title").value(deal.getTitle()))
//        .andExpect(jsonPath("$.description").value(deal.getDescription()))
//        .andExpect(jsonPath("$.originalPrice").value(deal.getOriginalPrice()))
//        .andExpect(jsonPath("$.price").value(deal.getPrice()))
//        .andExpect(jsonPath("$.store").value(deal.getStore().toString()))
//        .andExpect(jsonPath("$.category").value(deal.getCategory()))
//        .andExpect(jsonPath("$.coverPhoto").value(deal.getCoverPhoto()))
//        .andExpect(jsonPath("$.photos", hasSize(deal.getPhotos().size())))
//        .andExpect(jsonPath("$.dealUrl").value(deal.getDealUrl()))
//        .andExpect(jsonPath("$.dealScore").value(deal.getDealScore()))
//        .andExpect(jsonPath("$.upvoters", hasSize(deal.getUpvoters().size())))
//        .andExpect(jsonPath("$.downvoters", hasSize(deal.getDownvoters().size())))
//        .andExpect(jsonPath("$.views").value(deal.getViews()))
//        .andExpect(jsonPath("$.status").value(deal.getStatus().toString()))
//        .andExpect(jsonPath("$.createdAt").isNotEmpty());
//  }

  @Test
  @DisplayName("GET /deals (returns empty)")
  public void getDealsReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = get("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals (returns 1 deal)")
  public void getDealsReturnsOneDeal() throws Exception {
    final User user = mongoTemplate.insert(DummyUsers.user3);
    when(securityService.getUser()).thenReturn(user);
    final Deal deal = mongoTemplate.insert(DummyDeals.deal1);
    final RequestBuilder requestBuilder = get("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].*", hasSize(18)))
        .andExpect(jsonPath("$.content[0].id").value(deal.getId()))
        .andExpect(jsonPath("$.content[0].postedBy").value(user.getId()))
        .andExpect(jsonPath("$.content[0].title").value(deal.getTitle()))
        .andExpect(jsonPath("$.content[0].description").value(deal.getDescription()))
        .andExpect(jsonPath("$.content[0].originalPrice").value(deal.getOriginalPrice()))
        .andExpect(jsonPath("$.content[0].price").value(deal.getPrice()))
        .andExpect(jsonPath("$.content[0].store").value(deal.getStore().toString()))
        .andExpect(jsonPath("$.content[0].category").value(deal.getCategory()))
        .andExpect(jsonPath("$.content[0].coverPhoto").value(deal.getCoverPhoto()))
        .andExpect(jsonPath("$.content[0].photos", hasSize(deal.getPhotos().size())))
        .andExpect(jsonPath("$.content[0].dealUrl").value(deal.getDealUrl()))
        .andExpect(jsonPath("$.content[0].dealScore").value(deal.getDealScore()))
        .andExpect(jsonPath("$.content[0].upvoters", hasSize(deal.getUpvoters().size())))
        .andExpect(jsonPath("$.content[0].downvoters", hasSize(deal.getDownvoters().size())))
        .andExpect(jsonPath("$.content[0].views").value(deal.getViews()))
        .andExpect(jsonPath("$.content[0].status").value(deal.getStatus().toString()))
        .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty());
  }

  @Test
  @DisplayName("POST /deals/{id}/comments")
  public void createsComment() throws Exception {
    final User user = mongoTemplate.insert(DummyUsers.user3);
    when(securityService.getUser()).thenReturn(user);
    final Deal deal = mongoTemplate.insert(DummyDeals.deal1);
    final Comment comment = DummyComments.comment1;
    comment.setDealId(new ObjectId(deal.getId()));
    final RequestBuilder requestBuilder = post("/deals/" + deal.getId() + "/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(comment))
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.dealId").value(comment.getDealId().toString()))
        .andExpect(jsonPath("$.postedBy.id").value(user.getId()))
        .andExpect(jsonPath("$.message").value(comment.getMessage()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  @DisplayName("POST /deals/{id}/reports")
  public void shouldCreateDealReportThenReturnDealReport() throws Exception {
    final User user = mongoTemplate.insert(DummyUsers.user1);
    when(securityService.getUser()).thenReturn(user);
    final Deal deal = mongoTemplate.insert(DummyDeals.deal1);
    final DealReport dealReport = DummyDealReports.dealReport1;
    final RequestBuilder requestBuilder = post("/deals/" + deal.getId() + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dealReport))
        .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(requestBuilder).andExpect(status().isCreated());
  }

}
