package hr.fer.tel.rassus.server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handle(ResponseStatusException e) {
        Map<String, String> body = new HashMap<>();
        body.put("error", e.getReason());
        body.put("status", String.valueOf(e.getStatusCode().value()));
        return new ResponseEntity<>(body, e.getStatusCode());
    }
}
