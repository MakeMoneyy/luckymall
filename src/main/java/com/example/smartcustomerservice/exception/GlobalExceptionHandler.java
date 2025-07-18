package com.example.smartcustomerservice.exception;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        
        List<String> errorMessages = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "请求参数验证失败");
        response.put("errors", errorMessages);
        
        log.warn("请求参数验证失败: {}", errorMessages);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理通义千问API异常
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "AI服务调用失败");
        response.put("error", ex.getMessage());
        
        log.error("AI服务调用失败: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理API Key缺失异常
     */
    @ExceptionHandler(NoApiKeyException.class)
    public ResponseEntity<Map<String, Object>> handleNoApiKeyException(NoApiKeyException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "API密钥配置错误");
        
        log.error("API密钥配置错误: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理输入参数缺失异常
     */
    @ExceptionHandler(InputRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleInputRequiredException(InputRequiredException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "请求参数缺失");
        response.put("error", ex.getMessage());
        
        log.warn("请求参数缺失: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "服务器内部错误");
        
        log.error("未处理的异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 