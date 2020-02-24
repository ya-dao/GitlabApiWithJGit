package com.cqnu.enums;

/**
 * Project Api在响应Json中对应的字段名称
 * @author zh
 * @date 2019/9/18
 */
public enum ProjectResponseKey {
    PROJECT_ID("id"),
    GROUP("namespace"),
    GROUP_ID("id"),
    HTTP_URL_TO_REPO("http_url_to_repo");


    private String value;

    ProjectResponseKey(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

}
