package com.halildurmus.hotdeals.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

class DealRepositoryTest extends BaseIntegrationTest {

  @MockBean
  private SecurityService securityService;

  @Autowired
  private DealRepository repository;

  @AfterEach
  void cleanUp() {
    this.repository.deleteAll();
  }

  @Test
  void findAllByPostedByReturnsOneDeal() {
    final User user = DummyUsers.user1;
    final Deal deal = DummyDeals.deal1;
    deal.setPostedBy(new ObjectId(user.getId()));
    when(securityService.getUser()).thenReturn(user);
    this.repository.save(deal);
    final ObjectId objectId = new ObjectId(user.getId());
    final List<Deal> deals = repository.findAllByPostedByOrderByCreatedAtDesc(objectId,
        Pageable.unpaged()).getContent();

    assertFalse(deals.isEmpty());
    assertEquals(deals.get(0).getPostedBy(), objectId);
  }

  @Test
  void findAllByPostedByReturnsEmptyArray() {
    final ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");
    final List<Deal> deals = repository.findAllByPostedByOrderByCreatedAtDesc(objectId,
        Pageable.unpaged()).getContent();

    assertTrue(deals.isEmpty());
  }

}
