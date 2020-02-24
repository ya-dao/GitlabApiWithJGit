package com.cqnu.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author zh
 * @date 2019/12/31
 */
public class TestJGitUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(TestJGitUtils.class);

    public static final String TEST_PATH = "D:\\git-api\\";
    public static final String GIT_REMOTE_URL = "http://10.73.1.87/iot/ui-engine/apps/testcreate.git";
    public static final String TEMP_DIRECTORY = "TestGitRepository2738379276988133503";
    public static final String LOCAL_REPOSITORY_DIRECTORY = TEST_PATH + TEMP_DIRECTORY;
    public static final String BRANCH_NAME_SINGLE = "develop";
    public static final String BRANCH_NAME_PATH = "feature/gitlab";

    /**
     * 查检测试目录是否存在, 如果不存在则先创建
     */
    @BeforeClass
    public static void setup(){
        boolean notExists = Files.notExists(Paths.get(LOCAL_REPOSITORY_DIRECTORY));
        if (notExists) {
            try {
                Files.createDirectory(Paths.get(LOCAL_REPOSITORY_DIRECTORY));
            } catch (IOException e) {
                LOGGER.error("创建测试文件夹失败, 路径: {}", LOCAL_REPOSITORY_DIRECTORY , e);
            }
        }
    }

    @Test
    public void testCloneRemoteRepository() {
        JGitUtils.cloneRemoteRepository(LOCAL_REPOSITORY_DIRECTORY, GIT_REMOTE_URL);
    }

    @Test
    public void testCheckoutBranch_PathName() {
        JGitUtils.checkoutBranch(TEST_PATH, BRANCH_NAME_PATH);
    }

    @Test
    public void testCheckoutBranch_SingleName() {
        JGitUtils.checkoutBranch(TEST_PATH, BRANCH_NAME_SINGLE);
    }

    @Test
    public void testCreateNewLocalRepository() {
        JGitUtils.CreateNewLocalRepository("TestGitRepository", TEST_PATH);
    }

    @Test
    public void testAddRemote() {
        JGitUtils.addRemote(LOCAL_REPOSITORY_DIRECTORY, GIT_REMOTE_URL, null);
    }

    @Test
    public void testAddToLocalRepository() {
        JGitUtils.addToLocalRepository(LOCAL_REPOSITORY_DIRECTORY);
    }

    @Test
    public void testCommitToLocalRepository() {
        JGitUtils.commitToLocalRepository(LOCAL_REPOSITORY_DIRECTORY, "init repository");
    }

    @Test
    public void testPushToRemoteRepository() {
        JGitUtils.pushToRemoteRepository(LOCAL_REPOSITORY_DIRECTORY, GIT_REMOTE_URL);
    }

    @Test
    public void testPullFromRemoteRepository() {
        JGitUtils.pullFromRemoteRepository(LOCAL_REPOSITORY_DIRECTORY, null);
    }

    @Test
    public void testDeleteTempDirectory() {
        JGitUtils.deleteTempDirectory(LOCAL_REPOSITORY_DIRECTORY);
    }
}
