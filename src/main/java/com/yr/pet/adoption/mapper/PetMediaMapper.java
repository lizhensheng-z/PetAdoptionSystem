package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.PetMediaEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 宠物媒体Mapper接口
 * @author yr
 * @since 2026-01-01
 */
@Repository
public interface PetMediaMapper extends BaseMapper<PetMediaEntity> {

    /**
     * 获取宠物的媒体列表
     */
    @Select("SELECT * FROM pet_media WHERE pet_id = #{petId} AND deleted = 0 ORDER BY sort ASC, id ASC")
    List<PetMediaEntity> selectByPetId(@Param("petId") Long petId);

    /**
     * 获取宠物媒体数量
     */
    @Select("SELECT COUNT(*) FROM pet_media WHERE pet_id = #{petId} AND deleted = 0")
    int countByPetId(@Param("petId") Long petId);

    /**
     * 获取最大排序号
     */
    @Select("SELECT COALESCE(MAX(sort), 0) FROM pet_media WHERE pet_id = #{petId} AND deleted = 0")
    int getMaxSortByPetId(@Param("petId") Long petId);
}