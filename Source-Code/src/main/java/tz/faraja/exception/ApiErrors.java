package tz.faraja.exception;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;
@RestControllerAdvice public class ApiErrors  {
    @ExceptionHandler( {
        IllegalArgumentException.class,IllegalStateException.class
    }
    ) ResponseEntity<Map<String,String>> bad(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
    }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class) ResponseEntity<Map<String,String>> validation(org.springframework.web.bind.MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("error",e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(java.util.stream.Collectors.joining("; "))));
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) ResponseEntity<Map<String,String>> conflict(Exception e) {
        return ResponseEntity.status(409).body(Map.of("error","This record conflicts with an existing record. Check unique names and linked IDs."));
    }
}
