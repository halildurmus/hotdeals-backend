package com.halildurmus.hotdeals.deal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.halildurmus.hotdeals.BaseControllerUnitTest;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.CommentService;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.deal.dto.DealPostDTO;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import com.halildurmus.hotdeals.report.comment.CommentReportService;
import com.halildurmus.hotdeals.report.deal.DealReportService;
import com.halildurmus.hotdeals.report.dummy.DummyCommentReports;
import com.halildurmus.hotdeals.report.dummy.DummyDealReports;
import com.halildurmus.hotdeals.store.dummy.DummyStores;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.util.NestedServletException;

@Import({DealController.class, MapStructMapperImpl.class})
public class DealControllerTest extends BaseControllerUnitTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired private JacksonTester<DealPostDTO> json;

  @Autowired private MockMvc mvc;

  @MockBean private CommentService commentService;

  @MockBean private CommentReportService commentReportService;

  @MockBean private DealReportService dealReportService;

  @MockBean private DealService service;

  @MockBean private EsDealService esDealService;

  @Test
  @DisplayName("GET /deals (returns empty array)")
  public void getDealsReturnsEmptyArray() throws Exception {
    when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/deals");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals (returns 1 deal)")
  public void getDealsReturnsOneDeal() throws Exception {
    var deal = DummyDeals.deal1;
    var pagedDeals = new PageImpl<>(List.of(deal));
    when(service.findAll(any(Pageable.class))).thenReturn(pagedDeals);
    var request = get("/deals");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(18)))
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
        .andExpect(jsonPath("$[0].createdAt").value(deal.getCreatedAt().toString()))
        .andExpect(jsonPath("$[0].updatedAt").value(deal.getUpdatedAt().toString()));
  }

  @Test
  @DisplayName("GET /deals/count/byPostedBy")
  public void returnsNumberOfDealsThatGivenUserPosted() throws Exception {
    var id = DummyUsers.user1.getId();
    when(service.countDealsByPostedBy(new ObjectId(id))).thenReturn(3);
    var request = get("/deals/count/byPostedBy?postedBy=" + id);

    mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$").value(3));
  }

  @Test
  @DisplayName("GET /deals/count/byPostedBy (invalid id)")
  public void getDealsCountByPostedByThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = get("/deals/count/byPostedBy?postedBy=" + id);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("GET /deals/count/byStoreId")
  public void returnsNumberOfDealsThatGivenStoreHas() throws Exception {
    var id = DummyStores.store1.getId();
    when(service.countDealsByStore(new ObjectId(id))).thenReturn(4);
    var request = get("/deals/count/byStoreId?storeId=" + id);

    mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$").value(4));
  }

  @Test
  @DisplayName("GET /deals/count/byStoreId (invalid id)")
  public void getDealsCountByStoreThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = get("/deals/count/byStoreId?storeId=" + id);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("GET /deals/search/byCategory?category={category} (returns empty array)")
  public void findDealsByCategoryReturnsEmptyArray() throws Exception {
    var deal = DummyDeals.deal1;
    when(service.getDealsByCategory(anyString(), any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/deals/search/byCategory?category=" + deal.getCategory());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/byCategory?category={category} (returns 1 deal)")
  public void findsDealsByCategory() throws Exception {
    var deal = DummyDeals.deal1;
    var pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getDealsByCategory(anyString(), any(Pageable.class))).thenReturn(pagedDeals);
    var request = get("/deals/search/byCategory?category=" + deal.getCategory());

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
  @DisplayName("GET /deals/search/byStoreId?storeId={id} (returns empty array)")
  public void findDealsByStoreIdReturnsEmptyArray() throws Exception {
    var deal = DummyDeals.deal1;
    when(service.getDealsByStoreId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(Page.empty());
    var request = get("/deals/search/byStoreId?storeId=" + deal.getStore());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/byStoreId?storeId={id} (returns 1 deal)")
  public void findsDealsByStoreId() throws Exception {
    var deal = DummyDeals.deal1;
    var pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getDealsByStoreId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(pagedDeals);
    var request = get("/deals/search/byStoreId?storeId=" + deal.getStore());

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
  @DisplayName("GET /deals/search/latestActive (returns empty array)")
  public void getLatestActiveDealsReturnsEmptyArray() throws Exception {
    when(service.getLatestActiveDeals(any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/deals/search/latestActive");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/latestActive (returns 1 deal)")
  public void returnsLatestActiveDeals() throws Exception {
    var deal = DummyDeals.deal1;
    var pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getLatestActiveDeals(any(Pageable.class))).thenReturn(pagedDeals);
    var request = get("/deals/search/latestActive");

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
  @DisplayName("GET /deals/search/mostLikedActive (returns empty array)")
  public void getMostLikedActiveDealsReturnsEmptyArray() throws Exception {
    when(service.getMostLikedActiveDeals(any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/deals/search/mostLikedActive");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/mostLikedActive (returns 1 deal)")
  public void returnsMostLikedActiveDeals() throws Exception {
    var deal = DummyDeals.deal1;
    var pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getMostLikedActiveDeals(any(Pageable.class))).thenReturn(pagedDeals);
    var request = get("/deals/search/mostLikedActive");

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
  @DisplayName("GET /deals/searches (missing query)")
  public void getSearchesValidationFailsDueToMissingQuery() throws Exception {
    var request = get("/deals/searches");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException()
                        instanceof MissingServletRequestParameterException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Required request parameter 'query' for method parameter type String is not present")));
  }

  @Test
  @DisplayName("GET /deals/searches (unsupported sortBy)")
  public void getSearchesValidationFailsDueToUnsupportedSortBy() throws Exception {
    var request = get("/deals/searches?query=max&sortBy=invalid");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            status()
                .reason(equalTo("Invalid sortBy! Supported sortBy values => [createdAt, price]")));
  }

  @Test
  @DisplayName("GET /deals/searches (unsupported order)")
  public void getSearchesValidationFailsDueToUnsupportedOrder() throws Exception {
    var request = get("/deals/searches?query=max&order=invalid");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            status().reason(equalTo("Invalid order! Supported order values => [asc, desc]")));
  }

  @Test
  @DisplayName("GET /deals/suggestions (missing query)")
  public void getSuggestionsValidationFailsDueToMissingQuery() throws Exception {
    var request = get("/deals/suggestions");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException()
                        instanceof MissingServletRequestParameterException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Required request parameter 'query' for method parameter type String is not present")));
  }

  @Test
  @DisplayName("GET /deals/suggestions (invalid query length)")
  public void getSuggestionsValidationFailsDueToInvalidQueryLength() {
    var request = get("/deals/suggestions?query=a");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("GET /deals/{id}")
  public void returnsGivenDeal() throws Exception {
    var deal = DummyDeals.deal1;
    when(service.findById(deal.getId())).thenReturn(Optional.of(deal));
    var request = get("/deals/" + deal.getId());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").value(deal.getId()))
        .andExpect(jsonPath("$.postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /deals/{id} (deal not found)")
  public void getDealThrowsDealNotFoundException() {
    var deal = DummyDeals.deal1;
    when(service.findById(deal.getId())).thenReturn(Optional.empty());
    var request = get("/deals/" + deal.getId());

    assertThrows(
        DealNotFoundException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /deals")
  public void createsDeal() throws Exception {
    var deal = DummyDeals.deal1;
    var dealPostDTO = mapStructMapper.dealToDealPostDTO(deal);
    when(service.create(any(Deal.class))).thenReturn(deal);
    var request =
        post("/deals")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(dealPostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").value(deal.getId()))
        .andExpect(jsonPath("$.postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("POST /deals (empty body)")
  public void postDealValidationFails() throws Exception {
    var request =
        post("/deals")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'title'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'description'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'originalPrice'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'price'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'category'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'store'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'coverPhoto'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'dealUrl'")));
  }

  @Test
  @DisplayName("PATCH /deals/{id} (empty body)")
  public void patchDealValidationFails() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request =
        patch("/deals/" + id)
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType("application/json-patch+json");

    mvc.perform(request)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof HttpMessageNotReadableException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Cannot deserialize value of type `java.util.ArrayList<com.github.fge.jsonpatch.JsonPatchOperation>`")));
  }

  @Test
  @DisplayName("PATCH /deals/{id}")
  public void patchesGivenDealsStatus() throws Exception {
    var deal = DummyDeals.deal1;
    deal.setStatus(DealStatus.EXPIRED);
    when(service.patch(anyString(), any(JsonPatch.class))).thenReturn(deal);
    var jsonPatch = "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"EXPIRED\"}]";
    var request =
        patch("/deals/" + deal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .content(jsonPatch)
            .contentType("application/json-patch+json");

    mvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").value(deal.getId()))
        .andExpect(jsonPath("$.postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("PUT /deals/{id}")
  public void updatesGivenDeal() throws Exception {
    var deal = DummyDeals.deal1;
    var dealPostDTO = mapStructMapper.dealToDealPostDTO(deal);
    when(service.findById(anyString())).thenReturn(Optional.of(deal));
    when(service.update(any(Deal.class))).thenReturn(deal);
    var request =
        put("/deals/" + deal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(dealPostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").value(deal.getId()))
        .andExpect(jsonPath("$.postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("PUT /deals/{id} (empty body)")
  public void putDealValidationFails() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request =
        put("/deals/" + id)
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'title'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'description'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'originalPrice'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'price'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'category'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'store'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'coverPhoto'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealPostDTO' on field 'dealUrl'")));
  }

  @Test
  @DisplayName("DELETE /deals/{id}")
  public void deletesGivenDeal() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request = delete("/deals/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /deals/{id} (invalid id)")
  public void deleteDealThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = delete("/deals/" + id);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("GET /deals/{id}/comments (returns empty array)")
  public void getDealCommentsReturnsEmptyArray() throws Exception {
    var deal = DummyDeals.deal1;
    when(commentService.getCommentsByDealId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(Page.empty());
    var request = get("/deals/" + deal.getId() + "/comments");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.count").value(0))
        .andExpect(jsonPath("$.comments", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/{id}/comments (returns 1 comment)")
  public void getDealCommentsReturnsOneComment() throws Exception {
    var deal = DummyDeals.deal1;
    var comment = DummyComments.comment1;
    var pagedComments = new PageImpl<>(List.of(comment));
    when(commentService.getCommentsByDealId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(pagedComments);
    var request = get("/deals/" + deal.getId() + "/comments");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.count").value(1))
        .andExpect(jsonPath("$.comments", hasSize(1)))
        .andExpect(jsonPath("$.comments[0].*", hasSize(5)))
        .andExpect(jsonPath("$.comments[0].id").value(comment.getId()))
        .andExpect(jsonPath("$.comments[0].postedBy.id").value(comment.getPostedBy().getId()))
        .andExpect(jsonPath("$.comments[0].dealId").value(comment.getDealId().toString()))
        .andExpect(jsonPath("$.comments[0].message").value(comment.getMessage()))
        .andExpect(jsonPath("$.comments[0].createdAt").value(comment.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("GET /deals/{id}/comment-count")
  public void returnsNumberOfCommentsThatGivenDealHas() throws Exception {
    var id = DummyDeals.deal1.getId();
    when(commentService.getCommentCountByDealId(any(ObjectId.class))).thenReturn(3);
    var request = get("/deals/" + id + "/comment-count");

    mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$").value(3));
  }

  @Test
  @DisplayName("GET /deals/{id}/comment-count (invalid id)")
  public void getDealCommentCountThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = get("/deals/" + id + "/comment-count");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /deals/{id}/comments")
  public void createsComment() throws Exception {
    var id = DummyDeals.deal1.getId();
    var comment = DummyComments.comment1;
    var commentPostDTO = mapStructMapper.commentToCommentPostDTO(comment);
    when(service.findById(anyString())).thenReturn(Optional.of(DummyDeals.deal1));
    when(commentService.save(any(Comment.class))).thenReturn(comment);
    var request =
        post("/deals/" + id + "/comments")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentPostDTO))
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.id").value(comment.getId()))
        .andExpect(jsonPath("$.postedBy.id").value(comment.getPostedBy().getId()))
        .andExpect(jsonPath("$.dealId").value(comment.getDealId().toString()))
        .andExpect(jsonPath("$.message").value(comment.getMessage()))
        .andExpect(jsonPath("$.createdAt").value(comment.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("POST /deals/{id}/comments (invalid id)")
  public void postCommentThrowsConstraintViolationException() throws JsonProcessingException {
    var id = "23478fsf234";
    var comment = DummyComments.comment1;
    var commentPostDTO = mapStructMapper.commentToCommentPostDTO(comment);
    var request =
        post("/deals/" + id + "/comments")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentPostDTO))
            .contentType(MediaType.APPLICATION_JSON);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /deals/{id}/comments (empty body)")
  public void postCommentValidationFailsDueToEmptyBody() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request =
        post("/deals/" + id + "/comments")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'commentPostDTO' on field 'message'")));
  }

  @Test
  @DisplayName("POST /deals/{dealId}/comments/{commentId}/reports ")
  public void createsCommentReport() throws Exception {
    var dealId = DummyDeals.deal1.getId();
    var commentId = DummyComments.comment1.getId();
    var commentReport = DummyCommentReports.commentReport1;
    var commentReportPostDTO = mapStructMapper.commentReportToCommentReportPostDTO(commentReport);
    when(service.findById(anyString())).thenReturn(Optional.of(DummyDeals.deal1));
    when(commentService.findById(anyString())).thenReturn(Optional.of(DummyComments.comment1));
    var request =
        post("/deals/" + dealId + "/comments/" + commentId + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentReportPostDTO))
            .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /deals/{dealId}/comments/{commentId}/reports (invalid comment id)")
  public void postCommentReportThrowsConstraintViolationException() throws JsonProcessingException {
    var dealId = DummyDeals.deal1.getId();
    var commentId = "23478fsf234";
    var commentReport = DummyCommentReports.commentReport1;
    var commentReportPostDTO = mapStructMapper.commentReportToCommentReportPostDTO(commentReport);
    var request =
        post("/deals/" + dealId + "/comments/" + commentId + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentReportPostDTO))
            .contentType(MediaType.APPLICATION_JSON);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /deals/{dealId}/comments/{commentId}/reports (empty body)")
  public void postCommentReportValidationFailsDueToEmptyBody() throws Exception {
    var dealId = DummyDeals.deal1.getId();
    var commentId = DummyComments.comment1.getId();
    var request =
        post("/deals/" + dealId + "/comments/" + commentId + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Field error in object 'commentReportPostDTO' on field 'reasons'")));
  }

  @Test
  @DisplayName("POST /deals/{id}/reports")
  public void createsDealReport() throws Exception {
    var id = DummyDeals.deal1.getId();
    var dealReport = DummyDealReports.dealReport1;
    var dealReportPostDTO = mapStructMapper.dealReportToDealReportPostDTO(dealReport);
    when(service.findById(anyString())).thenReturn(Optional.of(DummyDeals.deal1));
    var request =
        post("/deals/" + id + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealReportPostDTO))
            .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /deals/{id}/reports (invalid id)")
  public void postDealReportThrowsConstraintViolationException() throws JsonProcessingException {
    var id = "23478fsf234";
    var dealReport = DummyDealReports.dealReport1;
    var dealReportPostDTO = mapStructMapper.dealReportToDealReportPostDTO(dealReport);
    var request =
        post("/deals/" + id + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealReportPostDTO))
            .contentType(MediaType.APPLICATION_JSON);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /deals/{id}/reports (empty body)")
  public void postDealReportValidationFailsDueToEmptyBody() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request =
        post("/deals/" + id + "/reports")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealReportPostDTO' on field 'reasons'")));
  }

  @Test
  @DisplayName("PUT /deals/{id}/votes")
  public void savesDealVote() throws Exception {
    var deal = DummyDeals.deal1;
    var dealVote = DealVote.builder().voteType(DealVoteType.UP).build();
    when(service.vote(anyString(), any(DealVoteType.class))).thenReturn(deal);
    var request =
        put("/deals/" + deal.getId() + "/votes")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealVote))
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(17)))
        .andExpect(jsonPath("$.id").value(deal.getId()))
        .andExpect(jsonPath("$.postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.createdAt").value(deal.getCreatedAt().toString()));
  }

  @Test
  @DisplayName("PUT /deals/{id}/votes (invalid id)")
  public void voteDealThrowsConstraintViolationException() throws JsonProcessingException {
    var id = "23478fsf234";
    var dealVote = DealVote.builder().voteType(DealVoteType.UP).build();
    var request =
        put("/deals/" + id + "/votes")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealVote))
            .contentType(MediaType.APPLICATION_JSON);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("PUT /deals/{id}/votes (invalid vote type (DealVoteType.UNVOTE))")
  public void voteDealValidationFailsDueToInvalidVoteType() throws Exception {
    var id = DummyDeals.deal1.getId();
    var dealVote = DealVote.builder().voteType(DealVoteType.UNVOTE).build();
    var request =
        put("/deals/" + id + "/votes")
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dealVote))
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            status().reason(equalTo("To unvote the deal you need to make a DELETE request!")));
  }

  @Test
  @DisplayName("PUT /deals/{id}/votes (empty body)")
  public void voteDealValidationFailsDueToEmptyBody() throws Exception {
    var id = DummyDeals.deal1.getId();
    var request =
        put("/deals/" + id + "/votes")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'dealVote' on field 'voteType'")));
  }

  @Test
  @DisplayName("DELETE /deals/{id}/votes")
  public void deletesVoteFromDeal() throws Exception {
    var deal = DummyDeals.deal1;
    when(service.vote(anyString(), any(DealVoteType.class))).thenReturn(deal);
    var request = delete("/deals/" + deal.getId() + "/votes");
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /deals/{id}/votes (invalid id)")
  public void deleteVoteThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = delete("/deals/" + id + "/votes");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }
}
