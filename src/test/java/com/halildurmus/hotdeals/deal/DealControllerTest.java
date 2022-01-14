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
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.deal.DTO.DealPostDTO;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.deal.es.EsDealService;
import com.halildurmus.hotdeals.exception.DealNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import com.halildurmus.hotdeals.report.deal.DTO.DealReportPostDTO;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.deal.DealReportService;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.util.NestedServletException;

@Import({DealController.class, MapStructMapperImpl.class})
public class DealControllerTest extends BaseControllerUnitTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired
  private JacksonTester<DealPostDTO> json;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private CommentService commentService;

  @MockBean
  private DealReportService dealReportService;

  @MockBean
  private DealService service;

  @MockBean
  private EsDealService esDealService;

  @Test
  @DisplayName("GET /deals (returns empty array)")
  public void getDealsReturnsEmptyArray() throws Exception {
    when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
    final RequestBuilder request = get("/deals");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals (returns 1 deal)")
  public void getDealsReturnsOneDeal() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final Page<Deal> pagedDeals = new PageImpl<>(List.of(deal));
    when(service.findAll(any(Pageable.class))).thenReturn(pagedDeals);
    final RequestBuilder request = get("/deals");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].*", hasSize(18)))
        .andExpect(jsonPath("$.content[0].id").value(deal.getId()))
        .andExpect(jsonPath("$.content[0].postedBy").value(deal.getPostedBy().toString()))
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
        .andExpect(jsonPath("$.content[0].createdAt").value(deal.getCreatedAt().toString()))
        .andExpect(jsonPath("$.content[0].updatedAt").value(deal.getUpdatedAt().toString()));
  }

  @Test
  @DisplayName("GET /deals/count/byPostedBy")
  public void returnsNumberOfDealsThatGivenUserPosted() throws Exception {
    final String id = DummyUsers.user1.getId();
    when(service.countDealsByPostedBy(new ObjectId(id))).thenReturn(3);
    final RequestBuilder request = get("/deals/count/byPostedBy?postedBy=" + id);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(3));
  }

  @Test
  @DisplayName("GET /deals/count/byPostedBy (invalid id)")
  public void getDealsCountByPostedByThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = get("/deals/count/byPostedBy?postedBy=" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("GET /deals/count/byStore")
  public void returnsNumberOfDealsThatGivenStoreHas() throws Exception {
    final String id = DummyStores.store1.getId();
    when(service.countDealsByStore(new ObjectId(id))).thenReturn(4);
    final RequestBuilder request = get("/deals/count/byStore?storeId=" + id);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(4));
  }

  @Test
  @DisplayName("GET /deals/count/byStore (invalid id)")
  public void getDealsCountByStoreThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = get("/deals/count/byStore?storeId=" + id);

    assertThrows(ConstraintViolationException.class, () -> {
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
    final Deal deal = DummyDeals.deal1;
    when(service.getDealsByCategory(anyString(), any(Pageable.class))).thenReturn(Page.empty());
    final RequestBuilder request = get("/deals/search/byCategory?category=" + deal.getCategory());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/byCategory?category={category} (returns 1 deal)")
  public void findsDealsByCategory() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final Page<Deal> pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getDealsByCategory(anyString(), any(Pageable.class))).thenReturn(pagedDeals);
    final RequestBuilder request = get("/deals/search/byCategory?category=" + deal.getCategory());

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
    final Deal deal = DummyDeals.deal1;
    when(service.getDealsByStoreId(any(ObjectId.class), any(Pageable.class))).thenReturn(
        Page.empty());
    final RequestBuilder request = get(
        "/deals/search/byStoreId?storeId=" + deal.getStore());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/byStoreId?storeId={id} (returns 1 deal)")
  public void findsDealsByStoreId() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final Page<Deal> pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getDealsByStoreId(any(ObjectId.class), any(Pageable.class))).thenReturn(
        pagedDeals);
    final RequestBuilder request = get(
        "/deals/search/byStoreId?storeId=" + deal.getStore());

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
    final RequestBuilder request = get("/deals/search/latestActive");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/latestActive (returns 1 deal)")
  public void returnsLatestActiveDeals() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final Page<Deal> pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getLatestActiveDeals(any(Pageable.class))).thenReturn(pagedDeals);
    final RequestBuilder request = get("/deals/search/latestActive");

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
    final RequestBuilder request = get("/deals/search/mostLikedActive");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals/search/mostLikedActive (returns 1 deal)")
  public void returnsMostLikedActiveDeals() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final Page<Deal> pagedDeals = new PageImpl<>(List.of(deal));
    when(service.getMostLikedActiveDeals(any(Pageable.class))).thenReturn(pagedDeals);
    final RequestBuilder request = get("/deals/search/mostLikedActive");

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
    final RequestBuilder request = get("/deals/searches");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MissingServletRequestParameterException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains(
                    "Required request parameter 'query' for method parameter type String is not present")));
  }

  @Test
  @DisplayName("GET /deals/searches (unsupported sortBy)")
  public void getSearchesValidationFailsDueToUnsupportedSortBy() throws Exception {
    final RequestBuilder request = get("/deals/searches?query=max&sortBy=invalid");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(
            equalTo("Invalid sortBy! Supported sortBy values => [createdAt, price]")));
  }

  @Test
  @DisplayName("GET /deals/searches (unsupported order)")
  public void getSearchesValidationFailsDueToUnsupportedOrder() throws Exception {
    final RequestBuilder request = get("/deals/searches?query=max&order=invalid");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(
            equalTo("Invalid order! Supported order values => [asc, desc]")));
  }

  @Test
  @DisplayName("GET /deals/suggestions (missing query)")
  public void getSuggestionsValidationFailsDueToMissingQuery() throws Exception {
    final RequestBuilder request = get("/deals/suggestions");

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MissingServletRequestParameterException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains(
                    "Required request parameter 'query' for method parameter type String is not present")));
  }

  @Test
  @DisplayName("GET /deals/suggestions (invalid query length)")
  public void getSuggestionsValidationFailsDueToInvalidQueryLength() {
    final RequestBuilder request = get("/deals/suggestions?query=a");

    assertThrows(ConstraintViolationException.class, () -> {
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
    final Deal deal = DummyDeals.deal1;
    when(service.findById(deal.getId())).thenReturn(Optional.of(deal));
    final RequestBuilder request = get("/deals/" + deal.getId());

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
    final Deal deal = DummyDeals.deal1;
    when(service.findById(deal.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/deals/" + deal.getId());

    assertThrows(DealNotFoundException.class, () -> {
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
    final Deal deal = DummyDeals.deal1;
    final DealPostDTO dealPostDTO = mapStructMapper.dealToDealPostDTO(deal);
    when(service.create(any(Deal.class))).thenReturn(deal);
    final RequestBuilder request = post("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(dealPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
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
    final RequestBuilder request = post("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'title'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'description'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'originalPrice'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'price'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'category'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'store'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'coverPhoto'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'dealUrl'")));
  }

  @Test
  @DisplayName("PATCH /deals/{id} (empty body)")
  public void patchDealValidationFails() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = patch("/deals/" + id)
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
  @DisplayName("PATCH /deals/{id}")
  public void patchesGivenDealsStatus() throws Exception {
    final Deal deal = DummyDeals.deal1;
    deal.setStatus(DealStatus.EXPIRED);
    when(service.patch(anyString(), any(JsonPatch.class))).thenReturn(deal);
    final String jsonPatch =
        "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"EXPIRED\"}]";
    final RequestBuilder request = patch("/deals/" + deal.getId())
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
    final Deal deal = DummyDeals.deal1;
    final DealPostDTO dealPostDTO = mapStructMapper.dealToDealPostDTO(deal);
    when(service.findById(anyString())).thenReturn(Optional.of(deal));
    when(service.update(any(Deal.class))).thenReturn(deal);
    final RequestBuilder request = put("/deals/" + deal.getId())
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
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = put("/deals/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'title'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'description'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'originalPrice'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'price'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'category'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'store'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'coverPhoto'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealPostDTO' on field 'dealUrl'")));
  }

  @Test
  @DisplayName("DELETE /deals/{id}")
  public void deletesGivenDeal() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = delete("/deals/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /deals/{id} (invalid id)")
  public void deleteDealThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/deals/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
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
    final Deal deal = DummyDeals.deal1;
    when(commentService.getCommentsByDealId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(Page.empty());
    final RequestBuilder request = get("/deals/" + deal.getId() + "/comments");

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
    final Deal deal = DummyDeals.deal1;
    final Comment comment = DummyComments.comment1;
    final Page<Comment> pagedComments = new PageImpl<>(List.of(comment));
    when(commentService.getCommentsByDealId(any(ObjectId.class), any(Pageable.class)))
        .thenReturn(pagedComments);
    final RequestBuilder request = get("/deals/" + deal.getId() + "/comments");

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
    final String id = DummyDeals.deal1.getId();
    when(commentService.getCommentCountByDealId(any(ObjectId.class))).thenReturn(3);
    final RequestBuilder request = get("/deals/" + id + "/comment-count");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(3));
  }

  @Test
  @DisplayName("GET /deals/{id}/comment-count (invalid id)")
  public void getDealCommentCountThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = get("/deals/" + id + "/comment-count");

    assertThrows(ConstraintViolationException.class, () -> {
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
    final String id = DummyDeals.deal1.getId();
    final Comment comment = DummyComments.comment1;
    final CommentPostDTO commentPostDTO = mapStructMapper.commentToCommentPostDTO(comment);
    when(service.findById(anyString())).thenReturn(Optional.of(DummyDeals.deal1));
    when(commentService.save(any(Comment.class))).thenReturn(comment);
    final RequestBuilder request = post("/deals/" + id + "/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(commentPostDTO))
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
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
    final String id = "23478fsf234";
    final Comment comment = DummyComments.comment1;
    final CommentPostDTO commentPostDTO = mapStructMapper.commentToCommentPostDTO(comment);
    final RequestBuilder request = post("/deals/" + id + "/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(commentPostDTO))
        .contentType(MediaType.APPLICATION_JSON);

    assertThrows(ConstraintViolationException.class, () -> {
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
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = post("/deals/" + id + "/comments")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'commentPostDTO' on field 'message'")));
  }

  @Test
  @DisplayName("POST /deals/{id}/reports")
  public void createsReport() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final DealReport dealReport = DummyDealReports.dealReport1;
    final DealReportPostDTO dealReportPostDTO = mapStructMapper.dealReportToDealReportPostDTO(
        dealReport);
    when(service.findById(anyString())).thenReturn(Optional.of(DummyDeals.deal1));
    final RequestBuilder request = post("/deals/" + id + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dealReportPostDTO))
        .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(request).andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /deals/{id}/reports (invalid id)")
  public void postReportThrowsConstraintViolationException() throws JsonProcessingException {
    final String id = "23478fsf234";
    final DealReport dealReport = DummyDealReports.dealReport1;
    final DealReportPostDTO dealReportPostDTO = mapStructMapper.dealReportToDealReportPostDTO(
        dealReport);
    final RequestBuilder request = post("/deals/" + id + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dealReportPostDTO))
        .contentType(MediaType.APPLICATION_JSON);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("POST /deals/{id}/reports (empty body)")
  public void postReportValidationFailsDueToEmptyBody() throws Exception {
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = post("/deals/" + id + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealReportPostDTO' on field 'reasons'")));
  }

  @Test
  @DisplayName("PUT /deals/{id}/votes")
  public void savesDealVote() throws Exception {
    final Deal deal = DummyDeals.deal1;
    final DealVote dealVote = DealVote.builder().voteType(DealVoteType.UP).build();
    when(service.vote(anyString(), any(DealVoteType.class))).thenReturn(deal);
    final RequestBuilder request = put("/deals/" + deal.getId() + "/votes")
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
    final String id = "23478fsf234";
    final DealVote dealVote = DealVote.builder().voteType(DealVoteType.UP).build();
    final RequestBuilder request = put("/deals/" + id + "/votes")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dealVote))
        .contentType(MediaType.APPLICATION_JSON);

    assertThrows(ConstraintViolationException.class, () -> {
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
    final String id = DummyDeals.deal1.getId();
    final DealVote dealVote = DealVote.builder().voteType(DealVoteType.UNVOTE).build();
    final RequestBuilder request = put("/deals/" + id + "/votes")
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
    final String id = DummyDeals.deal1.getId();
    final RequestBuilder request = put("/deals/" + id + "/votes")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'dealVote' on field 'voteType'")));
  }

  @Test
  @DisplayName("DELETE /deals/{id}/votes")
  public void deletesVoteFromDeal() throws Exception {
    final Deal deal = DummyDeals.deal1;
    when(service.vote(anyString(), any(DealVoteType.class))).thenReturn(deal);
    final RequestBuilder request = delete("/deals/" + deal.getId() + "/votes");
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /deals/{id}/votes (invalid id)")
  public void deleteVoteThrowsConstraintViolationException() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/deals/" + id + "/votes");

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

}
