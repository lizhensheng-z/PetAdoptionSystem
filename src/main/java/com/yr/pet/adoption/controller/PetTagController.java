package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.PetTagMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.model.dto.PetTagBatchRequest;
import com.yr.pet.adoption.model.dto.PetTagResponse;
import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.service.PetTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物标签关联控制器
 * <p>
 * 提供宠物与标签的关联管理功能
 * </p>
 *
 * @author yr
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/pet-tags")
@Tag(name = "宠物标签管理", description = "宠物与标签的关联管理接口")
@Validated
public class PetTagController {

    @Autowired
    private PetTagService petTagService;
    
    @Autowired
    private PetMapper petMapper;
    
    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取宠物的标签列表
     */
    @GetMapping("/pet/{petId}")
    @Operation(summary = "获取宠物标签", description = "获取指定宠物的所有标签")
    public R<PetTagResponse> getPetTags(
            @Parameter(description = "宠物ID", example = "1")
            @PathVariable Long petId) {
        
        // 检查宠物是否存在
        if (petMapper.selectById(petId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        List<Long> tagIds = petTagService.getTagIdsByPetId(petId);
        
        if (tagIds.isEmpty()) {
            return R.ok(new PetTagResponse());
        }
        
        List<TagEntity> tags = tagMapper.selectBatchIds(tagIds);
        
        PetTagResponse response = new PetTagResponse();
        response.setPetId(petId);
        
        List<PetTagResponse.TagInfo> tagInfos = tags.stream()
            .map(tag -> {
                PetTagResponse.TagInfo info = new PetTagResponse.TagInfo();
                info.setId(tag.getId());
                info.setName(tag.getName());
                info.setTagType(tag.getTagType());
                return info;
            })
            .collect(Collectors.toList());
        
        response.setTags(tagInfos);
        
        return R.ok(response);
    }

    /**
     * 批量设置宠物标签
     */
    @PostMapping("/pet/{petId}")
    @PreAuthorize("hasAuthority('pet:update')")
    @Operation(summary = "设置宠物标签", description = "批量设置宠物的标签，会替换原有标签")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> setPetTags(
            @Parameter(description = "宠物ID", example = "1")
            @PathVariable Long petId,
            @Valid @RequestBody PetTagBatchRequest request) {
        
        if (!petId.equals(request.getPetId())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "宠物ID不匹配");
        }
        
        // 检查宠物是否存在
        if (petMapper.selectById(petId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        // 检查标签是否存在
        if (!request.getTagIds().isEmpty()) {
            long validTagCount = tagMapper.selectBatchIds(request.getTagIds()).size();
            if (validTagCount != request.getTagIds().size()) {
                throw new BizException(ErrorCode.PARAM_ERROR, "存在无效的标签ID");
            }
        }
        
        // 使用Service层方法批量设置标签
        petTagService.setPetTags(petId, request.getTagIds());
        
        return R.ok();
    }

    /**
     * 添加单个标签到宠物
     */
    @PostMapping("/pet/{petId}/tag/{tagId}")
    @PreAuthorize("hasAuthority('pet:update')")
    @Operation(summary = "添加宠物标签", description = "为宠物添加单个标签")
    public R<Void> addPetTag(
            @Parameter(description = "宠物ID", example = "1")
            @PathVariable Long petId,
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long tagId) {
        
        // 检查宠物是否存在
        if (petMapper.selectById(petId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        // 检查标签是否存在
        if (tagMapper.selectById(tagId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        // 检查是否已关联
        LambdaQueryWrapper<PetTagEntity> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(PetTagEntity::getPetId, petId)
                   .eq(PetTagEntity::getTagId, tagId);
        if (petTagService.count(checkWrapper) > 0) {
            throw new BizException(ErrorCode.DUPLICATE_OPERATION, "标签已关联到该宠物");
        }
        
        PetTagEntity petTag = new PetTagEntity();
        petTag.setPetId(petId);
        petTag.setTagId(tagId);
        
        petTagService.save(petTag);
        
        return R.ok();
    }

    /**
     * 从宠物移除单个标签
     */
    @DeleteMapping("/pet/{petId}/tag/{tagId}")
    @PreAuthorize("hasAuthority('pet:update')")
    @Operation(summary = "移除宠物标签", description = "从宠物移除单个标签")
    public R<Void> removePetTag(
            @Parameter(description = "宠物ID", example = "1")
            @PathVariable Long petId,
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long tagId) {
        
        // 检查宠物是否存在
        if (petMapper.selectById(petId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        // 检查标签是否存在
        if (tagMapper.selectById(tagId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        // 检查是否已关联
        LambdaQueryWrapper<PetTagEntity> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(PetTagEntity::getPetId, petId)
                   .eq(PetTagEntity::getTagId, tagId);
        if (petTagService.count(checkWrapper) == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签未关联到该宠物");
        }
        
        // 逻辑删除关联记录
        LambdaQueryWrapper<PetTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetTagEntity::getPetId, petId)
               .eq(PetTagEntity::getTagId, tagId);
        
        petTagService.remove(wrapper);
        
        return R.ok();
    }

    /**
     * 清空宠物的所有标签
     */
    @DeleteMapping("/pet/{petId}")
    @PreAuthorize("hasAuthority('pet:update')")
    @Operation(summary = "清空宠物标签", description = "移除宠物的所有标签")
    public R<Void> clearPetTags(
            @Parameter(description = "宠物ID", example = "1")
            @PathVariable Long petId) {
        
        // 检查宠物是否存在
        if (petMapper.selectById(petId) == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        petTagService.clearPetTags(petId);
        
        return R.ok();
    }
}
