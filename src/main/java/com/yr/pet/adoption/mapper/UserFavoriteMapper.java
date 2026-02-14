package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.UserFavoriteEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户收藏表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavoriteEntity> {

    @Select("""
        SELECT id, user_id, pet_id, deleted, create_time
        FROM user_favorite
        WHERE user_id = #{userId}
          AND pet_id = #{petId}
        LIMIT 1
    """)
    UserFavoriteEntity selectAny(@Param("userId") Long userId,
                                 @Param("petId") Long petId);
}
