package com.cqnu.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.cqnu.api.UserApi;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.exception.ParameterValidateException;
import com.cqnu.utils.GitUrlBuilder;
import com.cqnu.utils.HttpUtils;
import com.cqnu.utils.JGitUtils;
import jodd.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zh
 * @date 2019/12/30
 */
@Component
public class UserApiImpl implements UserApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGitApiImpl.class);

    private static final String PARAMETER_USERNAME = "username";

    private static final String KEY_ID = "id";

    @Override
    public Integer getUserIdByUsername(String username) throws GitApiRequestException {

        JSONArray jsonArray = getUsersByUsername(username);
        if (jsonArray.isEmpty()) {
            String exceptionMessage = "Gitlab: 该用户名不存在，username：" + username;
            throw new GitApiRequestException(exceptionMessage, HttpStatus.HTTP_NOT_FOUND);
        }
        return jsonArray.getJSONObject(0).getInteger(KEY_ID);
    }

    @Override
    public JSONArray getUsersByUsername(String username) throws GitApiRequestException {

        if (username == null) {
            throw new ParameterValidateException(HttpStatus.HTTP_BAD_REQUEST, "参数不能为null. username：null");
        }
        Map<String, String> tokenMap = JGitUtils.getStringTokenMap();
        tokenMap.put(PARAMETER_USERNAME, username);

        String url = GitUrlBuilder.create().addUsers().build();
        try {
            return HttpUtils.requestGetForJsonArray(url, tokenMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);
            String exceptionMessage = "Gitlab: 根据用户名获取用户ID失败，username：" + username;
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }
}
