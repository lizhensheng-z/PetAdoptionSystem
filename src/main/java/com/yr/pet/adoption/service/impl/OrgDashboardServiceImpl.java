package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.service.OrgDashboardService;
import com.yr.pet.adoption.service.OrgProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构管理首页服务实现
 * @author 宗平
 * @since 2026-02-17
 */
@Service
public class OrgDashboardServiceImpl implements OrgDashboardService {

    @Autowired
    private PetMapper petMapper;
    
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;
    
    @Autowired
    private CheckinPostMapper checkinPostMapper;
    
    @Autowired
    private OrgProfileMapper orgProfileMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PetMediaMapper petMediaMapper;
    
    @Autowired
    private OrgProfileService orgProfileService;

    @Override
    public OrgStatisticsResponse getStatistics(Long orgUserId) {
        OrgStatisticsResponse response = new OrgStatisticsResponse();
        
        // 获取机构的所有宠物ID
        List<PetEntity> orgPets = petMapper.selectList(
                Wrappers.lambdaQuery(PetEntity.class)
                        .eq(PetEntity::getOrgUserId, orgUserId)
                        .eq(PetEntity::getDeleted, 0)
        );
        
        List<Long> orgPetIds = orgPets.stream().map(PetEntity::getId).collect(Collectors.toList());
        
        // 统计各状态宠物数量
        response.setTotalPets((int) orgPets.stream()
                .filter(p -> List.of("PUBLISHED", "APPLYING").contains(p.getStatus()))
                .count());
        response.setPublishedPets((int) orgPets.stream()
                .filter(p -> "PUBLISHED".equals(p.getStatus()))
                .count());
        response.setDraftPets((int) orgPets.stream()
                .filter(p -> "DRAFT".equals(p.getStatus()))
                .count());
        response.setUnderReviewPets((int) orgPets.stream()
                .filter(p -> "PENDING_AUDIT".equals(p.getStatus()))
                .count());
        
        if (orgPetIds.isEmpty()) {
            response.setPendingApplications(0);
            response.setMonthlyAdoptions(0);
            response.setTotalAdoptions(0);
            response.setPendingFollowups(0);
        } else {
            // 待处理申请数
            response.setPendingApplications(Math.toIntExact(adoptionApplicationMapper.selectCount(
                    Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                            .in(AdoptionApplicationEntity::getPetId, orgPetIds)
                            .in(AdoptionApplicationEntity::getStatus, "SUBMITTED", "UNDER_REVIEW")
                            .eq(AdoptionApplicationEntity::getDeleted, 0)
            )));
            
            // 本月领养数
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            response.setMonthlyAdoptions(Math.toIntExact(adoptionApplicationMapper.selectCount(
                    Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                            .in(AdoptionApplicationEntity::getPetId, orgPetIds)
                            .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                            .ge(AdoptionApplicationEntity::getDecidedTime, startOfMonth)
                            .eq(AdoptionApplicationEntity::getDeleted, 0)
            )));
            
            // 累计领养数
            response.setTotalAdoptions(Math.toIntExact(adoptionApplicationMapper.selectCount(
                    Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                            .in(AdoptionApplicationEntity::getPetId, orgPetIds)
                            .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                            .eq(AdoptionApplicationEntity::getDeleted, 0)
            )));
            
            // 待回访数 - 使用更安全的查询方式
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            long pendingFollowups = 0;
            
            // 先查询符合条件的领养记录
            List<AdoptionApplicationEntity> adoptedApplications = adoptionApplicationMapper.selectList(
                    Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                            .in(AdoptionApplicationEntity::getPetId, orgPetIds)
                            .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                            .lt(AdoptionApplicationEntity::getDecidedTime, sevenDaysAgo)
                            .eq(AdoptionApplicationEntity::getDeleted, 0)
            );
            
            if (!adoptedApplications.isEmpty()) {
                List<Long> adoptedPetIds = adoptedApplications.stream()
                        .map(AdoptionApplicationEntity::getPetId)
                        .collect(Collectors.toList());
                
                if (!adoptedPetIds.isEmpty()) {
                    // 查询这些宠物是否有7天内的打卡记录
                    long recentCheckinCount = checkinPostMapper.selectCount(
                            Wrappers.lambdaQuery(CheckinPostEntity.class)
                                    .in(CheckinPostEntity::getPetId, adoptedPetIds)
                                    .ge(CheckinPostEntity::getCreateTime, sevenDaysAgo)
                                    .eq(CheckinPostEntity::getDeleted, 0)
                    );
                    
                    pendingFollowups = adoptedPetIds.size() - recentCheckinCount;
                }
            }
            
            response.setPendingFollowups((int) Math.max(0, pendingFollowups));
        }
        
        response.setTimestamp(LocalDateTime.now());
        
        return response;
    }

    @Override
    public TodoListResponse getTodos(Long orgUserId, String type, Integer limit) {
        TodoListResponse response = new TodoListResponse();
        
        // 这里简化实现，实际应该根据type筛选不同类型的待办事项
        // 实际实现需要复杂的SQL查询和关联
        
        response.setTodos(List.of()); // 简化实现
        response.setTotalCount(0);
        
        return response;
    }

