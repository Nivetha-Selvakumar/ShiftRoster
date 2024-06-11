package com.example.shiftroster.persistance.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.xml.validation.ValidatorHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TemplateValidator {

    private RequestValidationConfig requestValidationConfig;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support/json/template.json");
        requestValidationConfig = new ObjectMapper().readValue(resource.getInputStream(), RequestValidationConfig.class);
    }

    public void basicValidation(String templateType, String startDate, String endDate, String empId) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put("templateType", templateType);
        actualParameters.put("startDate", startDate);
        actualParameters.put("endDate", endDate);
        actualParameters.put("empId", empId);

        // Validate header fields
        validateFields(actualParameters, requestValidationConfig.getTemplate().getHeader());

        // Validate queryParam fields
        validateFields(actualParameters, requestValidationConfig.getTemplate().getQueryParam());
    }

    private void validateFields(Map<String, String> actualParameters, Iterable<RequestValidationComponent> components) {
        for (RequestValidationComponent component : components) {
            String value = actualParameters.get(component.getName());

            if (Boolean.parseBoolean(component.getRequired()) && (StringUtils.isEmpty(value))) {
                throw new IllegalArgumentException(component.getName() + " is required.");
            }

            if (component.getSize() != null && value != null && value.length() > Integer.parseInt(component.getSize())) {
                throw new IllegalArgumentException(component.getName() + " must not exceed " + component.getSize() + " characters.");
            }

            if (component.getFormat() != null && value != null) {
                Pattern pattern = Pattern.compile(component.getFormat());
                if (!pattern.matcher(value).matches()) {
                    throw new IllegalArgumentException(component.getName() + "format does not matches.");
                }
            }
        }
    }
}

