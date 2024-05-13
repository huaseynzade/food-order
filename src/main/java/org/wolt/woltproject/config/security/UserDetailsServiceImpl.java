package org.wolt.woltproject.config.security;


import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.RoleEnum;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity client = (UserEntity) repository.findByUsername(username).orElseThrow();
        List<String> roles = new ArrayList<>();
        Set<RoleEnum> authorities = Set.of(client.getRole());
        for (RoleEnum authority : authorities) {
            roles.add(authority.name());
        }
        UserDetails userDetails;
        userDetails = User.builder()
                .username(client.getUsername())
                .password(client.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
        return userDetails;
    }
}
