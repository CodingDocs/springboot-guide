package cn.javaguide.rest.common;

public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败");
    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}