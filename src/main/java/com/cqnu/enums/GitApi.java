package com.cqnu.enums;

/**
 * Git提供的api操作分类
 * @author zh
 * @date 2019/9/18
 */
public enum GitApi {

    PROJECTS("projects"),
    GROUPS("groups"),
    USERS("users"),
    MEMBERS("members");

    private String value;

    GitApi(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
