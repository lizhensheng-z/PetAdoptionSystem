package com.yr.pet.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @author 李振生
 */
@Data
//@ApiModel(description = "AI-用户提问记录")
@TableName(value = "ai_question_record")
public class QuestionRecordDO extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;

//    @ApiModelProperty(value = "用户ID")
    private Long userId;

    public static final Integer TYPE_QUESTION = 1;
    public static final Integer TYPE_RESPONSE = 2;

//    @ApiModelProperty(value = "类型，1问题 2回复")
    private Integer type;


//    @ApiModelProperty(value = "用户会话id")
    private Long sessionId;


//    @ApiModelProperty(value = "用户问题id（系统）")
    private Long questionId;

//    @ApiModelProperty(value = "deepseek回复id （外部）")
    private String responseId;

//    @ApiModelProperty(value = "请求原文")
    private String reqText;
//    @ApiModelProperty(value = "回复文本-原文")
    private String respText;

//              @ApiModelProperty(value = "tokens使用量")
    private Integer tokensUsed;
    public static final Integer STATUS_FAIL = 0;
    public static final Integer STATUS_SUCCESS = 1;
    public static final Integer STATUS_INTERRUPT = 2;
//    @ApiModelProperty(value = "状态，0失败 1成功 2中断")
    private Integer status;
    //deepseek响应成功 标志
    public static String RESPONSE_SUCCESS = "SUCCESS";
//    @ApiModelProperty(value = "备注")
    private String remark;
//    @ApiModelProperty(value = "请求体")
    private String reqParam;
//    @ApiModelProperty(value = "响应体")
    private String respResult;
//    @ApiModelProperty(value = "是否删除")
    private Boolean ifDelete;
}
