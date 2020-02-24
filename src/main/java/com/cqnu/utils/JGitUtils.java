package com.cqnu.utils;

import com.cqnu.exception.GitApiRequestException;
import com.cqnu.exception.JGitOperationException;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * todo 添加注释 finish
 * todo 引入Apollo组件
 *
 * @author zh
 * @date 2019/9/20
 */
public class JGitUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitUtils.class);

    private static final String DEFAULT_ZIP_ARCHIVE_URL = "/repository/master/archive.zip";
    private static final String DEFAULT_VISIBILITY = "defaultVisibility";
    private static final String NAMESPACE_JGIT = "JGit";
    private static final String DEFAULT_VALUE = "";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PRIVATE_TOKEN = "privateToken";
    private static final String KEY_GIT_SERVER_URL = "gitServerUrl";
    private static final Config CONFIG = ConfigService.getConfig(NAMESPACE_JGIT);
    /**
     * 使用ThreadLocal解决线程安全问题
     */
    private static ThreadLocal<Map<String, Object>> tokenHolder = new ThreadLocal<>();

    private static String getUsername() {
        return CONFIG.getProperty(KEY_USERNAME, DEFAULT_VALUE);
    }

    private static String getPassword() {
        return CONFIG.getProperty(KEY_PASSWORD, DEFAULT_VALUE);
    }

    public static String getPrivateToken() {
        return CONFIG.getProperty(KEY_PRIVATE_TOKEN, DEFAULT_VALUE);
    }

    /**
     * 末尾不能带反斜框
     */
    static String getGitServerUrl() {
        return CONFIG.getProperty(KEY_GIT_SERVER_URL, DEFAULT_VALUE);
    }

    /**
     * 返回创建应用和群组时默认的可见性
     *
     * @return 默认的可见级别
     */
    public static String getDefaultVisibility() {
        return CONFIG.getProperty(DEFAULT_VISIBILITY, DEFAULT_VALUE);
    }

    /**
     * 统一携带访问token的参数列表(线程局部变量)
     *
     * @return 返回携带访问token的Map
     */
    public static Map<String, Object> getTokenMap() {
        Map<String, Object> parameterMap = tokenHolder.get();
        if (parameterMap == null) {
            parameterMap = new HashMap<>();
            tokenHolder.set(parameterMap);
        } else {
            parameterMap.clear();
        }
        // 向目标的参数集合parameterList中追加新的private_token
        parameterMap.put(KEY_PRIVATE_TOKEN, JGitUtils.getPrivateToken());
        return parameterMap;
    }

    public static Map<String, String> getStringTokenMap() {
        Map<String, Object> tokenMap = JGitUtils.getTokenMap();
        Map<String, String> parameterMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : tokenMap.entrySet()) {
            parameterMap.put(entry.getKey(), entry.getValue().toString());
        }
        return parameterMap;
    }

    /**
     * 根据默认的用户信息返回认证信息
     *
     * @return 默认的用户名
     */
    public static UsernamePasswordCredentialsProvider getCredential() {
        return new UsernamePasswordCredentialsProvider(getUsername(), getPassword());
    }

    /**
     * 从指定的URL克隆远程仓库到指定目录下的重载函数
     *
     * @see JGitUtils#cloneRemoteRepository(java.io.File, java.lang.String)
     */
    public static void cloneRemoteRepository(String directory, String gitRemoteUrl) throws JGitOperationException {
        cloneRemoteRepository(new File(directory), gitRemoteUrl);
    }


    /**
     * 从指定的URL克隆远程仓库到指定目录下
     *
     * @param directory    指定存放克隆的仓库的目录路径
     * @param gitRemoteUrl 需要克隆的远程仓库地址
     * @throws JGitOperationException 当出现以下情况时,会抛出此异常.
     *                                1. 远端仓库地址无效
     *                                2. 网络连接中数据传输失败
     *                                3. 克隆远程仓库操作失败
     */
    public static void cloneRemoteRepository(File directory, String gitRemoteUrl) throws JGitOperationException {
        try (final Git result = Git.cloneRepository()
                .setCredentialsProvider(JGitUtils.getCredential())
                .setURI(gitRemoteUrl)
                .setDirectory(directory)
                .call()
        ) {
           LOGGER.info("仓库克隆成功: {}", result.getRepository().getDirectory());
        } catch (InvalidRemoteException e) {
            final String message = "JGit: 远端仓库地址无效";
            throw new JGitOperationException(message, e);
        } catch (TransportException e) {
            final String message = "JGit: 网络连接失败";
            throw new JGitOperationException(message, e);
        } catch (GitAPIException e) {
            final String message = "JGit: 克隆远程仓库操作失败";
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 切换分支, 当该分支不存在时, 则自动创建并切换.
     *
     * @param directory  本地git文件夹路径
     * @param branchName 要切换的分支名称
     * @throws JGitOperationException 切换分支操作失败, 抛出此异常.
     */
    public static void checkoutBranch(String directory, String branchName) throws JGitOperationException {
        try (final Git git = getGit(directory)) {
            if (!git.branchList().call().isEmpty()) {
                git.checkout().setName(branchName).setCreateBranch(true).call();
                LOGGER.info("JGit: 切换分支成功. "
                        + "git directory = {}, branchName = {}", directory, git.getRepository().getBranch());
            } else {
                final String message = "JGit: 切换分支操作失败. 当前没有主分支, 请先完成一次commit操作.";
                throw new JGitOperationException(message);
            }
        } catch (IOException | GitAPIException e) {
            final String message = "JGit: 切换分支操作失败"
                    + "git directory = " + directory
                    + ", branchName = " + branchName;
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 创建新的本地临时仓库, 三个参数的重载写法
     *
     * @see JGitUtils#CreateNewLocalRepository(java.lang.String, java.lang.String, java.lang.String)
     */
    public static void CreateNewLocalRepository(String prefix, String filePath) throws JGitOperationException {
        CreateNewLocalRepository(prefix, "", filePath);
    }

    /**
     * 创建新的本地临时仓库
     *
     * @param prefix   本地仓库的前缀
     * @param suffix   本地仓库的后缀
     * @param filePath 本地仓库的文件夹路径
     * @throws JGitOperationException 创建文件夹或者初始化Git时失败时, 则抛出此异常.
     */
    public static void CreateNewLocalRepository(String prefix, String suffix, String filePath) throws JGitOperationException {
        // 1. 创建临时文件(指定的前缀 + 随机数 + 指定后缀)
        File tempFile = createNewTempFile(prefix, suffix, filePath);
        // 3. 创建新的本地git仓库
        try (Git git = Git.init().setDirectory(tempFile).call();) {
            LOGGER.info("git仓库初始化完毕. path: {}", tempFile.getName());
        } catch (GitAPIException e) {
            String message = "创建本地仓库出错. path: " + tempFile.getName();
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 添加新的远程push/pull地址
     *
     * @param directory  指定本地存放仓库的目录路径
     * @param remoteUrl  需要添加的远程地址
     * @param remoteName 代表此远程地址的名称
     * @throws JGitOperationException 当uri格式错误或者远程地址添加失败时, 则抛出此异常.
     */
    public static void addRemote(String directory, String remoteUrl, String remoteName) {
        try (Git git = getGit(directory)){
            if (remoteName == null) {
                remoteName = Constants.DEFAULT_REMOTE_NAME;
            }
            git.remoteSetUrl().setRemoteName(remoteName).setRemoteUri(new URIish(remoteUrl)).call();
        } catch (URISyntaxException e) {
            throw new JGitOperationException("JGit: uri格式错误. remoteUrl: " + remoteUrl, e);
        } catch (GitAPIException e) {
            throw new JGitOperationException("JGit: 设置远程地址失败. remoteUrl: " + remoteUrl + "remoteName: " + remoteName, e);
        }
    }

    /**
     * 添加文件到本地仓库
     *
     * @param directory 要添加的本地仓库目录地址
     */
    public static void addToLocalRepository(String directory) {
        try (Git git = getGit(directory)){
            git.add().addFilepattern(".").call();
        } catch (GitAPIException e) {
            final String message = "JGit: 添加文件到暂存区失败, directory: " + directory;
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 提交代码到本地仓库
     *
     * @param directory git本地仓库目录地址
     * @param message   本次代码提交所附带的描述
     */
    public static void commitToLocalRepository(String directory, String message) {
        try (Git git = getGit(directory)){
            git.commit().setMessage(message).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            message = "JGit: 提交文件到本地仓库失败";
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 简化参数的重载函数, 参考具体实现函数
     * @see JGitUtils#pushToRemoteRepository(java.lang.String, java.lang.String)
     */
    public static void pushToRemoteRepository(String directory) throws JGitOperationException {
       pushToRemoteRepository(directory, null);
    }

        /**
         * 推送代码到默认或者指定的远程仓库
         *
         * @param directory     git本地仓库目录地址
         * @param gitRemoteName 推送的远程名称, 若为null则采用默认的origin远程信息
         * @throws JGitOperationException 推送代码操作失败时, 抛出此异常
         */
    public static void pushToRemoteRepository(String directory, String gitRemoteName) throws JGitOperationException {
        try (Git git = getGit(directory)){
            PushCommand pushCommand = git.push().setCredentialsProvider(getCredential()).setPushAll();
            if (gitRemoteName != null) {
                pushCommand.setRemote(gitRemoteName);
            }
            pushCommand.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            final String message = "JGit: 推送代码操作失败";
            throw new JGitOperationException(message, e);
        }
    }

    /**
     * 推送代码到默认或者指定的远程仓库
     *
     * @param repositoryPath git本地仓库目录地址
     * @param gitRemoteName  推送的远程名称 若为null则采用默认的origin远程信息
     * @throws JGitOperationException pull代码操作失败时, 抛出此异常
     */
    public static void pullFromRemoteRepository(String repositoryPath, String gitRemoteName) throws JGitOperationException {
        // 1. 打开仓库并拉取代码
        try (Git git = getGit(repositoryPath)) {
            PullCommand pullCommand = git.pull().setCredentialsProvider(JGitUtils.getCredential());
            if (gitRemoteName != null) {
                pullCommand.setRemote(gitRemoteName);
            }
            pullCommand.call();
            LOGGER.info("JGit: pull成功. repository: {}, gitRemoteName: {}", repositoryPath ,gitRemoteName);
        } catch (GitAPIException e) {
            throw new JGitOperationException("JGit: pull远程代码出错. "
                    + "gitRemoteName: " + gitRemoteName, e);
        }
    }

    /**
     * 从已有的仓库路径中获取Git对象
     *
     * @param directory git本地仓库目录地址
     * @throws JGitOperationException 当仓库不存在或者无法访问时会抛出此异常,里面已经封装了状态码和错误信息
     */
    private static Git getGit(String directory) throws JGitOperationException {
        if (directory == null) {
            String message = "JGit: directory不能为null, 请传递有效的本地仓库路径, " +
                    "如需克隆仓库请调用JGitUtils#CreateNewLocalRepository. ";
            throw new JGitOperationException(message);
        }

        try {
            return Git.open(new File(directory));
        } catch (IOException e) {
            String message = "JGit: 本地仓库已经存在,但无法访问.";
            throw new JGitOperationException(message, e);
        }
    }


    /**
     * 删除临时文件夹
     */
    public static void deleteTempDirectory(String path) {
        if (path == null) {
            return;
        }

        delete(new File(path));
    }

    /**
     * 递归删除文件
     *
     * @param file 本次需要删除的文件
     */
    private static void delete(File file) {
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    delete(child);
                }
            }
        } else {
            file.delete();
        }
    }

    private static File createNewTempFile(String prefix, String suffix, String filePath) {
        File tempFile;
        try {
            tempFile = File.createTempFile(prefix, "", new File(filePath));
        } catch (IOException e) {
            throw new JGitOperationException("创建临时文件失败:" + prefix + suffix + filePath);
        }
        // 2. 删除已经存在的临时文件
        if (!tempFile.delete()) {
            throw new JGitOperationException("删除原临时文件夹失败:" + tempFile.getAbsolutePath());
        }
        return tempFile;
    }

    /**
     * 下载指定git应用的zip源代码包
     * @param gitUrl 应用git地址, 具体下载链接由此方法拼接
     * @param outputStream 用于写入下载内容的输出流
     */
    public static void downloadArchive(String gitUrl, OutputStream outputStream) {
        gitUrl += DEFAULT_ZIP_ARCHIVE_URL;
        LOGGER.info("下载应用. gitUrl = {}", gitUrl);
        try {
            HttpUtils.download(gitUrl, JGitUtils.getTokenMap(), outputStream);
        } catch (GitApiRequestException e) {
            String message = "JGit: 下载git应用的zip源代码包失败.";
            throw new JGitOperationException(message, e);
        }
    }
}
