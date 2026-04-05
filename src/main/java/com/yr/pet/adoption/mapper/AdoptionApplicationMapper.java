package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.model.entity.AdoptionApplicationEntity;
import com.yr.pet.adoption.model.vo.MyApplicationVO;
import com.yr.pet.adoption.model.vo.OrgApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 领养申请Mapper接口
 * @author yr
 * @since 2026-01-01
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplicationEntity> {

    /**
     * 统计用户某宠物的有效申请次数（排除已取消/已拒绝的申请）
     */
    @Select("SELECT COUNT(*) FROM adoption_application WHERE user_id = #{userId} AND pet_id = #{petId} AND status NOT IN ('CANCELLED', 'REJECTED') AND deleted = 0")
    int countValidApplicationsByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 统计用户某宠物的所有申请次数（包括已取消/已拒绝）
     */
    @Select("SELECT COUNT(*) FROM adoption_application WHERE user_id = #{userId} AND pet_id = #{petId} AND deleted = 0")
    int countAllApplicationsByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 获取用户某宠物的最新申请状态
     */
    @Select("SELECT status FROM adoption_application WHERE user_id = #{userId} AND pet_id = #{petId} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    String getLatestApplicationStatus(@Param("userId") Long userId, @Param("petId") Long petId);

    /**
     * 统计宠物进行中的申请数量
     */
    @Select("SELECT COUNT(*) FROM adoption_application WHERE pet_id = #{petId} AND status IN ('SUBMITTED', 'UNDER_REVIEW', 'INTERVIEW', 'HOME_VISIT') AND deleted = 0")
    int countActiveApplicationsByPetId(@Param("petId") Long petId);

    /**
     * 统计用户进行中的申请数量
     */
    @Select("SELECT COUNT(*) FROM adoption_application WHERE user_id = #{userId} AND status IN ('SUBMITTED', 'UNDER_REVIEW', 'INTERVIEW', 'HOME_VISIT') AND deleted = 0")
    int countActiveApplicationsByUserId(@Param("userId") Long userId);

    /**
     * 获取我的申请列表
     */
    IPage<MyApplicationVO> selectMyApplications(
            Page<MyApplicationVO> page,
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("sortBy") String sortBy,
            @Param("order") String order
    );

    /**
     * 获取机构的申请列表
     */
    IPage<OrgApplicationVO> selectOrgApplications(
            Page<OrgApplicationVO> page,
            @Param("orgUserId") Long orgUserId,
            @Param("petId") Long petId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("order") String order
    );

    /**
     * 根据宠物ID查询所有申请
     */
    @Select("SELECT * FROM adoption_application WHERE pet_id = #{petId} AND deleted = 0")
    List<AdoptionApplicationEntity> selectByPetId(@Param("petId") Long petId);
}