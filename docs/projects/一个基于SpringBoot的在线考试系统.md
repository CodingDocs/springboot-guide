[大家好！我是 Guide 哥，Java 后端开发。一个会一点前端，喜欢烹饪的自由少年。](https://www.yuque.com/docs/share/71251673-1fef-416e-93d7-489a25a9eda5?#%20%E3%80%8A%E8%B5%B0%E8%BF%91JavaGuide%E3%80%8B)

## 前言

最近看到了一个考试系统，感觉做的挺不错，并且也比较成熟，所以我就简单玩了一下。另外，考试系统应用场景还挺多的，不论是对于在校大学生还是已经工作的小伙伴，并且，类似的私活也有很多。

![在线考试系统后台管理主页](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/FireShot%20Capture%20027%20-%20%E4%B8%BB%E9%A1%B5%20-%20localhost.png)

下面我就把这个项目分享给小伙伴们，非常值得学习，拿来即用！

_为了一步一步演示，让小伙伴们都能成功部署/运行项目， Guide 哥自己本地搭建了项目环境，并将项目成功跑了起来。照着我的步骤，新手也能成功把项目跑起来！_

_如果你“感动”的话，点个赞/在看，就是对我最大的支持！_

另外，以下内容不涉及代码分析，整体代码结构比较清晰，熟悉了基本功能之后会很容易看明白。

## 介绍

[uexam](https://gitee.com/mindskip/uexam) 是一款前后端分离的在线考试系统。这款在线考试系统，不光支持 web 端，同时还支持微信小程序端。

[uexam](https://gitee.com/mindskip/uexam) 界面设计美观，代码整体结构清晰，表设计比较规范。

[uexam](https://gitee.com/mindskip/uexam) 后端基于 Spring Boot 2.0+MySQL/PostgreSQL+Redis+MyBatis，前端基于 Vue，采用前端后端分离开发！

另外，这个项目提供了 MySQL 和 PostgreSQL 两种不同的数据库版本，下面我以 PostgreSQL 数据库版本的来演示（_建议大家使用和体验 PostgreSQL 版本_）。

项目地址：[https://gitee.com/SnailClimb/uexam](https://gitee.com/SnailClimb/uexam) 。

## 软件架构

![软件架构图](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E8%BD%AF%E4%BB%B6%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)

## 使用效果

样式以及操作体验都是非常不错的，这也是我推荐这个项目很重要的一个原因。

### 管理端

#### 添加学科

在创建题目之前，你需要首要创建学科。这里我们创建的学科是编程，年级是三年级。

![添加学科](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E6%B7%BB%E5%8A%A0%E5%AD%A6%E7%A7%91.jpg)

#### 添加题目

可以看到这里可以添加多种题型: 单选题、多选题、判断题、填空题、简答题。

![添加题目](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E6%B7%BB%E5%8A%A0%E9%A2%98%E7%9B%AE.jpg)

我们以单选题为例，添加题目界面如下。

![添加题目页面](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/iShot2020-09-03%E4%B8%8B%E5%8D%8805.25.30.jpg)

添加成功之后，题目列表就会出现我们刚刚添加的题目。

![题目创建成功](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E9%A2%98%E7%9B%AE%E5%88%9B%E5%BB%BA%E6%88%90%E5%8A%9F.jpg)

#### 添加试卷

有了学科和题目之后才能添加试卷。

![添加试卷](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E6%B7%BB%E5%8A%A0%E8%AF%95%E5%8D%B7.jpg)

添加成功之后，试卷列表就会出现我们刚刚添加的试卷。

![试卷创建成功](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E8%AF%95%E5%8D%B7%E5%88%9B%E5%BB%BA%E6%88%90%E5%8A%9F.jpg)

#### 添加学生

**注意：这里的学生要和我们前面创建的学科对应的年级对应上。**

![添加学生](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E6%B7%BB%E5%8A%A0%E5%AD%A6%E7%94%9F.jpg)

### 学生端

使用我们刚刚创建的学生账号登录，你会发现主页多了一个试卷。这个试卷就是我们刚刚在管理端创建的。

![学生端-主页](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E5%AD%A6%E7%94%9F%E7%AB%AF-%E4%B8%BB%E9%A1%B5.jpg)

试卷答题界面如下。

![学生端-试卷](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/%E5%AD%A6%E7%94%9F%E7%AB%AF-%E8%AF%95%E5%8D%B7.jpg)

## 启动

### 后端

我们这里以 PostgreSQL 数据库版本来演示。

#### 安装 PostgreSQL

**这里我们使用 Docker 下载最近版的 PostgreSQL 镜像 ，默认大家已经安装了 Docker。**

```bash
$ docker pull postgres
```

**查看 PostgreSQL 镜像：**

```bash
$ docker images |grep postgres
postgres                latest              62473370e7ee        2 weeks ago         314MB
```

**运行 PostgreSQL：**

```bash
$ docker run -d -p 5432:5432 --name postgresql -e POSTGRES_PASSWORD=123456 postgres
```

#### 安装 Redis

**这里我们使用 Docker 下载最近版的 Redis 镜像 ，默认大家已经安装了 Docker。**

```bash
$ docker pull redis
```

**查看 Redis 镜像：**

```bash
$ docker images |grep redis
```

**运行 Redis：**

```bash
$ docker run -itd --name redis-test -p 6379:6379 redis
```

#### 创建数据库并执行数据库脚本

首先创建一个名字叫做`xzs` 的数据库，然后执行相应的数据库脚本即可（数据库脚本在 `uexam/source/xzs/sql` 目录下。）。

#### 配置文件修改

使用 IntelliJ IDEA 打开 `uexam/source/xzs` （后台代码），修改 `application-dev.yml` ，将 postgesql/mysql、redis 的服务地址改为自己本地的。

#### 启动项目

直接运行 `XzsApplication` 即可。

![](https://guide-blog-images.oss-cn-shenzhen.aliyuncs.com/2020-9/image-20200903180710467.png)

启动成功后，打开下面的链接即可跳转到对应的端：

- 学生系统地址：http://localhost:8000/student
- 管理端地址：http://localhost:8000/admin

**注意：这种方式，前端虽然也启动了，也能访问，不过是内嵌在后端项目中。如果如果我们需要前后端分离的话，需要单独运行前端项目**

### 前端

小程序端的就不演示了，我这里只演示一下 web 端的。

web 端代码在 `uexam/source/vue` 下，我们需要首先进入这个目录,然后分别对 `xzs-admin` (管理端) 和 `xzs-student` （学生端）执行下面两个命令。

**1.下载相关依赖**

```bash
$ npm install
```

**2.启动项目**

```bash
$ npm run serve
```

启动完成之后，打开下面的链接即可跳转到对应的端：

- 学生系统地址：http://localhost:8001
- 管理端地址：http://localhost:8002