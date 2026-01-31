package org.development.ExpenceTracker.dto;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String activationToken;

    public ProfileDTO() {
    }

    public ProfileDTO(Long id, String name, String email, String password, String profileImageUrl,
                      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isActive,
                      String activationToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.activationToken = activationToken;
    }
}
