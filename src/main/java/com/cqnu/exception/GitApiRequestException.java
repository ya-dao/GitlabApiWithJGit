package com.cqnu.exception;

import jodd.http.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 在使用GitApi时,HTTP请求返回的状态码与预期不符.即状态码大于400.
 * @author zh
 * @date 2019/9/18
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GitApiRequestException extends Exception{
    private Integer code;

    public GitApiRequestException(Integer code) {
        this.code = code;
    }

    public GitApiRequestException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public GitApiRequestException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public static Integer getStatusCode(Throwable e){
//        e.printStackTrace();
        Integer statusCode = HttpStatus.HTTP_INTERNAL_ERROR;
        if (e instanceof GitApiRequestException) {
            statusCode = ((GitApiRequestException)e).getCode();
        }
        return statusCode;
    }
}
