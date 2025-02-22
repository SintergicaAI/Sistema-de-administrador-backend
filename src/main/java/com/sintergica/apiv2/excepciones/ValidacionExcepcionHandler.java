package com.sintergica.apiv2.excepciones;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidacionExcepcionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> manejarValidacionExcepciones(
      MethodArgumentNotValidException ex) {
    Map<String, String> errores = new HashMap<>();

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String campo = ((FieldError) error).getField();
              String mensaje = error.getDefaultMessage();
              errores.put(campo, mensaje);
            });

    return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
  }
}
