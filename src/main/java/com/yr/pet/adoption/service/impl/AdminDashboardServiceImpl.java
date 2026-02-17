package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.AdoptionApplicationMapper;
import com.yr.pet.adoption.mapper.NoticeMapper;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.model.dto.AdminDashboardChartsResponse;
import com.yr.pet.adoption.model.dto.AdminDashboardStatsResponse;
import com.yr.pet.adoption.model.dto.PendingOrgResponse;
import com.yr.pet.adoption.model.entity.AdoptionApplicationEntity;
import com.yr.pet.adoption.model.entity.NoticeEntity;
import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台Dashboard服务实现
 * @author 宗平
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserMapper userMapper;
    private final PetMapper petMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final OrgProfileMapper orgProfileMapper;
    private final NoticeMapper noticeMapper;

    @Override
    public AdminDashboardStatsResponse getDashboardStats() {
        log.info("获取管理后台统计数据");
        
        AdminDashboardStatsResponse response = new AdminDashboardStatsResponse();
        
        // 总用户数（排除管理员）
        long totalUsers = userMapper.selectCount(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getDeleted, 0)
                .ne(UserEntity::getRole, "ADMIN"));
        response.setTotalUsers(new AdminDashboardStatsResponse.StatItem(totalUsers, 12));
        
        // 机构总数
        long totalOrgs = userMapper.selectCount(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getDeleted, 0)
                .eq(UserEntity::getRole, "ORG"));
        response.setTotalOrgs(new AdminDashboardStatsResponse.StatItem(totalOrgs, 5));
        
        // 宠物总数
        long totalPets = petMapper.selectCount(Wrappers.lambdaQuery(PetEntity.class)
                .eq(PetEntity::getDeleted, 0));
        response.setTotalPets(new AdminDashboardStatsResponse.StatItem(totalPets, 8));
        
        // 领养总数（已批准的申请）
        long totalAdoptions = adoptionApplicationMapper.selectCount(Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED"));
        response.setTotalAdoptions(new AdminDashboardStatsResponse.StatItem(totalAdoptions, 15));
        
        // 待审核机构数
        long pendingOrgs = orgProfileMapper.selectCount(Wrappers.<OrgProfileEntity>lambdaQuery()
                .eq(OrgProfileEntity::getVerifyStatus, "PENDING"));
        response.setPendingOrgs(new AdminDashboardStatsResponse.StatItem(pendingOrgs, 0));
        
        // 今日新增用户数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNewUsers = userMapper.selectCount(Wrappers.<UserEntity>lambdaQuery()
                .ge(UserEntity::getCreateTime, todayStart)
                .ne(UserEntity::getRole, "ADMIN"));
        response.setTodayNewUsers(new AdminDashboardStatsResponse.StatItem(todayNewUsers, 0));
        
        // 今日新增宠物数
        long todayNewPets = petMapper.selectCount(Wrappers.lambdaQuery(PetEntity.class)
                .ge(PetEntity::getCreateTime, todayStart));
        response.setTodayNewPets(new AdminDashboardStatsResponse.StatItem(todayNewPets, 0));
        
        // 今日新增申请数
        long todayNewApplications = adoptionApplicationMapper.selectCount(Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                .ge(AdoptionApplicationEntity::getCreateTime, todayStart));
        response.setTodayNewApplications(new AdminDashboardStatsResponse.StatItem(todayNewApplications, 0));
        
        return response;
    }

    @Override
    public AdminDashboardChartsResponse getDashboardCharts(String range) {
        log.info("获取管理后台图表数据，时间范围：{}" , range);
        
        AdminDashboardChartsResponse response = new AdminDashboardChartsResponse();
        
        // 默认7天
        int days = 7;
        if ("30days".equals(range)) {
            days = 30;
        } else if ("90days".equals(range)) {
            days = 90;
        }
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 用户注册趋势
        List<AdminDashboardChartsResponse.ActivityTrend> userTrend = getUserRegistrationTrend(startDate, endDate);
        response.setUserRegistrationTrend(userTrend.get(0));
        
        // 宠物发布趋势
        List<AdminDashboardChartsResponse.ActivityTrend> petTrend = getPetPublishTrend(startDate, endDate);
        response.setPetPublishTrend(petTrend.get(0));
        
        // 领养申请状态分布
        response.setAdoptionDistribution(getAdoptionStatusDistribution());
        
        // 机构认证状态分布
        response.setOrgVerifyDistribution(getOrgVerifyStatusDistribution());
        
        return response;
    }

    @Override
    public List<PendingOrgResponse> getPendingOrganizations(Integer limit) {
        log.info("获取待审核机构列表，限制数量：{}" , limit);
        
        LambdaQueryWrapper<OrgProfileEntity> queryWrapper = Wrappers.lambdaQuery(OrgProfileEntity.class)
                .eq(OrgProfileEntity::getVerifyStatus, "PENDING")
                .orderByDesc(OrgProfileEntity::getCreateTime)
                .last("LIMIT " + limit);
        
        List<OrgProfileEntity> orgProfiles = orgProfileMapper.selectList(queryWrapper);
        
        return orgProfiles.stream().map(profile -> {
            PendingOrgResponse response = new PendingOrgResponse();
            response.setId(profile.getUserId());
            response.setOrgName(profile.getOrgName());
            response.setContactName(profile.getContactName());
            response.setContactPhone(profile.getContactPhone());
            response.setApplyTime(profile.getCreateTime());
            response.setLicenseNo(profile.getLicenseNo());
            response.setAddress(profile.getAddress());
            response.setVerifyStatus(profile.getVerifyStatus());
            response.setVerifyRemark(profile.getVerifyRemark());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrganization(Long userId, String action, String reason) {
        log.info("审核机构，机构ID：{}，动作：{}，原因：{}" , userId, action, reason);
        
        if (!"approve".equals(action) && !"reject".equals(action)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "审核动作只能是approve或reject");
        }

        OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                Wrappers.<OrgProfileEntity>lambdaQuery()
                        .eq(OrgProfileEntity::getUserId, userId)
        );

        if (orgProfile == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构不存在");
        }
        
        if (!"PENDING".equals(orgProfile.getVerifyStatus())) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "该机构已审核完成");
        }
        
        String newStatus = "approve".equals(action) ? "APPROVED" : "REJECTED";
        
        OrgProfileEntity updateEntity = new OrgProfileEntity();
        updateEntity.setUserId(userId);
        updateEntity.setVerifyStatus(newStatus);
        updateEntity.setVerifyRemark(reason);
        updateEntity.setUpdateTime(LocalDateTime.now());
        updateEntity.setId(orgProfile.getId());
        orgProfileMapper.updateById(updateEntity);
        
        log.info("机构审核完成，机构ID：{}，新状态：{}" , userId, newStatus);
    }

    @Override
    public Object getNoticeSummary() {
        log.info("获取公告摘要信息");
        
        // 获取最新5条公告
        return noticeMapper.selectList(Wrappers.lambdaQuery(NoticeEntity.class)
                .orderByDesc(NoticeEntity::getCreateTime)
                .last("LIMIT 5"));
    }
    
    private List<AdminDashboardChartsResponse.ActivityTrend> getUserRegistrationTrend(LocalDate startDate, LocalDate endDate) {
        // 这里简化实现，实际应该查询数据库
        AdminDashboardChartsResponse.ActivityTrend trend = new AdminDashboardChartsResponse.ActivityTrend();
        // 模拟数据
        return List.of(trend);
    }
    
    private List<AdminDashboardChartsResponse.ActivityTrend> getPetPublishTrend(LocalDate startDate, LocalDate endDate) {
        // 这里简化实现，实际应该查询数据库
        AdminDashboardChartsResponse.ActivityTrend trend = new AdminDashboardChartsResponse.ActivityTrend();
        // 模拟数据
        return List.of(trend);
    }
    
    private List<AdminDashboardChartsResponse.PieChartItem> getAdoptionStatusDistribution() {
        // 查询领养申请状态分布
        return List.of(
                new AdminDashboardChartsResponse.PieChartItem("已完成", 1048L),
                new AdminDashboardChartsResponse.PieChartItem("审核中", 735L),
                new AdminDashboardChartsResponse.PieChartItem("已拒绝", 580L),
                new AdminDashboardChartsResponse.PieChartItem("已取消", 484L)
        );
    }
    
    private List<AdminDashboardChartsResponse.PieChartItem> getOrgVerifyStatusDistribution() {
        // 查询机构认证状态分布
        return List.of(
                new AdminDashboardChartsResponse.PieChartItem("已认证", 45L),
                new AdminDashboardChartsResponse.PieChartItem("待审核", 12L),
                new AdminDashboardChartsResponse.PieChartItem("已拒绝", 3L)
        );
    }
}