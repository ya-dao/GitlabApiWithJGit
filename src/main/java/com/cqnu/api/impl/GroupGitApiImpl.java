package com.cqnu.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.api.GroupGitApi;
import com.cqnu.enums.AccessLevel;
import com.cqnu.enums.GroupField;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.model.ExceptionModel;
import com.cqnu.model.GroupModel;
import com.cqnu.utils.GitUrlBuilder;
import com.cqnu.utils.HttpUtils;
import com.cqnu.utils.JGitUtils;
import jodd.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Gitlab Group Api相关操作
 * 创建组/给组里添加用户/根据用户id获取组的id
 *
 * @author zh
 * @date 2019/9/19
 */
@Component
public class GroupGitApiImpl implements GroupGitApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGitApiImpl.class);

    private static final String DEFAULT_UI_ENGINE_GROUP_NAME = "apps";

    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_VISIBILITY = "visibility";
    private static final String PARAMETER_ACCESS_LEVEL = "access_level";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_USER_ID = "user_id";


    @Override
    public String getDefaultUIEngineGroupId() throws GitApiRequestException {
        JSONArray groupNames = this.getGroupIdsByGroupName(DEFAULT_UI_ENGINE_GROUP_NAME);
        if (groupNames != null && !groupNames.isEmpty()) {
            return this.parseGroupIdFromJsonArray(groupNames, DEFAULT_UI_ENGINE_GROUP_NAME);
        }
        return null;
    }

    @Override
    public GroupModel createGroup(String groupName) throws GitApiRequestException {
        Map<String, Object> tokenMap = JGitUtils.getTokenMap();
        tokenMap.put(PARAMETER_NAME, groupName);
        tokenMap.put(PARAMETER_PATH, groupName);
        tokenMap.put(PARAMETER_VISIBILITY, JGitUtils.getDefaultVisibility());

        String url = GitUrlBuilder.create().addGroups().build();
        try {
            String responseText = HttpUtils.requestPostForString(url, tokenMap);
            return convertToGroupModel(responseText);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab: 创建组失败. "
                            + "url: " + url
                            + ", groupName: " + groupName).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public Integer addUserToGroup(String userId, String groupId) throws GitApiRequestException {
        Map<String, Object> tokenMap = JGitUtils.getTokenMap();
        tokenMap.put(PARAMETER_USER_ID, userId);
        tokenMap.put(PARAMETER_ACCESS_LEVEL,
                String.valueOf(AccessLevel.DEVELOPER.getLevel()));
        tokenMap.put(PARAMETER_VISIBILITY,
                JGitUtils.getDefaultVisibility());

        String url = GitUrlBuilder.create().addGroups().addPathVariable(groupId).addMembers().build();
        try {
            return HttpUtils.requestPostForStatusCode(url, tokenMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab: 添加用户到指定的组失败."
                            + "groupId: " + groupId
                            + "userId: " + userId).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public JSONArray getGroupIdsByGroupName(String groupName) throws GitApiRequestException {
        Map<String, String> tokenMap = JGitUtils.getStringTokenMap();
        tokenMap.put(PARAMETER_SEARCH, groupName);

        String url = GitUrlBuilder.create().addGroups().build();
        try {
            JSONArray jsonArray = HttpUtils.requestGetForJsonArray(url, tokenMap);
            if (jsonArray.isEmpty()) {
                throw new GitApiRequestException("GitLab:该组名不存在, groupName=" + groupName,
                        HttpStatus.HTTP_NOT_FOUND);
            }
            return jsonArray;
        } catch (GitApiRequestException e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab:根据组名获取组ID失败. groupName: " + groupName).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public String parseGroupIdFromJsonArray(JSONArray jsonArray, String groupName) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (groupName.equals(jsonObject.getString(GroupField.NAME.getValue()))) {
                return jsonObject.getString(GroupField.ID.getValue());
            }
        }
        return null;
    }

    @Override
    public Integer deleteUserFromGroup(String userId, String groupId) throws GitApiRequestException {
        Map<String, String> tokenMap = JGitUtils.getStringTokenMap();

        String url = GitUrlBuilder.create().addGroups().addPathVariable(groupId).addMembers().addPathVariable(userId).build();
        try {
            return HttpUtils.requestDelete(url, tokenMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);
            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab:从指定的组中删除用户失败."
                            + "groupId: " + groupId
                            + "userId: " + userId).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public GroupModel convertToGroupModel(String responseText) {
        JSONObject jsonObject = JSONObject.parseObject(responseText);
        return this.convertToGroupModel(jsonObject);
    }

    @Override
    public Integer deleteGroup(String groupId) throws GitApiRequestException {
        String url = GitUrlBuilder.create().addGroups().addPathVariable(groupId).build();
        Map<String, String> tokenMap = JGitUtils.getStringTokenMap();
        try {
            return HttpUtils.requestDelete(url, tokenMap);
        } catch (Exception e) {
            String exceptionMessage = "Gitlab: 删除组失败. "
                    + "url: " + url
                    + ", groupId:" + groupId;
            Integer statusCode = GitApiRequestException.getStatusCode(e);
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public GroupModel convertToGroupModel(JSONObject jsonObject) {
        GroupModel groupModel = new GroupModel();
        groupModel.setId(jsonObject.getInteger(GroupField.ID.getValue()));
        groupModel.setName(jsonObject.getString(GroupField.NAME.getValue()));
        groupModel.setPath(jsonObject.getString(GroupField.PATH.getValue()));
        groupModel.setWebUrl(jsonObject.getString(GroupField.WEB_URL.getValue()));
        groupModel.setParentId(jsonObject.getInteger(GroupField.PARENT_ID.getValue()));
        return groupModel;
    }

}
