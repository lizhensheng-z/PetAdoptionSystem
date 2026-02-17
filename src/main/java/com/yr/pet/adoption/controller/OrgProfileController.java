package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.OrgProfileRequest;
import com.yr.pet.adoption.model.dto.OrgProfileResponse;
import com.yr.pet.adoption.service.OrgProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 机构资料管理 前端控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-17
 */
@RestController
@RequestMapping("/api/org/profile")
@Tag(name = "机构资料管理", description = "机构资料相关接口")
public class OrgProfileController {

    @Autowired
    private OrgProfileService orgProfileService;

    /**
     * 获取当前机构资料
     */
    @GetMapping
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取机构资料", description = "获取当前登录机构的资料信息")
    public R<OrgProfileResponse> getOrgProfile() {
        Long userId = UserContext.getUserId();
        OrgProfileResponse profile = orgProfileService.getOrgProfile(userId);
        return R.ok(profile);
    }

    /**
     * 创建或更新机构资料
     */
    @PostMapping
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "保存机构资料", description = "创建或更新机构资料")
    public R<OrgProfileResponse> saveOrgProfile(@Valid @RequestBody OrgProfileRequest request) {
        Long userId = UserContext.getUserId();
        OrgProfileResponse profile = orgProfileService.saveOrgProfile(userId, request);
        return R.ok(profile);
    }

    /**
     * 更新机构认证状态（管理员使用）
     */
    @PutMapping("/verify/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新认证状态", description = "管理员更新机构认证状态")
    public R<Void> updateVerifyStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "认证状态", required = true) @RequestParam String verifyStatus,
            @Parameter(description = "认证备注") @RequestParam(required = false) String verifyRemark) {
        
        orgProfileService.updateVerifyStatus(userId, verifyStatus, verifyRemark);
        return R.ok();
    }

    /**
     * 检查机构资料是否完整
     */
    @GetMapping("/complete")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "检查资料完整性", description = "检查当前机构资料是否完整")
    public R<Boolean> isOrgProfileComplete() {
        Long userId = UserContext.getUserId();
        boolean complete = orgProfileService.isOrgProfileComplete(userId);
        return R.ok(complete);
    }
}