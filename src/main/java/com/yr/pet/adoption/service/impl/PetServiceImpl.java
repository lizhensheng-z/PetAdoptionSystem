package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.PetMediaMapper;
import com.yr.pet.adoption.mapper.PetTagMapper;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.entity.PetMediaEntity;
import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.yr.pet.adoption.service.PetService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物管理服务实现类
 * @author yr
 * @since 2024-01-01
 */
@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetMediaMapper petMediaMapper;

    @Autowired
    private PetTagMapper petTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public IPage<PetListResponse> getPetList(PetQueryRequest request) {
        Page<PetListResponse> page = new Page<>(request.getPageNo(), request.getPageSize());
        
        // 解析标签ID列表
        List<Long> tagIds = null;
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            String[] tagArray = request.getTags().split(",");
            tagIds = new ArrayList<>();
            for (String tag : tagArray) {
                try {
                    tagIds.add(Long.parseLong(tag.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效的标签ID
                }
            }
        }

        return petMapper.selectPetList(
            page,
            request.getSpecies(),
            request.getBreed(),
            request.getGender(),
            request.getSizeMin(),
            request.getSizeMax(),
            request.getAgeMin(),
            request.getAgeMax(),
            request.getSterilized(),
            request.getVaccinated(),
            request.getKeyword(),
            tagIds,
            request.getLng() != null ? request.getLng().doubleValue() : null,
            request.getLat() != null ? request.getLat().doubleValue() : null,
            request.getDistance(),
            request.getSortBy(),
            request.getOrder()
        );
    }

    @Override
    public PetDetailResponse getPetDetail(Long petId, Double lng, Double lat) {
        PetListResponse pet = petMapper.selectPetDetailById(petId);
        if (pet == null) {
            throw new BusinessException("宠物不存在");
        }

        PetDetailResponse response = new PetDetailResponse();
        BeanUtils.copyProperties(pet, response);
        
        // 设置创建时间
        PetEntity petEntity = petMapper.selectById(petId);
        response.setCreateTime(petEntity.getCreateTime());

        // 设置机构信息（简化处理，实际需要查询机构表）
        OrgProfileResponse orgProfile = new OrgProfileResponse();
        orgProfile.setId(pet.getOrgUserId());
        orgProfile.setOrgName(pet.getOrgName());
        response.setOrgProfile(orgProfile);

        // 设置统计信息（简化处理）
        PetStatisticsResponse statistics = new PetStatisticsResponse();
        statistics.setViewCount(0);
        statistics.setFavoriteCount(0);
        statistics.setApplicationCount(pet.getApplicationCount());
        statistics.setAdoptedCount(0);
        response.setStatistics(statistics);

        // 设置申请状态（简化处理）
        ApplicationStatusResponse applicationStatus = new ApplicationStatusResponse();
        applicationStatus.setCanApply(true);
        response.setApplicationStatus(applicationStatus);

        return response;
    }

    @Override
    @Transactional
    public PetCreateResponse createPet(Long orgUserId, PetCreateRequest request) {
        PetEntity pet = new PetEntity();
        BeanUtils.copyProperties(request, pet);
        pet.setOrgUserId(orgUserId);
        pet.setStatus("DRAFT");
        pet.setAuditStatus("NONE");
        pet.setDeleted(0);
        pet.setCreateTime(LocalDateTime.now());
        pet.setUpdateTime(LocalDateTime.now());
        
        petMapper.insert(pet);

        // 保存标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePetTags(pet.getId(), request.getTagIds());
        }

        PetCreateResponse response = new PetCreateResponse();
        response.setId(pet.getId());
        response.setStatus(pet.getStatus());
        response.setAuditStatus(pet.getAuditStatus());
        response.setCreateTime(pet.getCreateTime());

        return response;
    }

    @Override
    @Transactional
    public void updatePet(Long orgUserId, Long petId, PetUpdateRequest request) {
        // 验证宠物是否存在且属于该机构
        if (petMapper.countByIdAndOrgUserId(petId, orgUserId) == 0) {
            throw new BusinessException("宠物不存在或无权限修改");
        }

        PetEntity pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BusinessException("宠物不存在");
        }

        // 检查状态是否允许修改
        if (!("DRAFT".equals(pet.getStatus()) || "REJECTED".equals(pet.getStatus()))) {
            throw new BusinessException("当前状态不允许修改");
        }

        // 更新字段
        if (request.getName() != null) pet.setName(request.getName());
        if (request.getSpecies() != null) pet.setSpecies(request.getSpecies());
        if (request.getBreed() != null) pet.setBreed(request.getBreed());
        if (request.getGender() != null) pet.setGender(request.getGender());
        if (request.getAgeMonth() != null) pet.setAgeMonth(request.getAgeMonth());
        if (request.getSize() != null) pet.setSize(request.getSize());
        if (request.getColor() != null) pet.setColor(request.getColor());
        if (request.getSterilized() != null) pet.setSterilized(request.getSterilized());
        if (request.getVaccinated() != null) pet.setVaccinated(request.getVaccinated());
        if (request.getDewormed() != null) pet.setDewormed(request.getDewormed());
        if (request.getHealthDesc() != null) pet.setHealthDesc(request.getHealthDesc());
        if (request.getPersonalityDesc() != null) pet.setPersonalityDesc(request.getPersonalityDesc());
        if (request.getAdoptRequirements() != null) pet.setAdoptRequirements(request.getAdoptRequirements());
        if (request.getLng() != null) pet.setLng(request.getLng());
        if (request.getLat() != null) pet.setLat(request.getLat());

        pet.setAuditStatus("NONE");
        pet.setUpdateTime(LocalDateTime.now());

        petMapper.updateById(pet);

        // 更新标签关联
        if (request.getTagIds() != null) {
            petTagMapper.deleteByPetId(petId);
            savePetTags(petId, request.getTagIds());
        }
    }

    @Override
    public PetMediaUploadResponse uploadPetMedia(Long orgUserId, Long petId, MultipartFile file, String mediaType, Integer sort) {
        // 验证宠物是否存在且属于该机构
        if (petMapper.countByIdAndOrgUserId(petId, orgUserId) == 0) {
            throw new BusinessException("宠物不存在或无权限操作");
        }

        // 检查媒体数量限制
        int mediaCount = petMediaMapper.countByPetId(petId);
        if (mediaCount >= 30) {
            throw new BusinessException("单个宠物最多上传30个媒体文件");
        }

        // 这里应该调用文件上传服务，简化处理
        String fileUrl = "https://example.com/pet-" + petId + "-" + System.currentTimeMillis() + ".jpg";

        PetMediaEntity media = new PetMediaEntity();
        media.setPetId(petId);
        media.setUrl(fileUrl);
        media.setMediaType(mediaType);
        media.setSort(sort != null ? sort : petMediaMapper.getMaxSortByPetId(petId) + 1);
        media.setDeleted(0);
        media.setCreateTime(LocalDateTime.now());

        petMediaMapper.insert(media);

        PetMediaUploadResponse response = new PetMediaUploadResponse();
        response.setId(media.getId());
        response.setPetId(petId);
        response.setUrl(fileUrl);
        response.setMediaType(mediaType);
        response.setSort(media.getSort());

        return response;
    }

    @Override
    public void deletePetMedia(Long orgUserId, Long petId, Long mediaId) {
        // 验证宠物是否存在且属于该机构
        if (petMapper.countByIdAndOrgUserId(petId, orgUserId) == 0) {
            throw new BusinessException("宠物不存在或无权限操作");
        }

        PetMediaEntity media = petMediaMapper.selectById(mediaId);
        if (media == null || media.getDeleted() == 1) {
            throw new BusinessException("媒体文件不存在");
        }

        if (!media.getPetId().equals(petId)) {
            throw new BusinessException("媒体文件不属于该宠物");
        }

        media.setDeleted(1);
        petMediaMapper.updateById(media);
    }

    @Override
    @Transactional
    public PetAuditResponse submitPetAudit(Long orgUserId, Long petId) {
        // 验证宠物是否存在且属于该机构
        if (petMapper.countByIdAndOrgUserId(petId, orgUserId) == 0) {
            throw new BusinessException("宠物不存在或无权限操作");
        }

        PetEntity pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BusinessException("宠物不存在");
        }

        // 检查状态是否允许提交审核
        if (!("DRAFT".equals(pet.getStatus()) || "REJECTED".equals(pet.getStatus()))) {
            throw new BusinessException("当前状态不允许提交审核");
        }

        // 检查是否已上传图片
        int mediaCount = petMediaMapper.countByPetId(petId);
        if (mediaCount == 0) {
            throw new BusinessException("必须上传至少1张图片");
        }

        // 更新宠物状态
        pet.setStatus("PENDING_AUDIT");
        pet.setAuditStatus("PENDING");
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);

        // 这里应该创建审核记录，简化处理
        PetAuditResponse response = new PetAuditResponse();
        response.setPetId(petId);
        response.setAuditId(System.currentTimeMillis()); // 简化处理
        response.setStatus("PENDING_AUDIT");
        response.setAuditStatus("PENDING");
        response.setSubmitTime(LocalDateTime.now());

        return response;
    }

    @Override
    @Transactional
    public void deletePet(Long orgUserId, Long petId, PetDeleteRequest request) {
        // 验证宠物是否存在且属于该机构
        if (petMapper.countByIdAndOrgUserId(petId, orgUserId) == 0) {
            throw new BusinessException("宠物不存在或无权限操作");
        }

        PetEntity pet = petMapper.selectById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BusinessException("宠物不存在");
        }

        // 逻辑删除
        pet.setDeleted(1);
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);

        // 这里应该记录审计日志
    }

    @Override
    public IPage<OrgPetListResponse> getOrgPetList(Long orgUserId, OrgPetQueryRequest request) {
        Page<OrgPetListResponse> page = new Page<>(request.getPageNo(), request.getPageSize());
        return petMapper.selectOrgPetList(
            page,
            orgUserId,
            request.getStatus(),
            request.getAuditStatus(),
            request.getSortBy(),
            request.getOrder()
        );
    }

    /**
     * 保存宠物标签关联
     */
    private void savePetTags(Long petId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            // 验证标签是否存在
            if (tagMapper.selectById(tagId) == null) {
                continue;
            }

            // 检查是否已关联
            if (petTagMapper.countByPetIdAndTagId(petId, tagId) > 0) {
                continue;
            }

            PetTagEntity petTag = new PetTagEntity();
            petTag.setPetId(petId);
            petTag.setTagId(tagId);
            petTag.setDeleted(0);
            petTag.setCreateTime(LocalDateTime.now());
            petTagMapper.insert(petTag);
        }
    }
}