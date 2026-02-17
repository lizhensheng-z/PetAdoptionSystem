package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.model.dto.OrgProfileRequest;
import com.yr.pet.adoption.model.dto.OrgProfileResponse;
import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.service.OrgProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * <p>
 * 救助机构资料表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-17
 */
@Service
public class OrgProfileServiceImpl extends ServiceImpl<OrgProfileMapper, OrgProfileEntity> implements OrgProfileService {

    @Override
    public OrgProfileResponse getOrgProfile(Long userId) {
        OrgProfileEntity orgProfile = lambdaQuery()
                .eq(OrgProfileEntity::getUserId, userId)
                .eq(OrgProfileEntity::getDeleted, 0)
                .one();
        
        if (orgProfile == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }
        
        return convertToResponse(orgProfile);
    }

    @Override
    @Transactional
    public OrgProfileResponse saveOrgProfile(Long userId, OrgProfileRequest request) {
        OrgProfileEntity orgProfile = lambdaQuery()
                .eq(OrgProfileEntity::getUserId, userId)
                .eq(OrgProfileEntity::getDeleted, 0)
                .one();
        
        if (orgProfile == null) {
            // 创建新的机构资料
            orgProfile = new OrgProfileEntity();
            orgProfile.setUserId(userId);
            orgProfile.setVerifyStatus("PENDING");
            orgProfile.setDeleted(0);
            orgProfile.setCreateTime(LocalDateTime.now());
        }
        
        // 更新机构资料
        BeanUtils.copyProperties(request, orgProfile);
        orgProfile.setUpdateTime(LocalDateTime.now());
        
        saveOrUpdate(orgProfile);
        
        return convertToResponse(orgProfile);
    }

    @Override
    @Transactional
    public boolean updateVerifyStatus(Long userId, String verifyStatus, String verifyRemark) {
        OrgProfileEntity orgProfile = lambdaQuery()
                .eq(OrgProfileEntity::getUserId, userId)
                .eq(OrgProfileEntity::getDeleted, 0)
                .one();
        
        if (orgProfile == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }
        
        orgProfile.setVerifyStatus(verifyStatus);
        orgProfile.setVerifyRemark(verifyRemark);
        orgProfile.setUpdateTime(LocalDateTime.now());
        
        return updateById(orgProfile);
    }

    @Override
    public boolean isOrgProfileComplete(Long userId) {
        OrgProfileEntity orgProfile = lambdaQuery()
                .eq(OrgProfileEntity::getUserId, userId)
                .eq(OrgProfileEntity::getDeleted, 0)
                .one();
        
        return isOrgProfileComplete(orgProfile);
    }

    @Override
    public String getOrgVerifyStatus(Long userId) {
        OrgProfileEntity orgProfile = lambdaQuery()
                .eq(OrgProfileEntity::getUserId, userId)
                .eq(OrgProfileEntity::getDeleted, 0)
                .one();
        
        return orgProfile != null ? orgProfile.getVerifyStatus() : "PENDING";
    }

    /**
     * 检查机构资料是否完整
     * @param orgProfile 机构资料实体
     * @return 是否完整
     */
    private boolean isOrgProfileComplete(OrgProfileEntity orgProfile) {
        return orgProfile != null 
                && StringUtils.hasText(orgProfile.getOrgName())
                && StringUtils.hasText(orgProfile.getContactName())
                && StringUtils.hasText(orgProfile.getContactPhone())
                && StringUtils.hasText(orgProfile.getAddress())
                && StringUtils.hasText(orgProfile.getProvince())
                && StringUtils.hasText(orgProfile.getCity());
    }

    /**
     * 将实体转换为响应DTO
     * @param entity 机构资料实体
     * @return 机构资料响应
     */
    private OrgProfileResponse convertToResponse(OrgProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        OrgProfileResponse response = new OrgProfileResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}