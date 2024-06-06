package com.example.shiftroster.persistance.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class ControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException exception){
        Map<String, String> errorMsg = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error->errorMsg.put(error.getField(), error.getDefaultMessage()));
        return errorMsg;
    }

}
