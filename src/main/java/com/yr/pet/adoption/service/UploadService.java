package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传服务
 */
public interface UploadService {

    /**
     * 上传文件
     */
    UploadResponse upload(MultipartFile file, String type) throws IOException;
}
