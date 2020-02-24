package com.cqnu.enums;

/**
 * @author zh
 * @date 2019/9/19
 */
public enum  AccessLevel {

//    10 => Guest access
//    20 => Reporter access
//    30 => Developer access
//    40 => Master access
//    50 => Owner access # Only valid for groups

    GUEST(10),
    REPORTER(20),
    DEVELOPER(30),
    MASTER(40),
    OWNER(50);


    private int level;

    AccessLevel(int level){
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }
}
