package com.halildurmus.hotdeals.deal.es;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.DealStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "deal")
@TypeAlias("esDeal")
@Data
@NoArgsConstructor
public class EsDeal {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;

  @Field(type = FieldType.Keyword)
  private String postedBy;

  @Field(type = FieldType.Search_As_You_Type)
  private String title;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Keyword)
  private String coverPhoto;

  @Field(type = FieldType.Double)
  private Double originalPrice;

  @Field(type = FieldType.Keyword)
  private DealStatus status;

  @Field(type = FieldType.Date)
  private Instant createdAt;

  @Field(type = FieldType.Nested)
  private List<NumberFacet> numberFacets = new ArrayList<>();

  @Field(type = FieldType.Nested)
  private List<StringFacet> stringFacets = new ArrayList<>();

  public EsDeal(Deal deal) {
    this.id = deal.getId();
    this.postedBy = deal.getPostedBy().toString();
    this.title = deal.getTitle();
    this.description = deal.getDescription();
    this.coverPhoto = deal.getCoverPhoto();
    this.originalPrice = deal.getOriginalPrice();
    this.status = deal.getStatus();
    this.createdAt = deal.getCreatedAt();
    var priceFacet = new NumberFacet("price", deal.getPrice());
    this.numberFacets.add(priceFacet);
    var categoryFacet = new StringFacet("category", deal.getCategory());
    var storeFacet = new StringFacet("store", deal.getStore().toString());
    this.stringFacets.add(categoryFacet);
    this.stringFacets.add(storeFacet);
  }
}
