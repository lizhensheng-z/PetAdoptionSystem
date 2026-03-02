package com.yr.pet.ai.model.dto;

import lombok.Data;

@Data
public class TemplatePageQueryDTO  extends PageQueryDTO {
//      @ApiModelProperty(value = "模板名称")
      private Integer type;
//      @ApiModelProperty(value="问题名称")
       private String question;

}
