package com.halildurmus.hotdeals.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    List<Deal> deals = dealRepository.findAllByPostedBy(objectId);

    assertFalse(deals.isEmpty());
    assertEquals(deals.get(0).getPostedBy(), objectId);
  }

  @Test
  void findByPostedByShouldNotReturnDealIfNoDealFound() {
    final ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");

    List<Deal> deals = dealRepository.findAllByPostedBy(objectId);

    assertTrue(deals.isEmpty());
  }

}
