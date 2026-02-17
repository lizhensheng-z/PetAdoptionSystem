package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.OrgProfileRequest;
import com.yr.pet.adoption.model.dto.OrgProfileResponse;
import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 救助机构资料表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-17
 */
public interface OrgProfileService extends IService<OrgProfileEntity> {

    /**
     * 获取机构资料
     * @param userId 用户ID
     * @return 机构资料响应
     */
    OrgProfileResponse getOrgProfile(Long userId);

    /**
     * 创建或更新机构资料
     * @param userId 用户ID
     * @param request 机构资料请求
     * @return 机构资料响应
     */
    OrgProfileResponse saveOrgProfile(Long userId, OrgProfileRequest request);

    /**
     * 更新机构认证状态（管理员使用）
     * @param userId 用户ID
     * @param verifyStatus 认证状态
     * @param verifyRemark 认证备注
     * @return 是否成功
     */
    boolean updateVerifyStatus(Long userId, String verifyStatus, String verifyRemark);

    /**
     * 检查机构资料是否完整
     * @param userId 用户ID
     * @return 是否完整
     */
    boolean isOrgProfileComplete(Long userId);

    /**
     * 获取机构认证状态
     * @param userId 用户ID
     * @return 认证状态
     */
    String getOrgVerifyStatus(Long userId);
}