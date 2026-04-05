package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.PetTagEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 宠物-标签关联Mapper接口
 * @author yr
 * @since 2026-01-01
 */
@Repository
public interface PetTagMapper extends BaseMapper<PetTagEntity> {

    /**
     * 获取宠物的标签ID列表
     */
    @Select("SELECT tag_id FROM pet_tag WHERE pet_id = #{petId} AND deleted = 0")
    List<Long> selectTagIdsByPetId(@Param("petId") Long petId);

    /**
     * 删除宠物的所有标签关联
     */
    @Update("UPDATE pet_tag SET deleted = 1 WHERE pet_id = #{petId}")
    void deleteByPetId(@Param("petId") Long petId);

    /**
     * 检查标签是否已关联到宠物
     */
    @Select("SELECT COUNT(*) FROM pet_tag WHERE pet_id = #{petId} AND tag_id = #{tagId} AND deleted = 0")
    int countByPetIdAndTagId(@Param("petId") Long petId, @Param("tagId") Long tagId);
}