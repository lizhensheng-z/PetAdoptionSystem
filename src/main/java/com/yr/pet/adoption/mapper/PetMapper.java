package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.dto.PetListResponse;
import com.yr.pet.adoption.model.dto.OrgPetListResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 宠物档案Mapper接口
 * @author yr
 * @since 2026-01-01
 */
@Repository
public interface PetMapper extends BaseMapper<PetEntity> {

    /**
     * 获取宠物列表（游客/用户通用）
     */
    IPage<PetListResponse> selectPetList(Page<PetListResponse> page, 
                                       @Param("species") String species,
                                       @Param("breed") String breed,
                                       @Param("gender") String gender,
                                       @Param("sizeMin") Integer sizeMin,
                                       @Param("sizeMax") Integer sizeMax,
                                       @Param("ageMin") Integer ageMin,
                                       @Param("ageMax") Integer ageMax,
                                       @Param("sterilized") Boolean sterilized,
                                       @Param("vaccinated") Boolean vaccinated,
                                       @Param("keyword") String keyword,
                                       @Param("tags") List<Long> tags,
                                       @Param("lng") Double lng,
                                       @Param("lat") Double lat,
                                       @Param("distance") Integer distance,
                                       @Param("sortBy") String sortBy,
                                       @Param("order") String order);

    /**
     * 机构查看自己的宠物列表
     */
    IPage<OrgPetListResponse> selectOrgPetList(Page<OrgPetListResponse> page,
                                             @Param("orgUserId") Long orgUserId,
                                             @Param("status") String status,
                                             @Param("auditStatus") String auditStatus,
                                             @Param("sortBy") String sortBy,
                                             @Param("order") String order);

    /**
     * 根据ID获取宠物详情
     */
    PetListResponse selectPetDetailById(@Param("petId") Long petId);

    /**
     * 检查宠物是否属于指定机构
     */
    @Select("SELECT COUNT(*) FROM pet WHERE id = #{petId} AND org_user_id = #{orgUserId} AND deleted = 0")
    int countByIdAndOrgUserId(@Param("petId") Long petId, @Param("orgUserId") Long orgUserId);

    /**
     * 获取机构宠物数量
     */
    @Select("SELECT COUNT(*) FROM pet WHERE org_user_id = #{orgUserId} AND deleted = 0")
    int countByOrgUserId(@Param("orgUserId") Long orgUserId);
}