package com.example.shiftroster.persistance.validator;

import java.util.*;

public class RequestValidationItem {

    List<RequestValidationComponent> body;

    List<RequestValidationComponent> header;

    List<RequestValidationComponent> pathVariable;

    List<RequestValidationComponent> queryParam;

    public List<RequestValidationComponent> getBody() {
        return body;
    }

    public void setBody(List<RequestValidationComponent> body) {
        this.body = body;
    }

    public List<RequestValidationComponent> getHeader() {
        return header;
    }

    public void setHeader(List<RequestValidationComponent> header) {
        this.header = header;
    }

    public List<RequestValidationComponent> getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(List<RequestValidationComponent> pathVariable) {
        this.pathVariable = pathVariable;
    }

    public List<RequestValidationComponent> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(List<RequestValidationComponent> queryParam) {
        this.queryParam = queryParam;
    }
}
