package org.home.mma.client.api.controllers.errors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.home.mma.data.dto.ErrorDto;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
public class RestErrorController implements ErrorController {
    private static final String PATH = "/error";

    ErrorAttributes errorAttributes;

    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(PATH)
    public ResponseEntity<ErrorDto> error(WebRequest webRequest) {

        Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE)
        );

        HttpStatus httpStatus = HttpStatus.valueOf((Integer) attributes.get("status"));

        return ResponseEntity
                .status(httpStatus.value())
                .body(
                        ErrorDto.builder()
                                .error(httpStatus.getReasonPhrase())
                                .description((String) attributes.get("message"))
                                .build()
                );
    }
}
