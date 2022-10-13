package com.halildurmus.hotdeals.deal;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.comment.CommentRepository;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.deal.es.EsDealRepository;
import com.halildurmus.hotdeals.report.deal.DealReportRepository;
import com.halildurmus.hotdeals.report.dummy.DummyCommentReports;
import com.halildurmus.hotdeals.report.dummy.DummyDealReports;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.UserRepository;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

public class DealIntegrationTest extends BaseIntegrationTest {

  @Autowired private CommentRepository commentRepository;

  @Autowired private DealReportRepository dealReportRepository;

  @Autowired private DealRepository dealRepository;

  @Autowired private DealService dealService;

  @Autowired private EsDealRepository esDealRepository;

  @Autowired private JacksonTester<Deal> json;

  @Autowired private MockMvc mvc;

  private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

  @MockBean private SecurityService securityService;

  @Autowired private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    commentRepository.deleteAll();
    dealRepository.deleteAll();
    dealReportRepository.deleteAll();
    esDealRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("POST /deals")
  public void createsDeal() throws Exception {
    var user = userRepository.save(DummyUsers.user1);
    when(securityService.getUser()).thenReturn(user);
    var deal = DummyDeals.deal1;
    var request =
        post("/deals")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(deal).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.postedBy").value(user.getId()))
        .andExpect(jsonPath("$.title").value(deal.getTitle()))
        .andExpect(jsonPath("$.description").value(deal.getDescription()))
        .andExpect(jsonPath("$.originalPrice").value(deal.getOriginalPrice()))
        .andExpect(jsonPath("$.price").value(deal.getPrice()))
        .andExpect(jsonPath("$.store").value(deal.getStore().toString()))
        .andExpect(jsonPath("$.category").value(deal.getCategory()))
        .andExpect(jsonPath("$.coverPhoto").value(deal.getCoverPhoto()))
        .andExpect(jsonPath("$.photos", hasSize(deal.getPhotos().size())))
        .andExpect(jsonPath("$.dealUrl").value(deal.getDealUrl()))
        .andExpect(jsonPath("$.dealScore").value(deal.getDealScore()))
        .andExpect(jsonPath("$.upvoters", hasSize(deal.getUpvoters().size())))
        .andExpect(jsonPath("$.downvoters", hasSize(deal.getDownvoters().size())))
        .andExpect(jsonPath("$.views").value(deal.getViews()))
        .andExpect(jsonPath("$.status").value(deal.getStatus().toString()))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  @DisplayName("GET /deals (returns empty)")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void getDealsReturnsEmptyArray() throws Exception {
    var request =
        get("/deals").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals (returns 1 deal)")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void getDealsReturnsOneDeal() throws Exception {
    var user = userRepository.save(DummyUsers.user3);
    when(securityService.getUser()).thenReturn(user);
    var deal = dealRepository.save(DummyDeals.deal1);
    var request =
        get("/deals").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(18)))
        .andExpect(jsonPath("$[0].id").value(deal.getId()))
        .andExpect(jsonPath("$[0].postedBy").value(user.getId()))
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
        .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$[0].updatedAt").isNotEmpty());
  }

  @Test
  @DisplayName("GET /deals/suggestions (returns empty)")
  public void getSuggestionsReturnsEmptyArray() throws Exception {
    var request =
        get("/deals/suggestions?query=invalid")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/suggestions (returns 1 suggestion)")
  public void getSuggestionsReturnsOneSuggestion() throws Exception {
    var user = userRepository.save(DummyUsers.user3);
    when(securityService.getUser()).thenReturn(user);
    var deal = dealService.create(DummyDeals.deal1);
    var request =
        get("/deals/suggestions?query=book")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(deal.getId()))
        .andExpect(jsonPath("$[0].title").value(deal.getTitle()));
  }

  @Test
  @DisplayName("POST /deals/{id}/comments")
  public void createsComment() throws Exception {
    var user = userRepository.save(DummyUsers.user3);
    when(securityService.getUser()).thenReturn(user);
    var deal = dealRepository.save(DummyDeals.deal1);
    var comment = DummyComments.comment1;
    comment.setDealId(new ObjectId(deal.getId()));
    var request =
        post("/deals/" + deal.getId() + "/comments")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(comment))
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
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
  @DisplayName("POST /deals/{id}/comments/{commentId}/reports")
  public void shouldCreateCommentReportThenReturnCommentReport() throws Exception {
    var user = userRepository.save(DummyUsers.user1);
    when(securityService.getUser()).thenReturn(user);
    var deal = dealRepository.save(DummyDeals.deal1);
    var dummyComment = DummyComments.comment1;
    dummyComment.setPostedBy(user);
    dummyComment.setDealId(new ObjectId(deal.getId()));
    var comment = commentRepository.save(dummyComment);
    var commentReport = DummyCommentReports.commentReport1;
    var request =
        post("/deals/" + deal.getId() + "/comments/" + comment.getId() + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentReport))
            .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /deals/{id}/reports")
  public void shouldCreateDealReportThenReturnDealReport() throws Exception {
    var user = userRepository.save(DummyUsers.user1);
    when(securityService.getUser()).thenReturn(user);
    var deal = dealRepository.save(DummyDeals.deal1);
    var dealReport = DummyDealReports.dealReport1;
    var request =
        post("/deals/" + deal.getId() + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealReport))
            .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isCreated());
  }
}
