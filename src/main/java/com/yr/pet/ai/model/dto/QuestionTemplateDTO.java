package com.yr.pet.ai.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 批量新增问题模板-单条创建数据 DTO
 */
@Data
//@ApiModel(description = "AI-问题模板创建参数")
public class QuestionTemplateDTO {
    //   @ApiModelProperty(value = "id")
    private Long id;

    // @ApiModelProperty(value = "问题内容", required = true)
    @NotBlank(message = "内容不能为空")
    @Size(max = 255, message = "问题长度不能超过255字符")
    private String question;
    @NotBlank(message = "内容不能为空")
//    @ApiModelProperty(value = "答案内容", required = true)
    private String answer;

//    @ApiModelProperty(value = "类型，1-问题模板，2-免责声明")
    private Integer type;

//    @ApiModelProperty(value = "排序号，可选")
    private Integer sortNo;
}

