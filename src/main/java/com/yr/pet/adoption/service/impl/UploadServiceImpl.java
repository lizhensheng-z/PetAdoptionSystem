package com.yr.pet.adoption.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.yr.pet.adoption.model.dto.UploadResponse;
import com.yr.pet.adoption.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        
        log.info("验证文件类型 - 文件名: {}, 内容类型: {}, 类型参数: {}", 
                 originalFilename, contentType, type);
        
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        
        // 提取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        }
        
        log.info("提取的文件扩展名: {}", extension);

        // 支持的文件类型映射
        switch (type) {
            case "avatar":
                if (!isValidExtension(extension, "jpg,jpeg,png,webp")) {
                    throw new RuntimeException("头像文件格式必须为jpg、jpeg、png或webp，当前格式: " + extension);
                }
                break;
            case "pet_media":
                if (!isValidExtension(extension, "jpg,jpeg,png,webp,mp4,webm")) {
                    throw new RuntimeException("宠物媒体文件格式必须为jpg、jpeg、png、webp、mp4或webm，当前格式: " + extension);
                }
                break;
            case "checkin_media":
                if (!isValidExtension(extension, "jpg,jpeg,png,webp,mp4,webm")) {
                    throw new RuntimeException("打卡媒体文件格式必须为jpg、jpeg、png、webp、mp4或webm，当前格式: " + extension);
                }
                break;
            case "application_document":
                if (!isValidExtension(extension, "pdf,jpg,jpeg,png")) {
                    throw new RuntimeException("申请文档格式必须为pdf、jpg、jpeg或png，当前格式: " + extension);
                }
                break;
            default:
                throw new RuntimeException("不支持的文件类型参数: " + type + 
                    "。支持的类型: avatar, pet_media, checkin_media, application_document");
        }
    }
    
    /**
     * 验证文件扩展名是否有效
     */
    private boolean isValidExtension(String extension, String validExtensions) {
        if (extension == null || extension.trim().isEmpty()) {
            return false;
        }
        String[] validExts = validExtensions.split(",");
        for (String validExt : validExts) {
            if (validExt.trim().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证文件大小
     */
    private void validateFileSize(MultipartFile file, String type) {
        long size = file.getSize();
        long maxSize;
        String typeName;

        switch (type) {
            case "avatar":
                maxSize = 5 * 1024 * 1024; // 5MB
                typeName = "头像";
                break;
            case "pet_media":
            case "checkin_media":
                String contentType = file.getContentType();
                if (contentType != null && contentType.startsWith("image")) {
                    maxSize = 10 * 1024 * 1024; // 10MB
                    typeName = "图片";
                } else {
                    maxSize = 100 * 1024 * 1024; // 100MB
                    typeName = "视频";
                }
                break;
            case "application_document":
                maxSize = 10 * 1024 * 1024; // 10MB
                typeName = "申请文档";
                break;
            default:
                throw new RuntimeException("不支持的文件类型");
        }

        if (size > maxSize) {
            String maxSizeStr = formatFileSize(maxSize);
            String actualSizeStr = formatFileSize(size);
            throw new RuntimeException(String.format("%s文件大小不能超过%s，当前文件大小为%s", 
                typeName, maxSizeStr, actualSizeStr));
        }
    }

    /**
     * 格式化文件大小显示
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1fMB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1fGB", size / (1024.0 * 1024 * 1024));
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
