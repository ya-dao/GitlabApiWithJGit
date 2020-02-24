package com.cqnu.utils;

import com.cqnu.enums.GitApi;

/**
 * GitApi的Url构造器,防止手工拼凑出错
 * @author zh
 * @date 2019/9/20
 */
public class GitUrlBuilder {

    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_USER_ID = "user_id";
    private static final String PARAMETER_GROUP_ID = "group_id";
    private static final String PARAMETER_PROJECT_ID = "project_id";

    private String url;

    /**
     * 惟一构造方法,默认从HttpUtils中获取Url进行初始化,不支持手动指定
     */
    public GitUrlBuilder() {
        url = JGitUtils.getGitServerUrl();
    }

    /**
     * 静态方法获取HttpUrlBuilder对象,创建一个构造器,默认从HttpUtils中获取Url初始化该构造器
     * @return 一个构造器对象
     */
    public static GitUrlBuilder create(){
        GitUrlBuilder builder = new GitUrlBuilder();
        builder.url = JGitUtils.getGitServerUrl();
        return builder;
    }

    /**
     * 添加用户path
     * @return 当前实例,方便链式调用
     */
    public GitUrlBuilder addUsers(){
        url = url + "/" + GitApi.USERS.getValue();
        return this;
    }
    /**
     * 添加项目path
     * @return 当前实例,方便链式调用
     */
    public GitUrlBuilder addProjects(){
        url = url + "/" + GitApi.PROJECTS.getValue();
        return this;
    }

    /**
     * 添加群组path
     */
    public GitUrlBuilder addGroups(){
        url = url + "/" + GitApi.GROUPS.getValue();
        return this;
    }

    /**
     * 添加成员path
     */
    public GitUrlBuilder addMembers(){
        url = url + "/" + GitApi.MEMBERS.getValue();
        return this;
    }

    /**
     * 添加参数group_id
     */
    public GitUrlBuilder addParameterGroupId(){
        url = url + "/:" + PARAMETER_GROUP_ID;
        return this;
    }

    /**
     * 添加参数user_id
     */
    public GitUrlBuilder addParameterUserId(){
        url = url + "/:" + PARAMETER_USER_ID;
        return this;
    }

    /**
     * 添加参数project_id
     */
    public GitUrlBuilder addParameterProjectId(){
        url = url + "/:" + PARAMETER_PROJECT_ID;
        return this;
    }

    /**
     * 添加参数id
     */
    public GitUrlBuilder addParameterId(){
        url = url + "/:" + PARAMETER_ID;
        return this;
    }

    /**
     * 添加pathVariable
     *
     * 用于在Post请求中无法使用entity中的nameValuePair替换pathVariable的情况
     */
    public GitUrlBuilder addPathVariable(String pathVariable){
        url = url + "/" + pathVariable;
        return this;
    }

    /**
     * 将构造器中的内容以需要的的形式返回
     * @return 将构造好的url以字符串类型返回
     */
    public String build(){
        return this.url;
    }
}
