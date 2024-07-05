package com.example.shiftroster.persistence.advice;

import com.example.shiftroster.persistence.Exception.AggregateException;
import com.example.shiftroster.persistence.Exception.CommonException;
import com.example.shiftroster.persistence.Exception.MisMatchException;
import com.example.shiftroster.persistence.Exception.NotFoundException;
import com.example.shiftroster.persistence.util.AppConstant;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerAdviceTest {


    private final ControllerAdvice controllerAdvice = new ControllerAdvice();

    @Test
    public void handleInvalidArgumentTest(){
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objectName", "field1", "error message 1");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null,bindingResult);
        Map<String, String> result = controllerAdvice.handleInvalidArgument(exception);
        Map<String, String> expected = new HashMap<>();
        expected.put("field1", "error message 1");
        assertEquals(expected, result);
    }

    @Test
    public void testNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found");
        Map<String, String> result = controllerAdvice.notFoundException(exception);
        assertEquals(1, result.size());
        assertEquals("Not found", result.get(AppConstant.ERROR));
    }

    @Test
    public void testMisMatchException() {
        MisMatchException exception = new MisMatchException("Mismatch");
        Map<String, String> result = controllerAdvice.misMatchException(exception);
        assertEquals(1, result.size());
        assertEquals("Mismatch", result.get(AppConstant.ERROR));
    }

    @Test
    public void testIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");
        Map<String, String> result = controllerAdvice.illegalArgumentException(exception);
        assertEquals(1, result.size());
        assertEquals("Illegal argument", result.get(AppConstant.ERROR));
    }

    @Test
    public void testCommonException() {
        CommonException exception = new CommonException("Common exception");
        Map<String, String> result = controllerAdvice.commonException(exception);
        assertEquals(1, result.size());
        assertEquals("Common exception", result.get(AppConstant.ERROR));
    }

    @Test
    public void testAggregateException() {
        AggregateException exception = new AggregateException("Aggregate exception", null);
        Map<String, String> result = controllerAdvice.aggregateException(exception);
        assertEquals(1, result.size());
        assertEquals("Aggregate exception", result.get(AppConstant.ERROR));
    }
}
