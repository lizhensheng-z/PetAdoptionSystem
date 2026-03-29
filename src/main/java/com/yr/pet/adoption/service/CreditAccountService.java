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

    /**
     * 变更用户信用分
     * @param userId 用户ID
     * @param delta 分数变化（正数增加，负数减少）
     * @param reason 变更原因代码
     * @param refType 关联类型
     * @param refId 关联ID
     */
    void changeCredit(Long userId, Integer delta, String reason, String refType, Long refId);

    /**
     * 打卡奖励信用分
     * @param userId 用户ID
     * @param content 打卡内容
     * @param hasMedia 是否有图片
     * @param checkinId 打卡记录ID
     */
    void rewardCheckin(Long userId, String content, boolean hasMedia, Long checkinId);

    /**
     * 领养成功奖励信用分
     * @param userId 用户ID
     * @param applicationId 申请ID
     */
    void rewardAdoptionSuccess(Long userId, Long applicationId);

    /**
     * 取消申请扣减信用分
     * @param userId 用户ID
     * @param applicationId 申请ID
     */
    void penalizeCancelApply(Long userId, Long applicationId);

    /**
     * 申请被拒扣减信用分
     * @param userId 用户ID
     * @param applicationId 申请ID
     */
    void penalizeApplyRejected(Long userId, Long applicationId);

    /**
     * 违规处罚扣减信用分
     * @param userId 用户ID
     * @param delta 扣减分数
     * @param reason 处罚原因
     */
    void penalizeViolation(Long userId, Integer delta, String reason);
}
