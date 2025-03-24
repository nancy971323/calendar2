package com.company.calendar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 資源未找到異常
 * <p>
 * 當請求的資源（如員工、事件等）在系統中不存在時拋出此異常。
 * 此異常會被 {@link GlobalExceptionHandler} 捕獲並處理，返回 404 HTTP 狀態碼。
 * </p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 構造函數
     *
     * @param message 異常訊息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 構造函數
     *
     * @param resourceName 資源名稱
     * @param fieldName 欄位名稱
     * @param fieldValue 欄位值
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s 不存在：%s = '%s'", resourceName, fieldName, fieldValue));
    }
}