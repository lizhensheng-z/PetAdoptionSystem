package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.CreditAccountMapper;
import com.yr.pet.adoption.mapper.CreditLogMapper;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.model.dto.CreditLogResponse;
import com.yr.pet.adoption.model.dto.CreditLogsRequest;
import com.yr.pet.adoption.model.dto.UserCreditSummaryResponse;
import com.yr.pet.adoption.model.entity.CreditAccountEntity;
import com.yr.pet.adoption.model.entity.CreditLogEntity;
import com.yr.pet.adoption.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 信用服务实现类
 * 提供用户信用相关的业务功能实现
 * 
 * @author 宗平
 * @since 2024-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditAccountMapper creditAccountMapper;
    private final CreditLogMapper creditLogMapper;
    private final UserMapper userMapper;

    @Override
    public UserCreditSummaryResponse getUserCreditSummary(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户ID不能为空");
        }

        // 获取信用账户信息
        CreditAccountEntity account = creditAccountMapper.selectById(userId);
        if (account == null) {
            // 如果用户没有信用账户，创建一个默认的
            account = new CreditAccountEntity();
            account.setUserId(userId);
            account.setScore(0);
            account.setLevel(0);
            account.setLastCalcTime(LocalDateTime.now());
            creditAccountMapper.insert(account);
        }

        // 获取用户创建时间
        LocalDateTime userCreateTime = userMapper.selectById(userId).getCreateTime();

        // 获取打卡统计信息
        Integer totalCheckins = creditLogMapper.getTotalCheckinDays(userId);
        if (totalCheckins == null) {
            totalCheckins = 0;
        }

        Integer consecutiveCheckins = creditLogMapper.getConsecutiveCheckinDays(userId);
        if (consecutiveCheckins == null) {
            consecutiveCheckins = 0;
        }

        LocalDateTime lastCheckinDateTime = creditLogMapper.getLastCheckinDate(userId);
        LocalDate lastCheckinDate = lastCheckinDateTime != null ? lastCheckinDateTime.toLocalDate() : null;

        // 计算等级信息
        String levelName = getLevelName(account.getScore());
        String levelIcon = getLevelIcon(account.getScore());
        String levelColor = getLevelColor(account.getScore());
        
        // 计算下一等级信息
        String nextLevelName = getNextLevelName(account.getScore());
        Integer nextLevelThreshold = getNextLevelThreshold(account.getScore());
        Integer nextLevelProgress = calculateNextLevelProgress(account.getScore(), nextLevelThreshold);

        // 计算排名信息
        Integer totalUsers = Math.toIntExact(userMapper.selectCount(null));
        Integer rank = calculateRank(account.getScore());
        String ranking = calculateRankingPercentage(rank, totalUsers);

        // 获取最近变动信息
        Integer recentChange = getRecentChange(userId);
        String recentChangeReason = getRecentChangeReason(userId);

        // 构建响应
        UserCreditSummaryResponse response = new UserCreditSummaryResponse();
        response.setScore(account.getScore());
        response.setLevelName(levelName);
        response.setLevelIcon(levelIcon);
        response.setLevelColor(levelColor);
        response.setTotalCheckins(totalCheckins);
        response.setConsecutiveCheckins(consecutiveCheckins);
        response.setNextLevelName(nextLevelName);
        response.setNextLevelThreshold(nextLevelThreshold);
        response.setNextLevelProgress(nextLevelProgress);
        response.setRanking(ranking);
        response.setTotalUsers(totalUsers);
        response.setRank(rank);
        response.setRecentChange(recentChange);
        response.setRecentChangeReason(recentChangeReason);
        response.setLastCheckinDate(lastCheckinDate);
        response.setCreatedAt(userCreateTime);
        response.setUpdatedAt(account.getUpdateTime());

        return response;
    }

    @Override
    public PageResult<CreditLogResponse> getUserCreditLogs(Long userId, CreditLogsRequest request) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户ID不能为空");
        }

        // 构建分页对象
        Page<CreditLogEntity> page = new Page<>(request.getPageNo(), request.getPageSize());

        // 处理时间范围
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (StringUtils.hasText(request.getStartDate())) {
            startTime = LocalDate.parse(request.getStartDate(), formatter).atStartOfDay();
        }
        if (StringUtils.hasText(request.getEndDate())) {
            endTime = LocalDate.parse(request.getEndDate(), formatter).atTime(23, 59, 59);
        }

        // 查询信用日志
        IPage<CreditLogEntity> creditLogPage = creditLogMapper.selectUserCreditLogs(
                page, userId, request.getType(), startTime, endTime);

        // 转换为响应DTO
        List<CreditLogResponse> logResponses = creditLogPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResult<>(
                logResponses,
                (int) creditLogPage.getCurrent(),
                (int) creditLogPage.getSize(),
                creditLogPage.getTotal(),
                Integer.parseInt(String.valueOf(creditLogPage.getPages()))
        );
    }

    private CreditLogResponse convertToResponse(CreditLogEntity entity) {
        CreditLogResponse response = new CreditLogResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setDelta(entity.getDelta());
        response.setReason(entity.getReason());
        response.setType(entity.getReason()); // 使用reason作为type
        response.setRelatedId(entity.getRefId());
        response.setRelatedType(entity.getRefType());
        response.setCreateTime(entity.getCreateTime());
        response.setBalance(calculateBalance(entity.getUserId(), entity.getId()));
        response.setMetadata(buildMetadata(entity));
        return response;
    }

    private String getLevelName(Integer score) {
        if (score >= 1000) return "金牌领养人";
        if (score >= 800) return "资深领养人";
        if (score >= 600) return "高级领养人";
        if (score >= 400) return "中级领养人";
        if (score >= 200) return "初级领养人";
        return "新手领养人";
    }

    private String getLevelIcon(Integer score) {
        if (score >= 1000) return "crown";
        if (score >= 800) return "shield-check";
        if (score >= 600) return "star";
        if (score >= 400) return "heart";
        if (score >= 200) return "paw";
        return "user";
    }

    private String getLevelColor(Integer score) {
        if (score >= 1000) return "#FFD700";
        if (score >= 800) return "#FF8C42";
        if (score >= 600) return "#4CAF50";
        if (score >= 400) return "#2196F3";
        if (score >= 200) return "#9C27B0";
        return "#607D8B";
    }

    private String getNextLevelName(Integer score) {
        if (score >= 1000) return "金牌领养人";
        if (score >= 800) return "金牌领养人";
        if (score >= 600) return "资深领养人";
        if (score >= 400) return "高级领养人";
        if (score >= 200) return "中级领养人";
        return "初级领养人";
    }

    private Integer getNextLevelThreshold(Integer score) {
        if (score >= 1000) return 1000;
        if (score >= 800) return 1000;
        if (score >= 600) return 800;
        if (score >= 400) return 600;
        if (score >= 200) return 400;
        return 200;
    }

    private Integer calculateNextLevelProgress(Integer currentScore, Integer nextThreshold) {
        if (nextThreshold <= 0) return 100;
        
        Integer previousThreshold = 0;
        if (nextThreshold == 200) previousThreshold = 0;
        else if (nextThreshold == 400) previousThreshold = 200;
        else if (nextThreshold == 600) previousThreshold = 400;
        else if (nextThreshold == 800) previousThreshold = 600;
        else if (nextThreshold == 1000) previousThreshold = 800;
        
        int progress = (int) (((double)(currentScore - previousThreshold) / (nextThreshold - previousThreshold)) * 100);
        return Math.min(progress, 100);
    }

    private Integer calculateRank(Integer score) {
        // 简化的排名计算，实际应该基于数据库查询
        return Math.max(1, (int) (Math.random() * 100) + 1);
    }

    private String calculateRankingPercentage(Integer rank, Integer totalUsers) {
        if (totalUsers <= 0) return "Top 100%";
        
        double percentage = ((double) rank / totalUsers) * 100;
        if (percentage <= 1) return "Top 1%";
        if (percentage <= 5) return "Top 5%";
        if (percentage <= 10) return "Top 10%";
        if (percentage <= 20) return "Top 20%";
        if (percentage <= 30) return "Top 30%";
        return "Top 50%";
    }

    private Integer getRecentChange(Long userId) {
        // 获取最近一条信用日志的变动值
        // 这里简化处理，实际应该查询最近一条记录
        return 15;
    }

    private String getRecentChangeReason(Long userId) {
        // 获取最近变动的原因
        // 这里简化处理，实际应该查询最近一条记录
        return "连续7天打卡";
    }

    private Integer calculateBalance(Long userId, Long logId) {
        // 计算变动后的积分余额
        // 这里简化处理，实际应该基于数据库查询计算
        CreditAccountEntity account = creditAccountMapper.selectById(userId);
        return account != null ? account.getScore() : 0;
    }

    private Map<String, Object> buildMetadata(CreditLogEntity entity) {
        Map<String, Object> metadata = new HashMap<>();
        
        // 根据变动类型构建不同的元数据
        String reason = entity.getReason();
        if ("CHECKIN".equals(reason)) {
            metadata.put("petName", "小橘");
            metadata.put("checkinId", entity.getRefId());
            metadata.put("imageCount", 3);
        } else if ("STREAK".equals(reason)) {
            metadata.put("streakDays", 7);
        } else if ("OVERDUE".equals(reason)) {
            metadata.put("overdueDays", 2);
        } else if ("VACCINE".equals(reason)) {
            metadata.put("petName", "小橘");
            metadata.put("checkinId", entity.getRefId());
            metadata.put("vaccineType", "三联疫苗");
        }
        
        return metadata;
    }
}