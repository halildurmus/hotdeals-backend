package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.comment.CommentRepository;
import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.report.comment.CommentReportRepository;
import com.halildurmus.hotdeals.report.dummy.DummyCommentReports;
import com.halildurmus.hotdeals.user.UserRepository;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class CommentReportIntegrationTest extends BaseIntegrationTest {

  @Autowired private CacheManager cacheManager;

  @Autowired private CommentRepository commentRepository;

  @Autowired private CommentReportRepository commentReportRepository;

  @Autowired private MockMvc mvc;

  @Autowired private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    commentRepository.deleteAll();
    commentReportRepository.deleteAll();
    userRepository.deleteAll();
    for (var name : cacheManager.getCacheNames()) {
      var cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }

  @Test
  @DisplayName("GET /comment-reports (returns empty)")
  public void getCommentReportsReturnsEmptyArray() throws Exception {
    var request =
        get("/comment-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comment-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /comment-reports (returns 1 comment report)")
  public void getCommentReportsReturnsOneCommentReport() throws Exception {
    var user = userRepository.save(DummyUsers.user1);
    var comment = commentRepository.save(DummyComments.comment1);
    var commentReport = DummyCommentReports.commentReport1;
    commentReport.setReportedComment(comment);
    commentReport.setReportedBy(user);
    commentReportRepository.save(commentReport);
    var request =
        get("/comment-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.comment-reports", hasSize(1)))
        .andExpect(jsonPath("$._embedded.comment-reports[0].*", hasSize(8)))
        .andExpect(jsonPath("$._embedded.comment-reports[0].id").isNotEmpty())
        .andExpect(jsonPath("$._embedded.comment-reports[0].reportedBy.id").value(user.getId()))
        .andExpect(
            jsonPath("$._embedded.comment-reports[0].reportedComment.id").value(comment.getId()))
        .andExpect(jsonPath("$._embedded.comment-reports[0].reasons", hasSize(2)))
        .andExpect(
            jsonPath("$._embedded.comment-reports[0].message").value(commentReport.getMessage()))
        .andExpect(jsonPath("$._embedded.comment-reports[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$._embedded.comment-reports[0].updatedAt").isNotEmpty());
  }
}
