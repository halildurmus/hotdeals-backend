package com.halildurmus.hotdeals.notification;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseControllerUnitTest;
import com.halildurmus.hotdeals.notification.dummy.DummyNotifications;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Import(NotificationController.class)
public class NotificationControllerTest extends BaseControllerUnitTest {

  @Autowired
  private JacksonTester<Notification> json;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private NotificationService service;

  @Test
  @DisplayName("POST /notifications (success)")
  public void sendsNotification() throws Exception {
    when(service.send(any(Notification.class))).thenReturn(1);

    final RequestBuilder request = post("/notifications")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyNotifications.notification1).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$").value(1));
  }

  @Test
  @DisplayName("POST /notifications (validation fails)")
  public void postValidationFails() throws Exception {
    final RequestBuilder request = post("/notifications")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'notification' on field 'data'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'notification' on field 'tokens'")));
  }

}
