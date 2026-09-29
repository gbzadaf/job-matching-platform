package com.gabrielf.job_matching_platform.security;

import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserService userService;

    public User getCurrentUser(UserDetails userDetails) {
        return userService.findEntityByEmail(userDetails.getUsername());

    }

}
