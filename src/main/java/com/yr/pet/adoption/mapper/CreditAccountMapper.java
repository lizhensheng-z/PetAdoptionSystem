package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.CreditAccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 信用账户Mapper接口
 * 对应数据库表：credit_account
 * 
 * @author 宗平
 * @since 2024-02-18
 */
@Mapper
public interface CreditAccountMapper extends BaseMapper<CreditAccountEntity> {
}