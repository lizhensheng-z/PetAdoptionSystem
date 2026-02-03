package com.yr.pet.adoption.security;

import com.yr.pet.adoption.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情类
 * @author yr
 * @since 2024-01-01
 */
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user, List<String> permissions) {
        this.user = user;
        this.authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }



    public Long getUserId() {
        return user.getId();
    }

    public String getRole() {
        return user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "NORMAL".equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getDeleted() == 0;
    }
}