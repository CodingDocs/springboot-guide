package top.snailclimb.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import top.snailclimb.config.AliyunOSSConfig;
import top.snailclimb.util.AliyunOSSUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Auther: SnailClimb
 * @Date: 2018/12/2 16:56
 * @Description: 阿里云OSS服务相关工具类
 */
@Controller
public class UploadFileController {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private static final String TO_PATH = "upLoad";
    private static final String RETURN_PATH = "success";
    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;
    @Autowired
    private AliyunOSSConfig aliyunOSSConfig;

    /**
     * 测试上传文件到阿里云OSS存储
     *
     * @return
     */
    @RequestMapping("/test")
    public String hello() {
        File file = new File("E:/Picture/测试.txt");
        AliyunOSSUtil aliyunOSSUtil = new AliyunOSSUtil();
        String url = aliyunOSSUtil.upLoad(file, aliyunOSSConfig);
        System.out.println(url);
        return "success";
    }

    @RequestMapping("/toUpLoadFile")
    public String toUpLoadFile() {
        return TO_PATH;
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = "/uploadFile")
    public String uploadBlog(@RequestParam("file") MultipartFile file) {
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
                    String uploadUrl = aliyunOSSUtil.upLoad(newFile, aliyunOSSConfig);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RETURN_PATH;
    }
}
