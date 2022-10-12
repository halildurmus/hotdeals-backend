package com.halildurmus.hotdeals.deal.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halildurmus.hotdeals.deal.DealRepository;
import com.halildurmus.hotdeals.deal.DealSearchParams;
import com.halildurmus.hotdeals.deal.PriceRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class EsDealServiceImpl implements EsDealService {

  private static final int MAX_SUGGESTION = 5;

  private static final String SEARCH_DEALS_ENDPOINT = "/deal/_search";

  @Autowired private DealRepository dealRepository;

  @Autowired private EsDealRepository repository;

  @Autowired private RestHighLevelClient client;

  @Override
  public Page<EsDeal> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  private MultiMatchQueryBuilder createAutocompleteQuery(String query) {
    return new MultiMatchQueryBuilder(query, "title", "title._2gram", "title._3gram")
        .type(Type.BOOL_PREFIX);
  }

  @Override
  public JsonNode getSuggestions(String query) {
    final var request = new Request("GET", "/deal/_search");
    var searchSource = new SearchSourceBuilder();
    searchSource.size(MAX_SUGGESTION);
    searchSource.query(createAutocompleteQuery(query));
    // We only need the title property from the response
    searchSource.fetchSource("title", null);
    request.setJsonEntity(searchSource.toString());

    final JsonNode jsonNode;
    try {
      var response = highLevelClient.getLowLevelClient().performRequest(request);
      var responseBody = EntityUtils.toString(response.getEntity());
      jsonNode = new ObjectMapper().readTree(responseBody).get("hits").get("hits");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return jsonNode;
  }

  private NestedQueryBuilder createStringFacetFilter(String facetName, String facetValue) {
    var facetGroup = "stringFacets";
    var boolQuery = new BoolQueryBuilder();
    var facetNameTermQuery = new TermQueryBuilder(facetGroup + ".facetName", facetName);
    var facetValueTermQuery = new TermQueryBuilder(facetGroup + ".facetValue", facetValue);
    boolQuery.filter(facetNameTermQuery).filter(facetValueTermQuery);
    return new NestedQueryBuilder(facetGroup, boolQuery, ScoreMode.Avg);
  }

  private NestedQueryBuilder createNumberFacetFilter(String facetName, Double from, Double to) {
    var facetGroup = "numberFacets";
    var boolQuery = new BoolQueryBuilder();
    var facetNameTermQuery = new TermQueryBuilder(facetGroup + ".facetName", facetName);
    var facetValueRangeQuery = QueryBuilders.rangeQuery(facetGroup + ".facetValue").gte(from);
    if (to != null) {
      facetValueRangeQuery.lt(to);
    }
    boolQuery.filter(facetNameTermQuery).filter(facetValueRangeQuery);
    return new NestedQueryBuilder(facetGroup, boolQuery, ScoreMode.Avg);
  }

  private FieldSortBuilder createCreatedAtSort(SortOrder sortOrder) {
    return SortBuilders.fieldSort("createdAt").setNumericType("date").order(sortOrder);
  }

  private FieldSortBuilder createPriceSort(SortOrder sortOrder) {
    var nestedPath = "numberFacets";
    var fieldName = "numberFacets.facetValue";
    var termQuery = new TermQueryBuilder(nestedPath + ".facetName", "price");
    var nestedSort = new NestedSortBuilder(nestedPath).setFilter(termQuery);
    return SortBuilders.fieldSort(fieldName).order(sortOrder).setNestedSort(nestedSort);
  }

  private List<QueryBuilder> createCategoryFilters(List<String> categories) {
    List<QueryBuilder> nestedQueries = new ArrayList<>();
    if (categories.size() > 1) {
      var boolQuery = new BoolQueryBuilder();
      for (var category : categories) {
        boolQuery.should(createStringFacetFilter("category", category));
      }
      nestedQueries.add(boolQuery);
    } else {
      nestedQueries.add(createStringFacetFilter("category", categories.get(0)));
    }

    return nestedQueries;
  }

  private List<QueryBuilder> createPriceFilters(List<PriceRange> priceRanges) {
    List<QueryBuilder> nestedQueries = new ArrayList<>();
    var boolQuery = new BoolQueryBuilder();
    for (var pr : priceRanges) {
      boolQuery.should(createNumberFacetFilter("price", pr.getFrom(), pr.getTo()));
    }
    nestedQueries.add(boolQuery);
    return nestedQueries;
  }

  private List<QueryBuilder> createStoreFilters(List<String> stores) {
    List<QueryBuilder> nestedQueries = new ArrayList<>();
    if (stores.size() > 1) {
      var boolQuery = new BoolQueryBuilder();
      for (var store : stores) {
        boolQuery.should(createStringFacetFilter("store", store));
      }
      nestedQueries.add(boolQuery);
    } else {
      nestedQueries.add(createStringFacetFilter("store", stores.get(0)));
    }

    return nestedQueries;
  }

  private MultiMatchQueryBuilder createMultiMatchQuery(String query) {
    return new MultiMatchQueryBuilder(query, "title", "description");
  }

  private TermQueryBuilder createTermQuery() {
    return new TermQueryBuilder("status", "ACTIVE");
  }

  private BoolQueryBuilder createBoolQuery(Boolean hideExpired, String query) {
    var boolQuery = new BoolQueryBuilder();
    if (hideExpired) {
      // Filter out expired deals
      boolQuery.filter(createTermQuery());
    }
    boolQuery.must(createMultiMatchQuery(query));
    return boolQuery;
  }

  private BoolQueryBuilder createFilters(DealSearchParams searchParams, String filterToBeExcluded) {
    var boolQuery = new BoolQueryBuilder();
    if (searchParams.getCategories() == null
        && searchParams.getPrices() == null
        && searchParams.getStores() == null) {
      return boolQuery;
    }

    List<QueryBuilder> filters = new ArrayList<>();
    if (searchParams.getCategories() != null && !Objects.equals(filterToBeExcluded, "category")) {
      filters.addAll(createCategoryFilters(searchParams.getCategories()));
    }
    if (searchParams.getPrices() != null && !Objects.equals(filterToBeExcluded, "price")) {
      filters.addAll(createPriceFilters(searchParams.getPrices()));
    }
    if (searchParams.getStores() != null && !Objects.equals(filterToBeExcluded, "store")) {
      filters.addAll(createStoreFilters(searchParams.getStores()));
    }

    for (var filter : filters) {
      boolQuery.filter(filter);
    }

    return boolQuery;
  }

  private FieldSortBuilder createSort(String sortBy, String order) {
    var sortOrder = order.equals("asc") ? SortOrder.ASC : SortOrder.DESC;
    final FieldSortBuilder fieldSort;
    if (sortBy.equals("createdAt")) {
      fieldSort = createCreatedAtSort(sortOrder);
    } else {
      fieldSort = createPriceSort(sortOrder);
    }

    return fieldSort;
  }

  private NestedAggregationBuilder createAllFiltersSubAgg(String facetGroup) {
    var stringFacets = new NestedAggregationBuilder(facetGroup, facetGroup);
    var facetNames = new TermsAggregationBuilder("names").field(facetGroup + ".facetName");
    var facetValues = new TermsAggregationBuilder("values").field(facetGroup + ".facetValue");
    facetNames.subAggregation(facetValues);
    return stringFacets.subAggregation(facetNames);
  }

  private FilterAggregationBuilder createAllFiltersAgg(DealSearchParams searchParams) {
    var allFiltersAgg =
        new FilterAggregationBuilder("aggAllFilters", createFilters(searchParams, null));
    return allFiltersAgg
        .subAggregation(createAllFiltersSubAgg("stringFacets"))
        .subAggregation(createAllFiltersSubAgg("numberFacets"));
  }

  private FilterAggregationBuilder createFilterAgg(String fieldName, String facetName) {
    return new FilterAggregationBuilder(
        "aggSpecial", QueryBuilders.matchQuery(fieldName, facetName));
  }

  private NestedAggregationBuilder createNestedSubAgg(String facetGroup, String facetName) {
    var stringFacets = new NestedAggregationBuilder(facetGroup, facetGroup);
    var facetNames = new TermsAggregationBuilder("names").field(facetGroup + ".facetName");
    var facetValues = new TermsAggregationBuilder("values").field(facetGroup + ".facetValue");
    facetNames.subAggregation(facetValues);
    var filterAgg =
        createFilterAgg(facetGroup + ".facetName", facetName).subAggregation(facetNames);
    return stringFacets.subAggregation(filterAgg);
  }

  private FilterAggregationBuilder createCategoryAgg(DealSearchParams searchParams) {
    var categoryAgg =
        new FilterAggregationBuilder("aggCategory", createFilters(searchParams, "category"));
    var nestedAgg = createNestedSubAgg("stringFacets", "category");
    return categoryAgg.subAggregation(nestedAgg);
  }

  private FilterAggregationBuilder createStoreAgg(DealSearchParams searchParams) {
    var storeAgg = new FilterAggregationBuilder("aggStore", createFilters(searchParams, "store"));
    var nestedAgg = createNestedSubAgg("stringFacets", "store");
    return storeAgg.subAggregation(nestedAgg);
  }

  private FilterAggregationBuilder createPriceAgg(DealSearchParams searchParams) {
    var priceAgg = new FilterAggregationBuilder("aggPrice", createFilters(searchParams, "price"));
    var facetValues = new RangeAggregationBuilder("values");
    facetValues
        .field("numberFacets.facetValue")
        .keyed(false)
        .addRange(0, 1)
        .addRange(1, 5)
        .addRange(5, 10)
        .addRange(10, 20)
        .addRange(20, 50)
        .addRange(50, 100)
        .addRange(100, 250)
        .addRange(250, 500)
        .addRange(500, 1000)
        .addRange(1000, 1500)
        .addRange(1500, 2000)
        .addUnboundedFrom(2000);
    var facetNames = new TermsAggregationBuilder("names").field("numberFacets.facetName");
    facetNames.subAggregation(facetValues);
    var filterAgg = createFilterAgg("numberFacets.facetName", "price").subAggregation(facetNames);
    var nestedAgg =
        new NestedAggregationBuilder("numberFacets", "numberFacets").subAggregation(filterAgg);
    return priceAgg.subAggregation(nestedAgg);
  }

  private List<AggregationBuilder> createAggregations(DealSearchParams searchParams) {
    List<AggregationBuilder> aggregations = new ArrayList<>();
    aggregations.add(createAllFiltersAgg(searchParams));
    aggregations.add(createCategoryAgg(searchParams));
    aggregations.add(createPriceAgg(searchParams));
    aggregations.add(createStoreAgg(searchParams));
    return aggregations;
  }

  private Request createSearchRequest(DealSearchParams searchParams, Pageable pageable) {
    final var request = new Request("GET", SEARCH_DEALS_ENDPOINT);
    var searchSource = new SearchSourceBuilder();
    searchSource.from(pageable.getPageNumber()).size(pageable.getPageSize());

    if (searchParams.getSortBy() != null) {
      searchSource.sort(createSort(searchParams.getSortBy(), searchParams.getOrder()));
    }

    if (!ObjectUtils.isEmpty(searchParams.getQuery())) {
      searchSource.query(createBoolQuery(searchParams.getHideExpired(), searchParams.getQuery()));
    }

    for (var aggregation : createAggregations(searchParams)) {
      searchSource.aggregation(aggregation);
    }

    searchSource.postFilter(createFilters(searchParams, null));
    request.setJsonEntity(searchSource.toString());

    return request;
  }

  @Override
  public JsonNode searchDeals(DealSearchParams searchParams, Pageable pageable) {
    var request = createSearchRequest(searchParams, pageable);
    final JsonNode jsonNode;
    try {
      var response = highLevelClient.getLowLevelClient().performRequest(request);
      var responseBody = EntityUtils.toString(response.getEntity());
      jsonNode = new ObjectMapper().readTree(responseBody);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return jsonNode;
  }

  @Override
  public EsDeal save(EsDeal esDeal) {
    return repository.save(esDeal);
  }
}
