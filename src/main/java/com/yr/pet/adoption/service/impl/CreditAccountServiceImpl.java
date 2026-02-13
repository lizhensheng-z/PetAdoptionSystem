package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.service.CreditAccountService;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信用账户表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class CreditAccountServiceImpl extends ServiceImpl<CreditAccountMapper, CreditAccountEntity> implements CreditAccountService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;
    @Autowired
    private CheckinPostMapper checkinPostMapper;
    @Autowired
    private CreditLogMapper creditLogMapper;
    @Autowired
    private ConfigMapper configMapper;

    @Override
    public CreditInfoResponse getCreditInfo(Long userId) {
        CreditInfoResponse response = new CreditInfoResponse();
        
        // 获取用户信息
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
        }
        
        // 获取信用账户
        CreditAccountEntity account = this.getById(userId);
        int score = account != null ? account.getScore() : 0;
        int level = account != null ? account.getLevel() : 0;
        
        response.setScore(score);
        response.setLevel(level);
        response.setLevelName(getLevelName(level));
        response.setLevelDescription(getLevelDescription(level));
        
        // 等级进度
        CreditInfoResponse.LevelProgress progress = new CreditInfoResponse.LevelProgress();
        progress.setCurrentScore(score);
        progress.setNextLevelScore(getNextLevelScore(level));
        progress.setRemainingScore(Math.max(0, progress.getNextLevelScore() - score));
        
        int levelStartScore = getLevelStartScore(level);
        int levelRange = progress.getNextLevelScore() - levelStartScore;
        progress.setPercentage(levelRange > 0 ? (score - levelStartScore) * 100 / levelRange : 100);
        response.setProgressToNextLevel(progress);
        
        // 统计数据
        CreditInfoResponse.CreditStatistics statistics = new CreditInfoResponse.CreditStatistics();
        
        // 申请统计
        LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getUserId, userId);
        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(appQuery);
        statistics.setTotalApplications(applications.size());
        
        long approved = applications.stream()
                .filter(a -> "APPROVED".equals(a.getStatus()))
                .count();
        statistics.setSuccessfulAdoptions((int) approved);
        statistics.setSuccessRate(applications.isEmpty() ? 0.0 : (double) approved / applications.size());
        
        // 打卡统计
        LambdaQueryWrapper<CheckinPostEntity> checkinQuery = new LambdaQueryWrapper<CheckinPostEntity>()
                .eq(CheckinPostEntity::getUserId, userId);
        List<CheckinPostEntity> checkins = checkinPostMapper.selectList(checkinQuery);
        statistics.setCheckinCount(checkins.size());
        
        if (!checkins.isEmpty()) {
            LocalDateTime lastCheckin = checkins.get(0).getCreateTime();
            for (CheckinPostEntity checkin : checkins) {
                if (checkin.getCreateTime().isAfter(lastCheckin)) {
                    lastCheckin = checkin.getCreateTime();
                }
            }
            statistics.setLastCheckinTime(lastCheckin);
        }
        
        // 违规统计
        LambdaQueryWrapper<CreditLogEntity> creditQuery = new LambdaQueryWrapper<CreditLogEntity>()
                .eq(CreditLogEntity::getUserId, userId)
                .eq(CreditLogEntity::getReason, "VIOLATION");
        long violations = creditLogMapper.selectCount(creditQuery);
        statistics.setViolations((int) violations);
        
        response.setStatistics(statistics);
        
        // 最近活动
        LambdaQueryWrapper<CreditLogEntity> logQuery = new LambdaQueryWrapper<CreditLogEntity>()
                .eq(CreditLogEntity::getUserId, userId)
                .orderByDesc(CreditLogEntity::getCreateTime)
                .last("LIMIT 10");
        
        List<CreditLogEntity> logs = creditLogMapper.selectList(logQuery);
        List<CreditInfoResponse.CreditLogItem> recentActivities = new ArrayList<>();
        
        int beforeScore = score;
        for (CreditLogEntity log : logs) {
            CreditInfoResponse.CreditLogItem item = new CreditInfoResponse.CreditLogItem();
            item.setLogId(log.getId());
            item.setDelta(log.getDelta());
            item.setReason(log.getReason());
            item.setRefType(log.getRefType());
            item.setRefId(log.getRefId());
            item.setBeforeScore(beforeScore - log.getDelta());
            item.setAfterScore(beforeScore);
            item.setCreateTime(log.getCreateTime());
            beforeScore = item.getBeforeScore();
            
            recentActivities.add(item);
        }
        response.setRecentActivities(recentActivities);
        
        // 徽章（简化实现）
        response.setBadges(calculateBadges(userId, checkins.size(), (int) approved));
        
        // 最后计算时间
        if (account != null) {
            response.setLastCalcTime(account.getLastCalcTime());
        }
        
        return response;
    }

    @Override
    public PageResult<CreditLogItem> getCreditLogs(Long userId, String reason, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<CreditLogEntity> query = new LambdaQueryWrapper<CreditLogEntity>()
                .eq(CreditLogEntity::getUserId, userId);
        
        if (StringUtils.hasText(reason)) {
            query.eq(CreditLogEntity::getReason, reason);
        }
        
        query.orderByDesc(CreditLogEntity::getCreateTime);
        
        Page<CreditLogEntity> page = new Page<>(pageNo, pageSize);
        Page<CreditLogEntity> result = creditLogMapper.selectPage(page, query);
        
        List<CreditLogItem> list = new ArrayList<>();
        
        // 计算信用分
        CreditAccountEntity account = this.getById(userId);
        int currentScore = account != null ? account.getScore() : 0;
        
        int beforeScore = currentScore;
        for (CreditLogEntity entity : result.getRecords()) {
            CreditLogItem item = new CreditLogItem();
            item.setLogId(entity.getId());
            item.setUserId(entity.getUserId());
            item.setDelta(entity.getDelta());
            item.setReason(entity.getReason());
            item.setReasonDisplay(getReasonDisplay(entity.getReason(), entity.getDelta()));
            item.setRefType(entity.getRefType());
            item.setRefId(entity.getRefId());
            item.setBeforeScore(beforeScore - entity.getDelta());
            item.setAfterScore(beforeScore);
            item.setCreateTime(entity.getCreateTime());
            beforeScore = item.getBeforeScore();
            
            list.add(item);
        }
        
        return new PageResult<>(list, pageNo, pageSize, result.getTotal(), 
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public Integer calculateCheckinScore(String content, boolean hasMedia) {
        int baseScore = getConfigValue("credit.checkin.base", 2);
        int mediaBonus = hasMedia ? getConfigValue("credit.checkin.mediaBonus", 1) : 0;
        int textBonus = (content != null && content.length() > 30) 
                ? getConfigValue("credit.checkin.textBonus", 1) : 0;
        
        return baseScore + mediaBonus + textBonus;
    }

    private int getConfigValue(String key, int defaultValue) {
        ConfigEntity config = configMapper.selectOne(
                new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, key));
        if (config != null && StringUtils.hasText(config.getConfigValue())) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String getLevelName(int level) {
        switch (level) {
            case 0: return "新注册";
            case 1: return "铜牌用户";
            case 2: return "银牌用户";
            case 3: return "金牌用户";
            case 4: return "铂金用户";
            default: return "未知";
        }
    }

    private String getLevelDescription(int level) {
        switch (level) {
            case 0: return "新用户注册，基础权限";
            case 1: return "信用分 25-49，可申请领养";
            case 2: return "信用分 50-99，申请更易通过";
            case 3: return "信用分 100-149，优先推荐";
            case 4: return "信用分 150+，VIP待遇";
            default: return "";
        }
    }

    private int getLevelStartScore(int level) {
        switch (level) {
            case 0: return 0;
            case 1: return 25;
            case 2: return 50;
            case 3: return 100;
            case 4: return 150;
            default: return 0;
        }
    }

    private int getNextLevelScore(int level) {
        switch (level) {
            case 0: return 25;
            case 1: return 50;
            case 2: return 100;
            case 3: return 150;
            case 4: return 200;
            default: return 25;
        }
    }

    private String getReasonDisplay(String reason, Integer delta) {
        if ("CHECKIN".equals(reason)) {
            if (delta == 4) return "完成打卡（基础2分+内容1分+媒体1分）";
            if (delta == 3) return "完成打卡（基础2分+内容1分）";
            if (delta == 2) return "完成打卡（基础2分）";
            return "打卡";
        }
        if ("VIOLATION".equals(reason)) {
            return "违规扣分";
        }
        if ("APPLY".equals(reason)) {
            return "发起领养申请";
        }
        if ("APPROVED".equals(reason)) {
            return "领养申请通过";
        }
        return reason;
    }

    private List<CreditInfoResponse.BadgeInfo> calculateBadges(Long userId, int checkinCount, int adoptionCount) {
        List<CreditInfoResponse.BadgeInfo> badges = new ArrayList<>();
        
        // 初心者：第一次打卡
        if (checkinCount >= 1) {
            CreditInfoResponse.BadgeInfo badge = new CreditInfoResponse.BadgeInfo();
            badge.setId(1L);
            badge.setName("初心者");
            badge.setDescription("完成第一次打卡");
            badges.add(badge);
        }
        
        // 打卡达人：累计打卡10次
        if (checkinCount >= 10) {
            CreditInfoResponse.BadgeInfo badge = new CreditInfoResponse.BadgeInfo();
            badge.setId(2L);
            badge.setName("打卡达人");
            badge.setDescription("累计打卡10次");
            badges.add(badge);
        }
        
        // 铲屎官：成功领养
        if (adoptionCount >= 1) {
            CreditInfoResponse.BadgeInfo badge = new CreditInfoResponse.BadgeInfo();
            badge.setId(3L);
            badge.setName("铲屎官");
            badge.setDescription("成功领养宠物");
            badges.add(badge);
        }
        
        return badges;
    }
}
