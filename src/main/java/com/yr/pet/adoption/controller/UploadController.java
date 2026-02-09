package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.UploadResponse;
import com.yr.pet.adoption.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传相关接口")
public class UploadController {

    @Resource
    private UploadService uploadService;

    /**
     * 文件上传（通用）
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "通用文件上传接口")
    public R<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) throws IOException {
        UploadResponse response = uploadService.upload(file, type);
        return R.ok(response);
    }
}
