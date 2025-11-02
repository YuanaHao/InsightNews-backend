package com.sosd.insightnews.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyuncs.exceptions.ClientException;
import com.sosd.insightnews.util.properties.AliOssProperties;
import com.sosd.insightnews.util.properties.SmsCodeProperty;
import com.sosd.insightnews.service.AliService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.sosd.insightnews.constant.MessageConstant.FILE_ERROR;
import static com.sosd.insightnews.constant.MessageConstant.FILE_FORMAT_ERROR;
import static com.sosd.insightnews.constant.RedisConstants.VERIFY_CODE;
import static com.sosd.insightnews.constant.RedisConstants.VERIFY_CODE_TTL;
import static com.sosd.insightnews.util.FileUtil.*;
import static com.sosd.insightnews.util.properties.AliOssProperties.MAX_FILE_SIZE;


@Service
public class AliServiceImpl implements AliService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private SmsCodeProperty smsCodeProperty;

    @Autowired
    private AliOssProperties aliOssProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void uploadAvatar(String avatarBase64, String id) {
        OSS ossClient = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret());
    }

    @Override
    public String getAvatar(String id) {
        return null;
    }

    @Override
    public CompletableFuture<String> uploadFile(MultipartFile file) throws IOException {
        //保存上传后返回的云端文件URLs
        CompletableFuture<String> future;
        //获取文件名
        String originalFilename = file.getOriginalFilename();
        //判断文件是否存在
        if(originalFilename == null){
            throw new IOException(FILE_ERROR);
        }
        // 多线程上传文件 使用supplyAsync方法,用来返回url
        future = CompletableFuture.supplyAsync(() -> {
            //判断是否为图片
            if(isImage(file)){
                return simpleUpload(file, originalFilename);
            }else if(isVideo(file)) {
                //要判断文件大小是否超出5MB
                if (file.getSize() <= MAX_FILE_SIZE) {
                    //没有超出限制，就直接上传oss
                    return simpleUpload(file, originalFilename);
                }else{
                    //如果超出限制，就要分片上传oss
                    try {
                        return fileUploadZone(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                //非图片和视频抛出异常
                throw new RuntimeException(FILE_FORMAT_ERROR);
            }
        });
        return future;
    }

    //Send verification code
    @Override
    public void sendCode(String phone) throws Exception {
        //Generate a four-digit verification code
        String code = RandomUtil.randomNumbers(6);
        //Set the timeout period
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        Config config = new Config()
                .setAccessKeyId(smsCodeProperty.accessKeyID)
                .setAccessKeySecret(smsCodeProperty.accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);
        //Assemble request object
        SendSmsRequest request = new SendSmsRequest()
                .setSignName(smsCodeProperty.signName)
                .setTemplateCode(smsCodeProperty.templateId)
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        stringRedisTemplate.opsForValue().set(VERIFY_CODE + phone, code, VERIFY_CODE_TTL, TimeUnit.MINUTES);
        SendSmsResponse sendSmsResponse = client.sendSms(request);
        //Check whether the SMS message is successfully sent
        if (sendSmsResponse.getBody().getCode() == null) {
            stringRedisTemplate.delete(VERIFY_CODE);
            throw new ClientException("验证码发送失败");
        }
    }

    private String simpleUpload(MultipartFile file, String originalFilename) {
        try {
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID() + extension;
            // 设置上传到云存储的路径
            return upload(file.getBytes(), objectName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */
    private String upload(byte[] bytes, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret());
        try {
            ossClient.putObject(aliOssProperties.getBucketName(), objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("捕获到OSSException，这意味着您的请求已发送到OSS,"
                    + "但由于某种原因被拒绝并出现错误响应。");
            System.out.println("错误信息:" + oe.getErrorMessage());
            System.out.println("错误代码:" + oe.getErrorCode());
            System.out.println("请求ID:" + oe.getRequestId());
            System.out.println("主机ID:" + oe.getHostId());
        } catch (Exception e) {
            System.out.println("捕获了一个ClientException，这意味着客户端在试图与OSS通信时遇到了严重的内部问题，例如无法访问网络。");
            System.out.println("错误消息:" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(aliOssProperties.getBucketName())
                .append(".")
                .append(aliOssProperties.getEndpoint())
                .append("/")
                .append(objectName);
        log.info("File upload:{}", stringBuilder);
        return stringBuilder.toString();
    }

    //文件分片上传
    private String fileUploadZone(MultipartFile multipartFile) throws Exception {
        //获取文件的原始名字
        String originalfileName = multipartFile.getOriginalFilename();
        //文件后缀
        String suffix = originalfileName.substring(originalfileName.lastIndexOf(".") + 1);
        //重新命名文件，文件夹要是改动，app记录删除的地方一并改动
        String pack = "file/";
        String fileName = "file_" + System.currentTimeMillis() + "." + suffix;
        String filePath = pack + fileName;
        OSS ossClient = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret());
        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliOssProperties.getBucketName(), fileName);
            ObjectMetadata metadata = new ObjectMetadata();
            // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
            metadata.setHeader("x-oss-forbid-overwrite", "true");
            // 指定Object的存储类型。
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
            // 根据文件自动设置ContentType。如果不设置，ContentType默认值为application/oct-srream。
            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(new File(filePath), fileName));
            }
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId。
            String uploadId = upresult.getUploadId();
            // 根据uploadId执行取消分片上传事件或者列举已上传分片的操作。
            // 如果您需要根据uploadId执行取消分片上传事件的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后获取uploadId。
            // 如果您需要根据uploadId执行列举已上传分片的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后，且在调用CompleteMultipartUpload完成分片上传之前获取uploadId。
            // System.out.println(uploadId);
            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1 * 1024 * 1024L;   //1 MB。
            // 根据上传的数据大小计算分片数。
            long fileSize = multipartFile.getSize();
            int partCount = (int) (fileSize / partSize);
            if (fileSize % partSize != 0) {
                partCount++;
            }
            File file = multipartFileToFile(multipartFile);
            if(!file.exists()){
                throw new Exception(FILE_ERROR);
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileSize - startPos) : partSize;
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(aliOssProperties.getBucketName());
                uploadPartRequest.setKey(fileName);
                uploadPartRequest.setUploadId(uploadId);
                // 设置上传的分片流。
                InputStream instream = new FileInputStream(file);
                instream.skip(startPos);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }
            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(aliOssProperties.getBucketName(), fileName, uploadId, partETags);
            // 完成分片上传。
            ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            StringBuilder stringBuilder = new StringBuilder("https://");
            stringBuilder
                    .append(aliOssProperties.getBucketName())
                    .append(".")
                    .append(aliOssProperties.getEndpoint())
                    .append("/")
                    .append(fileName);
            log.info("File upload:{}", stringBuilder);
            return stringBuilder.toString();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw oe;
        } catch (com.aliyun.oss.ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}


