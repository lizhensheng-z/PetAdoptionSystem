package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.UserFavoriteService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 收藏管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "收藏管理", description = "用户收藏相关接口")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/favorites/{petId}")
//    @PreAuthorize("hasAuthority('favorite:manage')")
    @Operation(summary = "添加收藏", description = "收藏指定的宠物")
    public R addFavorite(@PathVariable Long petId) {
        Long userId = UserContext.getUserId();
        userFavoriteService.addFavorite(userId, petId);
        return R.ok("收藏成功");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorites/{petId}")
//    @PreAuthorize("hasAuthority('favorite:manage')")
    @Operation(summary = "取消收藏", description = "取消对指定宠物的收藏")
    public R removeFavorite(@PathVariable Long petId) {
        Long userId = UserContext.getUserId();
        userFavoriteService.removeFavorite(userId, petId);
        return R.ok("取消收藏成功");
    }

    /**
     * 获取我的收藏列表
     */
    @GetMapping("/favorites/my")
    @Operation(summary = "获取我的收藏列表", description = "获取当前用户的收藏列表")
    public R<PageResult<FavoriteListItem>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<FavoriteListItem> result = userFavoriteService.getMyFavorites(userId, pageNo, pageSize);
        return R.ok(result);
    }

    /**
     * 收藏切换（收藏/取消收藏）
     */
    @PostMapping("/favorites/toggle")
//    @PreAuthorize("hasAuthority('favorite:manage')")
    @Operation(summary = "收藏切换", description = "切换宠物的收藏状态，已收藏则取消，未收藏则添加")
    public R<FavoriteToggleResponse> toggleFavorite(@Valid @RequestBody FavoriteToggleRequest request) {
        Long userId = UserContext.getUserId();
        Long petId = request.getPetId();
        
        boolean isFavorited = userFavoriteService.isFavorited(userId, petId);
        FavoriteToggleResponse response = new FavoriteToggleResponse();
        
        if (isFavorited) {
            // 取消收藏
            userFavoriteService.removeFavorite(userId, petId);
            response.setFavorited(false);
            response.setMessage("取消收藏成功");
        } else {
            // 添加收藏
            userFavoriteService.addFavorite(userId, petId);
            response.setFavorited(true);
            response.setMessage("收藏成功");
        }
        
        return R.ok(response);
    }

    /**
     * 检查收藏状态
     */
    @GetMapping("/favorites/check")
//    @PreAuthorize("hasAuthority('favorite:manage')")
    @Operation(summary = "检查收藏状态", description = "检查指定宠物是否已被当前用户收藏")
    public R<FavoriteCheckResponse> checkFavoriteStatus(
            @Parameter(description = "宠物ID", required = true, example = "1")
            @RequestParam @NotNull @Min(1) Long petId) {
        
        Long userId = UserContext.getUserId();
        boolean isFavorited = userFavoriteService.isFavorited(userId, petId);
        
        FavoriteCheckResponse response = new FavoriteCheckResponse();
        response.setFavorited(isFavorited);
        
        return R.ok(response);
    }
}
