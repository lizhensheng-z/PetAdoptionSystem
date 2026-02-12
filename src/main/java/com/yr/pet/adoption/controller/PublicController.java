package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公共接口控制器
 * 无需认证即可访问的公共接口
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "公共接口", description = "无需认证的公共接口")
public class PublicController {
    
    private final PublicService publicService;
    
    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取系统配置", description = "获取前端需要的系统配置信息")
    public R<SystemConfigResponse> getSystemConfig() {
        SystemConfigResponse config = publicService.getSystemConfig();
        return R.ok(config);
    }
    
    /**
     * 获取公告列表
     */
    @GetMapping("/notices")
    @Operation(summary = "获取公告列表", description = "获取平台公告列表")
    public R<PageResult<NoticeResponse>> getNotices(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<NoticeResponse> notices = publicService.getNotices(pageNo, pageSize);
        return R.ok(notices);
    }
    
    /**
     * 获取标签库
     */
    @GetMapping("/tags")
    @Operation(summary = "获取标签库", description = "获取宠物标签库")
    public R<TagResponse> getTags(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "true") Boolean enabled) {
        TagResponse tags = publicService.getTags(type, enabled);
        return R.ok(tags);
    }
    
    /**
     * 获取图片验证码
     */
    @GetMapping("/captcha")
    @Operation(summary = "获取图片验证码", description = "获取图片验证码用于防刷")
    public R<CaptchaResponse> getCaptcha(@RequestParam Long timestamp) {
        CaptchaResponse captcha = publicService.getCaptcha(timestamp);
        return R.ok(captcha);
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "发送邮件或短信验证码")
    public R<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        publicService.sendCode(request);
        return R.ok();
    }
    
    /**
     * 验证码验证
     */
    @PostMapping("/verify-code")
    @Operation(summary = "验证码验证", description = "验证验证码并返回临时token")
    public R<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        VerifyCodeResponse response = publicService.verifyCode(request);
        return R.ok(response);
    }
    
    /**
     * 获取省市区数据
     */
    @GetMapping("/regions")
    @Operation(summary = "获取省市区数据", description = "获取中国省市区三级联动数据")
    public R<RegionResponse> getRegions(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String parentCode) {
        RegionResponse regions = publicService.getRegions(level, parentCode);
        return R.ok(regions);
    }
    
    /**
     * 地址地理编码
     */
    @PostMapping("/geocode")
    @Operation(summary = "地址地理编码", description = "将地址转换为经纬度坐标")
    public R<GeocodeResponse> geocode(@Valid @RequestBody GeocodeRequest request) {
        GeocodeResponse response = publicService.geocode(request);
        return R.ok(response);
    }
    
    /**
     * 计算两点距离
     */
    @PostMapping("/distance")
    @Operation(summary = "计算两点距离", description = "计算两个坐标点之间的距离")
    public R<DistanceResponse> calculateDistance(@Valid @RequestBody DistanceRequest request) {
        DistanceResponse response = publicService.calculateDistance(request);
        return R.ok(response);
    }
    
    /**
     * 获取周边宠物机构
     */
    @GetMapping("/nearby-orgs")
    @Operation(summary = "获取周边宠物机构", description = "根据坐标获取附近的宠物机构")
    public R<NearbyOrgResponse> getNearbyOrgs(
            @RequestParam Double lng,
            @RequestParam Double lat,
            @RequestParam(defaultValue = "50") Double distance,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        NearbyOrgResponse response = publicService.getNearbyOrgs(lng, lat, distance, pageNo, pageSize);
        return R.ok(response);
    }
}