package com.geology.geolabsystem.tracking.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public record SecurityUser(UserEntity user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override public String getPassword()  { return user.getPassword(); }
    @Override public String getUsername()   { return user.getUsername(); }

    @Override public boolean isEnabled()               { return Boolean.TRUE.equals(user.getEnabled()); }
    @Override public boolean isAccountNonLocked()      { return Boolean.TRUE.equals(user.getAccountNonLocked()); }
    @Override public boolean isAccountNonExpired()      { return Boolean.TRUE.equals(user.getAccountNonExpired()); }
    @Override public boolean isCredentialsNonExpired()  { return Boolean.TRUE.equals(user.getCredentialsNonExpired()); }
}

