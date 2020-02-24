package com.cqnu.model;

import lombok.Data;

/**
 * @author zh
 * @date 2019/9/18
 */
@Data
public class ProjectModel {
    private Integer projectId;
    /**
     * 不指定默认为当前用户的namespace所对应的groupId
     */
    private Integer groupId;
    private String gitUrl;
    /**
     * 应用前/后端类型
     */
    private Integer projectType;
}
