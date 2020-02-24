package com.cqnu.api;

import com.alibaba.fastjson.JSONArray;
import com.cqnu.exception.GitApiRequestException;

/**
 * @author zh
 * @date 2019/12/31
 */
public interface UserApi {
    /**
     * 根据用户名查询所有满足条件的用户信息, 并返回第一个用户ID
     * @param username 指定查询的用户名, 为空时会抛出ParameterValidateException异常
     * @return 从所有满足条件的用户信息中返回第一个用户的ID
     * @throws GitApiRequestException 当以下情况下会抛出此异常
     *                              1. 向服务器发送该请求失败
     *                              2. 用户名不存在
     */
    Integer getUserIdByUsername(String username) throws GitApiRequestException;

    /**
     * 根据用户名查询并返回所有满足条件的用户信息
     * @param username 指定查询的用户名, 为空时会抛出ParameterValidateException异常
     * @return 所有满足条件的用户信息
     * @throws GitApiRequestException 向服务器发送该请求失败时, 会抛出此异常
     */
    JSONArray getUsersByUsername(String username) throws GitApiRequestException;
}
