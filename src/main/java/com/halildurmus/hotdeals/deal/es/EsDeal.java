package com.halildurmus.hotdeals.deal.es;

import com.halildurmus.hotdeals.deal.Deal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "deal")
@TypeAlias("deal")
@Data
@NoArgsConstructor
public class EsDeal {

  @Id
  @Field(type = FieldType.Text)
  private String id;

  @Field(type = FieldType.Search_As_You_Type)
  private String title;

  @Field(type = FieldType.Text)
  private String description;

  public EsDeal(Deal deal) {
    this.id = deal.getId();
    this.title = deal.getTitle();
    this.description = deal.getDescription();
  }

}