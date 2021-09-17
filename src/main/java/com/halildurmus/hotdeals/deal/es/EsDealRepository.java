package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDealRepository extends ElasticsearchRepository<EsDeal, String> {

  @Query("{\"multi_match\": {\"query\": \"?0\",\"fields\": [\"description^1.0\",\"title^1.0\"],\"type\": \"phrase_prefix\",\"operator\": \"OR\",\"slop\": 0,\"prefix_length\": 0,\"max_expansions\": 50,\"zero_terms_query\": \"NONE\",\"auto_generate_synonyms_phrase_query\": true,\"fuzzy_transpositions\": true,\"boost\": 1}}")
  @Highlight(fields = {
      @HighlightField(name = "title")
  }, parameters = @HighlightParameters(
      preTags = "<strong>",
      postTags = "</strong>",
      fragmentSize = 500,
      numberOfFragments = 3
  ))
  List<SearchHit<EsDeal>> queryDeals(String keyword, Pageable pageable);

}