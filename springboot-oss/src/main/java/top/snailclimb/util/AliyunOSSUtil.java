package top.snailclimb.util;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.snailclimb.config.AliyunOSSConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Component
public class AliyunOSSUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AliyunOSSUtil.class);
    public static String FILE_URL;

    /**
     * 上传文件。
     *
     * @param file            需要上传的文件路径
     * @param aliyunOSSConfig 阿里云的配置类
     * @return 如果上传的文件是图片的话，会返回图片的"URL"，如果非图片的话会返回"非图片，不可预览。文件路径为：+文件路径"
     */
    public static String upLoad(File file, AliyunOSSConfig aliyunOSSConfig) {
        // 默认值为：true
        boolean isImage = true;
        // 判断所要上传的图片是否是图片，图片可以预览，其他文件不提供通过URL预览
        try {
            Image image = ImageIO.read(file);
            if (image != null) {
                isImage = true;
            } else {
                isImage = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("------OSS文件上传开始--------" + file.getName());
        String bucketName = aliyunOSSConfig.getBucketname();
        System.out.println("获取到的bucketName为:" + bucketName);
        String endpoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getKeyid();
        String accessKeySecret = aliyunOSSConfig.getKeysecret();
        String fileHost = aliyunOSSConfig.getFilehost();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        // 判断文件
        if (file == null) {
            return null;
        }
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 判断容器是否存在,不存在就创建
            if (!client.doesBucketExist(bucketName)) {
                client.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                client.createBucket(createBucketRequest);
            }
            // 设置文件路径和名称
            String fileUrl = fileHost + "/" + (dateStr + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + file.getName());
            if (isImage) {//如果是图片，则图片的URL为：....
                FILE_URL = "https://" + bucketName + "." + endpoint + "/" + fileUrl;
            } else {
                FILE_URL = "非图片，不可预览。文件路径为：" + fileUrl;
            }

            // 上传文件
            PutObjectResult result = client.putObject(new PutObjectRequest(bucketName, fileUrl, file));
            // 设置权限(公开读)
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                logger.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (OSSException oe) {
            logger.error(oe.getMessage());
        } catch (ClientException ce) {
            logger.error(ce.getErrorMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
        return FILE_URL;
    }
}

