package com.sintergica.apiv2.exceptions.globals;

import com.sintergica.apiv2.exceptions.company.CompanyMismatchException;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.role.RolNotFound;
import com.sintergica.apiv2.exceptions.user.*;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFound.class)
  public ResponseEntity<Warnings> handleUserNotFound(UserNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(PasswordConflict.class)
  public ResponseEntity<Warnings> handlePasswordConflict(PasswordConflict ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(UserConflict.class)
  public ResponseEntity<Warnings> handleUserConflict(UserConflict ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(CompanyNotFound.class)
  public ResponseEntity<Warnings> handleCompanyNotFound(CompanyNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(RolNotFound.class)
  public ResponseEntity<Warnings> handleRolNameNotFound(RolNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(GroupNotFound.class)
  public ResponseEntity<Warnings> handleGroupNotFound(GroupNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(CompanyMismatchException.class)
  public ResponseEntity<Warnings> handleCompanyMismatch(CompanyMismatchException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new Warnings(ex.getMessage(), new Date()));
  }

  @ExceptionHandler(CompanyUserConflict.class)
  public ResponseEntity<Warnings> handleCompanyConflictUser(CompanyUserConflict ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new Warnings(ex.getMessage(), new Date()));
  }
}
