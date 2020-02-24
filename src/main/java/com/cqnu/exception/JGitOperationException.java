package com.cqnu.exception;

/**
 * 用于使用JGit时,封装抛出的异常
 * @author zh
 * @date 2019/9/18
 */
public class JGitOperationException extends RuntimeException{


    public JGitOperationException() { }

    public JGitOperationException(String message) {
        super(message);
    }

    public JGitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
