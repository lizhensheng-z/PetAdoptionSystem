package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.mapper.PetAuditMapper;
import com.yr.pet.adoption.mapper.PetMediaMapper;
import com.yr.pet.adoption.mapper.PetTagMapper;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.model.dto.PetAuditDetailResponse;
import com.yr.pet.adoption.model.dto.PetAuditRequest;
import com.yr.pet.adoption.model.dto.PendingPetResponse;
import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.model.entity.PetAuditEntity;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.entity.PetMediaEntity;
import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.service.PetAuditService;
import com.yr.pet.adoption.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物发布审核服务实现类
 * @author yr
 * @since 2026-02-01
 */
@Service
@RequiredArgsConstructor
public class PetAuditServiceImpl extends ServiceImpl<PetAuditMapper, PetAuditEntity> implements PetAuditService {

    private final PetService petService;
    private final OrgProfileMapper orgProfileMapper;
    private final PetMediaMapper petMediaMapper;
    private final PetTagMapper petTagMapper;
    private final TagMapper tagMapper;

    @Override
    public PageResult<PendingPetResponse> getPendingPets(Integer pageNo, Integer pageSize, String species) {
        // 构建查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PetEntity::getStatus, "PENDING_AUDIT")
                   .eq(PetEntity::getAuditStatus, "PENDING")
                   .eq(PetEntity::getDeleted, 0);

        if (StringUtils.hasText(species)) {
            queryWrapper.eq(PetEntity::getSpecies, species);
        }

        queryWrapper.orderByAsc(PetEntity::getCreateTime);

        // 分页查询
        Page<PetEntity> page = new Page<>(pageNo, pageSize);
        IPage<PetEntity> petPage = petService.page(page, queryWrapper);

        // 转换为响应数据
        List<PendingPetResponse> petList = petPage.getRecords().stream()
                .map(this::convertToPendingPetResponse)
                .collect(Collectors.toList());

        return new PageResult<>(
                petList,
                (int) petPage.getCurrent(),
                (int) petPage.getSize(),
                petPage.getTotal()
        );
    }

    @Override
    public PetAuditDetailResponse getPetAuditDetail(Long petId) {
        PetEntity pet = petService.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        return convertToPetAuditDetailResponse(pet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPet(Long adminId, PetAuditRequest request) {
        // 1. 验证宠物是否存在
        PetEntity pet = petService.getById(request.getPetId());
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        // 2. 验证宠物状态
        if (!"PENDING_AUDIT".equals(pet.getStatus()) || !"PENDING".equals(pet.getAuditStatus())) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "该宠物不在待审核状态");
        }

        // 3. 创建审核记录
        PetAuditEntity auditRecord = new PetAuditEntity();
        auditRecord.setPetId(pet.getId());
        auditRecord.setOrgUserId(pet.getOrgUserId());
        auditRecord.setAuditorId(adminId);
        auditRecord.setAuditTime(LocalDateTime.now());
        auditRecord.setDeleted((byte) 0);
        auditRecord.setCreateTime(LocalDateTime.now());
        auditRecord.setUpdateTime(LocalDateTime.now());

        // 4. 根据审核动作更新状态
        if ("approve".equalsIgnoreCase(request.getAction())) {
            // 审核通过
            pet.setStatus("PUBLISHED");
            pet.setAuditStatus("APPROVED");
            pet.setPublishedTime(LocalDateTime.now());

            auditRecord.setStatus("APPROVED");
            auditRecord.setRemark(request.getRemark() != null ? request.getRemark() : "审核通过");
        } else if ("reject".equalsIgnoreCase(request.getAction())) {
            // 审核拒绝
            pet.setStatus("DRAFT");
            pet.setAuditStatus("REJECTED");

            auditRecord.setStatus("REJECTED");
            auditRecord.setRemark(request.getRemark() != null ? request.getRemark() : "审核拒绝");
        } else {
            throw new BizException(ErrorCode.PARAM_ERROR, "无效的审核动作");
        }

        pet.setUpdateTime(LocalDateTime.now());

        // 5. 保存更新
        petService.updateById(pet);
        this.save(auditRecord);
    }

