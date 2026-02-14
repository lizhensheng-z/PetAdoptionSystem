package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.CreditAccountEntity;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信用账户表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface CreditAccountService extends IService<CreditAccountEntity> {

    /**
     * 获取信用信息
     */
    CreditInfoResponse getCreditInfo(Long userId);

    /**
     * 获取信用详情（含历史记录）
     */
    CreditDetailResponse getCreditDetail(Long userId);

    /**
     * 获取信用流水
     */
    PageResult<CreditLogItem> getCreditLogs(Long userId, String reason, Integer pageNo, Integer pageSize);

    /**
     * 计算打卡信用分
     */
    Integer calculateCheckinScore(String content, boolean hasMedia);
}
