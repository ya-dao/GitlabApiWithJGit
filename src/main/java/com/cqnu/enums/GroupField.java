package com.cqnu.enums;

/**
 * 从群组操作返回的消息中提取部分感兴趣的字段
 * @author zh
 * @date 2019/9/18
 */
public enum GroupField {
    ID("id"),
    NAME("name"),
    PATH("path"),
    PARENT_ID("parent_id"),
    WEB_URL("web_url");


    private String value;

    GroupField(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

}