    @Override
    public long countPendingPets() {
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PetEntity::getStatus, "PENDING_AUDIT")
                   .eq(PetEntity::getAuditStatus, "PENDING")
                   .eq(PetEntity::getDeleted, 0);
        return petService.count(queryWrapper);
    }

    /**
     * 转换为待审核宠物响应
     */
    private PendingPetResponse convertToPendingPetResponse(PetEntity pet) {
        PendingPetResponse response = new PendingPetResponse();
        response.setPetId(pet.getId());
        response.setPetName(pet.getName());
        response.setCoverUrl(pet.getCoverUrl());
        response.setSpecies(pet.getSpecies());
        response.setBreed(pet.getBreed());
        response.setGender(pet.getGender());
        response.setAgeMonth(pet.getAgeMonth());
        response.setSize(pet.getSize());
        response.setOrgUserId(pet.getOrgUserId());
        response.setAuditStatus(pet.getAuditStatus());
        response.setSubmitTime(pet.getUpdateTime());

        // 获取机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                new LambdaQueryWrapper<OrgProfileEntity>()
                        .eq(OrgProfileEntity::getUserId, pet.getOrgUserId())
                        .eq(OrgProfileEntity::getDeleted, 0)
        );
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
            response.setContactName(orgProfile.getContactName());
            response.setContactPhone(orgProfile.getContactPhone());
        }

        return response;
    }

    /**
     * 转换为宠物审核详情响应
     */
    private PetAuditDetailResponse convertToPetAuditDetailResponse(PetEntity pet) {
        PetAuditDetailResponse response = new PetAuditDetailResponse();
        response.setPetId(pet.getId());
        response.setName(pet.getName());
        response.setCoverUrl(pet.getCoverUrl());
        response.setSpecies(pet.getSpecies());
        response.setBreed(pet.getBreed());
        response.setGender(pet.getGender());
        response.setAgeMonth(pet.getAgeMonth());
        response.setSize(pet.getSize());
        response.setColor(pet.getColor());
        response.setSterilized(pet.getSterilized());
        response.setVaccinated(pet.getVaccinated());
        response.setDewormed(pet.getDewormed());
        response.setHealthDesc(pet.getHealthDesc());
        response.setPersonalityDesc(pet.getPersonalityDesc());
        response.setAdoptRequirements(pet.getAdoptRequirements());
        response.setAuditStatus(pet.getAuditStatus());
        response.setSubmitTime(pet.getUpdateTime());
        response.setStatus(pet.getStatus());
        response.setOrgUserId(pet.getOrgUserId());

        // 获取图片列表
        List<String> images = getPetImages(pet.getId());
        response.setImages(images);

        // 获取标签列表
        List<String> tags = getPetTags(pet.getId());
        response.setTags(tags);

        // 获取机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                new LambdaQueryWrapper<OrgProfileEntity>()
                        .eq(OrgProfileEntity::getUserId, pet.getOrgUserId())
                        .eq(OrgProfileEntity::getDeleted, 0)
        );
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
            response.setContactName(orgProfile.getContactName());
            response.setContactPhone(orgProfile.getContactPhone());
            response.setOrgAddress(orgProfile.getAddress());
        }

        return response;
    }

    /**
     * 获取宠物图片列表
     */
    private List<String> getPetImages(Long petId) {
        LambdaQueryWrapper<PetMediaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetMediaEntity::getPetId, petId)
               .eq(PetMediaEntity::getMediaType, "IMAGE")
               .eq(PetMediaEntity::getDeleted, 0)
               .orderByAsc(PetMediaEntity::getSort);

        return petMediaMapper.selectList(wrapper).stream()
                .map(PetMediaEntity::getUrl)
                .collect(Collectors.toList());
    }

    /**
     * 获取宠物标签列表
     */
    private List<String> getPetTags(Long petId) {
        // 查询宠物标签关联
        LambdaQueryWrapper<PetTagEntity> petTagWrapper = new LambdaQueryWrapper<>();
        petTagWrapper.eq(PetTagEntity::getPetId, petId)
                     .eq(PetTagEntity::getDeleted, 0);
        List<Long> tagIds = petTagMapper.selectList(petTagWrapper).stream()
                .map(PetTagEntity::getTagId)
                .collect(Collectors.toList());

        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询标签名称
        LambdaQueryWrapper<TagEntity> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(TagEntity::getId, tagIds)
                 .eq(TagEntity::getEnabled, 1);

        return tagMapper.selectList(tagWrapper).stream()
                .map(TagEntity::getName)
                .collect(Collectors.toList());
    }
}