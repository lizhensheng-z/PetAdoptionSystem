package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.ConfigRequest;
import com.yr.pet.adoption.model.dto.ConfigResponse;
import com.yr.pet.adoption.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统配置表（推荐权重/信用规则等） 前端控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/admin/configs")
@Tag(name = "系统配置管理", description = "系统配置相关接口")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取配置列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取配置列表", description = "分页获取系统配置列表，支持按配置键和备注搜索")
    public R<PageResult<ConfigResponse>> listConfigs(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer pageNo,
            
            @Parameter(description = "每页条数", example = "10") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "配置键（模糊搜索）") 
            @RequestParam(required = false) String configKey,
            
            @Parameter(description = "备注（模糊搜索）") 
            @RequestParam(required = false) String remark) {
        
        PageResult<ConfigResponse> result = configService.listConfigs(pageNo, pageSize, configKey, remark);
        return R.ok(result);
    }

    /**
     * 创建配置
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建配置", description = "创建新的系统配置")
    public R<ConfigResponse> createConfig(
            @Valid @RequestBody ConfigRequest request) {
        
        ConfigResponse response = configService.createConfig(request);
        return R.ok(response);
    }

    /**
     * 更新配置
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新配置", description = "更新指定ID的系统配置")
    public R<ConfigResponse> updateConfig(
            @Parameter(description = "配置ID", required = true, example = "1") 
            @PathVariable Long id,
            
            @Valid @RequestBody ConfigRequest request) {
        
        ConfigResponse response = configService.updateConfig(id, request);
        return R.ok(response);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除配置", description = "删除指定ID的系统配置")
    public R<Void> deleteConfig(
            @Parameter(description = "配置ID", required = true, example = "1") 
            @PathVariable Long id) {
        
        configService.deleteConfig(id);
        return R.ok();
    }

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/value/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取配置值", description = "根据配置键获取对应的配置值")
    public R<String> getConfigValue(
            @Parameter(description = "配置键", required = true, example = "app_name") 
            @PathVariable String configKey) {
        
        String value = configService.getConfigValue(configKey);
        return R.ok(value);
    }
}