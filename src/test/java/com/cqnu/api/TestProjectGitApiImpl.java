package com.cqnu.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.enums.ProjectEnum;
import com.cqnu.exception.GitApiRequestException;
import com.cqnu.model.ProjectModel;
import com.ctrip.framework.apollo.mockserver.EmbeddedApollo;
import jodd.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zh
 * @date 2019/9/18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class TestProjectGitApiImpl{

    // 启动apollo的mockServer
    @ClassRule
    public static EmbeddedApollo embeddedApollo = new EmbeddedApollo();

    private static final String PROJECT_NAME = "api-test-create";
    private static final String PROJECT_NAME_IN_GROUP = "api-test-create1";
    private static final String PROJECT_NAME_NEW = "api-test-create-new";
    private static final String GROUP_ID = "473";
    private static final String PROJECT_ID = "978";

    @Autowired
    private ProjectGitApi projectGitApi;

    @Autowired
    private UserApi userApi;

    @Test
    public void testConvertModel() {
        String wholeContent = "{\n" +
                "\t\"ssh_url_to_repo\":\"git@10.73.1.87:cadet_ZhangHao/api-test-create.git\",\n" +
                "\t\"issues_enabled\":true,\n" +
                "\t\"only_allow_merge_if_all_discussions_are_resolved\":false,\n" +
                "\t\"_links\":{\n" +
                "\t\t\"merge_requests\":\"http://10.73.1.87/api/v4/projects/680/merge_requests\",\n" +
                "\t\t\"members\":\"http://10.73.1.87/api/v4/projects/680/members\",\n" +
                "\t\t\"self\":\"http://10.73.1.87/api/v4/projects/680\",\n" +
                "\t\t\"repo_branches\":\"http://10.73.1.87/api/v4/projects/680/repository/branches\",\n" +
                "\t\t\"issues\":\"http://10.73.1.87/api/v4/projects/680/issues\",\n" +
                "\t\t\"events\":\"http://10.73.1.87/api/v4/projects/680/events\",\n" +
                "\t\t\"labels\":\"http://10.73.1.87/api/v4/projects/680/labels\"\n" +
                "       },\n" +
                "\t\"request_access_enabled\":false,\n" +
                "\t\"open_issues_count\":0,\n" +
                "\t\"snippets_enabled\":true,\n" +
                "\t\"created_at\":\"2019-09-18T08:43:43.134Z\",\n" +
                "\t\"import_status\":\"none\",\n" +
                "\t\"path\":\"api-test-create\",\n" +
                "\t\"archived\":false,\n" +
                "\t\"tag_list\":[],\n" +
                "\t\"last_activity_at\":\"2019-09-18T08:43:43.134Z\",\n" +
                "\t\"shared_runners_enabled\":true,\n" +
                "\t\"id\":680,\n" +
                "\t\"container_registry_enabled\":true,\n" +
                "\t\"owner\":{\n" +
                "\t\t\"avatar_url\":\"http://www.gravatar.com/avatar/399b3b53dbd053a86efc20ca027020a2?s=80&d=identicon\",\n" +
                "\t\t\"web_url\":\"http://10.73.1.87/cadet_ZhangHao\",\n" +
                "\t\t\"name\":\"ZhangHao\",\n" +
                "\t\t\"id\":136,\n" +
                "\t\t\"state\":\"active\",\n" +
                "\t\t\"username\":\"cadet_ZhangHao\"\n" +
                "   },\n" +
                "\t\"lfs_enabled\":true,\n" +
                "\t\"visibility\":\"internal\",\n" +
                "\t\"printing_merge_request_link_enabled\":true,\n" +
                "\t\"path_with_namespace\":\"cadet_ZhangHao/api-test-create\",\n" +
                "\t\"resolve_outdated_diff_discussions\":false,\n" +
                "\t\"merge_requests_enabled\":true,\n" +
                "\t\"jobs_enabled\":true,\n" +
                "\t\"shared_with_groups\":[],\n" +
                "\t\"http_url_to_repo\":\"http://10.73.1.87/cadet_ZhangHao/api-test-create.git\",\n" +
                "\t\"only_allow_merge_if_pipeline_succeeds\":false,\n" +
                "\t\"web_url\":\"http://10.73.1.87/cadet_ZhangHao/api-test-create\",\n" +
                "\t\"wiki_enabled\":true,\n" +
                "\t\"public_jobs\":true,\n" +
                "\t\"name\":\"api-test-create\",\n" +
                "\t\"creator_id\":136,\n" +
                "\t\"namespace\":{\n" +
                "\t\t\"path\":\"cadet_ZhangHao\",\n" +
                "\t\t\"kind\":\"user\",\n" +
                "\t\t\"name\":\"cadet_ZhangHao\",\n" +
                "\t\t\"id\":171,\n" +
                "\t\t\"full_path\":\"cadet_ZhangHao\"\n" +
                "   },\n" +
                "\t\"name_with_namespace\":\"ZhangHao / api-test-create\",\n" +
                "\t\"star_count\":0,\n" +
                "\t\"forks_count\":0,\n" +
                "\t\"runners_token\":\"QGjyVGGR45vX_mZx4oLh\"" +
                "}";

        ProjectModel model = projectGitApi.convertProjectModel(wholeContent, ProjectEnum.BACK_END);
        System.out.println(model.toString());
    }

    @Test
    public void testCreateRemoteProject() throws GitApiRequestException {
        final ProjectModel remoteProject =
                projectGitApi.createRemoteProject(PROJECT_NAME, ProjectEnum.FRONT_END);
        Assert.assertNotNull(remoteProject);
        Assert.assertNotNull(remoteProject.getProjectId());
        Assert.assertNotNull(remoteProject.getGitUrl());
        System.out.println(remoteProject);
    }

    @Test
    public void testCreateRemoteProjectWithGroup() throws GitApiRequestException {
        final ProjectModel remoteProject = projectGitApi.createRemoteProject(PROJECT_NAME_IN_GROUP, GROUP_ID, ProjectEnum.FRONT_END);
        Assert.assertNotNull(remoteProject);
        Assert.assertNotNull(remoteProject.getProjectId());
        Assert.assertNotNull(remoteProject.getGitUrl());
        System.out.println(remoteProject);
    }

    @Test(expected = GitApiRequestException.class)
    public void testCreateRemoteProjectFailed() throws GitApiRequestException {
        projectGitApi.createRemoteProject(PROJECT_NAME, ProjectEnum.BACK_END);
    }

    @Test
    public void testUpdateRemoteProject() throws GitApiRequestException {
        ProjectModel remoteProject = projectGitApi.updateRemoteProject(PROJECT_ID, PROJECT_NAME_NEW);
        System.out.println(remoteProject);
        Assert.assertNotNull(remoteProject);
        Assert.assertNotNull(remoteProject.getProjectId());
        Assert.assertNotNull(remoteProject.getGitUrl());
    }

    @Test
    public void testGetProjectIdByProjectName() throws GitApiRequestException {
        JSONArray nameArray = projectGitApi.getProjectIdByProjectName(PROJECT_NAME);
        Assert.assertNotNull(nameArray);
        for (int i = 0; i < nameArray.size(); i++) {
            JSONObject jsonObject = nameArray.getJSONObject(i);
            ProjectModel projectModel = projectGitApi.convertProjectModel(jsonObject, ProjectEnum.BACK_END);
            System.out.println(projectModel.toString());
        }
    }

    @Test
    public void testAddUserToProject() throws GitApiRequestException {
        Integer userId = userApi.getUserIdByUsername("owen");
        Integer statusCode = projectGitApi.addUserToProject(String.valueOf(userId), PROJECT_ID);
        Assert.assertNotNull(statusCode);
        Assert.assertEquals(statusCode.intValue(), HttpStatus.HTTP_CREATED);
    }

    @Test
    public void testDeleteUserFromProject() throws GitApiRequestException {
        Integer userId = userApi.getUserIdByUsername("owen");
        Integer statusCode = projectGitApi.deleteUserFromProject(String.valueOf(userId), PROJECT_ID);
        Assert.assertNotNull(statusCode);
        Assert.assertEquals(statusCode.intValue(), HttpStatus.HTTP_NO_CONTENT);
    }

    @Test
    public void testDeleteProject() throws GitApiRequestException {
        Integer statusCode = projectGitApi.deleteProject(PROJECT_ID);
        Assert.assertNotNull(statusCode);
        Assert.assertEquals(statusCode.intValue(), HttpStatus.HTTP_ACCEPTED);
    }
}
