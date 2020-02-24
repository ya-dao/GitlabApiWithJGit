# 简介
使用JGit框架完成Git常用操作和调用Gitlab提供的Api.
涉及的依赖如下:
1. Apollo配置中心
2. Jodd框架的HTTP模块
3. Spring-Aop相关模块(只是简单的统计下方法耗时, 可移除)
4. FastJSON


## 准备工作
* jar中依赖apollo组件, 需要在`resources/META-INF`下创建`app.properties`文件, 在里面添加`app.name`, `app.id`属性.
## 使用方法
1. Git的本地操作均以静态方法的形式封装在JGitUtils类里面, 具体方法如下:
    * addRemote()
    * addToLocalRepository()
    * cloneRemoteRepository()
    * commitToLocalRepository()
    * createNewLocalRepository()
    * deleteTempDirectory()
    * pullFromRemoteRepository()
    * pushToRemoteRepository()
    * checkoutBranch()
2. Gitlab的Api操作统一分类定义在了XXGitApi接口中, 有如下几类:
    * 应用(application)相关Api
    * 组(group)相关Api
    * 用户(user)相关Api