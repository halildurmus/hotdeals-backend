package com.halildurmus.hotdeals.deal.es;

import com.halildurmus.hotdeals.deal.Deal;
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
@TypeAlias("deal")
@Data
@NoArgsConstructor
public class EsDeal {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;

  @Field(type = FieldType.Search_As_You_Type)
  private String title;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Nested)
  private List<NumberFacet> numberFacets = new ArrayList<>();

  @Field(type = FieldType.Nested)
  private List<StringFacet> stringFacets = new ArrayList<>();

  public EsDeal(Deal deal) {
    this.id = deal.getId();
    this.title = deal.getTitle();
    this.description = deal.getDescription();
    final NumberFacet discountPriceFacet = new NumberFacet("discountPrice",
        deal.getDiscountPrice());
    this.numberFacets.add(discountPriceFacet);
    final StringFacet categoryFacet = new StringFacet("category", deal.getCategory());
    final StringFacet storeFacet = new StringFacet("store", deal.getStore().toString());
    this.stringFacets.add(categoryFacet);
    this.stringFacets.add(storeFacet);
  }

}