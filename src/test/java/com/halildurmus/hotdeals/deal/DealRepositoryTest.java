package com.halildurmus.hotdeals.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

class DealRepositoryTest extends BaseIntegrationTest {

  @Autowired private DealRepository dealRepository;

  @MockBean private SecurityService securityService;

  @AfterEach
  void cleanUp() {
    this.dealRepository.deleteAll();
  }

  @Test
  void findAllByPostedByReturnsOneDeal() {
    var user = DummyUsers.user1;
    var deal = DummyDeals.deal1;
    deal.setPostedBy(new ObjectId(user.getId()));
    when(securityService.getUser()).thenReturn(user);
    this.dealRepository.save(deal);
    var objectId = new ObjectId(user.getId());
    var deals =
        dealRepository
            .findAllByPostedByOrderByCreatedAtDesc(objectId, Pageable.unpaged())
            .getContent();

    assertFalse(deals.isEmpty());
    assertEquals(deals.get(0).getPostedBy(), objectId);
  }

  @Test
  void findAllByPostedByReturnsEmptyArray() {
    var objectId = new ObjectId("607345b0eeeee1452898128b");
    var deals =
        dealRepository
            .findAllByPostedByOrderByCreatedAtDesc(objectId, Pageable.unpaged())
            .getContent();

    assertTrue(deals.isEmpty());
  }
}
