package com.taskmanagement.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {
//    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
//    public ResponseEntity<String> handleAccessDenied(
//            org.springframework.security.access.AccessDeniedException ex
//    ) {
//        System.out.println("🔥 GlobalExceptionHandler CALLED");
//
//        return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(ex.getMessage());
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ex.getMessage());
//    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}
