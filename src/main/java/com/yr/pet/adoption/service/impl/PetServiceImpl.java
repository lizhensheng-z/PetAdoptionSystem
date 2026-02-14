package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.model.dto.PetDetailResponse;
import com.yr.pet.adoption.model.dto.PetListRequest;
import com.yr.pet.adoption.model.dto.PetListResponse;
import com.yr.pet.adoption.model.dto.PetSuggestResponse;
import com.yr.pet.adoption.model.dto.SimilarPetResponse;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 宠物服务实现类
 * @author yr
 * @since 2024-02-14
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, PetEntity> implements PetService {

    @Autowired
    private PetMediaMapper petMediaMapper;
    
    @Autowired
    private PetTagMapper petTagMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private OrgProfileMapper orgProfileMapper;

    @Override
    public PageResult<PetListResponse> getPetList(PetListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询已发布且审核通过的宠物
        queryWrapper.eq(PetEntity::getStatus, "PUBLISHED")
                   .eq(PetEntity::getAuditStatus, "APPROVED")
                   .eq(PetEntity::getDeleted, 0);

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = "%" + request.getKeyword() + "%";
            queryWrapper.and(wrapper -> wrapper
                .like(PetEntity::getName, keyword)
                .or()
                .like(PetEntity::getBreed, keyword)
                .or()
                .like(PetEntity::getPersonalityDesc, keyword)
            );
        }

        // 物种筛选
        if (StringUtils.hasText(request.getSpecies())) {
            queryWrapper.eq(PetEntity::getSpecies, request.getSpecies());
        }

        // 性别筛选
        if (StringUtils.hasText(request.getGender())) {
            queryWrapper.eq(PetEntity::getGender, request.getGender());
        }

        // 年龄范围筛选
        if (request.getAgeMin() != null) {
            queryWrapper.ge(PetEntity::getAgeMonth, request.getAgeMin());
        }
        if (request.getAgeMax() != null) {
            queryWrapper.le(PetEntity::getAgeMonth, request.getAgeMax());
        }

        // 体型筛选
        if (StringUtils.hasText(request.getSize())) {
            queryWrapper.eq(PetEntity::getSize, request.getSize());
        }

        // 疫苗状态筛选
        if (request.getVaccinated() != null) {
            queryWrapper.eq(PetEntity::getVaccinated, request.getVaccinated());
        }

        // 绝育状态筛选
        if (request.getSterilized() != null) {
            queryWrapper.eq(PetEntity::getSterilized, request.getSterilized());
        }

        // 排序
        if ("published_time".equals(request.getSortBy())) {
            if ("asc".equalsIgnoreCase(request.getOrder())) {
                queryWrapper.orderByAsc(PetEntity::getPublishedTime);
            } else {
                queryWrapper.orderByDesc(PetEntity::getPublishedTime);
            }
        } else {
            // 默认按发布时间降序
            queryWrapper.orderByDesc(PetEntity::getPublishedTime);
        }

        // 分页查询
        Page<PetEntity> page = new Page<>(request.getPage(), request.getPageSize());
        IPage<PetEntity> petPage = this.page(page, queryWrapper);

        // 转换为响应数据
        List<PetListResponse> petList = petPage.getRecords().stream()
            .map(pet -> convertToListResponse(pet, request.getLng(), request.getLat()))
            .collect(Collectors.toList());

        return new PageResult<>(
            petList,
            (int) petPage.getCurrent(),
            (int) petPage.getSize(),
            petPage.getTotal()
        );
    }

    @Override
    public PetDetailResponse getPetDetail(Long id) {
        PetEntity pet = this.getById(id);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "宠物不存在");
        }
        
        // 检查宠物状态
        if (!"PUBLISHED".equals(pet.getStatus()) || !"APPROVED".equals(pet.getAuditStatus())) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "宠物不存在或已下架");
        }

        return convertToDetailResponse(pet);
    }

    @Override
    public PageResult<PetListResponse> getRecommendPets(Long userId, Integer pageNo, Integer pageSize, 
                                                       BigDecimal lng, BigDecimal lat) {
        // 这里简化实现，实际应该基于用户偏好进行智能推荐
        // 目前先按发布时间降序返回
        PetListRequest request = new PetListRequest();
        request.setPage(pageNo);
        request.setPageSize(pageSize);
        request.setLng(lng);
        request.setLat(lat);
        
        PageResult<PetListResponse> result = getPetList(request);
        
        // 为推荐结果添加匹配分数（简化实现）
        result.getList().forEach(pet -> {
            pet.setMatchScore(80 + new Random().nextInt(20)); // 80-100的随机分数
        });
        
        return result;
    }

    @Override
    public PetSuggestResponse getSearchSuggestions(String keyword) {
        PetSuggestResponse response = new PetSuggestResponse();
        
        if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
            return response;
        }

        // 获取品种建议
        LambdaQueryWrapper<PetEntity> breedWrapper = new LambdaQueryWrapper<>();
        breedWrapper.select(PetEntity::getBreed)
                   .like(PetEntity::getBreed, "%" + keyword + "%")
                   .groupBy(PetEntity::getBreed)
                   .last("LIMIT 10");
        List<String> breeds = this.list(breedWrapper).stream()
            .map(PetEntity::getBreed)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 获取宠物名称建议
        LambdaQueryWrapper<PetEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.select(PetEntity::getName)
                   .like(PetEntity::getName, "%" + keyword + "%")
                   .groupBy(PetEntity::getName)
                   .last("LIMIT 10");
        List<String> names = this.list(nameWrapper).stream()
            .map(PetEntity::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 获取标签建议
        LambdaQueryWrapper<TagEntity> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.select(TagEntity::getName)
                 .like(TagEntity::getName, "%" + keyword + "%")
                 .eq(TagEntity::getEnabled, 1)
                 .groupBy(TagEntity::getName)
                 .last("LIMIT 10");
        List<String> tags = tagMapper.selectList(tagWrapper).stream()
            .map(TagEntity::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        response.setBreeds(breeds);
        response.setKeywords(names);
        response.setTags(tags);

        return response;
    }

    @Override
    public List<SimilarPetResponse> getSimilarPets(Long petId, Integer limit) {
        PetEntity currentPet = this.getById(petId);
        if (currentPet == null || currentPet.getDeleted() == 1) {
            return Collections.emptyList();
        }

        // 构建相似度查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(PetEntity::getId, petId)
                   .eq(PetEntity::getStatus, "PUBLISHED")
                   .eq(PetEntity::getAuditStatus, "APPROVED")
                   .eq(PetEntity::getDeleted, 0)
                   .orderByDesc(PetEntity::getPublishedTime)
                   .last("LIMIT " + (limit != null ? limit : 6));

        // 优先匹配相同物种和品种
        if (StringUtils.hasText(currentPet.getSpecies())) {
            queryWrapper.eq(PetEntity::getSpecies, currentPet.getSpecies());
        }
        if (StringUtils.hasText(currentPet.getBreed())) {
            queryWrapper.or().like(PetEntity::getBreed, "%" + currentPet.getBreed() + "%");
        }

        List<PetEntity> similarPets = this.list(queryWrapper);
        
        return similarPets.stream()
            .map(pet -> {
                SimilarPetResponse response = new SimilarPetResponse();
                BeanUtils.copyProperties(pet, response);
                
                // 设置图片和标签
                List<String> images = getPetImages(pet.getId());
                if (!images.isEmpty()) {
                    response.setCoverUrl(images.get(0));
                }
                
                List<String> tags = getPetTags(pet.getId());
                response.setTags(tags);
                
                // 设置机构信息
                OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
                if (orgProfile != null) {
                    response.setOrgName(orgProfile.getOrgName());
                }
                
                // 计算相似度分数（基于物种、品种、年龄、性别等）
                int score = calculateSimilarityScore(currentPet, pet);
                response.setSimilarityScore(score);
                
                return response;
            })
            .sorted((a, b) -> b.getSimilarityScore().compareTo(a.getSimilarityScore()))
            .collect(Collectors.toList());
    }

    /**
     * 计算宠物相似度分数
     */
    private int calculateSimilarityScore(PetEntity current, PetEntity target) {
        int score = 50; // 基础分数
        
        if (current.getSpecies() != null && current.getSpecies().equals(target.getSpecies())) {
            score += 20;
        }
        
        if (current.getBreed() != null && current.getBreed().equals(target.getBreed())) {
            score += 15;
        }
        
        if (current.getGender() != null && current.getGender().equals(target.getGender())) {
            score += 10;
        }
        
        if (current.getSize() != null && current.getSize().equals(target.getSize())) {
            score += 10;
        }
        
        // 年龄相近加分
        if (current.getAgeMonth() != null && target.getAgeMonth() != null) {
            int ageDiff = Math.abs(current.getAgeMonth() - target.getAgeMonth());
            if (ageDiff <= 3) {
                score += 10;
            } else if (ageDiff <= 6) {
                score += 5;
            }
        }
        
        return Math.min(score, 100);
    }

    /**
     * 将PetEntity转换为PetListResponse
     */
    private PetListResponse convertToListResponse(PetEntity pet, BigDecimal userLng, BigDecimal userLat) {
        PetListResponse response = new PetListResponse();
        BeanUtils.copyProperties(pet, response);

        // 设置图片列表
        List<String> images = getPetImages(pet.getId());
        response.setImages(images);

        // 设置标签列表
        List<String> tags = getPetTags(pet.getId());
        response.setTags(tags);

        // 设置机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
        }

        // 计算距离
        if (userLng != null && userLat != null && pet.getLng() != null && pet.getLat() != null) {
            BigDecimal distance = calculateDistance(userLng, userLat, pet.getLng(), pet.getLat());
            response.setDistance(distance);
        }

        return response;
    }

    /**
     * 将PetEntity转换为PetDetailResponse
     */
    private PetDetailResponse convertToDetailResponse(PetEntity pet) {
        PetDetailResponse response = new PetDetailResponse();
        BeanUtils.copyProperties(pet, response);

        // 设置媒体文件列表
        List<PetDetailResponse.PetMediaResponse> mediaList = getPetMediaList(pet.getId());
        response.setMediaList(mediaList);

        // 设置标签列表
        List<String> tags = getPetTags(pet.getId());
        response.setTags(tags);

        // 设置机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
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
               .orderByAsc(PetMediaEntity::getSort);
        
        return petMediaMapper.selectList(wrapper).stream()
            .map(PetMediaEntity::getUrl)
            .collect(Collectors.toList());
    }

    /**
     * 获取宠物媒体文件列表
     */
    private List<PetDetailResponse.PetMediaResponse> getPetMediaList(Long petId) {
        LambdaQueryWrapper<PetMediaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetMediaEntity::getPetId, petId)
               .orderByAsc(PetMediaEntity::getSort);
        
        return petMediaMapper.selectList(wrapper).stream()
            .map(media -> {
                PetDetailResponse.PetMediaResponse mediaResponse = new PetDetailResponse.PetMediaResponse();
                mediaResponse.setId(media.getId());
                mediaResponse.setUrl(media.getUrl());
                mediaResponse.setMediaType(media.getMediaType());
                mediaResponse.setSort(media.getSort());
                return mediaResponse;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取宠物标签列表
     */
    private List<String> getPetTags(Long petId) {
        // 查询宠物标签关联
        LambdaQueryWrapper<PetTagEntity> petTagWrapper = new LambdaQueryWrapper<>();
        petTagWrapper.eq(PetTagEntity::getPetId, petId);
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

    /**
     * 计算两点之间的距离（公里）
     */
    private BigDecimal calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        if (lng1 == null || lat1 == null || lng2 == null || lat2 == null) {
            return null;
        }

        // 使用Haversine公式计算距离
        double earthRadius = 6371; // 地球半径（公里）
        
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return BigDecimal.valueOf(distance).setScale(1, RoundingMode.HALF_UP);
    }
}