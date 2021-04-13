package com.halildurmus.hotdeals.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
class DealRepositoryTest {

  @Autowired
  private DealRepository dealRepository;

  @AfterEach
  void cleanUp() {
    this.dealRepository.deleteAll();
  }

  @Test
  void findByPostedByShouldReturnDeal() {
    this.dealRepository.save(DummyDeals.deal1);
    final ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");

    Optional<Deal> deal = dealRepository.findByPostedBy(objectId);

    assertTrue(deal.isPresent());
    assertEquals(deal.get().getPostedBy(), objectId);
  }

  @Test
  void findByPostedByShouldNotReturnDealIfNoDealFound() {
    final ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");

    Optional<Deal> deal = dealRepository.findByPostedBy(objectId);

    assertTrue(deal.isEmpty());
  }

}
