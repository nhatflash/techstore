package com.prm292.techstore.userdetails;

import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (username.contains("@")) {
            user = userRepository.findFirstByEmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials."));
        } else {
            user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials."));
        }
        return CustomUserDetails.create(user);
    }
}
