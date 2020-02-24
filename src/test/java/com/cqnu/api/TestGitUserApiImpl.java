package com.cqnu.api;

import com.cqnu.api.impl.UserApiImpl;
import com.cqnu.exception.GitApiRequestException;
import org.junit.Assert;
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
public class TestGitUserApiImpl {
    @Autowired
    private UserApiImpl userService;

    @Test
    public void testGetUserIdByUsername() throws GitApiRequestException {
        Integer userId = userService.getUserIdByUsername("owen");
        Assert.assertNotNull(userId);
        System.out.println(userId);
    }

    @Test(expected = GitApiRequestException.class)
    public void testGetUserIdByUsernameNotFound() throws GitApiRequestException {
        Integer userId = userService.getUserIdByUsername("owen1");
        Assert.assertNotNull(userId);
        System.out.println(userId);
    }
}
