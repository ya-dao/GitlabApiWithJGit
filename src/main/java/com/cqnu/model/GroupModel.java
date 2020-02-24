package com.cqnu.model;

import lombok.Data;

/**
 * @author zh
 * @date 2019/9/19
 */
@Data
public class GroupModel {

    private Integer id;
    private String name;
    private String path;
    private String webUrl;
    private Integer parentId;
}
