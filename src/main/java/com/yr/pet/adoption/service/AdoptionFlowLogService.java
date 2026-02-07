package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.vo.ApplicationFlowLogVO;

import java.util.List;

/**
 * 领养申请流程日志服务接口
 * @author yr
 * @since 2024-01-01
 */
public interface AdoptionFlowLogService {

    /**
     * 获取申请流程日志
     * @param applicationId 申请ID
     * @return 流程日志列表
     */
    List<ApplicationFlowLogVO> getApplicationLogs(Long applicationId);
}