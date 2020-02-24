package com.cqnu.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.api.ProjectGitApi;
import com.cqnu.enums.AccessLevel;
import com.cqnu.enums.ProjectEnum;
import com.cqnu.enums.ProjectResponseKey;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.exception.ParameterValidateException;
import com.cqnu.model.ExceptionModel;
import com.cqnu.model.ProjectModel;
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
 * @date 2019/9/19
 */
@Component
public class ProjectGitApiImpl implements ProjectGitApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGitApiImpl.class);

    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_VISIBILITY = "visibility";
    private static final String PARAMETER_NAMESPACE_ID = "namespace_id";
    private static final String PARAMETER_USER_ID = "user_id";
    private static final String PARAMETER_ACCESS_LEVEL = "access_level";

    @Override
    public ProjectModel createRemoteProject(String projectName, ProjectEnum projectEnum) throws GitApiRequestException {
        return this.createRemoteProject(projectName, null, projectEnum);
    }

    @Override
    public ProjectModel createRemoteProject(String projectName, String groupId, ProjectEnum projectEnum)
            throws GitApiRequestException {
        Map<String, Object> parameterMap = JGitUtils.getTokenMap();
        parameterMap.put(PARAMETER_NAME, projectName);
        parameterMap.put(PARAMETER_VISIBILITY, JGitUtils.getDefaultVisibility());
        if (groupId != null) {
            parameterMap.put(PARAMETER_NAMESPACE_ID, groupId);
        }

        String url = GitUrlBuilder.create().addProjects().build();
        try {
            String responseText = HttpUtils.requestPostForString(url, parameterMap);
            return convertProjectModel(responseText, projectEnum);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = "Gitlab: 创建远端应用失败."
                    + "url: " + url
                    + ", projectName: " + projectName
                    + ", groupId: " + groupId
                    + ", projectType: " + projectEnum.getType();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public ProjectModel updateRemoteProject(String projectId, String projectName) throws GitApiRequestException {
        Map<String, Object> tokenMap = JGitUtils.getTokenMap();
        tokenMap.put(PARAMETER_NAME, projectName);

        String url = GitUrlBuilder.create().addProjects().addPathVariable(projectId).build();
        try {
            JSONObject jsonObject = HttpUtils.requestPutForJsonObject(url, tokenMap);
            return convertProjectModel(jsonObject);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = "Gitlab: 修改远端应用失败."
                    + "url: " + url
                    + ", projectId: " + projectId
                    + ", projectName: " + projectName;
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public JSONArray getProjectIdByProjectName(String projectName) throws GitApiRequestException {
        Map<String, String> parameterMap = JGitUtils.getStringTokenMap();
        parameterMap.put("search", projectName);

        String url = GitUrlBuilder.create().addProjects().build();
        try {
            return HttpUtils.requestGetForJsonArray(url, parameterMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = "Gitlab: 根据指定的应用名称查找应用信息失败."
                    + "url: " + url
                    + ", projectName: " + projectName;
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public ProjectModel convertProjectModel(String response, ProjectEnum projectEnum) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        return this.convertProjectModel(jsonObject, projectEnum);
    }

    @Override
    public ProjectModel convertProjectModel(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        return this.convertProjectModel(jsonObject, null);
    }

    @Override
    public ProjectModel convertProjectModel(JSONObject jsonObject) {
        return this.convertProjectModel(jsonObject, null);
    }

    @Override
    public ProjectModel convertProjectModel(JSONObject jsonObject, ProjectEnum projectEnum) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setProjectId(jsonObject.getInteger(ProjectResponseKey.PROJECT_ID.getValue()));

        JSONObject groupJson = jsonObject.getJSONObject(ProjectResponseKey.GROUP.getValue());
        if (groupJson != null) {
            Integer groupId = groupJson.getInteger(ProjectResponseKey.GROUP_ID.getValue());
            projectModel.setGroupId(groupId);
        }

        String httpUrl = jsonObject.getString(ProjectResponseKey.HTTP_URL_TO_REPO.getValue());
        projectModel.setGitUrl(httpUrl);

        if (projectEnum != null) {
            projectModel.setProjectType(projectEnum.getType());
        }
        return projectModel;
    }

    @Override
    public Integer addUserToProject(String userId, String projectId) throws GitApiRequestException {
        if (userId == null || projectId == null) {
            throw new ParameterValidateException(HttpStatus.HTTP_BAD_REQUEST,
                    "参数不能为null。userId: " + userId + ", projectId: " + projectId);
        }
        Map<String, Object> tokenMap = JGitUtils.getTokenMap();
        tokenMap.put(PARAMETER_USER_ID, userId);
        tokenMap.put(PARAMETER_ACCESS_LEVEL,
                String.valueOf(AccessLevel.DEVELOPER.getLevel()));

        String url = GitUrlBuilder.create().addProjects().addPathVariable(projectId).addMembers().build();
        try {
            return HttpUtils.requestPostForStatusCode(url, tokenMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab:添加用户到指定的应用失败. "
                            + "url: " + url
                            + ", projectId: " + projectId
                            + ", userId: " + userId).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public Integer deleteProject(String projectId) throws GitApiRequestException {
        Map<String, String> parameterMap = JGitUtils.getStringTokenMap();
        String url = GitUrlBuilder.create().addProjects().addPathVariable(projectId).build();
        try{
            return HttpUtils.requestDelete(url, parameterMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab:删除指定的应用出错. "
                            + "url: " + url
                            + ", projectId: " + projectId).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }

    @Override
    public Integer deleteUserFromProject(String userId, String projectId) throws GitApiRequestException {
        String url = GitUrlBuilder.create().addProjects().addPathVariable(projectId).addMembers().addPathVariable(userId).build();

        Map<String, String> parameterMap = JGitUtils.getStringTokenMap();
        try {
            return HttpUtils.requestDelete(url, parameterMap);
        } catch (Exception e) {
            Integer statusCode = GitApiRequestException.getStatusCode(e);

            String exceptionMessage = new ExceptionModel(statusCode,
                    "Gitlab:删除指定应用里面的指定用户失败. "
                            + "url: " + url
                            + ", projectId:" + projectId
                            + ", userId: " + userId).toString();
            throw new GitApiRequestException(exceptionMessage, statusCode, e);
        }
    }
}
