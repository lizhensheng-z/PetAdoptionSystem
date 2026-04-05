package com.yr.pet.adoption.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 系统配置VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "系统配置信息")
public class SystemConfigVO {

    @Schema(description = "系统名称")
    private String systemName;

    @Schema(description = "系统版本")
    private String systemVersion;

    @Schema(description = "系统描述")
    private String systemDesc;

    @Schema(description = "宠物物种列表")
    private List<Map<String, String>> petSpecies;

    @Schema(description = "宠物性别列表")
    private List<Map<String, String>> petGenders;

    @Schema(description = "宠物体型列表")
    private List<Map<String, String>> petSizes;

    @Schema(description = "申请状态列表")
    private List<Map<String, String>> applicationStatuses;

    @Schema(description = "宠物状态列表")
    private List<Map<String, String>> petStatuses;

    @Schema(description = "审核状态列表")
    private List<Map<String, String>> auditStatuses;
}