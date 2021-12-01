package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDealRepository extends ElasticsearchRepository<EsDeal, String> {

  @Query("{\"multi_match\": {\"query\": \"?0\",\"type\": \"bool_prefix\",\"fields\": [\"title\",\"title._2gram\",\"title._3gram\"]}}")
  List<SearchHit<EsDeal>> getSuggestions(String query, Pageable pageable);

}