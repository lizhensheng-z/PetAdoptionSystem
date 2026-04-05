package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.exception.BizException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 机构管理服务测试类
 * @author yr
 * @since 2026-01-01
 */
@ExtendWith(MockitoExtension.class)
class OrgProfileServiceImplTest {

    @Mock
    private OrgProfileMapper orgProfileMapper;

    @InjectMocks
    private OrgProfileServiceImpl orgProfileService;

    @Test
    void testGetProfile_WhenExists() {
        // Given
        Long userId = 1L;
        OrgProfileEntity entity = new OrgProfileEntity();
        entity.setId(1L);
        entity.setUserId(userId);
        entity.setOrgName("测试机构");
        
        when(orgProfileMapper.selectOne(any())).thenReturn(entity);

        // When
        var result = orgProfileService.getProfile(userId);

        // Then
        assertNotNull(result);
        assertEquals("测试机构", result.getOrgName());
    }

    @Test
    void testGetProfile_WhenNotExists() {
        // Given
        Long userId = 1L;
        when(orgProfileMapper.selectOne(any())).thenReturn(null);

        // When & Then
        assertThrows(BizException.class, () -> orgProfileService.getProfile(userId));
    }




}