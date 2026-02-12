package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.AdoptionApplicationMapper;
import com.yr.pet.adoption.mapper.CheckinPostMapper;
import com.yr.pet.adoption.mapper.CreditAccountMapper;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.service.OrgProfileService;
import com.yr.pet.adoption.common.BizException;
import com.yr.pet.adoption.common.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 救助机构资料表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class OrgProfileServiceImpl extends ServiceImpl<OrgProfileMapper, OrgProfileEntity> implements OrgProfileService {

    private final PetMapper petMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final CheckinPostMapper checkinPostMapper;
    private final CreditAccountMapper creditAccountMapper;
    private final UserMapper userMapper;

    public OrgProfileServiceImpl(PetMapper petMapper, AdoptionApplicationMapper adoptionApplicationMapper,
                               CheckinPostMapper checkinPostMapper, CreditAccountMapper creditAccountMapper,
                               UserMapper userMapper) {
        this.petMapper = petMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.checkinPostMapper = checkinPostMapper;
        this.creditAccountMapper = creditAccountMapper;
        this.userMapper = userMapper;
    }

    @Override
    public OrgProfileResponse getProfile(Long userId) {
        OrgProfileEntity entity = getOne(new LambdaQueryWrapper<OrgProfileEntity>()
                .eq(OrgProfileEntity::getUserId, userId));
        
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }

        OrgProfileResponse response = new OrgProfileResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, OrgProfileUpdateRequest request) {
        OrgProfileEntity entity = getOne(new LambdaQueryWrapper<OrgProfileEntity>()
                .eq(OrgProfileEntity::getUserId, userId));
        
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }

        BeanUtils.copyProperties(request, entity, "id", "userId", "verifyStatus", "verifyRemark");
        
        // 如果地址信息发生变化，需要重新审核
        boolean addressChanged = !StringUtils.hasText(entity.getAddress()) || 
                !entity.getAddress().equals(request.getAddress()) ||
                !StringUtils.hasText(entity.getProvince()) || 
                !entity.getProvince().equals(request.getProvince()) ||
                !StringUtils.hasText(entity.getCity()) || 
                !entity.getCity().equals(request.getCity());
        
        if (addressChanged) {
            entity.setVerifyStatus("PENDING");
            entity.setVerifyRemark(null);
        }
        
        updateById(entity);
    }

    @Override
    public OrgStatisticsResponse getStatistics(Long userId) {
        OrgStatisticsResponse response = new OrgStatisticsResponse();
        
        // 概览统计
        OrgOverviewStatistics overview = new OrgOverviewStatistics();
        LambdaQueryWrapper<PetEntity> petQuery = new LambdaQueryWrapper<PetEntity>()
                .eq(PetEntity::getOrgUserId, userId);
        
        overview.setTotalPets(Math.toIntExact(petMapper.selectCount(petQuery)));
        overview.setPublishedPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getStatus, "PUBLISHED"))));
        overview.setDraftPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getStatus, "DRAFT"))));
        overview.setAdoptedPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getStatus, "ADOPTED"))));
        overview.setPendingAuditPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getAuditStatus, "PENDING"))));
        response.setOverview(overview);
        
        // 申请统计
        OrgApplicationStatistics applications = new OrgApplicationStatistics();
        LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .inSql(AdoptionApplicationEntity::getPetId, 
                       "SELECT id FROM pet WHERE org_user_id = " + userId);
        
        applications.setTotalApplications(Math.toIntExact(adoptionApplicationMapper.selectCount(appQuery)));
        applications.setPendingReview(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().eq(AdoptionApplicationEntity::getStatus, "UNDER_REVIEW"))));
        applications.setInProgress(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().in(AdoptionApplicationEntity::getStatus, 
                List.of("INTERVIEW", "HOME_VISIT")))));
        applications.setApproved(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().eq(AdoptionApplicationEntity::getStatus, "APPROVED"))));
        applications.setRejected(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().eq(AdoptionApplicationEntity::getStatus, "REJECTED"))));
        response.setApplications(applications);
        
        // 领养统计
        OrgAdoptionStatistics adoption = new OrgAdoptionStatistics();
        int approved = applications.getApproved();
        int total = applications.getTotalApplications();
        adoption.setSuccessRate(total > 0 ? BigDecimal.valueOf(approved)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).doubleValue() : 0.0);
        
        // 计算平均处理天数
        adoption.setAverageProcessDays(calculateAverageProcessDays(userId));
        
        // 本月统计
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        adoption.setThisMonthAdoptions(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .ge(AdoptionApplicationEntity::getDecidedTime, monthStart))));
        adoption.setThisMonthApplications(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().ge(AdoptionApplicationEntity::getCreateTime, monthStart))));
        response.setAdoption(adoption);
        
        // 信用统计
        OrgCredibilityStatistics credibility = calculateCredibilityStatistics(userId);
        response.setCredibility(credibility);
        
        return response;
    }

    @Override
    public PageResult<OrgAdoptionRecord> getAdoptionRecords(Long userId, Long petId, Long targetUserId, 
            String month, Integer pageNo, Integer pageSize) {
        
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .inSql(AdoptionApplicationEntity::getPetId, 
                       "SELECT id FROM pet WHERE org_user_id = " + userId);
        
        if (petId != null) {
            query.eq(AdoptionApplicationEntity::getPetId, petId);
        }
        if (targetUserId != null) {
            query.eq(AdoptionApplicationEntity::getUserId, targetUserId);
        }
        if (StringUtils.hasText(month)) {
            LocalDateTime monthStart = LocalDateTime.parse(month + "-01T00:00:00");
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            query.between(AdoptionApplicationEntity::getDecidedTime, monthStart, monthEnd);
        }
        
        Page<AdoptionApplicationEntity> page = new Page<>(pageNo, pageSize);
        Page<AdoptionApplicationEntity> result = adoptionApplicationMapper.selectPage(page, query);
        
        List<OrgAdoptionRecord> records = result.getRecords().stream()
                .map(this::convertToAdoptionRecord)
                .collect(Collectors.toList());
        
        return new PageResult<>(records, pageNo, pageSize, result.getTotal(), 
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public FollowupReminderResponse getFollowupReminders(Long userId) {
        // 获取该机构已领养的宠物
        List<AdoptionApplicationEntity> adoptions = adoptionApplicationMapper.selectList(
                new LambdaQueryWrapper<AdoptionApplicationEntity>()
                        .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                        .inSql(AdoptionApplicationEntity::getPetId, 
                               "SELECT id FROM pet WHERE org_user_id = " + userId));
        
        List<FollowupReminder> overdue = List.of();
        List<FollowupReminder> upcoming = List.of();
        
        for (AdoptionApplicationEntity adoption : adoptions) {
            FollowupReminder reminder = convertToFollowupReminder(adoption);
            
            if (reminder.getIsOverdue()) {
                overdue.add(reminder);
            } else if (reminder.getDaysRemaining() != null && reminder.getDaysRemaining() <= 3) {
                upcoming.add(reminder);
            }
        }
        
        FollowupReminderResponse response = new FollowupReminderResponse();
        response.setOverdue(overdue);
        response.setUpcoming(upcoming);
        return response;
    }

    private OrgAdoptionRecord convertToAdoptionRecord(AdoptionApplicationEntity entity) {
        OrgAdoptionRecord record = new OrgAdoptionRecord();
        record.setId(entity.getId());
        record.setPetId(entity.getPetId());
        record.setUserId(entity.getUserId());
        record.setAdoptedTime(entity.getDecidedTime());
        
        // 获取宠物名称
        PetEntity pet = petMapper.selectById(entity.getPetId());
        if (pet != null) {
            record.setPetName(pet.getName());
        }
        
        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            record.setUserName(user.getUsername());
            record.setUserPhone(maskPhoneNumber(user.getPhone()));
        }
        
        // 获取用户信用
        CreditAccountEntity credit = creditAccountMapper.selectById(entity.getUserId());
        if (credit != null) {
            UserCreditInfo creditInfo = new UserCreditInfo();
            creditInfo.setScore(credit.getScore());
            creditInfo.setLevel(credit.getLevel());
            record.setUserCredit(creditInfo);
        }
        
        // 计算天数
        if (entity.getDecidedTime() != null) {
            long days = ChronoUnit.DAYS.between(entity.getDecidedTime(), LocalDateTime.now());
            record.setDaysSinceAdoption((int) days);
        }
        
        // 计算打卡情况
        LambdaQueryWrapper<CheckinPostEntity> checkinQuery = new LambdaQueryWrapper<CheckinPostEntity>()
                .eq(CheckinPostEntity::getUserId, entity.getUserId())
                .eq(CheckinPostEntity::getPetId, entity.getPetId());
        int checkinCount = Math.toIntExact(checkinPostMapper.selectCount(checkinQuery));
        record.setCheckinCount(checkinCount);
        
        if (entity.getDecidedTime() != null) {
            long days = ChronoUnit.DAYS.between(entity.getDecidedTime(), LocalDateTime.now());
            record.setCheckinRate(days > 0 ? (double) checkinCount / days : 0.0);
        }
        
        // 计算用户状态和风险等级
        record.setUserStatus(determineUserStatus(user));
        record.setRiskLevel(calculateRiskLevel(record, user));
        
        return record;
    }

    private FollowupReminder convertToFollowupReminder(AdoptionApplicationEntity entity) {
        FollowupReminder reminder = new FollowupReminder();
        reminder.setPetId(entity.getPetId());
        reminder.setUserId(entity.getUserId());
        reminder.setAdoptedTime(entity.getDecidedTime());
        
        // 获取宠物名称
        PetEntity pet = petMapper.selectById(entity.getPetId());
        if (pet != null) {
            reminder.setPetName(pet.getName());
        }
        
        // 获取用户名
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            reminder.setUserName(user.getUsername());
        }
        
        if (entity.getDecidedTime() != null) {
            long days = ChronoUnit.DAYS.between(entity.getDecidedTime(), LocalDateTime.now());
            reminder.setDaysSinceAdoption((int) days);
            
            // 预期打卡天数为7、30、90天
            Integer expectedCheckinDay = null;
            Boolean isOverdue = false;
            Integer daysRemaining = null;
            
            if (days < 7) {
                expectedCheckinDay = 7;
                daysRemaining = 7 - (int) days;
            } else if (days < 30) {
                expectedCheckinDay = 30;
                daysRemaining = 30 - (int) days;
            } else if (days < 90) {
                expectedCheckinDay = 90;
                daysRemaining = 90 - (int) days;
            } else {
                // 超过90天，检查是否按时打卡
                expectedCheckinDay = 90;
                isOverdue = true;
            }
            
            reminder.setExpectedCheckinDay(expectedCheckinDay);
            reminder.setIsOverdue(isOverdue);
            reminder.setDaysRemaining(daysRemaining);
        }
        
        reminder.setRiskLevel("低");
        
        return reminder;
    }

    /**
     * 计算平均处理天数
     */
    private Integer calculateAverageProcessDays(Long userId) {
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .inSql(AdoptionApplicationEntity::getPetId, 
                       "SELECT id FROM pet WHERE org_user_id = " + userId)
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .isNotNull(AdoptionApplicationEntity::getDecidedTime)
                .isNotNull(AdoptionApplicationEntity::getCreateTime);
        
        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(query);
        if (applications.isEmpty()) {
            return 0;
        }
        
        long totalDays = applications.stream()
                .mapToLong(app -> ChronoUnit.DAYS.between(app.getCreateTime(), app.getDecidedTime()))
                .sum();
        
        return (int) (totalDays / applications.size());
    }

    /**
     * 计算信用统计
     */
    private OrgCredibilityStatistics calculateCredibilityStatistics(Long userId) {
        OrgCredibilityStatistics credibility = new OrgCredibilityStatistics();
        
        // 获取该机构的已领养用户
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .inSql(AdoptionApplicationEntity::getPetId, 
                       "SELECT id FROM pet WHERE org_user_id = " + userId);
        
        List<AdoptionApplicationEntity> adoptions = adoptionApplicationMapper.selectList(query);
        if (adoptions.isEmpty()) {
            credibility.setAverageUserCreditScore(0);
            credibility.setUserViolations(0);
            credibility.setOrgRating(5.0);
            credibility.setReviewCount(0);
            return credibility;
        }
        
        List<Long> userIds = adoptions.stream()
                .map(AdoptionApplicationEntity::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 计算平均信用分
        LambdaQueryWrapper<CreditAccountEntity> creditQuery = new LambdaQueryWrapper<CreditAccountEntity>()
                .in(CreditAccountEntity::getUserId, userIds);
        List<CreditAccountEntity> credits = creditAccountMapper.selectList(creditQuery);
        
        double averageScore = credits.stream()
                .mapToInt(CreditAccountEntity::getScore)
                .average()
                .orElse(0.0);
        credibility.setAverageUserCreditScore((int) averageScore);
        
        // 计算用户违规（信用分低于60的视为违规）
        long violations = credits.stream()
                .mapToLong(credit -> credit.getScore() < 60 ? 1 : 0)
                .sum();
        credibility.setUserViolations((int) violations);
        
        // 计算机构评级（基于信用分和违规率）
        double violationRate = credits.isEmpty() ? 0 : (double) violations / credits.size();
        double rating = 5.0 - (violationRate * 2.0) - ((100 - averageScore) / 100.0);
        credibility.setOrgRating(Math.max(1.0, Math.min(5.0, rating)));
        
        credibility.setReviewCount(adoptions.size());
        
        return credibility;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 确定用户状态
     */
    private String determineUserStatus(UserEntity user) {
        if (user == null) {
            return "未知";
        }
        
        String status = user.getStatus();
        switch (status) {
            case "NORMAL":
                return "正常";
            case "BANNED":
                return "已封禁";
            default:
                return status;
        }
    }

    /**
     * 计算风险等级
     */
    private String calculateRiskLevel(OrgAdoptionRecord record, UserEntity user) {
        if (user == null) {
            return "未知";
        }
        
        // 如果用户被封禁，直接标记为高风险
        if ("BANNED".equals(user.getStatus())) {
            return "高";
        }
        
        // 基于信用分和打卡情况评估风险
        if (record.getUserCredit() != null) {
            int score = record.getUserCredit().getScore();
            if (score < 60) {
                return "高";
            } else if (score < 80) {
                return "中";
            }
        }
        
        // 检查打卡率
        if (record.getCheckinRate() != null && record.getDaysSinceAdoption() > 30) {
            if (record.getCheckinRate() < 0.3) {
                return "中";
            }
        }
        
        return "低";
    }
}
