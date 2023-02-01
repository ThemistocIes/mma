package org.home.mma.client.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.home.mma.store.dao.SendEmailTaskDao;
import org.home.mma.store.entities.SendEmailTaskEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class EmailController {
    SendEmailTaskDao sendEmailTaskDao;
    public static final String SEND_EMAIL = "/api/email/send";

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(SEND_EMAIL)
    public void sendEmail(
            @RequestParam("destination") String destination,
            @RequestParam String message) {

        sendEmailTaskDao.save(SendEmailTaskEntity.builder()
                .destination(destination)
                .message(message)
                .build());
    }
}
