package com.hospital.smartqueue.common.api;
import com.hospital.smartqueue.common.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ApiError> notFound(NotFoundException e,HttpServletRequest r){return error(HttpStatus.NOT_FOUND,"NOT_FOUND",e.getMessage(),r);}
 @ExceptionHandler(ConflictException.class) ResponseEntity<ApiError> conflict(ConflictException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,"CONFLICT",e.getMessage(),r);}
 @ExceptionHandler(DomainException.class) ResponseEntity<ApiError> bad(DomainException e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,"DOMAIN_RULE_VIOLATION",e.getMessage(),r);}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e,HttpServletRequest r){List<ApiError.FieldError> fields=e.getBindingResult().getFieldErrors().stream().map(f->new ApiError.FieldError(f.getField(),f.getDefaultMessage())).toList();return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"VALIDATION_ERROR","Request validation failed",r.getRequestURI(),fields));}
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR","Request body is malformed or contains invalid values",r);}
 @ExceptionHandler(ConstraintViolationException.class) ResponseEntity<ApiError> constraint(ConstraintViolationException e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR","Request validation failed",r);}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ApiError> dataConflict(DataIntegrityViolationException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,"CONFLICT","A record with the same unique value already exists",r);}
 private ResponseEntity<ApiError> error(HttpStatus s,String c,String m,HttpServletRequest r){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),c,m,r.getRequestURI(),List.of()));}
}
