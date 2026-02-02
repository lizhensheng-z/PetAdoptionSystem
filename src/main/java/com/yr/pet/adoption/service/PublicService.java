package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.*;

/**
 * 公共接口服务
 */
public interface PublicService {
    
    /**
     * 获取系统配置
     */
    SystemConfigResponse getSystemConfig();
    
    /**
     * 获取公告列表
     */
    PageResult<NoticeResponse> getNotices(Integer pageNo, Integer pageSize);
    
    /**
     * 获取标签库
     */
    TagResponse getTags(String type, Boolean enabled);
    
    /**
     * 获取图片验证码
     */
    CaptchaResponse getCaptcha(Long timestamp);
    
    /**
     * 发送验证码
     */
    void sendCode(SendCodeRequest request);
    
    /**
     * 验证码验证
     */
    VerifyCodeResponse verifyCode(VerifyCodeRequest request);
    
    /**
     * 获取省市区数据
     */
    RegionResponse getRegions(Integer level, String parentCode);
    
    /**
     * 地址地理编码
     */
    GeocodeResponse geocode(GeocodeRequest request);
    
    /**
     * 计算两点距离
     */
    DistanceResponse calculateDistance(DistanceRequest request);
    
    /**
     * 获取周边宠物机构
     */
    NearbyOrgResponse getNearbyOrgs(Double lng, Double lat, Double distance, Integer pageNo, Integer pageSize);
}