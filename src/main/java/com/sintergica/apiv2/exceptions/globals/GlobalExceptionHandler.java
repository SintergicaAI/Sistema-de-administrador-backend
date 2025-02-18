package com.sintergica.apiv2.exceptions.globals;

import com.sintergica.apiv2.exceptions.company.CompanyMismatchException;
import com.sintergica.apiv2.exceptions.company.CompanyNotFound;
import com.sintergica.apiv2.exceptions.company.CompanyUserConflict;
import com.sintergica.apiv2.exceptions.group.GroupNotFound;
import com.sintergica.apiv2.exceptions.role.RolNotFound;
import com.sintergica.apiv2.exceptions.user.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CompanyNotFound.class)
    public ResponseEntity<String> handleCompanyNotFound(CompanyNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RolNotFound.class)
    public ResponseEntity<String> handleRolNameNotFound(RolNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(GroupNotFound.class)
    public ResponseEntity<String> handleGroupNotFound(GroupNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CompanyMismatchException.class)
    public ResponseEntity<String> handleCompanyMismatch(CompanyMismatchException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(CompanyUserConflict.class)
    public ResponseEntity<String> handleCompanyConflictUser(CompanyUserConflict ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
