package com.cqnu.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.enums.ProjectEnum;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.model.ProjectModel;
import com.cqnu.api.impl.ProjectGitApiImpl;

/**
 * @author zh
 * @date 2019/12/31
 */
public interface ProjectGitApi {
    /**
     * 三个参数的简化版,如果不指定groupId,默认传null
     *
     * @see ProjectGitApiImpl#createRemoteProject(String, String, com.cqnu.enums.ProjectEnum)
     */
    ProjectModel createRemoteProject(String projectName, ProjectEnum projectEnum) throws GitApiRequestException;

    /**
     * 创建远端应用, 如果成功则返回该应用相关信息
     *
     * @param projectName 应用名称
     * @param groupId     应用所属namespace_id(可代表组ID),如果不指定Gitlab在创建时默认为用户名.
     * @param projectEnum 此项目类型
     * @return 如果成功则返回该应用相关信息模型
     * @throws GitApiRequestException 创建远端应用失败时,则抛出此异常
     */
    ProjectModel createRemoteProject(String projectName, String groupId, ProjectEnum projectEnum)
            throws GitApiRequestException;

    /**
     * 修改gitlab中指定id的project信息, 暂只支持修改应用名称
     * @param projectId 指定修改的id
     * @param projectName 新的应用名称
     * @return 返回修改后的新信息
     * @throws GitApiRequestException 修改应用信息失败时, 则抛出此异常
     */
    ProjectModel updateRemoteProject(String projectId, String projectName) throws GitApiRequestException;

    /**
     * 根据指定的应用名称查找应用信息
     */
    JSONArray getProjectIdByProjectName(String projectName) throws GitApiRequestException;

    /**
     * 另外一个函数的参数重载形式
     *
     * @param response    http响应的内容
     * @see ProjectGitApiImpl#convertProjectModel(com.alibaba.fastjson.JSONObject, com.cqnu.enums.ProjectEnum)
     */
    ProjectModel convertProjectModel(String response, ProjectEnum projectEnum);

    /**
     * 另外一个函数的参数重载形式
     *
     * @param response    http响应的内容
     * @see ProjectGitApiImpl#convertProjectModel(com.alibaba.fastjson.JSONObject, com.cqnu.enums.ProjectEnum)
     */
    ProjectModel convertProjectModel(String response);

    /**
     * 另外一个函数的参数重载形式
     *
     * @param jsonObject    http响应的内容转换成的json
     * @see ProjectGitApiImpl#convertProjectModel(com.alibaba.fastjson.JSONObject, com.cqnu.enums.ProjectEnum)
     */
    ProjectModel convertProjectModel(JSONObject jsonObject);

    /**
     * 将Http请求响应的内容转换成ProjectModel
     *
     * @param jsonObject  http响应的内容转换成的json
     * @param projectEnum 此项目类型
     * @return 返回封装完的ProjectModel对象
     */
    ProjectModel convertProjectModel(JSONObject jsonObject, ProjectEnum projectEnum);

    /**
     * 将指定的用户ID添加到指定组ID里面
     *
     * @param userId    待添加的用户id
     * @param projectId 指定添加的应用id
     * @throws GitApiRequestException 添加用户到指定的应用失败时, 抛出此异常
     * @return 返回响应的状态码, 成功应该返回201 CREATED
     */
    Integer addUserToProject(String userId, String projectId) throws GitApiRequestException;

    /**
     * 删除指定ID的应用
     *
     * @throws GitApiRequestException 删除指定的应用出错时, 抛出此异常
     * @return 返回响应的状态码, 成功返回202 ACCEPTED
     */
    Integer deleteProject(String projectId) throws GitApiRequestException;

    /**
     * 将指定的用户ID从指定ID的应用里面删除
     *
     * @param userId    待删除的用户id
     * @param projectId 指定删除用户的应用id
     * @throws GitApiRequestException 删除指定应用里面的指定用户失败时, 抛出此异常
     * @return 返回状态码, 成功应该返回204 NO_CONTENT
     */
    Integer deleteUserFromProject(String userId, String projectId) throws GitApiRequestException;
}
