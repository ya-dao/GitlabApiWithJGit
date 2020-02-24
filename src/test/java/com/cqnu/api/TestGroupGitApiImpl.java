package com.cqnu.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.model.GroupModel;
import jodd.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zh
 * @date 2019/9/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class TestGroupGitApiImpl {

    private static final String GROUP_NAME = "api-test-group";
    private static final String GROUP_NAME_APPS = "apps";

    private static final String GROUP_ID = "488";
    private static final String USER_ID = "179";

    @Autowired
    private GroupGitApi groupGitApi;

    @Test
    public void testCreateGroup() throws GitApiRequestException {
        GroupModel group = groupGitApi.createGroup(GROUP_NAME);
        System.out.println(group.toString());
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getId());
    }

    @Test
    public void testGetGroupIdsByGroupName() throws GitApiRequestException {
        JSONArray groupIds = groupGitApi.getGroupIdsByGroupName(GROUP_NAME);
        for (int i = 0; i < groupIds.size(); i++) {
            JSONObject jsonObject = groupIds.getJSONObject(i);
            GroupModel groupModel = groupGitApi.convertToGroupModel(jsonObject);
            System.out.println(groupModel);
        }
    }

    @Test
    public void testParseGroupIdFromJsonArray() throws GitApiRequestException {
        JSONArray groupIds = groupGitApi.getGroupIdsByGroupName(GROUP_NAME);
        String groupId = groupGitApi.parseGroupIdFromJsonArray(groupIds, GROUP_NAME);
        Assert.assertNotNull(groupId);
        System.out.println(groupId);
    }

    @Test
    public void testAddUserToGroup() throws GitApiRequestException {
        Integer statusCode = groupGitApi.addUserToGroup(USER_ID, GROUP_ID);
        Assert.assertEquals(statusCode.intValue(), HttpStatus.HTTP_CREATED);
    }

    @Test
    public void testDeleteUserFromGroup() throws GitApiRequestException {
        Integer statusCode = groupGitApi.deleteUserFromGroup(USER_ID, GROUP_ID);
        Assert.assertNotNull(statusCode);
        Assert.assertEquals(statusCode.intValue(), HttpStatus.HTTP_NO_CONTENT);
    }

    @Test
    public void testDeleteGroup() throws GitApiRequestException {
        Integer statusCode = groupGitApi.deleteGroup(GROUP_ID);
        Assert.assertNotNull(statusCode);
        Assert.assertEquals(statusCode.intValue(),HttpStatus.HTTP_NO_CONTENT);
    }


}