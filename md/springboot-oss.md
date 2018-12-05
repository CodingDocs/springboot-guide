笔主很早就开始用阿里云OSS 存储服务当做自己的图床了。如果没有用过阿里云OSS 存储服务或者不是很了解这个东西的可以看看官方文档，我这里就不多做介绍了。阿里云对象存储 OSS文档，：

[https://help.aliyun.com/product/31815.html?spm=a2c4g.11186623.6.540.4e401c62EyJK5T](https://help.aliyun.com/product/31815.html?spm=a2c4g.11186623.6.540.4e401c62EyJK5T)

本篇文章会介绍到 SpringBoot 整合阿里云OSS 存储服务实现文件上传下载以及简单的查看。其实今天将的应该算的上是一个简单的小案例了，涉及到的知识点还算是比较多。

<!-- MarkdownTOC -->

- [一 开发前的准备](#一-开发前的准备)
    - [1.1 前置知识](#11-前置知识)
    - [1.2 环境参数](#12-环境参数)
    - [1.3 你能学到什么](#13-你能学到什么)
    - [1.4 创建工程](#14-创建工程)
    - [1.5 项目结构](#15-项目结构)
    - [1.6 配置 pom 文件中的相关依赖](#16-配置-pom-文件中的相关依赖)
- [二 配置阿里云 OSS 存储相关属性](#二-配置阿里云-oss-存储相关属性)
    - [2.1 通过常量类配置（本项目使用的方式）](#21-通过常量类配置本项目使用的方式)
    - [2.2  通过.properties 配置](#22-通过properties-配置)
- [三 工具类相关方法编写](#三-工具类相关方法编写)
- [四 Controller 层编写相关测试方法](#四-controller-层编写相关测试方法)
- [五 启动类](#五-启动类)
- [六 上传图片相关前端页面](#六-上传图片相关前端页面)
- [七 测试我们的图床](#七-测试我们的图床)

<!-- /MarkdownTOC -->


## 一 开发前的准备

### 1.1 前置知识

具有 Java 基础以及SpringBoot 简单基础知识即可。

###  1.2 环境参数
 
- 开发工具：IDEA
- 基础工具：Maven+JDK8
- 所用技术：SpringBoot+阿里云OSS 存储服务 Java 相关API
- SpringBoot版本：2.1.0

### 1.3 你能学到什么


- SpringBoot 整合 阿里云OSS 存储服务并编写相关工具类
- SpringBoot 整合 thymeleaf 并实现前后端传值
- SpringBoot 从配置文件读取值并注入到类中
- 如何自己搭建一个图床使用（通过前端选择图片，支持预览，但不支持修改图片）

### 1.4 创建工程

创建一个基本的 SpringBoot 项目，我这里就不多说这方面问题了，具体可以参考下面这篇文章：

https://blog.csdn.net/qq_34337272/article/details/79563606

### 1.5 项目结构 

![项目结构 ](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/75109079.jpg)

### 1.6 配置 pom 文件中的相关依赖

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- Thymeleaf-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <!-- 阿里云OSS-->
        <dependency>
            <groupId>com.aliyun.oss</groupId>
            <artifactId>aliyun-sdk-oss</artifactId>
            <version>2.4.0</version>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
```

## 二 配置阿里云 OSS 存储相关属性

我在项目中使用的通过常量类来配置，不过你也可以使用 .properties 配置文件来配置，然后`@ConfigurationProperties` 注解注入到类中。

### 2.1 通过常量类配置（本项目使用的方式）

AliyunOSSConfigConstant.java

```java
/**
 * @Auther: SnailClimb
 * @Date: 2018/12/4 15:09
 * @Description: 阿里云OSS存储的相关常量配置.我这里通过常量类来配置的，当然你也可以通过.properties 配置文件来配置，
 * 然后利用 SpringBoot 的@ConfigurationProperties 注解来注入
 */
public class AliyunOSSConfigConstant {
    //私有构造方法 禁止该类初始化
    private AliyunOSSConfigConstant() {
    }

    //仓库名称
    public static final String BUCKE_NAME = "my-blog-to-use";
    //地域节点
    public static final String END_POINT = "oss-cn-beijing.aliyuncs.com";
    //AccessKey ID
    public static final String AccessKey_ID = "你的AccessKeyID";
    //Access Key Secret
    public static final String AccessKey_Secret = "你的AccessKeySecret";
    //仓库中的某个文件夹
    public static final String FILE_HOST = "test";
}
```



到阿里云 OSS 控制台：[https://oss.console.aliyun.com/overview](https://oss.console.aliyun.com/overview)获取上述相关信息：

获取 BUCKE_NAME 和 END_POINT：

![获取BUCKE_NAME和END_POINT](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/62719967.jpg)

获取AccessKey ID和Access Key Secret第一步：

![获取AccessKey ID和Access Key Secret第一步](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/56702589.jpg)

获取AccessKey ID和Access Key Secret第二步：

![获取AccessKey ID和Access Key Secret第二步](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-5/3395348.jpg)

### 2.2  通过.properties 配置

```properties
#OSS配置
aliyun.oss.bucketname=my-blog-to-use
aliyun.oss.endpoint=oss-cn-beijing.aliyuncs.com
#阿里云主账号AccessKey拥有所有API的访问权限，风险很高。建议创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
aliyun.oss.keyid=你的AccessKeyID
aliyun.oss.keysecret=你的AccessKeySecret
aliyun.oss.filehost=test
```

然后新建一个类将属性注入：

```java
@Component
@PropertySource(value = "classpath:application-oss.properties")
@ConfigurationProperties(prefix = "aliyun.oss")
/**
 * 阿里云oss的配置类
 */
public class AliyunOSSConfig {
    private String bucketname;
    private String endpoint;
    private String keyid;
    private String keysecret;
    private String filehost;
    ...
    此处省略getter、setter以及 toString方法
}    
```


## 三 工具类相关方法编写

该工具类主要提供了三个方法：上传文件 `upLoad(File file) `、通过文件名下载文件`downloadFile(String objectName, String localFileName) `、列出某个文件夹下的所有文件`listFile( )`。笔主比较懒，代码可能还比较简陋，各位可以懂懂自己的脑子，参考阿里云官方提供的相关文档来根据自己的需求来优化。Java API文档地址如下：

[https://help.aliyun.com/document_detail/32008.html?spm=a2c4g.11186623.6.703.238374b4PsMzWf](https://help.aliyun.com/document_detail/32008.html?spm=a2c4g.11186623.6.703.238374b4PsMzWf)

```java
/**
 * @Author: SnailClimb
 * @Date: 2018/12/1 16:56
 * @Description: 阿里云OSS服务相关工具类.
 * Java API文档地址：https://help.aliyun.com/document_detail/32008.html?spm=a2c4g.11186623.6.703.238374b4PsMzWf
 */
@Component
public class AliyunOSSUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AliyunOSSUtil.class);
    private static String FILE_URL;
    private static String bucketName = AliyunOSSConfigConstant.BUCKE_NAME;
    private static String endpoint = AliyunOSSConfigConstant.END_POINT;
    private static String accessKeyId = AliyunOSSConfigConstant.AccessKey_ID;
    private static String accessKeySecret = AliyunOSSConfigConstant.AccessKey_Secret;
    private static String fileHost = AliyunOSSConfigConstant.FILE_HOST;


    /**
     * 上传文件。
     *
     * @param file 需要上传的文件路径
     * @return 如果上传的文件是图片的话，会返回图片的"URL"，如果非图片的话会返回"非图片，不可预览。文件路径为：+文件路径"
     */
    public static String upLoad(File file) {
        // 默认值为：true
        boolean isImage = true;
        // 判断所要上传的图片是否是图片，图片可以预览，其他文件不提供通过URL预览
        try {
            Image image = ImageIO.read(file);
            isImage = image == null ? false : true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("------OSS文件上传开始--------" + file.getName());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        // 判断文件
        if (file == null) {
            return null;
        }
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 判断容器是否存在,不存在就创建
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            // 设置文件路径和名称
            String fileUrl = fileHost + "/" + (dateStr + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + file.getName());
            if (isImage) {//如果是图片，则图片的URL为：....
                FILE_URL = "https://" + bucketName + "." + endpoint + "/" + fileUrl;
            } else {
                FILE_URL = "非图片，不可预览。文件路径为：" + fileUrl;
            }

            // 上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, file));
            // 设置权限(公开读)
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                logger.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (OSSException oe) {
            logger.error(oe.getMessage());
        } catch (ClientException ce) {
            logger.error(ce.getErrorMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return FILE_URL;
    }


    /**
     * 通过文件名下载文件
     *
     * @param objectName    要下载的文件名
     * @param localFileName 本地要创建的文件名
     */
    public static void downloadFile(String objectName, String localFileName) {

        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(localFileName));
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 列举 test 文件下所有的文件
     */
    public static void listFile() {
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 构造ListObjectsRequest请求。
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);

        // 设置prefix参数来获取fun目录下的所有文件。
        listObjectsRequest.setPrefix("test/");
        // 列出文件。
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        // 遍历所有文件。
        System.out.println("Objects:");
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }
        // 遍历所有commonPrefix。
        System.out.println("CommonPrefixes:");
        for (String commonPrefix : listing.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }
}

```

## 四 Controller 层编写相关测试方法

上传文件 `upLoad(File file) `、通过文件名下载文件`downloadFile(String objectName, String localFileName) `、列出某个文件夹下的所有文件`listFile( )` 这三个方法都在下面有对应的简单测试。另外，还有一个方法` uploadPicture(@RequestParam("file") MultipartFile file, Model model)`对应于我们等下要实现的图床功能,该方法从前端接受到图片之后上传到阿里云OSS存储空间并返回上传成功的图片 URL 地址给前端。

**注意将下面的相关路径改成自己的，不然会报错！！！**

```java
/**
 * @Author: SnailClimb
 * @Date: 2018/12/2 16:56
 * @Description: 阿里云OSS服务Controller
 */
@Controller
@RequestMapping("/oss")
public class AliyunOSSController {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;

    /**
     * 测试上传文件到阿里云OSS存储
     *
     * @return
     */
    @RequestMapping("/testUpload")
    @ResponseBody
    public String testUpload() {
        File file = new File("E:/Picture/test.jpg");
        AliyunOSSUtil aliyunOSSUtil = new AliyunOSSUtil();
        String url = aliyunOSSUtil.upLoad(file);
        System.out.println(url);
        return "success";
    }
    /**
     * 通过文件名下载文件
     */
    @RequestMapping("/testDownload")
    @ResponseBody
    public String testDownload() {
        AliyunOSSUtil aliyunOSSUtil = new AliyunOSSUtil();
        aliyunOSSUtil.downloadFile(
                "test/2018-12-04/e3f892c27f07462a864a43b8187d4562-rawpixel-600782-unsplash.jpg","E:/Picture/e3f892c27f07462a864a43b8187d4562-rawpixel-600782-unsplash.jpg");
        return "success";
    }
    /**
     * 列出某个文件夹下的所有文件
     */
    @RequestMapping("/testListFile")
    @ResponseBody
    public String testListFile() {
        AliyunOSSUtil aliyunOSSUtil = new AliyunOSSUtil();
        aliyunOSSUtil.listFile();
        return "success";
    }

    /**
     * 文件上传（供前端调用）
     */
    @RequestMapping(value = "/uploadFile")
    public String uploadPicture(@RequestParam("file") MultipartFile file, Model model) {
        logger.info("文件上传");
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        try {

            if (file != null) {
                if (!"".equals(filename.trim())) {
                    File newFile = new File(filename);
                    FileOutputStream os = new FileOutputStream(newFile);
                    os.write(file.getBytes());
                    os.close();
                    file.transferTo(newFile);
                    // 上传到OSS
                    String uploadUrl = aliyunOSSUtil.upLoad(newFile);
                    model.addAttribute("url",uploadUrl);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "success";
    }
}
```

## 五 启动类

```java
@SpringBootApplication
public class SpringbootOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootOssApplication.class, args);
    }
}

```

## 六 上传图片相关前端页面

注意引入jquery ，避免前端出错。

**index.html**

JS 的内容主要是让我们上传的图片可以预览，就像我们在网站更换头像的时候一样。

```java
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>基于阿里云OSS存储的图床</title>
    <script th:src="@{/js/jquery-3.3.1.js}"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        #submit {
            margin-left: 15px;
        }

        .preview_box img {
            width: 200px;
        }
    </style>
</head>
<body>

<form action="/oss/uploadFile" enctype="multipart/form-data" method="post">
    <div class="form-group" id="group">
        <input type="file" id="img_input" name="file" accept="image/*">
        <label for="img_input" ></label>
    </div>
    <button type="submit" id="submit">上传</button>
    <!--预览图片-->
    <div class="preview_box"></div>
</form>
<script type="text/javascript">

    $("#img_input").on("change", function (e) {
        var file = e.target.files[0]; //获取图片资源
        // 只选择图片文件
        if (!file.type.match('image.*')) {
            return false;
        }
        var reader = new FileReader();
        reader.readAsDataURL(file); // 读取文件
        // 渲染文件
        reader.onload = function (arg) {

            var img = '<img class="preview" src="' + arg.target.result + '" alt="preview"/>';
            $(".preview_box").empty().append(img);
        }
    });
</script>
</body>
</html>

```

**success.html**

通过 `<span th:text="${url}"></span>` 引用后端传过来的值。
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>上传结果</title>
</head>
<body>
<h1>上传成功！</h1>
图片地址为：<span th:text="${url}"></span>
</body>
</html>
```

## 七 测试我们的图床

访问 ：[http://localhost:8080/](http://localhost:8080/)


① 上传图片

![](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/6278013.jpg)

② 图片上传成功返回图片地址

![](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/72081846.jpg)

③ 通过图片 URL 访问图片

![通过图片 URL 访问图片](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-4/27895245.jpg)


我们终于能够独立利用阿里云 OSS 完成一个自己的图床服务，但是其实如果你想用阿里云OSS当做图床可以直接使用极简图床：[http://jiantuku.com](http://jiantuku.com)  上传图片，比较方便！大家可能心里在想那你特么让我实现个图床干嘛？我觉得通过学习，大家以后可以做很多事情，比如 利用阿里云OSS 存储服务存放自己网站的相关图片。

