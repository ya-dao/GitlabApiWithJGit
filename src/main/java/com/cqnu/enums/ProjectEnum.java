package com.cqnu.enums;

/**
 * 应用类型枚举
 *
 * @author zh
 * @date 2019/9/18
 */
public enum ProjectEnum {
    BACK_END("app", 0),
    FRONT_END("fe", 1);


    private String name;
    private Integer type;

    ProjectEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Integer getType() {
        return this.type;
    }
}
