package com.yr.pet.adoption.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.yr.pet.adoption.model.dto.UploadResponse;
import com.yr.pet.adoption.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    // 阿里云OSS配置
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    @Value("${aliyun.oss.domain}")
    private String domain;

    @Override
    public UploadResponse upload(MultipartFile file, String type) throws IOException {
        // 验证文件类型
        validateFileType(file, type);

        // 验证文件大小
        validateFileSize(file, type);

        // 生成文件名
        String fileName = generateFileName(file, type);

        // 上传到OSS
        String url = uploadToOSS(file, fileName);

        // 构建响应
        UploadResponse response = new UploadResponse();
        response.setUrl(url);
        response.setFilename(fileName);
        response.setSize(file.getSize());
        response.setMime(file.getContentType());

        return response;
    }

    /**
     * 验证文件类型
     */
    private void validateFileType(MultipartFile file, String type) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase() : "";

        switch (type) {
            case "avatar":
                if (!extension.matches("(jpg|jpeg|png|webp)")) {
                    throw new RuntimeException("头像文件格式必须为jpg、jpeg、png或webp");
                }
                break;
            case "pet_media":
                if (!extension.matches("(jpg|jpeg|png|webp|mp4|webm)")) {
                    throw new RuntimeException("宠物媒体文件格式必须为jpg、jpeg、png、webp、mp4或webm");
                }
                break;
            case "checkin_media":
                if (!extension.matches("(jpg|jpeg|png|webp|mp4|webm)")) {
                    throw new RuntimeException("打卡媒体文件格式必须为jpg、jpeg、png、webp、mp4或webm");
                }
                break;
            case "application_document":
                if (!extension.matches("(pdf|jpg|jpeg|png)")) {
                    throw new RuntimeException("申请文档格式必须为pdf、jpg、jpeg或png");
                }
                break;
            default:
                throw new RuntimeException("不支持的文件类型");
        }
    }

    /**
     * 验证文件大小
     */
    private void validateFileSize(MultipartFile file, String type) {
        long size = file.getSize();
        long maxSize;

        switch (type) {
            case "avatar":
                maxSize = 5 * 1024 * 1024; // 5MB
                break;
            case "pet_media":
            case "checkin_media":
                String contentType = file.getContentType();
                if (contentType != null && contentType.startsWith("image")) {
                    maxSize = 10 * 1024 * 1024; // 10MB
                } else {
                    maxSize = 100 * 1024 * 1024; // 100MB
                }
                break;
            case "application_document":
                maxSize = 10 * 1024 * 1024; // 10MB
                break;
            default:
                throw new RuntimeException("不支持的文件类型");
        }

        if (size > maxSize) {
            throw new RuntimeException("文件大小超过限制");
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(MultipartFile file, String type) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase() : "";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        return String.format("%s/%s/%s_%s.%s", type, timestamp.substring(0, 8), timestamp.substring(8), uuid, extension);
    }

    /**
     * 上传到OSS
     */
    private String uploadToOSS(MultipartFile file, String fileName) throws IOException {
        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                ossClient.putObject(bucketName, fileName, inputStream);
            }

            // 构建URL
            return domain + "/" + fileName;
        } finally {
            // 关闭OSS客户端
            ossClient.shutdown();
        }
    }
}
