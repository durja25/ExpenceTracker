package org.development.ExpenceTracker.service;


import java.util.Collections;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.ProfileRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final ProfileRepo profileRepo;

    public UserService(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingUser = profileRepo.findByEmail(email)
                                                .orElseThrow(() -> new UsernameNotFoundException(
                                                    "User not found: " + email));

        return User.builder()
                   .username(existingUser.getEmail())
                   .password(existingUser.getPassword())
                   .authorities(Collections.emptyList())
                   .build();
    }






}
