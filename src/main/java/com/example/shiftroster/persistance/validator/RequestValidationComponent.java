package com.example.shiftroster.persistance.validator;

public class RequestValidationComponent {
    String name ;
    String required;
    String size;
    String format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }


    public String getFormat() {
        return format;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setFormat(String format) {
        this.format = format;
    }


}
