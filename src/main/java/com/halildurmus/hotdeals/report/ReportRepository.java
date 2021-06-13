package com.halildurmus.hotdeals.report;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository extends MongoRepository<Report, String> {

}