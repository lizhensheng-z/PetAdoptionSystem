package com.yr.pet.ai.model.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
//@ApiModel(description = "AI会话更新参数")
public class SessionUpdateDTO {
//    @ApiModelProperty(value = "会话ID", required = true)
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

//    @ApiModelProperty(value = "会话标题")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
}

