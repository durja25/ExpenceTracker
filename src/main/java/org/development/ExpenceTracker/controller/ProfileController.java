package org.development.ExpenceTracker.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.dto.AuthDTO;
import org.development.ExpenceTracker.dto.ProfileDTO;
import org.development.ExpenceTracker.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerUser(@RequestBody ProfileDTO profileDto) {
        ProfileDTO profileDTO1 = profileService.create(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDTO1);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam("token") String token) {
        boolean validateToken = profileService.validateToken(token);
        if (validateToken) {
            return ResponseEntity.ok("profile activated successfully");
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {

            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body(Map.of("message", "account is not active state"));

            }
            Map<String, Object> authAndGenerateToken = profileService.authAndGenerateToken(authDTO);
            return ResponseEntity.ok(authAndGenerateToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", e.getMessage()));

        }
    }

    @GetMapping("/me")
        public ResponseEntity<ProfileDTO> getProfile() {
            ProfileDTO profile = profileService.getPublicProfile(null);
            return ResponseEntity.ok(profile);
        }

}
