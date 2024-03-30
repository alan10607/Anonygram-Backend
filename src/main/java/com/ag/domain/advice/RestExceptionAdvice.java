package com.ag.domain.advice;

import com.ag.domain.dto.RestResponseEntity;
import com.ag.domain.exception.AgValidationException;
import com.ag.domain.exception.LockNotGotException;
import com.ag.domain.exception.base.AnonygramRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class RestExceptionAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //To prevent RestResponseEntity get re-wrap
        return !returnType.getParameterType().equals(RestResponseEntity.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }

        if (needWarpResponse(request)) {
            /*
            If body type is String.class, selectedConverterType will be StringHttpMessageConverter,
            need to transform to string.
            Normally selectedConverterType will be MappingJackson2HttpMessageConverter if returnType is not String.class
             */
            RestResponseEntity restResponseEntity = new RestResponseEntity(getHttpStatus(response), body);
            if (returnType.getParameterType().equals(String.class)) {
                return toJSONString(restResponseEntity);
            }
            return restResponseEntity;
        }

        return body;
    }

    private boolean needWarpResponse(ServerHttpRequest request) {
        return false;
//        return HttpUtil.isMatchPath(request.getURI().getPath(), SecurityConfig.REST_APIS);
    }

    private HttpStatus getHttpStatus(ServerHttpResponse response) {
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        return HttpStatus.valueOf(httpServletResponse.getStatus());
    }

    private String toJSONString(RestResponseEntity restResponseEntity) {
        try {
            return new ObjectMapper().writeValueAsString(restResponseEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Response body transform JSON string error");
        }
    }

    private Map<String, String> toErrorMap(Throwable throwable) {
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", throwable.getMessage());
        return errMap;
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(Throwable ex) {
        log.error("Debug exception", ex);
        return toErrorMap(ex);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("{}", ex.getMessage());
        return toErrorMap(ex);
    }

    @ExceptionHandler(value = {AnonygramRuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAnonygramRuntimeException(AnonygramRuntimeException ex) {
        log.error("{}", ex.getMessage());
        return toErrorMap(ex);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return toErrorMap(ex);
    }

    @ExceptionHandler(value = {AgValidationException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleAgValidationException(AgValidationException ex) {
        return toErrorMap(ex);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> suggestions = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            suggestions.put(error.getField(), error.getDefaultMessage());
        });
        return suggestions;
    }

    @ExceptionHandler(value = {LockNotGotException.class})
    @ResponseStatus(HttpStatus.LOCKED)
    public Map<String, String> handleLockNotGotException(LockNotGotException ex) {
        return toErrorMap(ex);
    }

}