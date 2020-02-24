package com.cqnu.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zh
 * @date 2019/9/18
 */
@Data
public class ExceptionModel implements Serializable {

    private static final long ERROR_BIT = 10000;

    /**
     * 错误码
     */
    private Integer errorCode;
    /**
     * 错误信息
     */
    private String message;

    public ExceptionModel(Integer errorCode, String message) {
        if (errorCode < 0) {
            throw new IllegalArgumentException("The error status_code must not be less than 0.errorCode[" + errorCode + "]");
        }
        this.message = message;
    }


    public static ExceptionModel create(Integer errorCode, String message) {
        return new ExceptionModel(errorCode, message);
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
