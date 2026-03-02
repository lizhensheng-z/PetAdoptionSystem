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
@Data
@EqualsAndHashCode(callSuper = true)
//@ApiModel(description = "AI用户会话")
@TableName("ai_session")
public class SessionDO extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long sessionId;
//    @ApiModelProperty(value = "会标题")
    private String title;
//    @ApiModelProperty(value = "是否删除")
    @TableLogic(value = "0", delval = "1")
    private Boolean ifDelete;

}
