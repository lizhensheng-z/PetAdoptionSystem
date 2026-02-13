package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.OrgProfileService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 机构管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/org")
public class OrgProfileController {

    @Autowired
    private OrgProfileService orgProfileService;
    /**
     * 获取机构资料
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('org:profile:read')")
    public R<OrgProfileResponse> getProfile() {
        Long userId = UserContext.getUserId();
        OrgProfileResponse response = orgProfileService.getProfile(userId);
        return R.ok(response);
    }

    /**
     * 更新机构资料
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('org:profile:update')")
    public R<Void> updateProfile(@Validated @RequestBody OrgProfileUpdateRequest request) {
        Long userId = UserContext.getUserId();
        orgProfileService.updateProfile(userId, request);
        return R.ok();
    }

    /**
     * 获取机构统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('org:statistics:read')")
    public R<OrgStatisticsResponse> getStatistics() {
        Long userId = UserContext.getUserId();
        OrgStatisticsResponse response = orgProfileService.getStatistics(userId);
        return R.ok(response);
    }

    /**
     * 获取领养完成记录
     */
    @GetMapping("/adoptions")
    @PreAuthorize("hasAuthority('org:adoptions:read')")
    public R<com.yr.pet.adoption.common.PageResult<OrgAdoptionRecord>> getAdoptionRecords(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "adoptedTime") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        Long currentUserId = UserContext.getUserId();
        com.yr.pet.adoption.common.PageResult<OrgAdoptionRecord> result = 
                orgProfileService.getAdoptionRecords(currentUserId, petId, userId, month, pageNo, pageSize);
        return R.ok(result);
    }

    /**
     * 获取回访提醒
     */
    @GetMapping("/followup-reminders")
    @PreAuthorize("hasAuthority('org:followup:read')")
    public R<FollowupReminderResponse> getFollowupReminders() {
        Long userId = UserContext.getUserId();
        FollowupReminderResponse response = orgProfileService.getFollowupReminders(userId);
        return R.ok(response);
    }
}
