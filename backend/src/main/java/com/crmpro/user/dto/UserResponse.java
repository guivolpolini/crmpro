package com.crmpro.user.dto;

import com.crmpro.user.entity.Role;
import com.crmpro.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String email;
    private Role role;
    private String avatarUrl;
    private String status;
    private Instant createdAt;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .organizationId(user.getOrganizationId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
