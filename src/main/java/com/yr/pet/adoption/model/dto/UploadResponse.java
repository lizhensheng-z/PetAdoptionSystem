package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传响应DTO
 */
@Data
@Schema(description = "文件上传响应")
public class UploadResponse {
    
    @Schema(description = "文件URL")
    private String url;
    
    @Schema(description = "文件名")
    private String filename;
    
    @Schema(description = "文件大小")
    private Long size;
    
    @Schema(description = "MIME类型")
    private String mime;
}