package com.geology.geolabsystem.tracking.security.service;

import com.geology.geolabsystem.tracking.security.entity.SecurityUser;
import com.geology.geolabsystem.tracking.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
