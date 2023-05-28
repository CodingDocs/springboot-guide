package com.twuc.webApp.exception;

/**
 * @author shuang.kou
 */
public class ErrorResponse {

    private String message;

    private String errorTypeName;

    public ErrorResponse() {
    }

    public ErrorResponse(Exception e) {
        this(e.getClass().getName(), e.getMessage());
    }

    public ErrorResponse(String errorTypeName, String message) {
        this.errorTypeName = errorTypeName;
        this.message = message;
    }

    public String getErrorTypeName() {
        return errorTypeName;
    }

    public void setErrorTypeName(String errorTypeName) {
        this.errorTypeName = errorTypeName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" + "errorTypeName='" + errorTypeName + '\'' + ", message='" + message + '\'' + '}';
    }
}
