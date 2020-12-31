package cn.javaguide.rest.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer status;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 数据封装
     */
    private T obj;

    protected ResponseResult(Integer status, String msg, T obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> ResponseResult<T> success(String message) {
        return new ResponseResult<T>(ResultCode.SUCCESS.getCode(), message, null);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }


    public static <T> ResponseResult<T> failed(ResultCode errorCode) {
        return new ResponseResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ResponseResult<T> failed(ResultCode errorCode, String message) {
        return new ResponseResult<T>(errorCode.getCode(), message, null);
    }

    public static <T> ResponseResult<T> failed(String message) {
        return new ResponseResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    public static <T> ResponseResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

}