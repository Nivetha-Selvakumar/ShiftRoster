package com.example.shiftroster.persistence.Exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AggregateException extends CommonException {
    private final List<Exception> exceptions;

    public AggregateException(String message, List<Exception> exceptions) {
        super(message);
        this.exceptions = exceptions;
    }
}
