package com.halildurmus.hotdeals.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
class DealRepositoryTest extends BaseIntegrationTest {

  static User fakeUser = new User("607345b0eeeee1452898128b");
  @MockBean
  SecurityService securityService;
  @Autowired
  private DealRepository dealRepository;

  @AfterEach
  void cleanUp() {
    this.dealRepository.deleteAll();
  }

  @Test
  void findByPostedByShouldReturnDeal() {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    this.dealRepository.save(DummyDeals.deal1);
    final ObjectId objectId = new ObjectId(fakeUser.getId());
    List<Deal> deals = dealRepository.findAllByPostedByOrderByCreatedAtDesc(objectId, null).getContent();

    assertFalse(deals.isEmpty());
    assertEquals(deals.get(0).getPostedBy(), objectId);
  }

  @Test
  void findByPostedByShouldNotReturnDealIfNoDealFound() {
    final ObjectId objectId = new ObjectId("607345b0eeeee1452898128b");
    List<Deal> deals = dealRepository.findAllByPostedByOrderByCreatedAtDesc(objectId, null).getContent();

    assertTrue(deals.isEmpty());
  }

}
