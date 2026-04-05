package com.yr.pet.adoption.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.vo.*;

/**
 * 领养申请服务接口
 * @author yr
 * @since 2026-01-01
 */
public interface AdoptionApplicationService {

    /**
     * 用户发起领养申请
     */
    AdoptionApplicationResponse createApplication(Long userId, AdoptionApplicationRequest request);

    /**
     * 用户查看我的领养申请列表
     */
    IPage<MyApplicationVO> getMyApplications(Long userId, String status, Integer pageNo, Integer pageSize, String sortBy, String order);

    /**
     * 用户查看申请详情
     */
    ApplicationDetailVO getApplicationDetail(Long userId, Long applicationId);

    /**
     * 用户撤回申请
     */
    void cancelApplication(Long userId, Long applicationId, ApplicationCancelRequest request);

    /**
     * 机构获取申请列表
     */
    IPage<OrgApplicationVO> getOrgApplications(Long orgUserId, Long petId, String status, String keyword, Integer pageNo, Integer pageSize, String sortBy, String order);

    /**
     * 机构查看申请详情
     */
    ApplicationDetailVO getOrgApplicationDetail(Long orgUserId, Long applicationId);

    /**
     * 机构流转申请状态
     */
    StatusUpdateResponse updateApplicationStatus(Long orgUserId, Long applicationId, StatusUpdateRequest request);
}