package com.yr.pet.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 李振生
 */
//@ApiModel(description = "AI-系统问题模板")
@Data
@TableName("ai_question_template")
@EqualsAndHashCode(callSuper = true)
public class QuestionTemplateDO extends BaseModel {
    @TableId(type = IdType.AUTO)
//    @ApiModelProperty(value = "问题模板id")
    private Long id;

//    @ApiModelProperty(value = "问题内容")
    private String question;
//    @ApiModelProperty(value = "答案内容")
    private String answer;
//    @ApiModelProperty(value = "排序号")
    private Integer sortNo;
//    @ApiModelProperty(value = "类型，1-问题模板，2-免责声明")
    private Integer type;
//    @ApiModelProperty(value = "是否删除")
    @TableLogic(value = "0", delval = "1")
    private Boolean ifDelete;

}
