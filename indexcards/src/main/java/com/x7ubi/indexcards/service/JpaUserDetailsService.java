package com.x7ubi.indexcards.service;

import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public JpaUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
