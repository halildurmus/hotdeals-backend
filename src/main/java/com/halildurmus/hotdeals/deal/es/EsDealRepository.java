package com.halildurmus.hotdeals.deal.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsDealRepository extends ElasticsearchRepository<EsDeal, String> {

}