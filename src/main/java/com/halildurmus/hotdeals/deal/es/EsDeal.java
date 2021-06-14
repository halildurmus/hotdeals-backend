package com.halildurmus.hotdeals.deal.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.halildurmus.hotdeals.deal.Deal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "deal")
@TypeAlias("deal")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class EsDeal {

  @Id
  private String id;

  private String title;

  private String description;

  public EsDeal(Deal deal) {
    this.id = deal.getId();
    this.title = deal.getTitle();
    this.description = deal.getDescription();
  }

}