package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.mapper.AdoptionApplicationMapper;
import com.yr.pet.adoption.mapper.AdoptionFlowLogMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.model.entity.AdoptionApplicationEntity;
import com.yr.pet.adoption.model.entity.AdoptionFlowLogEntity;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.model.vo.*;
import com.yr.pet.adoption.service.AdoptionApplicationService;
import com.yr.pet.adoption.exception.BusinessException;
import com.yr.pet.adoption.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 领养申请服务实现类
 * @author yr
 * @since 2024-01-01
 */
@Service
public class AdoptionApplicationServiceImpl implements AdoptionApplicationService {

    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Autowired
    private AdoptionFlowLogMapper adoptionFlowLogMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public AdoptionApplicationResponse createApplication(Long userId, AdoptionApplicationRequest request) {
        // 验证宠物是否存在且已发布
        PetEntity pet = petMapper.selectById(request.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new BusinessException("宠物不存在");
        }
        if (!"PUBLISHED".equals(pet.getStatus())) {
            throw new BusinessException("该宠物暂不接受申请");
        }

        // 检查用户是否已申请过该宠物
        if (adoptionApplicationMapper.countByUserIdAndPetId(userId, request.getPetId()) > 0) {
            throw new BusinessException("您已申请过该宠物");
        }

        // 检查进行中的申请数量
        int activeCount = adoptionApplicationMapper.countActiveApplicationsByPetId(request.getPetId());
        if (activeCount >= 10) { // 可配置的最大申请数
            throw new BusinessException("该宠物申请人数已满，请稍后再试");
        }

        // 创建申请
        AdoptionApplicationEntity application = new AdoptionApplicationEntity();
        application.setPetId(request.getPetId());
        application.setUserId(userId);
        try {
            application.setQuestionnaireJson(objectMapper.writeValueAsString(request.getQuestionnaire()));
        } catch (JsonProcessingException e) {
            throw new BusinessException("问卷数据格式错误");
        }
        application.setStatus("SUBMITTED");
        application.setDeleted(0);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        adoptionApplicationMapper.insert(application);

        // 创建流程日志
        AdoptionFlowLogEntity flowLog = new AdoptionFlowLogEntity();
        flowLog.setApplicationId(application.getId());
        flowLog.setFromStatus(null);
        flowLog.setToStatus("SUBMITTED");
        flowLog.setOperatorId(userId);
        flowLog.setRemark("用户提交申请");
        flowLog.setCreateTime(LocalDateTime.now());
        adoptionFlowLogMapper.insert(flowLog);

        // 更新宠物状态为申请中
        pet.setStatus("APPLYING");
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);

        AdoptionApplicationResponse response = new AdoptionApplicationResponse();
        response.setApplicationId(application.getId());
        response.setPetId(application.getPetId());
        response.setUserId(application.getUserId());
        response.setStatus(application.getStatus());
        response.setSubmitTime(application.getCreateTime());
        response.setEstimatedReviewTime("3-7天内");

