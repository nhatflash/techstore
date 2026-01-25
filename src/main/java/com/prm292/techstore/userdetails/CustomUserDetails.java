package com.prm292.techstore.userdetails;

import com.prm292.techstore.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Builder
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails create(User user) {
        Collection<GrantedAuthority> grantedAuthorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return CustomUserDetails.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .email(user.getEmail())
                .authorities(grantedAuthorities)
                .build();
    }

    @Override
    public @NullMarked Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NullMarked String getUsername() {
        return username;
    }
}