    @Override
    public List<PetListResponse> getRecentPets(Long orgUserId, Integer limit) {
        LambdaQueryWrapper<PetEntity> query = Wrappers.lambdaQuery(PetEntity.class)
                .eq(PetEntity::getOrgUserId, orgUserId)
                .eq(PetEntity::getDeleted, 0)
                .orderByDesc(PetEntity::getCreateTime)
                .last("LIMIT " + limit);
        
        List<PetEntity> pets = petMapper.selectList(query);
        
        return pets.stream().map(pet -> {
            PetListResponse response = new PetListResponse();
            response.setId(pet.getId());
            response.setName(pet.getName());
            response.setSpecies(pet.getSpecies());
            response.setBreed(pet.getBreed());
            response.setAgeMonth(pet.getAgeMonth());
            response.setGender(pet.getGender());
            response.setStatus(pet.getStatus());
            response.setPublishedTime(pet.getCreateTime());
            
            // 获取封面图片
            PetMediaEntity cover = petMediaMapper.selectOne(
                    Wrappers.lambdaQuery(PetMediaEntity.class)
                            .eq(PetMediaEntity::getPetId, pet.getId())

                            .eq(PetMediaEntity::getDeleted, 0)
                            .orderByAsc(PetMediaEntity::getSort)
                            .last("LIMIT 1")
            );
            if (cover != null) {
                response.setCoverUrl(cover.getUrl());
            }
            
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationListResponse> getRecentApplications(Long orgUserId, Integer limit) {
        // 获取机构的所有宠物ID
        List<Long> orgPetIds = petMapper.selectList(
                Wrappers.lambdaQuery(PetEntity.class)
                        .eq(PetEntity::getOrgUserId, orgUserId)
                        .eq(PetEntity::getDeleted, 0)
        ).stream().map(PetEntity::getId).collect(Collectors.toList());
        
        if (orgPetIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<AdoptionApplicationEntity> query = Wrappers.lambdaQuery(AdoptionApplicationEntity.class)
                .in(AdoptionApplicationEntity::getPetId, orgPetIds)
                .eq(AdoptionApplicationEntity::getDeleted, 0)
                .orderByDesc(AdoptionApplicationEntity::getCreateTime)
                .last("LIMIT " + limit);
        
        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(query);
        
        return applications.stream().map(app -> {
            ApplicationListResponse response = new ApplicationListResponse();
            response.setId(app.getId());
            response.setPetId(app.getPetId());
            response.setUserId(app.getUserId());
            response.setStatus(app.getStatus());
            response.setSubmitTime(app.getCreateTime());
            
            // 获取宠物信息
            PetEntity pet = petMapper.selectById(app.getPetId());
            if (pet != null) {
                response.setPetName(pet.getName());
                
                PetMediaEntity cover = petMediaMapper.selectOne(
                        Wrappers.lambdaQuery(PetMediaEntity.class)
                                .eq(PetMediaEntity::getPetId, pet.getId())

                                .eq(PetMediaEntity::getDeleted, 0)
                                .orderByAsc(PetMediaEntity::getSort)
                                .last("LIMIT 1")
                );
                if (cover != null) {
                    response.setPetCoverUrl(cover.getUrl());
                }
            }
            
            // 获取用户信息
            UserEntity user = userMapper.selectById(app.getUserId());
            if (user != null) {
                response.setUserName(user.getUsername());
                response.setUserAvatar(user.getAvatar());
            }
            
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public FollowupReminderListResponse getFollowupReminders(Long orgUserId, String status, Integer limit) {
        FollowupReminderListResponse response = new FollowupReminderListResponse();
        
        // 获取机构的所有宠物ID
        List<Long> orgPetIds = petMapper.selectList(
                Wrappers.lambdaQuery(PetEntity.class)
                        .eq(PetEntity::getOrgUserId, orgUserId)
                        .eq(PetEntity::getDeleted, 0)
        ).stream().map(PetEntity::getId).toList();
        
        if (orgPetIds.isEmpty()) {
            response.setList(List.of());
            return response;
        }
        
        // 简化实现，实际应该根据回访状态筛选
        response.setList(List.of()); // 简化实现
        
        return response;
    }

    @Override
    public OrgDashboardHomeResponse getHomeData(Long orgUserId) {
        OrgDashboardHomeResponse response = new OrgDashboardHomeResponse();
        
        // 获取统计数据
        response.setStatistics(getStatistics(orgUserId));
        
        // 获取待办事项
        response.setTodos(getTodos(orgUserId, null, 10).getTodos());
        
        // 获取最近宠物
        response.setRecentPets(getRecentPets(orgUserId, 5));
        
        // 获取最近申请
        response.setRecentApplications(getRecentApplications(orgUserId, 5));
        
        // 获取回访提醒
        response.setFollowupReminders(getFollowupReminders(orgUserId, "all", 5).getList());
        
        // 获取机构信息
        response.setOrgInfo(orgProfileService.getOrgProfile(orgUserId));
        
        return response;
    }
}