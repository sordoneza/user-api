package com.nisum.user.service;

import com.nisum.user.model.entity.User;
import com.nisum.user.repository.UserRepo;
import com.nisum.user.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority(user.getRole().name())))
                .build();
    }

    @Override
    public UserDetails loadUserByToken(String token) throws UsernameNotFoundException {

        String email = jwtUtil.extractSubject(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority(user.getRole().name())))
                .build();
    }
}
