package com.halildurmus.hotdeals.deal.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halildurmus.hotdeals.deal.Deal;
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
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
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

  @Autowired
  private DealRepository dealRepository;

  @Autowired
  private EsDealRepository repository;

  @Autowired
  private RestHighLevelClient client;

  @Override
  public EsDeal save(EsDeal esDeal) {
    return repository.save(esDeal);
  }

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
    final Request request = new Request("GET", "/deal/_search");
    final SearchSourceBuilder searchSource = new SearchSourceBuilder();
    searchSource.from(0).size(5);
    searchSource.query(createAutocompleteQuery(query));
    searchSource.fetchSource("title", null);
    request.setJsonEntity(searchSource.toString());

    final JsonNode jsonNode;
    try {
      final Response response = client.getLowLevelClient().performRequest(request);
      final String responseBody = EntityUtils.toString(response.getEntity());
      jsonNode = new ObjectMapper().readTree(responseBody).get("hits").get("hits");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return jsonNode;
  }

  private NestedQueryBuilder createStringFacetFilter(String facetName, String facetValue) {
    final String facetGroup = "stringFacets";
    final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    final TermQueryBuilder facetNameTermQuery = new TermQueryBuilder(
        facetGroup + ".facetName", facetName);
    final TermQueryBuilder facetValueTermQuery = new TermQueryBuilder(
        facetGroup + ".facetValue", facetValue);
    boolQuery.filter(facetNameTermQuery).filter(facetValueTermQuery);

    return new NestedQueryBuilder(facetGroup, boolQuery, ScoreMode.Avg);
  }

  private NestedQueryBuilder createNumberFacetFilter(String facetName, Double from, Double to) {
    final String facetGroup = "numberFacets";
    final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    final TermQueryBuilder facetNameTermQuery = new TermQueryBuilder(
        facetGroup + ".facetName", facetName);
    final RangeQueryBuilder facetValueRangeQuery = QueryBuilders.rangeQuery(
        facetGroup + ".facetValue").gte(from).lt(to);
    boolQuery.filter(facetNameTermQuery).filter(facetValueRangeQuery);

    return new NestedQueryBuilder(facetGroup, boolQuery, ScoreMode.Avg);
  }

  private FieldSortBuilder createCreatedAtSort(SortOrder sortOrder) {
    return SortBuilders.fieldSort("createdAt").setNumericType("date").order(sortOrder);
  }

  private FieldSortBuilder createPriceSort(SortOrder sortOrder) {
    final String nestedPath = "numberFacets";
    final String fieldName = "numberFacets.facetValue";
    final TermQueryBuilder termQuery = new TermQueryBuilder(nestedPath + ".facetName",
        "price");
    final NestedSortBuilder nestedSort = new NestedSortBuilder(nestedPath).setFilter(termQuery);

    return SortBuilders.fieldSort(fieldName).order(sortOrder).setNestedSort(nestedSort);
  }

  private List<QueryBuilder> createCategoryFilters(List<String> categories) {
    final List<QueryBuilder> nestedQueries = new ArrayList<>();
    if (categories.size() > 1) {
      final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
      for (String category : categories) {
        boolQuery.should(createStringFacetFilter("category", category));
      }
      nestedQueries.add(boolQuery);
    } else {
      nestedQueries.add(createStringFacetFilter("category", categories.get(0)));
    }

    return nestedQueries;
  }

  private List<QueryBuilder> createPriceFilters(List<PriceRange> priceRanges) {
    final List<QueryBuilder> nestedQueries = new ArrayList<>();
    final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    for (PriceRange pr : priceRanges) {
      boolQuery.should(createNumberFacetFilter("price", pr.getFrom(), pr.getTo()));
    }
    nestedQueries.add(boolQuery);

    return nestedQueries;
  }

  private List<QueryBuilder> createStoreFilters(List<String> stores) {
    final List<QueryBuilder> nestedQueries = new ArrayList<>();
    if (stores.size() > 1) {
      final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
      for (String store : stores) {
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
    //.fuzziness("AUTO");
  }

  private BoolQueryBuilder createFilters(DealSearchParams searchParams, String exclude) {
    final BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    if (searchParams.getCategories() == null && searchParams.getPrices() == null
        && searchParams.getStores() == null) {
      return boolQuery;
    }

    final List<QueryBuilder> filters = new ArrayList<>();
    if (searchParams.getCategories() != null && !Objects.equals(exclude, "category")) {
      filters.addAll(createCategoryFilters(searchParams.getCategories()));
    }
    if (searchParams.getPrices() != null && !Objects.equals(exclude, "price")) {
      filters.addAll(createPriceFilters(searchParams.getPrices()));
    }
    if (searchParams.getStores() != null && !Objects.equals(exclude, "store")) {
      filters.addAll(createStoreFilters(searchParams.getStores()));
    }

    for (QueryBuilder filter : filters) {
      boolQuery.filter(filter);
    }

    return boolQuery;
  }

  private FieldSortBuilder createSort(String sortBy, String order) {
    final SortOrder sortOrder = order.equals("asc") ? SortOrder.ASC : SortOrder.DESC;
    final FieldSortBuilder fieldSort;
    if (sortBy.equals("createdAt")) {
      fieldSort = createCreatedAtSort(sortOrder);
    } else {
      fieldSort = createPriceSort(sortOrder);
    }

    return fieldSort;
  }

  private NestedAggregationBuilder createAllFiltersSubAgg(String facetGroup) {
    final NestedAggregationBuilder stringFacets = new NestedAggregationBuilder(facetGroup,
        facetGroup);
    final TermsAggregationBuilder facetNames = new TermsAggregationBuilder("names").field(
        facetGroup + ".facetName");
    final TermsAggregationBuilder facetValues = new TermsAggregationBuilder("values").field(
        facetGroup + ".facetValue");
    facetNames.subAggregation(facetValues);

    return stringFacets.subAggregation(facetNames);
  }

  private FilterAggregationBuilder createAllFiltersAgg(DealSearchParams searchParams) {
    final FilterAggregationBuilder allFiltersAgg = new FilterAggregationBuilder(
        "aggAllFilters", createFilters(searchParams, null));

    return allFiltersAgg.subAggregation(createAllFiltersSubAgg("stringFacets"))
        .subAggregation(createAllFiltersSubAgg("numberFacets"));
  }

  private FilterAggregationBuilder createFilterAgg(String fieldName, String facetName) {
    return new FilterAggregationBuilder("aggSpecial",
        QueryBuilders.matchQuery(fieldName, facetName));
  }

  private NestedAggregationBuilder createNestedSubAgg(String facetGroup, String facetName) {
    final NestedAggregationBuilder stringFacets = new NestedAggregationBuilder(facetGroup,
        facetGroup);
    final TermsAggregationBuilder facetNames = new TermsAggregationBuilder("names").field(
        facetGroup + ".facetName");
    final TermsAggregationBuilder facetValues = new TermsAggregationBuilder("values").field(
        facetGroup + ".facetValue");
    facetNames.subAggregation(facetValues);
    final FilterAggregationBuilder filterAgg = createFilterAgg(
        facetGroup + ".facetName", facetName).subAggregation(facetNames);

    return stringFacets.subAggregation(filterAgg);
  }

  private FilterAggregationBuilder createCategoryAgg(DealSearchParams searchParams) {
    final FilterAggregationBuilder categoryAgg = new FilterAggregationBuilder(
        "aggCategory", createFilters(searchParams, "category"));
    final NestedAggregationBuilder nestedAgg = createNestedSubAgg(
        "stringFacets", "category");

    return categoryAgg.subAggregation(nestedAgg);
  }

  private FilterAggregationBuilder createStoreAgg(DealSearchParams searchParams) {
    final FilterAggregationBuilder storeAgg = new FilterAggregationBuilder(
        "aggStore", createFilters(searchParams, "store"));
    final NestedAggregationBuilder nestedAgg = createNestedSubAgg(
        "stringFacets", "store");

    return storeAgg.subAggregation(nestedAgg);
  }

  private FilterAggregationBuilder createPriceAgg(DealSearchParams searchParams) {
    final FilterAggregationBuilder priceAgg = new FilterAggregationBuilder(
        "aggPrice", createFilters(searchParams, "price"));
    final RangeAggregationBuilder facetValues = new RangeAggregationBuilder("values");
    facetValues.field("numberFacets.facetValue").keyed(false).addRange(0, 0).addRange(0.01, 5)
        .addRange(5, 10).addRange(10, 20).addRange(20, 50).addRange(50, 100).addRange(100, 250)
        .addRange(250, 500).addRange(500, 1000).addRange(1000, 1500).addRange(1500, 2000)
        .addUnboundedFrom(2000);
    final TermsAggregationBuilder facetNames = new TermsAggregationBuilder("names")
        .field("numberFacets.facetName");
    facetNames.subAggregation(facetValues);
    final FilterAggregationBuilder filterAgg = createFilterAgg(
        "numberFacets.facetName", "price")
        .subAggregation(facetNames);
    final NestedAggregationBuilder nestedAgg = new NestedAggregationBuilder("numberFacets",
        "numberFacets").subAggregation(filterAgg);

    return priceAgg.subAggregation(nestedAgg);
  }

  private List<AggregationBuilder> createAggregations(DealSearchParams searchParams) {
    final List<AggregationBuilder> aggregations = new ArrayList<>();
    aggregations.add(createAllFiltersAgg(searchParams));
    aggregations.add(createCategoryAgg(searchParams));
    aggregations.add(createPriceAgg(searchParams));
    aggregations.add(createStoreAgg(searchParams));

    return aggregations;
  }

  private Request createSearchRequest(DealSearchParams searchParams,
      Pageable pageable) {
    final Request request = new Request("GET", "/deal/_search");
    final SearchSourceBuilder searchSource = new SearchSourceBuilder();
    searchSource.from(pageable.getPageNumber()).size(pageable.getPageSize());

    if (searchParams.getSortBy() != null) {
      searchSource.sort(createSort(searchParams.getSortBy(), searchParams.getOrder()));
    }

    if (!ObjectUtils.isEmpty(searchParams.getQuery())) {
      searchSource.query(createMultiMatchQuery(searchParams.getQuery()));
    }

    for (AggregationBuilder aggregation : createAggregations(searchParams)) {
      searchSource.aggregation(aggregation);
    }

    searchSource.postFilter(createFilters(searchParams, null));

    System.out.println(searchSource);

    request.setJsonEntity(searchSource.toString());

    return request;
  }

  @Override
  public JsonNode searchDeals(DealSearchParams searchParams, Pageable pageable) {
    final Request request = createSearchRequest(searchParams, pageable);
    final JsonNode jsonNode;
    try {
      final Response response = client.getLowLevelClient().performRequest(request);
      final String responseBody = EntityUtils.toString(response.getEntity());
      jsonNode = new ObjectMapper().readTree(responseBody);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    return jsonNode;
  }

}
