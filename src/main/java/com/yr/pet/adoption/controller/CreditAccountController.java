package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.CreditAccountService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 信用管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "信用管理", description = "信用信息相关接口")
public class CreditAccountController {

    @Autowired
    private CreditAccountService creditAccountService;

    /**
     * 获取我的信用信息
     */
    @GetMapping("/credit/me")
//    @PreAuthorize("hasAuthority('credit:me')")
    @Operation(summary = "获取我的信用信息", description = "获取当前用户的信用分、等级、徽章等信息")
    public R<CreditInfoResponse> getCreditInfo() {
        Long userId = UserContext.getUserId();
        CreditInfoResponse response = creditAccountService.getCreditInfo(userId);
        return R.ok(response);
    }

    /**
     * 获取信用详情（含历史记录）
     */
    @GetMapping("/credit/detail")
    @Operation(summary = "获取信用详情", description = "获取当前用户的信用详情，包含历史记录和等级信息")
    public R<CreditDetailResponse> getCreditDetail() {
        Long userId = UserContext.getUserId();
        CreditDetailResponse response = creditAccountService.getCreditDetail(userId);
        return R.ok(response);
    }

    /**
     * 获取信用流水
     */
    @GetMapping("/credit/logs")
    @Operation(summary = "获取信用流水", description = "获取当前用户的信用分变更记录")
    public R<PageResult<CreditLogItem>> getCreditLogs(
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<CreditLogItem> result = creditAccountService.getCreditLogs(userId, reason, pageNo, pageSize);
        return R.ok(result);
    }
}
