> 昨晚搭建环境都花了好一会时间，主要在浪费在了安装 openoffice 这个依赖环境上(_Mac 需要手动安装_)。
>
> 然后，又一步一步功能演示，记录，调试项目，并且简单研究了一下核心代码之后才把这篇文章写完。
>
> 另外，这篇文章我还会简单分析一下项目核心代码。
>
> _如果有帮助，欢迎点赞/再看鼓励，我会开心很久 ღ( ´･ᴗ･` )比心_

## 项目介绍

官方是这样介绍 kkFileView 的：

> kkFileView 是使用 spring boot 打造文件文档在线预览项目解决方案，支持 doc、docx、ppt、pptx、xls、xlsx、zip、rar、mp4、mp3 以及众多类文本如 txt、html、xml、java、properties、sql、js、md、json、conf、ini、vue、php、py、bat、gitignore 等文件在线预览

**简单来说 kkFileView 就是常见的文件类型的在线预览解决方案。**

总的来说我觉得 kkFileView 是一个非常棒的开源项目，在线文件预览这个需求非常常见。感谢开源！

下面， 我站在一个“上帝”的角度从多个维度来评价一下 kkFileView：

1. 代码质量一般，有很多可以优化的地方比如：
   - `Controller` 层代码嵌套太多逻辑
   - 没有进行全局异常处理(_代码中是直接返回错误信息的 json 数据给前端，我并不推荐这样做_)
   - 返回值不需要通过`ObjectMapper`转换为 JSON 格式（`ResponseEntity`+`@RestController` 就行了）
   - ......
2. 使用的公司比较多，说明项目整体功能还是比较稳定和成熟的！
3. 代码整体逻辑还是比较清晰的，比较容易看懂，给作者们点个赞！

## 环境搭建

### 克隆项目

通过以下命令即可将项目克隆到本地：

```bash
git clone https://gitee.com/kekingcn/file-online-preview.git
```

### 安装 OpenOffice

office 类型的文件的预览依赖了 OpenOffice ，所以我们首先要安装 OpenOffice(Windows 下已内置，Linux 会自动安装，Mac OS 下需要手动安装)。

下面演示一下如何在 Mac 上安装 OpenOffice。

你可以通过以下命令安装最新版的 OpenOffice：

```bash
brew cask install openoffice
```

不过，这种方式下载可能会比较慢，你可以直接去官网下载 dmg 安装包。

官方下载地址：[https://www.openoffice.org/download/](https://www.openoffice.org/download/)

![OpenOffice下载	](https://img-blog.csdnimg.cn/20201015194327797.png)

很多小伙伴就要问了：**OpenOffice 是什么呢？**

[OpenOffice](https://www.openoffice.org/) 是 Apache 旗下的一款开源免费的文字处理软件，支持 Windows、Liunx、OS X 等主流操作系统。

OpenOffice 和 Windows 下 office 办公软件有点类似，不过其实开源免费的。

![why openoffice](https://img-blog.csdnimg.cn/20201015194328447.png)

### 启动项目

运行`FilePreviewApplication`的 main 方法，服务启动后，访问[http://localhost:8012/](http://localhost:8012/) 会看到如下界面，代表服务启动成功。

![项目启动成功](https://img-blog.csdnimg.cn/20201015194328801.png)

## 使用

我们首先上传了 3 个不同的类型的文件来分别演示一下图片、PDF、Word 文档的预览。

![](https://img-blog.csdnimg.cn/20201015194328978.png)

### 图片的预览

**kkFileView 支持 jpg，jpeg，png，gif 等多种格式图片的预览，还包括了翻转，缩放图片等操作。**

图片的预览效果如下。

![图片的预览效果](https://img-blog.csdnimg.cn/20201015194329517.png)

### Word 文档的预览

**kkFileView 支持 doc，docx 文档预览。**

另外，根据 Word 大小以及网速问题， Word 预览提供了两种模式：

- 每页 Word 转为图片预览
- 整个 Word 文档转成 PDF，再预览 PDF。

两种模式的适用场景如下

- **图片预览** ：Word 文件大（加载 PDF 速度比较慢）的情况。
- **PDF 预览** ：内网访问（加载 PDF 速度比较快）的情况。

图片预览模式预览效果如下：

![](https://img-blog.csdnimg.cn/20201015194329908.png)

PDF 预览模式预览效果如下：

![](https://img-blog.csdnimg.cn/20201015194330591.png)

### PDF 文档的预览

kkFileView 支持 PDF 文档预览。类似 Word 文档预览， PDF 预览提供了两种模式：

- 每页 Word 转为图片预览
- 整个 Word 文档转成 PDF，再预览 PDF。

由于和 Word 文档的预览展示效果一致，这里就不放图片了。

## 文件预览核心代码分析

### API 层

文件预览调用的接口是 `/onlinePreview` 。

通过分析 `/onlinePreview` 接口我们发现， 后端接收到预览请求之后，会从 URL 和请求中筛选出自己需要的信息比如文件后缀、文件名。

之后会调用`FilePreview`类 的 `filePreviewHandle()` 方法。`filePreviewHandle()` 方法是实现文件预览的核心方法。

```java
@RequestMapping(value = "/onlinePreview")
public String onlinePreview(String url, Model model, HttpServletRequest req) {
    FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
    req.setAttribute("fileKey", req.getParameter("fileKey"));
    model.addAttribute("pdfDownloadDisable", ConfigConstants.getPdfDownloadDisable());
    model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
    FilePreview filePreview = previewFactory.get(fileAttribute);
    logger.info("预览文件url：{}，previewType：{}", url, fileAttribute.getType());
    return filePreview.filePreviewHandle(url, model, fileAttribute);
}
```

`FilePreview` 是文件预览接口，不同的文件类型的预览都实现了 `FilePreview` 接口，并实现了 `filePreviewHandle()` 方法。

### 文件预览接口

```java
public interface FilePreview {
    String filePreviewHandle(String url, Model model, FileAttribute fileAttribute);
}
```

不同的文件类型的预览都实现了 `FilePreview` 接口，如下图所示。

![](https://img-blog.csdnimg.cn/20201015194330836.png)

不同文件类型的预览都会实现 `FilePreview` 接口，然后重写`filePreviewHandle()`方法。比如： `OfficeFilePreviewImpl` 这个主要负责处理 office 文件的预览、`PdfFilePreviewImpl` 主要负责处理 pdf 文件的预览。

### 文件预览具体实现分析

下面我们以 office 文件的预览为入口来分析。

首先要明确的是 excel 类型的预览是通过将 excel 文件转换为 HTML 实现的，其他类型 office 文件的预览是通过将文件转换为 PDF 或者是 图片的方式来预览的。

举个例子。我们上传了一份名为 `武汉市文化市场管理办法.docx` 的 Word 文件并预览的话，`jodconverter-web/src/main/file` 路径下会生成两个相关文件，这两个文件分别对应了我们提到的 PDF 预览 和 图片预览这两种方式。

- `武汉市文化市场管理办法.pdf`
- 由 Word 文件所转化得到的一系列图片

![](https://img-blog.csdnimg.cn/2020101519433136.png)

我们以一个名为 `武汉市文化市场管理办法.docx` 的文件来举例说明一下代码中是如何做的。

通过分析代码， 我们定位到了 `OfficeFilePreviewImpl` 这个主要负责处理 office 文件预览的类。

```java
/**
 * 处理office文件
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

}
```

我们来简单分析一下 `OfficeFilePreviewImpl` 类中实现预览的核心方法是 `filePreviewHandle` 。

> 说明：这部分代码的逻辑不够清晰，还可以抽方法优化以让人更容易读懂，感兴趣的小伙伴可以自己动手重构一下，然后去给作者提个 PR。

```java
    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        // 1.获取预览类型(image/pdf/html)，用户请求中传了officePreviewType参数就取参数的，没传取系统默认（image）
        String officePreviewType = model.asMap().get("officePreviewType") == null ? ConfigConstants.getOfficePreviewType() : model.asMap().get("officePreviewType").toString();
        // 2.获取 URL 地址
        String baseUrl = BaseUrlFilter.getBaseUrl();// http://localhost:8012/
        // 3.获取图片相关信息
        String suffix=fileAttribute.getSuffix();//文件后缀如docx
        String fileName=fileAttribute.getName();//文件名如：武汉市文化市场管理办法.docx
        // 4. 判断是否为 html 格式预览也就是判断文件否为 excel
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        // 5. 将文件的后缀名更换为 .pdf 或者 .html（excel文件的情况）
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");
        // 6. 转换后的文件输出的文件夹如 file-online-preview/jodconverter-web/src/main/file/武汉市文化市场管理办法.pdf）
        String outFilePath = FILE_DIR + pdfName;
        // 7 .判断之前是否已转换过，如果转换过，直接返回，否则执行转换
        // 文件第一次被预览的时候会首先对文件进行缓存处理
        if (!fileUtils.listConvertedFiles().containsKey(pdfName) || !ConfigConstants.isCacheEnabled()) {
            String filePath;
            // 下载文件
            ReturnResponse<String> response = downloadUtils.downLoad(fileAttribute, null);
            if (0 != response.getCode()) {
                model.addAttribute("fileType", suffix);
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            filePath = response.getContent();
            if (StringUtils.hasText(outFilePath)) {
                officeToPdf.openOfficeToPDF(filePath, outFilePath);
                if (isHtml) {
                    // 对转换后的文件进行操作(改变编码方式)
                    fileUtils.doActionConvertedFile(outFilePath);
                }
                if (ConfigConstants.isCacheEnabled()) {
                    // 加入缓存
                    fileUtils.addConvertedFile(pdfName, fileUtils.getRelativePath(outFilePath));
                }
            }
        }
        // 8.根据预览类型officePreviewType，选择不同的预览方式
        // 比如,如果预览类型officePreviewType为pdf则进行pdf方式预览
        if (!isHtml && baseUrl != null && (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType) || OFFICE_PREVIEW_TYPE_ALL_IMAGES.equals(officePreviewType))) {
            return getPreviewType(model, fileAttribute, officePreviewType, baseUrl, pdfName, outFilePath, pdfUtils, OFFICE_PREVIEW_TYPE_IMAGE);
        }
        model.addAttribute("pdfUrl", pdfName);
        return isHtml ? "html" : "pdf";
    }

```

项目地址： [https://github.com/kekingcn/kkFileView](https://github.com/kekingcn/kkFileView) 。


