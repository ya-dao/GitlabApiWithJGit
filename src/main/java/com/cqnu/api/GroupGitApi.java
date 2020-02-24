package com.cqnu.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.model.GroupModel;

/**
 * @author zh
 * @date 2019/12/31
 */
public interface GroupGitApi {

    /**
     * 获取默认的组id, 组名为apps
     * @return 默认的组id
     * @throws GitApiRequestException 异常相关信息参考以下方法:
     * @see com.cqnu.api.GroupGitApi#getGroupIdsByGroupName(java.lang.String)
     * @see com.cqnu.api.GroupGitApi#parseGroupIdFromJsonArray(com.alibaba.fastjson.JSONArray, java.lang.String)
     */
    String getDefaultUIEngineGroupId() throws GitApiRequestException;

    /**
     * 创建组
     *
     * @param groupName 需要创建的组名
     * @return 创建完该组的信息
     * @throws GitApiRequestException 创建组失败时, 抛出此异常
     */
    GroupModel createGroup(String groupName) throws GitApiRequestException;

    /**
     * 将指定的用户ID添加到指定组ID里面
     *
     * @param userId  待添加的用户id
     * @param groupId 指定添加的组id
     * @return 返回响应的状态码, 正确情况为201 CREATED
     * @throws GitApiRequestException 添加用户到指定的组失败时, 抛出此异常
     */
    Integer addUserToGroup(String userId, String groupId) throws GitApiRequestException;

    /**
     * 根据组名获取到组的ID
     *
     * @param groupName 组名
     * @throws GitApiRequestException 以下两种情况会导致抛出此异常:
     *                                1. 请求响应失败
     *                                2. 该组名不存在
     */
    JSONArray getGroupIdsByGroupName(String groupName) throws GitApiRequestException;

    /**
     * 从jsonArray中解析第一个匹配的groupId
     * @param jsonArray 根据groupName搜索到的group列表
     * @param groupName 指定的groupName
     * @return 第一个匹配的groupId
     */
    String parseGroupIdFromJsonArray(JSONArray jsonArray, String groupName);

    /**
     * 将指定的用户ID从指定ID的应用里面删除
     *
     * @param userId  待删除的用户id
     * @param groupId 指定删除用户的应用id
     * @return 请求响应的状态码 正确情况应该返回 204 NO_CONTENT
     * @throws GitApiRequestException 从指定的组中删除用户失败时, 抛出此异常
     */
    Integer deleteUserFromGroup(String userId, String groupId) throws GitApiRequestException;

    GroupModel convertToGroupModel(String responseText);

    /**
     * 删除指定的组, 返回响应的状态码
     * @param groupId 指定的组id
     * @return 响应的状态码, 正确的返回应该是204 NO_CONTENT
     * @throws GitApiRequestException
     */
    Integer deleteGroup(String groupId) throws GitApiRequestException;

    GroupModel convertToGroupModel(JSONObject jsonObject);
}
