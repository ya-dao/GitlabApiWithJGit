package com.cqnu.exception;

import com.cqnu.model.ExceptionModel;

/**
 * 参数校验失败异常
 * @author zh
 * @date 2019/12/30
 */
public class ParameterValidateException extends RuntimeException{

    private Integer statusCode;

    public ParameterValidateException(Integer statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ParameterValidateException(String message,Integer statusCode,  Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ParameterValidateException(ExceptionModel exceptionModel) {
        super(exceptionModel.getMessage());
        this.statusCode = exceptionModel.getErrorCode();
    }

    @Override
    public String toString() {
        return "ParameterValidateException{" +
                "statusCode=" + statusCode +
                '}';
    }
}
