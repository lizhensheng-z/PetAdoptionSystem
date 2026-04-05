package com.yr.pet.adoption.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标签VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "标签信息")
public class TagVO {

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "标签类型")
    private String tagType;

    @Schema(description = "标签类型描述")
    private String tagTypeDesc;

    @Schema(description = "是否启用")
    private Boolean enabled;
}