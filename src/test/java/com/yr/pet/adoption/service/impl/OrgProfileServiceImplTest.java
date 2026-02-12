package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.model.dto.OrgProfileUpdateRequest;
import com.yr.pet.adoption.service.OrgProfileService;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.common.BizException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 机构管理服务测试类
 * @author yr
 * @since 2024-01-01
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

    @Test
    void testUpdateProfile_WhenAddressChanged() {
        // Given
        Long userId = 1L;
        OrgProfileEntity entity = new OrgProfileEntity();
        entity.setId(1L);
        entity.setUserId(userId);
        entity.setOrgName("旧机构名");
        entity.setAddress("旧地址");
        
        OrgProfileUpdateRequest request = new OrgProfileUpdateRequest();
        request.setOrgName("新机构名");
        request.setAddress("新地址");
        request.setContactName("联系人");
        request.setContactPhone("13800138000");
        
        when(orgProfileMapper.selectOne(any())).thenReturn(entity);
        when(orgProfileMapper.updateById(any())).thenReturn(1);

        // When
        orgProfileService.updateProfile(userId, request);

        // Then
        verify(orgProfileMapper).updateById(argThat(updated -> 
            "新机构名".equals(updated.getOrgName()) &&
            "新地址".equals(updated.getAddress()) &&
            "PENDING".equals(updated.getVerifyStatus())
        ));
    }

    @Test
    void testUpdateProfile_WhenAddressNotChanged() {
        // Given
        Long userId = 1L;
        OrgProfileEntity entity = new OrgProfileEntity();
        entity.setId(1L);
        entity.setUserId(userId);
        entity.setOrgName("旧机构名");
        entity.setAddress("旧地址");
        entity.setVerifyStatus("APPROVED");
        
        OrgProfileUpdateRequest request = new OrgProfileUpdateRequest();
        request.setOrgName("新机构名");
        request.setAddress("旧地址");
        request.setContactName("联系人");
        request.setContactPhone("13800138000");
        
        when(orgProfileMapper.selectOne(any())).thenReturn(entity);
        when(orgProfileMapper.updateById(any())).thenReturn(1);

        // When
        orgProfileService.updateProfile(userId, request);

        // Then
        verify(orgProfileMapper).updateById(argThat(updated -> 
            "新机构名".equals(updated.getOrgName()) &&
            "旧地址".equals(updated.getAddress()) &&
            "APPROVED".equals(updated.getVerifyStatus())
        ));
    }
}