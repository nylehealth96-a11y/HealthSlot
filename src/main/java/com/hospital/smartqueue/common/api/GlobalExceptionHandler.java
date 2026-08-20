package com.hospital.smartqueue.common.api;
import com.hospital.smartqueue.common.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ApiError> notFound(NotFoundException e,HttpServletRequest r){return error(HttpStatus.NOT_FOUND,"NOT_FOUND",e.getMessage(),r);}
 @ExceptionHandler(ConflictException.class) ResponseEntity<ApiError> conflict(ConflictException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,"CONFLICT",e.getMessage(),r);}
 @ExceptionHandler(DomainException.class) ResponseEntity<ApiError> bad(DomainException e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,"DOMAIN_RULE_VIOLATION",e.getMessage(),r);}
 private ResponseEntity<ApiError> error(HttpStatus s,String c,String m,HttpServletRequest r){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),c,m,r.getRequestURI(),List.of()));}
}
