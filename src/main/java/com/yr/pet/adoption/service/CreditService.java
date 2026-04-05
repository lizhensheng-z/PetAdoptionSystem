package com.yr.pet.adoption.service;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.CreditLogResponse;
import com.yr.pet.adoption.model.dto.CreditLogsRequest;
import com.yr.pet.adoption.model.dto.UserCreditSummaryResponse;

/**
 * 信用服务接口
 * 提供用户信用相关的业务功能
 * 
 * @author 宗平
 * @since 2026-02-18
 */
public interface CreditService {

    /**
     * 获取用户信用摘要信息
     * 
     * @param userId 用户ID
     * @return 信用摘要响应
     */
    UserCreditSummaryResponse getUserCreditSummary(Long userId);

    /**
     * 获取用户信用积分变更流水
     * 
     * @param userId 用户ID
     * @param request 查询请求参数
     * @return 分页的信用日志响应
     */
    PageResult<CreditLogResponse> getUserCreditLogs(Long userId, CreditLogsRequest request);
}