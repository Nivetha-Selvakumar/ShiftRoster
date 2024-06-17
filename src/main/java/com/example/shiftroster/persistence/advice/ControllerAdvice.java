package com.example.shiftroster.persistence.advice;

import com.example.shiftroster.persistence.Exception.AggregateException;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.util.AppConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException exception){
        Map<String, String> errorMsg = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error->errorMsg.put(error.getField(), error.getDefaultMessage()));
        return errorMsg;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String,String> notFoundException(NotFoundException error){
        Map<String,String> errObj = new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(MisMatchException.class)
    public Map<String,String> misMatchException(MisMatchException error){
        Map<String,String> errObj=new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String,String> illegalArgumentException(IllegalArgumentException error){
        Map<String,String> errObj=new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommonException.class)
    public Map<String,String> commonException(CommonException error){
        Map<String,String> errObj = new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AggregateException.class)
    public Map<String,String> aggregateException(AggregateException error){
        Map<String,String> errObj = new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

}
