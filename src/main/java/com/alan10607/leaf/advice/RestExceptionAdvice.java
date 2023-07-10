package com.alan10607.leaf.advice;

import com.alan10607.leaf.dto.RestResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.alan10607.leaf.config.SecurityConfig.*;

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
        if(!needWarpResponse(request)){
            return body;
        }

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

    private boolean needWarpResponse(ServerHttpRequest request){
        String path = request.getURI().getPath();
        String[] targetPath = {FORUM_PATH, AUTH_PATH, REDIS_PATH};
        long match = Arrays.stream(targetPath).map(this::getPathPrefix)
                .filter(pathPrefix -> path.startsWith("pathPrefix"))
                .count();
        return match > 0;
    }

    private String getPathPrefix(String webConfigPath){
        int secondSlash = webConfigPath.indexOf("/", 1);
        return webConfigPath.substring(0, secondSlash);
    }

    private HttpStatus getHttpStatus(ServerHttpResponse response){
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        return HttpStatus.valueOf(httpServletResponse.getStatus());
    }

    private String toJSONString(RestResponseEntity restResponseEntity){
        try {
            return new ObjectMapper().writeValueAsString(restResponseEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Response body transform JSON string error");
        }
    }

    @ExceptionHandler(value = { Throwable.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(Throwable ex) {
        log.error("", ex);
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", ex.getMessage());
        return errMap;
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> suggestions = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            suggestions.put(error.getField(), error.getDefaultMessage());
        });
        return suggestions;
    }

}