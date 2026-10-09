package com.crmpro.auth.service;

import com.crmpro.auth.dto.LoginRequest;
import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RefreshTokenRequest;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.entity.RefreshToken;
import com.crmpro.auth.repository.RefreshTokenRepository;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.UnauthorizedException;
import com.crmpro.organization.dto.OrganizationResponse;
import com.crmpro.organization.entity.Organization;
import com.crmpro.organization.repository.OrganizationRepository;
import com.crmpro.security.JwtTokenProvider;
import com.crmpro.security.UserPrincipal;
import com.crmpro.user.dto.UserResponse;
import com.crmpro.user.entity.Role;
import com.crmpro.user.entity.User;
import com.crmpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Transactional
    public LoginResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        Organization org = Organization.builder()
                .name(request.getCompanyName())
                .document(request.getDocument())
                .phone(request.getPhone())
                .plan("FREE")
                .status("ACTIVE")
                .build();
        org = organizationRepository.save(org);

        User adminUser = User.builder()
                .organizationId(org.getId())
                .name(request.getAdminName())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);

        return createAuthResponse(adminUser, org);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("E-mail ou senha inválidos");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException("Este usuário está inativo");
        }

        Organization org = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new BusinessException("Organização associada não encontrada"));

        return createAuthResponse(user, org);
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expirado ou revogado");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        Organization org = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new BusinessException("Organização não encontrada"));

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = tokenProvider.generateAccessToken(principal);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .user(UserResponse.fromEntity(user))
                .organization(OrganizationResponse.fromEntity(org))
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        return UserResponse.fromEntity(user);
    }

    private LoginResponse createAuthResponse(User user, Organization org) {
        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshTokenValue = tokenProvider.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshTokenValue)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .user(UserResponse.fromEntity(user))
                .organization(OrganizationResponse.fromEntity(org))
                .build();
    }
}