        return response;
    }

    @Override
    public IPage<MyApplicationVO> getMyApplications(Long userId, String status, Integer pageNo, Integer pageSize, String sortBy, String order) {
        Page<MyApplicationVO> page = new Page<>(pageNo, pageSize);
        return adoptionApplicationMapper.selectMyApplications(page, userId, status, sortBy, order);
    }

    @Override
    public ApplicationDetailVO getApplicationDetail(Long userId, Long applicationId) {
        AdoptionApplicationEntity application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该申请");
        }

        // 查询宠物信息
        PetEntity pet = petMapper.selectById(application.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new BusinessException("宠物信息不存在");
        }



        // 查询用户信息
        UserEntity user = userMapper.selectById(userId);
        // 假设有用户信息查询逻辑

        ApplicationDetailVO vo = new ApplicationDetailVO();
        vo.setId(application.getId());
        vo.setPetId(application.getPetId());
        vo.setUserId(application.getUserId());
        vo.setStatus(application.getStatus());
        vo.setStatusDesc(getStatusDisplay(application.getStatus()));
        vo.setSubmitTime(application.getCreateTime());
        vo.setRejectReason(application.getRejectReason());
        vo.setOrgRemark(application.getOrgRemark());
        vo.setDecidedTime(application.getDecidedTime());

        // 设置宠物信息
        vo.setPetName(pet.getName());
        vo.setPetCoverUrl(pet.getCoverUrl());

        // 设置用户信息
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
            vo.setUserPhone(user.getPhone());
            vo.setUserEmail(user.getEmail());
        }

        // 解析问卷数据
        try {
            if (application.getQuestionnaireJson() != null) {
                vo.setQuestionnaire(objectMapper.readValue(application.getQuestionnaireJson(), Map.class));
            }
        } catch (JsonProcessingException e) {
            vo.setQuestionnaire(null);
        }

        // 设置是否可以取消
        vo.setCanCancel(List.of("SUBMITTED", "UNDER_REVIEW").contains(application.getStatus()));
        vo.setCanModify("SUBMITTED".equals(application.getStatus()));

        return vo;
    }

    @Override
    @Transactional
    public void cancelApplication(Long userId, Long applicationId, ApplicationCancelRequest request) {
        AdoptionApplicationEntity application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该申请");
        }

        // 检查状态是否允许取消
        if (!List.of("SUBMITTED", "UNDER_REVIEW").contains(application.getStatus())) {
            throw new BusinessException("当前状态不允许取消申请");
        }

        // 更新状态
        String oldStatus = application.getStatus();
        application.setStatus("CANCELLED");
        application.setUpdateTime(LocalDateTime.now());
        adoptionApplicationMapper.updateById(application);

        // 创建流程日志
        AdoptionFlowLogEntity flowLog = new AdoptionFlowLogEntity();
        flowLog.setApplicationId(application.getId());
        flowLog.setFromStatus(oldStatus);
        flowLog.setToStatus("CANCELLED");
        flowLog.setOperatorId(userId);
        flowLog.setRemark("用户撤回申请: " + (request.getReason() != null ? request.getReason() : "无"));
        flowLog.setCreateTime(LocalDateTime.now());
        adoptionFlowLogMapper.insert(flowLog);

        // 检查是否还有其他进行中的申请
        int activeCount = adoptionApplicationMapper.countActiveApplicationsByPetId(application.getPetId());
        if (activeCount == 0) {
            PetEntity pet = petMapper.selectById(application.getPetId());
            if (pet != null) {
                pet.setStatus("PUBLISHED");
                pet.setUpdateTime(LocalDateTime.now());
                petMapper.updateById(pet);
            }
        }
    }

    @Override
    public IPage<OrgApplicationVO> getOrgApplications(Long orgUserId, Long petId, String status, String keyword, Integer pageNo, Integer pageSize, String sortBy, String order) {
        Page<OrgApplicationVO> page = new Page<>(pageNo, pageSize);
        return adoptionApplicationMapper.selectOrgApplications(page, orgUserId, petId, status, keyword, sortBy, order);
    }

    @Override
    public ApplicationDetailVO getOrgApplicationDetail(Long orgUserId, Long applicationId) {
        AdoptionApplicationEntity application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请不存在");
        }

        // 验证机构权限 - 确保宠物属于该机构
        PetEntity pet = petMapper.selectById(application.getPetId());
        if (pet == null || !pet.getOrgUserId().equals(orgUserId)) {
            throw new BusinessException("无权查看该申请");
        }

        // 查询申请人信息（修复：使用申请人ID而非当前用户ID）
        UserEntity applicant = userMapper.selectById(application.getUserId());

        ApplicationDetailVO vo = new ApplicationDetailVO();
        vo.setId(application.getId());
        vo.setPetId(application.getPetId());
        vo.setUserId(application.getUserId());
        vo.setStatus(application.getStatus());
        vo.setStatusDesc(getStatusDisplay(application.getStatus()));
        vo.setSubmitTime(application.getCreateTime());
        vo.setRejectReason(application.getRejectReason());
        vo.setOrgRemark(application.getOrgRemark());
        vo.setDecidedTime(application.getDecidedTime());

        // 设置宠物信息
        vo.setPetName(pet.getName());
        vo.setPetCoverUrl(pet.getCoverUrl());

        // 设置申请人信息（修复：使用正确的用户信息）
        if (applicant != null) {
            vo.setUserName(applicant.getUsername());
            vo.setUserAvatar(applicant.getAvatar());
            vo.setUserPhone(applicant.getPhone());
            vo.setUserEmail(applicant.getEmail());
        }

        // 解析问卷数据
        try {
            if (application.getQuestionnaireJson() != null) {
                vo.setQuestionnaire(objectMapper.readValue(application.getQuestionnaireJson(), Map.class));
            }
        } catch (JsonProcessingException e) {
            vo.setQuestionnaire(null);
        }

        // 设置权限标识（机构视角）
        vo.setCanCancel(false); // 机构不能取消用户申请
        
        // 机构可以修改状态：当申请处于可处理状态时
        boolean canUpdateStatus = List.of("SUBMITTED", "UNDER_REVIEW", "INTERVIEW", "HOME_VISIT").contains(application.getStatus());
        vo.setCanModify(canUpdateStatus);

        return vo;
    }

    @Override
    @Transactional
    public StatusUpdateResponse updateApplicationStatus(Long orgUserId, Long applicationId, StatusUpdateRequest request) {
        AdoptionApplicationEntity application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请不存在");
        }

        // 验证机构权限
        PetEntity pet = petMapper.selectById(application.getPetId());
        if (pet == null || !pet.getOrgUserId().equals(orgUserId)) {
            throw new BusinessException("无权操作该申请");
        }

        // 验证状态转换
        String currentStatus = application.getStatus();
        String newStatus = request.getToStatus();
        
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BusinessException("无效的状态转换: " + currentStatus + " -> " + newStatus);
        }

        // 更新状态
        application.setStatus(newStatus);
        application.setUpdateTime(LocalDateTime.now());
        
        if ("REJECTED".equals(newStatus)) {
            application.setRejectReason(request.getRejectReason());
            application.setDecidedTime(LocalDateTime.now());
        } else if ("APPROVED".equals(newStatus)) {
            application.setDecidedTime(LocalDateTime.now());
        }
        
        if (request.getRemark() != null) {
            application.setOrgRemark(request.getRemark());
        }
        
        adoptionApplicationMapper.updateById(application);

        // 创建流程日志
        AdoptionFlowLogEntity flowLog = new AdoptionFlowLogEntity();
        flowLog.setApplicationId(application.getId());
        flowLog.setFromStatus(currentStatus);
        flowLog.setToStatus(newStatus);
        flowLog.setOperatorId(orgUserId);
        
        String remark = request.getRemark() != null ? request.getRemark() : "";
        if ("INTERVIEW".equals(newStatus) && request.getInterviewTime() != null) {
            remark += " 面谈时间: " + request.getInterviewTime();
        }
        flowLog.setRemark(remark);
        flowLog.setCreateTime(LocalDateTime.now());
        adoptionFlowLogMapper.insert(flowLog);

        // 如果批准，更新宠物状态并拒绝其他申请
        if ("APPROVED".equals(newStatus)) {
            pet.setStatus("ADOPTED");
            pet.setUpdateTime(LocalDateTime.now());
            petMapper.updateById(pet);

            // 拒绝其他进行中的申请
            List<AdoptionApplicationEntity> otherApplications = adoptionApplicationMapper.selectByPetId(application.getPetId())
                    .stream()
                    .filter(app -> !app.getId().equals(applicationId) && 
                           List.of("SUBMITTED", "UNDER_REVIEW", "INTERVIEW", "HOME_VISIT").contains(app.getStatus()))
                    .toList();

            for (AdoptionApplicationEntity otherApp : otherApplications) {
                otherApp.setStatus("REJECTED");
                otherApp.setRejectReason("该宠物已被领养");
                otherApp.setUpdateTime(LocalDateTime.now());
                adoptionApplicationMapper.updateById(otherApp);

                AdoptionFlowLogEntity otherFlowLog = new AdoptionFlowLogEntity();
                otherFlowLog.setApplicationId(otherApp.getId());
                otherFlowLog.setFromStatus(otherApp.getStatus());
                otherFlowLog.setToStatus("REJECTED");
                otherFlowLog.setOperatorId(orgUserId);
                otherFlowLog.setRemark("该宠物已被领养");
                otherFlowLog.setCreateTime(LocalDateTime.now());
                adoptionFlowLogMapper.insert(otherFlowLog);
            }
        }

        // 如果所有申请都被处理完，恢复宠物状态
        if ("REJECTED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
            int activeCount = adoptionApplicationMapper.countActiveApplicationsByPetId(application.getPetId());
            if (activeCount == 0) {
                pet.setStatus("PUBLISHED");
                pet.setUpdateTime(LocalDateTime.now());
                petMapper.updateById(pet);
            }
        }

        StatusUpdateResponse response = new StatusUpdateResponse();
        response.setApplicationId(applicationId);
        response.setFromStatus(currentStatus);
        response.setToStatus(newStatus);
        response.setUpdateTime(LocalDateTime.now());

        return response;
    }

    /**
     * 获取状态显示文本
     */
    private String getStatusDisplay(String status) {
        if (status == null) {
            return "未知状态";
        }

        return switch (status) {
            case "SUBMITTED" -> "已提交";
            case "UNDER_REVIEW" -> "审核中";
            case "INTERVIEW" -> "已约面谈";
            case "HOME_VISIT" -> "家访中";
            case "APPROVED" -> "已通过";
            case "REJECTED" -> "已拒绝";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }

    /**
     * 验证状态转换是否有效
     */
    private boolean isValidStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }

        return switch (fromStatus) {
            case "SUBMITTED" -> List.of("UNDER_REVIEW", "REJECTED", "CANCELLED").contains(toStatus);
            case "UNDER_REVIEW" -> List.of("INTERVIEW", "REJECTED", "CANCELLED").contains(toStatus);
            case "INTERVIEW" -> List.of("HOME_VISIT", "APPROVED", "REJECTED", "CANCELLED").contains(toStatus);
            case "HOME_VISIT" -> List.of("APPROVED", "REJECTED", "CANCELLED").contains(toStatus);
            case "APPROVED", "REJECTED", "CANCELLED" -> false; // 终止状态
            default -> false;
        };
    }
}