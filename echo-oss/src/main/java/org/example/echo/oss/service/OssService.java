package org.example.echo.oss.service;

import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;
import org.example.echo.oss.config.MinioProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Component
public class OssService {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProperties minioProperties;

    /**
     * 创建 bucket
     *
     * @param bucketName bucket 名称
     * @return
     */
    public boolean create(String bucketName) {
        try {
            final boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (exists) {
                throw new BusinessException(BizCodeEnum.OSS_BUCKET_EXISTS);
            }
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("创建Bucket失败 {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param file       文件
     * @param bucketName bucket 名称
     * @return
     */
    public String upload(MultipartFile file, String bucketName) {
        try {
            final ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .contentType(file.getContentType())
                    .object(UUID.randomUUID().toString().replace("-", "") + "." + getExtension(file.getOriginalFilename()))
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .build()
            );
            final String objectId = objectWriteResponse.object();
            return minioProperties.getEndpoint() + "/" + bucketName + "/" + objectId;
        } catch (Exception e) {
            log.error("上传文件失败 {}", e.getMessage(), e);
            throw new BusinessException(BizCodeEnum.OSS_UPLOAD_FAIL);
        }
    }

    private String getExtension(String originalName) {
        if (StringUtils.isEmpty(originalName)) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket 名称
     * @param fileName   文件名
     * @return
     */
    public void delete(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new BusinessException(BizCodeEnum.OSS_DELETE_FAIL);
        }
    }

}
