package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.service.OrgProfileService;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
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
import java.util.ArrayList;
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
    private final UserFavoriteMapper userFavoriteMapper;

    public OrgProfileServiceImpl(PetMapper petMapper, AdoptionApplicationMapper adoptionApplicationMapper,
                                 CheckinPostMapper checkinPostMapper, CreditAccountMapper creditAccountMapper,
                                 UserMapper userMapper, UserFavoriteMapper userFavoriteMapper) {
        this.petMapper = petMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.checkinPostMapper = checkinPostMapper;
        this.creditAccountMapper = creditAccountMapper;
        this.userMapper = userMapper;
        this.userFavoriteMapper = userFavoriteMapper;
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
            record.setUserPhone(user.getPhone());
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

    // ==================== 机构首页Dashboard接口实现 ====================

    @Override
    public DashboardStatisticsResponse getDashboardStatistics(Long userId) {
        DashboardStatisticsResponse response = new DashboardStatisticsResponse();

        // 在养宠物总数 (状态为 PUBLISHED、APPLYING 的宠物)
        LambdaQueryWrapper<PetEntity> petQuery = new LambdaQueryWrapper<PetEntity>()
                .eq(PetEntity::getOrgUserId, userId)
                .in(PetEntity::getStatus, List.of("PUBLISHED", "APPLYING"));
        response.setTotalPets(Math.toIntExact(petMapper.selectCount(petQuery)));

        // 待处理申请数 (状态为 SUBMITTED、UNDER_REVIEW 的申请)
        LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .inSql(AdoptionApplicationEntity::getPetId,
                       "SELECT id FROM pet WHERE org_user_id = " + userId)
                .in(AdoptionApplicationEntity::getStatus, List.of("SUBMITTED", "UNDER_REVIEW"));
        response.setPendingApplications(Math.toIntExact(adoptionApplicationMapper.selectCount(appQuery)));

        // 本月领养数 (当月 APPROVED 的申请数)
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        response.setMonthlyAdoptions(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone()
                        .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                        .ge(AdoptionApplicationEntity::getDecidedTime, monthStart))));

        // 待回访数 (已领养且回访超期的宠物数)
        response.setPendingFollowups(calculatePendingFollowups(userId));

        // 累计领养数 (历史 APPROVED 的申请总数)
        response.setTotalAdoptions(Math.toIntExact(adoptionApplicationMapper.selectCount(
                appQuery.clone().eq(AdoptionApplicationEntity::getStatus, "APPROVED"))));

        // 已发布宠物数 (状态为 PUBLISHED 的宠物数)
        response.setPublishedPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getStatus, "PUBLISHED"))));

        // 草稿宠物数 (状态为 DRAFT 的宠物数)
        response.setDraftPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getStatus, "DRAFT"))));

        // 审核中宠物数 (状态为 PENDING_AUDIT 的宠物数)
        response.setUnderReviewPets(Math.toIntExact(petMapper.selectCount(
                petQuery.clone().eq(PetEntity::getAuditStatus, "PENDING"))));

        return response;
    }

    @Override
    public TodoListResponse getDashboardTodos(Long userId, String type, Integer limit) {
        List<TodoItem> todos = new ArrayList<>();

        // 如果指定了类型，只返回该类型的待办事项
        if (!StringUtils.hasText(type) || "application".equals(type)) {
            // 获取待审核的领养申请
            LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                    .inSql(AdoptionApplicationEntity::getPetId,
                           "SELECT id FROM pet WHERE org_user_id = " + userId)
                    .in(AdoptionApplicationEntity::getStatus, List.of("SUBMITTED", "UNDER_REVIEW"))
                    .orderByDesc(AdoptionApplicationEntity::getCreateTime)
                    .last("LIMIT " + (limit != null ? limit : 10));

            List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(appQuery);
            for (AdoptionApplicationEntity app : applications) {
                TodoItem todo = convertApplicationToTodo(app);
                todos.add(todo);
            }
        }

        if (!StringUtils.hasText(type) || "followup".equals(type)) {
            // 获取待回访的宠物
            List<FollowupReminderItem> followups = getFollowupReminderList(userId, "overdue", limit).getList();
            for (FollowupReminderItem followup : followups) {
                TodoItem todo = convertFollowupToTodo(followup);
                todos.add(todo);
            }
        }

        if (!StringUtils.hasText(type) || "audit".equals(type)) {
            // 获取待审核的宠物发布
            LambdaQueryWrapper<PetEntity> petQuery = new LambdaQueryWrapper<PetEntity>()
                    .eq(PetEntity::getOrgUserId, userId)
                    .eq(PetEntity::getAuditStatus, "PENDING")
                    .orderByDesc(PetEntity::getCreateTime)
                    .last("LIMIT " + (limit != null ? limit : 10));

            List<PetEntity> pets = petMapper.selectList(petQuery);
            for (PetEntity pet : pets) {
                TodoItem todo = convertPetToTodo(pet);
                todos.add(todo);
            }
        }

        TodoListResponse response = new TodoListResponse();
        response.setTodos(todos);
        response.setTotalCount(todos.size());
        return response;
    }

    @Override
    public RecentPetListResponse getRecentPets(Long userId, Integer limit) {
        LambdaQueryWrapper<PetEntity> query = new LambdaQueryWrapper<PetEntity>()
                .eq(PetEntity::getOrgUserId, userId)
                .eq(PetEntity::getStatus, "PUBLISHED")
                .orderByDesc(PetEntity::getPublishedTime)
                .last("LIMIT " + (limit != null ? limit : 5));

        List<PetEntity> pets = petMapper.selectList(query);
        List<RecentPetItem> recentPets = pets.stream()
                .map(this::convertToRecentPetItem)
                .collect(Collectors.toList());

        RecentPetListResponse response = new RecentPetListResponse();
        response.setList(recentPets);
        return response;
    }

    @Override
    public RecentApplicationListResponse getRecentApplications(Long userId, Integer limit) {
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .inSql(AdoptionApplicationEntity::getPetId,
                       "SELECT id FROM pet WHERE org_user_id = " + userId)
                .orderByDesc(AdoptionApplicationEntity::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 5));

        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(query);
        List<RecentApplicationItem> recentApps = applications.stream()
                .map(this::convertToRecentApplicationItem)
                .collect(Collectors.toList());

        RecentApplicationListResponse response = new RecentApplicationListResponse();
        response.setList(recentApps);
        return response;
    }
    @Override
    public FollowupReminderListResponse getFollowupReminderList(Long userId, String status, Integer limit) {
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .inSql(AdoptionApplicationEntity::getPetId,
                        "SELECT id FROM pet WHERE org_user_id = " + userId)
                .orderByDesc(AdoptionApplicationEntity::getDecidedTime);

        List<AdoptionApplicationEntity> adoptions = adoptionApplicationMapper.selectList(query);

        // 关键：可变 list
        List<FollowupReminderItem> reminders = new ArrayList<>();

        for (AdoptionApplicationEntity adoption : adoptions) {
            FollowupReminderItem reminder = convertToFollowupReminderItem(adoption);

            if ("overdue".equals(status) && !"overdue".equals(reminder.getStatus())) {
                continue;
            }
            if ("soon".equals(status) && !"soon".equals(reminder.getStatus())) {
                continue;
            }

            reminders.add(reminder);

            if (limit != null && reminders.size() >= limit) {
                break;
            }
        }

        FollowupReminderListResponse response = new FollowupReminderListResponse();
        response.setList(reminders);
        return response;
    }


    @Override
    public DashboardHomeResponse getDashboardHome(Long userId) {
        DashboardHomeResponse response = new DashboardHomeResponse();

        // 获取统计数据
        response.setStatistics(getDashboardStatistics(userId));

        // 获取待办事项 (限制5条)
        response.setTodos(getDashboardTodos(userId, null, 5).getTodos());

        // 获取最近宠物 (限制4条)
        response.setRecentPets(getRecentPets(userId, 4).getList());

        // 获取最近申请 (限制3条)
        response.setRecentApplications(getRecentApplications(userId, 3).getList());

        // 获取回访提醒 (限制5条)
        response.setFollowupReminders(getFollowupReminderList(userId, "overdue", 5).getList());

        // 获取机构信息
        OrgProfileEntity orgProfile = getOne(new LambdaQueryWrapper<OrgProfileEntity>()
                .eq(OrgProfileEntity::getUserId, userId));
        if (orgProfile != null) {
            OrgInfo orgInfo = new OrgInfo();
            orgInfo.setId(orgProfile.getId());
            orgInfo.setUserId(orgProfile.getUserId());
            orgInfo.setOrgName(orgProfile.getOrgName());
            orgInfo.setCoverUrl(orgProfile.getCoverUrl());
            orgInfo.setVerifyStatus(orgProfile.getVerifyStatus());

            orgInfo.setCity(orgProfile.getCity());
            orgInfo.setDistrict(orgProfile.getDistrict());
            response.setOrgInfo(orgInfo);
        }

        return response;
    }

    // ==================== 辅助方法 ====================

    /**
     * 计算待回访数量
     */
    private Integer calculatePendingFollowups(Long userId) {
        // 获取已领养且超过7天未回访的宠物
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<AdoptionApplicationEntity> query = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .inSql(AdoptionApplicationEntity::getPetId,
                       "SELECT id FROM pet WHERE org_user_id = " + userId)
                .lt(AdoptionApplicationEntity::getDecidedTime, sevenDaysAgo);

        List<AdoptionApplicationEntity> adoptions = adoptionApplicationMapper.selectList(query);
        int pendingCount = 0;

        for (AdoptionApplicationEntity adoption : adoptions) {
            // 检查是否有最近7天的打卡记录
            LambdaQueryWrapper<CheckinPostEntity> checkinQuery = new LambdaQueryWrapper<CheckinPostEntity>()
                    .eq(CheckinPostEntity::getUserId, adoption.getUserId())
                    .eq(CheckinPostEntity::getPetId, adoption.getPetId())
                    .ge(CheckinPostEntity::getCreateTime, sevenDaysAgo);

            if (checkinPostMapper.selectCount(checkinQuery) == 0) {
                pendingCount++;
            }
        }

        return pendingCount;
    }

    /**
     * 转换领养申请为待办事项
     */
    private TodoItem convertApplicationToTodo(AdoptionApplicationEntity app) {
        TodoItem todo = new TodoItem();
        todo.setId(app.getId());
        todo.setType("application");
        todo.setStatus(app.getStatus());

        // 获取宠物信息
        PetEntity pet = petMapper.selectById(app.getPetId());
        if (pet != null) {
            todo.setPetName(pet.getName());
            todo.setPetId(pet.getId());
            todo.setPetCoverUrl(getPetCoverUrl(pet));
            todo.setTitle("\"" + pet.getName() + "\"领养申请待审核");
        }

        // 获取用户信息
        UserEntity user = userMapper.selectById(app.getUserId());
        if (user != null) {
            todo.setUserName(user.getUsername());
            todo.setUserId(user.getId());
            todo.setUserAvatar(user.getAvatar());
        }

        todo.setSubmitTime(formatDateTime(app.getCreateTime()));
        todo.setPriority("SUBMITTED".equals(app.getStatus()) ? "high" : "medium");

        return todo;
    }

    /**
     * 转换回访提醒为待办事项
     */
    private TodoItem convertFollowupToTodo(FollowupReminderItem followup) {
        TodoItem todo = new TodoItem();
        todo.setId(followup.getId());
        todo.setType("followup");
        todo.setTitle("\"" + followup.getPetName() + "\"回访已超期" + followup.getOverdueDays() + "天");
        todo.setPetName(followup.getPetName());
        todo.setPetId(followup.getPetId());
        todo.setPetCoverUrl(followup.getPetCoverUrl());
        todo.setAdoptionTime(followup.getAdoptedTime());
        todo.setOverdueDays(followup.getOverdueDays());
        todo.setLastFollowupTime(followup.getLastFollowupTime());
        todo.setPriority("urgent");

        return todo;
    }

    /**
     * 转换宠物为待办事项
     */
    private TodoItem convertPetToTodo(PetEntity pet) {
        TodoItem todo = new TodoItem();
        todo.setId(pet.getId());
        todo.setType("audit");
        todo.setTitle("\"" + pet.getName() + "\"待管理员审核");
        todo.setPetName(pet.getName());
        todo.setPetId(pet.getId());
        todo.setPetCoverUrl(getPetCoverUrl(pet));
        todo.setStatus(pet.getAuditStatus());
        todo.setSubmitTime(formatDateTime(pet.getCreateTime()));
        todo.setPriority("high");

        return todo;
    }

    /**
     * 转换宠物为最近宠物项
     */
    private RecentPetItem convertToRecentPetItem(PetEntity pet) {
        RecentPetItem item = new RecentPetItem();
        item.setId(pet.getId());
        item.setName(pet.getName());
        item.setSpecies(pet.getSpecies());
        item.setBreed(pet.getBreed());
        item.setAgeMonth(pet.getAgeMonth());
        item.setGender(pet.getGender());
        item.setCoverUrl(getPetCoverUrl(pet));
        item.setStatus(pet.getStatus());
        item.setPublishedTime(formatDateTime(pet.getPublishedTime()));


        // 统计收藏次数
        LambdaQueryWrapper<UserFavoriteEntity> favQuery = new LambdaQueryWrapper<UserFavoriteEntity>()
                .eq(UserFavoriteEntity::getPetId, pet.getId());
        // 假设有UserFavoriteMapper，这里暂时设置为0
        Long selectCount = userFavoriteMapper.selectCount(favQuery);
        item.setFavoriteCount(selectCount.intValue());
        item.setViewCount(selectCount.intValue());
        // 统计申请次数
        LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getPetId, pet.getId());
        item.setApplicationCount(Math.toIntExact(adoptionApplicationMapper.selectCount(appQuery)));

        return item;
    }

    /**
     * 转换申请为最近申请项
     */
    private RecentApplicationItem convertToRecentApplicationItem(AdoptionApplicationEntity app) {
        RecentApplicationItem item = new RecentApplicationItem();
        item.setId(app.getId());
        item.setPetId(app.getPetId());
        item.setUserId(app.getUserId());
        item.setStatus(app.getStatus());
        item.setStatusDesc(getApplicationStatusDesc(app.getStatus()));
        item.setSubmitTime(formatDateTime(app.getCreateTime()));

        // 获取宠物信息
        PetEntity pet = petMapper.selectById(app.getPetId());
        if (pet != null) {
            item.setPetName(pet.getName());
            item.setPetCoverUrl(getPetCoverUrl(pet));
        }

        // 获取用户信息
        UserEntity user = userMapper.selectById(app.getUserId());
        if (user != null) {
            item.setUserName(user.getUsername());
            item.setUserAvatar(user.getAvatar());
        }

        return item;
    }

    /**
     * 转换申请为回访提醒项
     */
    private FollowupReminderItem convertToFollowupReminderItem(AdoptionApplicationEntity adoption) {
        FollowupReminderItem reminder = new FollowupReminderItem();
        reminder.setId(adoption.getId());
        reminder.setPetId(adoption.getPetId());
        reminder.setAdoptionApplicationId(adoption.getId());
        reminder.setUserId(adoption.getUserId());
        reminder.setAdoptedTime(formatDateTime(adoption.getDecidedTime()));

        // 获取宠物信息
        PetEntity pet = petMapper.selectById(adoption.getPetId());
        if (pet != null) {
            reminder.setPetName(pet.getName());
            reminder.setPetCoverUrl(getPetCoverUrl(pet));
        }

        // 获取用户信息
        UserEntity user = userMapper.selectById(adoption.getUserId());
        if (user != null) {
            reminder.setUserName(user.getUsername());
            reminder.setUserPhone(maskPhoneNumber(user.getPhone()));
        }

        // 计算回访状态
        if (adoption.getDecidedTime() != null) {
            long daysSinceAdoption = ChronoUnit.DAYS.between(adoption.getDecidedTime(), LocalDateTime.now());
            reminder.setOverdueDays((int) Math.max(0, daysSinceAdoption - 7));

            // 检查是否有最近的打卡记录
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            LambdaQueryWrapper<CheckinPostEntity> checkinQuery = new LambdaQueryWrapper<CheckinPostEntity>()
                    .eq(CheckinPostEntity::getUserId, adoption.getUserId())
                    .eq(CheckinPostEntity::getPetId, adoption.getPetId())
                    .ge(CheckinPostEntity::getCreateTime, sevenDaysAgo);

            if (checkinPostMapper.selectCount(checkinQuery) > 0) {
                reminder.setStatus("soon"); // 有打卡记录，状态为即将到期
                reminder.setOverdueDays(0);
            } else if (daysSinceAdoption > 7) {
                reminder.setStatus("overdue"); // 超过7天无打卡
            } else {
                reminder.setStatus("soon"); // 未到7天
                reminder.setOverdueDays(0);
            }

            // 计算下次回访日期
            if (daysSinceAdoption < 7) {
                reminder.setNextFollowupDate(formatDate(adoption.getDecidedTime().plusDays(7)));
            } else if (daysSinceAdoption < 30) {
                reminder.setNextFollowupDate(formatDate(adoption.getDecidedTime().plusDays(30)));
            } else {
                reminder.setNextFollowupDate(formatDate(adoption.getDecidedTime().plusDays(90)));
            }
        }

        return reminder;
    }

    /**
     * 获取宠物封面URL
     */
    private String getPetCoverUrl(PetEntity pet) {
        // 这里应该从pet_media表获取封面图片
        // 暂时返回空字符串
        return "";
    }

    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString().replace("T", " ");
    }

    /**
     * 格式化日期
     */
    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate().toString();
    }

    /**
     * 获取审核状态描述
     */
    private String getVerifyStatusDesc(String status) {
        switch (status) {
            case "PENDING":
                return "待审核";
            case "APPROVED":
                return "已认证";
            case "REJECTED":
                return "已拒绝";
            default:
                return status;
        }
    }

    /**
     * 获取申请状态描述
     */
    private String getApplicationStatusDesc(String status) {
        switch (status) {
            case "SUBMITTED":
                return "已提交";
            case "UNDER_REVIEW":
                return "审核中";
            case "INTERVIEW":
                return "已约面谈";
            case "HOME_VISIT":
                return "家访中";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            case "CANCELLED":
                return "已取消";
            default:
                return status;
        }
    }
}
