package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "信用详情响应")
public class CreditDetailResponse {

    @Schema(description = "当前信用分")
    private Integer currentScore;

    @Schema(description = "信用等级")
    private String level;

    @Schema(description = "等级名称")
    private String levelName;

    @Schema(description = "信用分变化（正数为增加，负数为减少）")
    private Integer scoreChange;

    @Schema(description = "历史记录")
    private List<CreditLogItem> history;

    @Schema(description = "下一等级所需分数")
    private Integer nextLevelScore;

    @Schema(description = "距离下一等级还差多少分")
    private Integer scoreToNextLevel;
}