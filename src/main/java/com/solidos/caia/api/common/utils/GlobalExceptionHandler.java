package com.solidos.caia.api.common.utils;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.solidos.caia.api.common.models.CommonResponse;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<CommonResponse<String>> handleDataIntegrityException(DataIntegrityViolationException ex) {
    CommonResponse<String> response = CommonResponse.errorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Data Integrity Violation");

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<CommonResponse<String>> handleConstraintViolationException(ConstraintViolationException ex) {
    CommonResponse<String> response = CommonResponse.errorResponse(
        HttpStatus.CONFLICT.value(),
        ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(JWTVerificationException.class)
  public ResponseEntity<CommonResponse<String>> handleJWTVerificationException(JWTVerificationException ex) {
    CommonResponse<String> response = CommonResponse.errorResponse(
        HttpStatus.CONFLICT.value(),
        ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<CommonResponse<String>> handleIllegalStateException(IllegalStateException ex) {
    CommonResponse<String> response = CommonResponse.errorResponse(
        HttpStatus.CONFLICT.value(),
        ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }
}
