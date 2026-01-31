package org.development.ExpenceTracker.service;

import java.util.Map;
import java.util.UUID;
import org.development.ExpenceTracker.dto.AuthDTO;
import org.development.ExpenceTracker.dto.ProfileDTO;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.ProfileRepo;
import org.development.ExpenceTracker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${money.manager.backend.url}")
    private String activationURL;

    public ProfileService(ProfileRepo profileRepo, EmailService emailService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.profileRepo = profileRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public ProfileDTO create(ProfileDTO profileDto) {
        ProfileEntity profileEntity = toEntity(profileDto);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity.setPassword(passwordEncoder.encode(profileEntity.getPassword()));
        profileEntity = profileRepo.save(profileEntity);

        // activatopm mail
        String activationLink =
            activationURL +"/api/1.0/activate?token=" + profileEntity.getActivationToken();
        String subject = "Activate Your Account";
        String body = "click bellow link : " + activationLink;
        emailService.sendEmail(profileEntity.getEmail(), subject, body);

        return toDto(profileEntity);
    }

    public ProfileEntity toEntity(ProfileDTO profileDto) {
        return ProfileEntity.builder()
                            .id(profileDto.getId())
                            .name(profileDto.getName())
                            .email(profileDto.getEmail())
                            .password(profileDto.getPassword())
                            .profileImageUrl(profileDto.getPassword())
                            .createdAt(profileDto.getCreatedAt())
                            .updatedAt(profileDto.getUpdatedAt())
                            .isActive(profileDto.getIsActive())
                            .activationToken(profileDto.getActivationToken())
                            .build();
    }

    public ProfileDTO toDto(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                         .id(profileEntity.getId())
                         .name(profileEntity.getName())
                         .email(profileEntity.getEmail())
                         .password(profileEntity.getPassword())
                         .profileImageUrl(profileEntity.getPassword())
                         .createdAt(profileEntity.getCreatedAt())
                         .updatedAt(profileEntity.getUpdatedAt())
                         .isActive(profileEntity.getActive())
                         .activationToken(profileEntity.getActivationToken())
                         .build();
    }


    public boolean validateToken(String activateToken) {

        return profileRepo.findByActivationToken(activateToken).map(
            profile -> {
                profile.setActive(true);
                profileRepo.save(profile);
                return true;
            }
        ).orElse(false);

    }




    public boolean isAccountActive(String email) {
        return profileRepo.findByEmail(email)
                          .map(bean -> bean.getActive())
                          .orElse(false);


    }

    public Map<String, Object> authAndGenerateToken(AuthDTO authDTO) {
        try {

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            // Generate JWT token

            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                "token", token,
                "user",getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e){
            throw new RuntimeException("invalid password :"+e);
        }
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentProfile = null;
        if (email == null) {
            currentProfile = getCurrentProfile();
        } else {
            currentProfile = profileRepo.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("profile not found " + email));
        }

        return ProfileDTO.builder()
                         .id(currentProfile.getId())
                         .name(currentProfile.getName())
                         .email(currentProfile.getEmail())
                         .profileImageUrl(currentProfile.getProfileImageUrl())
                         .createdAt(currentProfile.getCreatedAt())
                         .updatedAt(currentProfile.getUpdatedAt())
                         .build();
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName()).orElseThrow(
            () -> new UsernameNotFoundException("profile not found " + authentication.getName()));
    }
}
