package com.yr.pet.ai.model.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalanceVO {
//    @ApiModelProperty(value = "是否可用")
    private Boolean available;   // is_available
//    @ApiModelProperty(value = "总余额")
    private BigDecimal totalBalance;
//    @ApiModelProperty(value = "赠送余额")
    private BigDecimal grantedBalance;  // granted_balance
//    @ApiModelProperty(value = "充值余额")
    private BigDecimal toppedUpBalance; // topped_up_balance
}