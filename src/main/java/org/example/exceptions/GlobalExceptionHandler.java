package org.example.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejaErrorDeValidacion(MethodArgumentNotValidException ex){
        var errores = ex.getBindingResult().getFieldErrors();

        String mensaje = "";

        for (var error : errores){
            mensaje += error.getField() + ": " + error.getDefaultMessage() + ", ";
        }

        mensaje = mensaje.substring(0, mensaje.length() - 2);

        return ResponseEntity.badRequest().body(mensaje);
    }
}
