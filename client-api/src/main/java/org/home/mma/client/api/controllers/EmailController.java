package org.home.mma.client.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class SampleController {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/api/email/send")
    public void sendEmail(
            @RequestParam("destination") String destination,
            @RequestParam String message
    ) {




    }
}
