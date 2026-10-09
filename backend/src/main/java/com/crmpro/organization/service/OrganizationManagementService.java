package com.crmpro.organization.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.organization.dto.OrganizationResponse;
import com.crmpro.organization.dto.UpdateOrganizationRequest;
import com.crmpro.organization.entity.Organization;
import com.crmpro.organization.repository.OrganizationRepository;
import com.crmpro.user.dto.CreateUserRequest;
import com.crmpro.user.dto.UserResponse;
import com.crmpro.user.entity.User;
import com.crmpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationManagementService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public OrganizationResponse getCurrentOrganization() {
        UUID organizationId = TenantContext.getTenantId();
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada"));
        return mapToOrgResponse(org);
    }

    @Transactional
    public OrganizationResponse updateOrganization(UpdateOrganizationRequest request) {
        UUID organizationId = TenantContext.getTenantId();
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada"));

        org.setName(request.getName());
        org.setLegalName(request.getLegalName());
        org.setDocument(request.getDocument());
        org.setEmail(request.getEmail());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());
        org.setLogoUrl(request.getLogoUrl());

        org = organizationRepository.save(org);
        return mapToOrgResponse(org);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listTeamMembers() {
        UUID organizationId = TenantContext.getTenantId();
        return userRepository.findAllByOrganizationId(organizationId).stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse addTeamMember(CreateUserRequest request) {
        UUID organizationId = TenantContext.getTenantId();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        User user = User.builder()
                .organizationId(organizationId)
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status("ACTIVE")
                .build();

        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public void removeTeamMember(UUID userId) {
        UUID organizationId = TenantContext.getTenantId();
        User user = userRepository.findByIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de equipe não encontrado"));

        userRepository.delete(user);
    }

    private OrganizationResponse mapToOrgResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .legalName(org.getLegalName())
                .document(org.getDocument())
                .email(org.getEmail())
                .phone(org.getPhone())
                .plan(org.getPlan())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.fromEntity(user);
    }
}
